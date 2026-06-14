import crypto from "crypto";
import { hashPassword, readAuth, signTokenPair, verifyPassword, verifyRefreshToken } from "./auth.js";
import { createRazorpaySubscription, razorpayKeyId, verifyWebhookSignature } from "./razorpay.js";
import { createShop, findShopByEmail, findShopById, findShopByRazorpaySubscription, listShops, updateShop } from "./store.js";

const planLimits = { NONE: 1, BASIC: 3, STANDARD: 10, PREMIUM: 0 };

function addDaysDate(days) {
  return new Date(Date.now() + days * 24 * 60 * 60 * 1000).toISOString().slice(0, 10);
}

function json(statusCode, body) {
  return {
    statusCode,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  };
}

function parseBody(event) {
  if (!event.body) return {};
  const raw = event.isBase64Encoded ? Buffer.from(event.body, "base64").toString("utf8") : event.body;
  return JSON.parse(raw);
}

function rawBody(event) {
  return event.isBase64Encoded ? Buffer.from(event.body || "", "base64").toString("utf8") : (event.body || "");
}

function publicShop(shop) {
  return {
    shopId: shop.shopId,
    shopName: shop.shopName,
    ownerName: shop.ownerName,
    email: shop.email,
    subscriptionStatus: currentStatus(shop),
    subscriptionPlan: shop.subscriptionPlan,
    subscriptionStartDate: shop.subscriptionStartDate,
    subscriptionExpiryDate: shop.subscriptionExpiryDate,
    allowedDevices: shop.allowedDevices,
    registeredDevices: shop.registeredDevices || [],
    registeredDeviceCount: (shop.registeredDevices || []).length,
    lastValidationTime: shop.lastValidationTime,
    razorpayCustomerId: shop.razorpayCustomerId,
    razorpaySubscriptionId: shop.razorpaySubscriptionId,
    razorpayPlanId: shop.razorpayPlanId,
    paymentStatus: shop.paymentStatus,
    createdAt: shop.createdAt,
    updatedAt: shop.updatedAt
  };
}

function currentStatus(shop) {
  if (shop.subscriptionStatus === "SUSPENDED") return "SUSPENDED";
  if (shop.subscriptionStatus === "PAYMENT_FAILED") return "PAYMENT_FAILED";
  if (!shop.subscriptionExpiryDate) return shop.subscriptionStatus || "EXPIRED";
  if (shop.subscriptionStatus === "TRIAL") {
    return new Date(shop.subscriptionExpiryDate).getTime() >= Date.now() ? "TRIAL" : "EXPIRED";
  }
  return new Date(shop.subscriptionExpiryDate).getTime() >= Date.now() ? "ACTIVE" : "EXPIRED";
}

function statusResponse(shop, deviceInstallId) {
  const registeredDevices = shop.registeredDevices || [];
  const allowedDevices = shop.allowedDevices ?? planLimits[shop.subscriptionPlan || "BASIC"];
  const deviceAllowed = allowedDevices === 0 || registeredDevices.some((device) => device.deviceId === deviceInstallId) || registeredDevices.length < allowedDevices;
  return {
    shopId: shop.shopId,
    shopName: shop.shopName,
    ownerName: shop.ownerName,
    email: shop.email,
    status: currentStatus(shop),
    subscriptionStatus: currentStatus(shop),
    plan: shop.subscriptionPlan || null,
    subscriptionPlan: shop.subscriptionPlan || null,
    expiryDate: shop.subscriptionExpiryDate || null,
    allowedDevices,
    registeredDeviceCount: shop.registeredDeviceCount ?? registeredDevices.length,
    deviceAllowed,
    serverTime: new Date().toISOString()
  };
}

function registerDeviceLocally(shop, deviceInstallId, deviceName) {
  const registeredDevices = shop.registeredDevices || [];
  if (registeredDevices.some((device) => device.deviceId === deviceInstallId)) return registeredDevices;
  const allowedDevices = shop.allowedDevices ?? planLimits[shop.subscriptionPlan || "BASIC"];
  if (allowedDevices !== 0 && registeredDevices.length >= allowedDevices) {
    throw Object.assign(new Error("Device limit exceeded."), { statusCode: 403 });
  }
  return registeredDevices.concat({
    deviceId: deviceInstallId,
    deviceName: deviceName || "Android device",
    registeredAt: new Date().toISOString(),
    lastSeenAt: new Date().toISOString(),
    lastLoginAt: new Date().toISOString(),
    status: "ACTIVE"
  });
}

async function requireShop(event) {
  const auth = readAuth(event);
  const shop = await findShopById(auth.shopId);
  if (!shop) throw Object.assign(new Error("Shop not found."), { statusCode: 404 });
  return { auth, shop };
}

function requireAdmin(event) {
  const auth = readAuth(event);
  if (auth.role !== "SUPER_ADMIN") throw Object.assign(new Error("Admin required."), { statusCode: 403 });
  return auth;
}

async function handleRegisterShop(body) {
  const now = new Date().toISOString();
  const email = String(body.email || "").trim().toLowerCase();
  if (await findShopByEmail(email)) {
    return json(409, { message: "Email already registered." });
  }
  const shopId = `SHOP_${Date.now()}`;
  const plan = "NONE";
  const deviceId = body.deviceId || body.deviceInstallId;
  const expiryDate = addDaysDate(7);
  const initialShop = {
    shopId,
    shopName: body.shopName,
    ownerName: body.ownerName,
    email,
    passwordHash: await hashPassword(body.password || ""),
    subscriptionStatus: "TRIAL",
    subscriptionPlan: plan,
    subscriptionStartDate: now.slice(0, 10),
    subscriptionExpiryDate: expiryDate,
    allowedDevices: 1,
    registeredDeviceCount: 0,
    registeredDevices: [],
    paymentStatus: "TRIAL",
    createdAt: now,
    updatedAt: now
  };
  const registeredDevices = registerDeviceLocally(initialShop, deviceId, body.deviceName);
  const shop = await createShop({
    ...initialShop,
    registeredDevices,
    registeredDeviceCount: registeredDevices.length
  });
  const tokens = signTokenPair({ shopId, role: "SHOP_ADMIN", deviceInstallId: deviceId });
  return json(200, {
    token: tokens.accessToken,
    ...tokens,
    shopId,
    shopName: shop.shopName,
    ownerName: shop.ownerName,
    email: shop.email,
    subscriptionStatus: "TRIAL",
    subscriptionPlan: "NONE",
    expiryDate,
    allowedDevices: 1,
    registeredDeviceCount: registeredDevices.length
  });
}

async function handleLogin(body) {
  const shop = await findShopByEmail(String(body.email || "").toLowerCase());
  if (!shop || !(await verifyPassword(body.password || "", shop.passwordHash))) {
    return json(401, { message: "Invalid email or password." });
  }
  const deviceId = body.deviceId || body.deviceInstallId;
  const registeredDevices = registerDeviceLocally(shop, deviceId, body.deviceName);
  const updated = await updateShop(shop.shopId, {
    registeredDevices,
    registeredDeviceCount: registeredDevices.length,
    lastValidationTime: new Date().toISOString()
  });
  const tokens = signTokenPair({ shopId: shop.shopId, role: "SHOP_ADMIN", deviceInstallId: deviceId });
  const status = statusResponse(updated, deviceId);
  return json(200, {
    token: tokens.accessToken,
    ...tokens,
    shopId: shop.shopId,
    shopName: shop.shopName,
    ownerName: shop.ownerName,
    email: shop.email,
    subscriptionStatus: status.subscriptionStatus,
    subscriptionPlan: status.subscriptionPlan,
    expiryDate: status.expiryDate,
    allowedDevices: status.allowedDevices,
    registeredDeviceCount: status.registeredDeviceCount,
    subscription: status
  });
}

async function handleCreateSubscription(event, renewal = false) {
  const { shop } = await requireShop(event);
  const body = parseBody(event);
  const plan = String(body.plan || shop.subscriptionPlan || "BASIC").toUpperCase();
  const razorpaySubscription = await createRazorpaySubscription(plan);
  await updateShop(shop.shopId, {
    subscriptionPlan: plan,
    allowedDevices: planLimits[plan],
    razorpaySubscriptionId: razorpaySubscription.id,
    razorpayPlanId: razorpaySubscription.plan_id,
    paymentStatus: renewal ? "RENEWAL_STARTED" : "STARTED"
  });
  return json(200, {
    shopId: shop.shopId,
    razorpayKeyId: razorpayKeyId(),
    razorpayCustomerId: razorpaySubscription.customer_id || null,
    razorpaySubscriptionId: razorpaySubscription.id,
    razorpayOrderId: null,
    amount: null,
    currency: "INR",
    plan
  });
}

async function handleAdminDashboard() {
  const shops = await listShops();
  const currentMonth = new Date().toISOString().slice(0, 7);
  const planWiseShops = {};
  let monthlyRevenue = 0;
  const recentRenewals = [];
  const paymentFailures = [];
  const expiryList = [];

  for (const shop of shops) {
    const plan = shop.subscriptionPlan || "UNKNOWN";
    planWiseShops[plan] = (planWiseShops[plan] || 0) + 1;
    if (shop.paymentStatus === "PAID" && String(shop.updatedAt || "").startsWith(currentMonth)) {
      monthlyRevenue += plan === "PREMIUM" ? 299 : plan === "STANDARD" ? 199 : 99;
      recentRenewals.push(`${shop.shopName || shop.shopId} - ${plan}`);
    }
    if (shop.paymentStatus === "FAILED") paymentFailures.push(shop.shopName || shop.shopId);
    if (shop.subscriptionExpiryDate) expiryList.push(`${shop.shopName || shop.shopId} - ${shop.subscriptionExpiryDate}`);
  }

  return json(200, {
    totalShops: shops.length,
    activeShops: shops.filter((shop) => currentStatus(shop) === "ACTIVE").length,
    expiredShops: shops.filter((shop) => currentStatus(shop) === "EXPIRED").length,
    suspendedShops: shops.filter((shop) => currentStatus(shop) === "SUSPENDED").length,
    monthlyRevenue,
    planWiseShops,
    deviceUsage: shops.reduce((total, shop) => total + (shop.registeredDevices || []).length, 0),
    recentRenewals,
    paymentFailures,
    expiryList
  });
}

async function handleWebhook(event) {
  const signature = event.headers?.["x-razorpay-signature"] || event.headers?.["X-Razorpay-Signature"];
  const bodyText = rawBody(event);
  if (!verifyWebhookSignature(bodyText, signature)) return json(400, { message: "Invalid webhook signature." });
  const payload = JSON.parse(bodyText);
  const eventName = payload.event;
  const subscriptionId = payload.payload?.subscription?.entity?.id || payload.payload?.payment?.entity?.subscription_id;
  const shop = subscriptionId ? await findShopByRazorpaySubscription(subscriptionId) : null;
  if (!shop) return json(200, { ok: true });

  const patch = {};
  if (eventName === "payment.captured" || eventName === "subscription.activated") {
    patch.subscriptionStatus = "ACTIVE";
    patch.paymentStatus = "PAID";
    patch.subscriptionStartDate = shop.subscriptionStartDate || new Date().toISOString();
    patch.subscriptionExpiryDate = new Date(Date.now() + 31 * 24 * 60 * 60 * 1000).toISOString();
  } else if (eventName === "payment.failed") {
    patch.subscriptionStatus = "PAYMENT_FAILED";
    patch.paymentStatus = "FAILED";
  } else if (eventName === "subscription.cancelled" || eventName === "subscription.completed") {
    patch.subscriptionStatus = "EXPIRED";
    patch.paymentStatus = eventName === "subscription.cancelled" ? "CANCELLED" : "COMPLETED";
  }
  if (Object.keys(patch).length) await updateShop(shop.shopId, patch);
  return json(200, { ok: true });
}

export async function handler(event) {
  try {
    const method = event.requestContext?.http?.method || event.httpMethod;
    const path = event.rawPath || event.path || "/";
    const body = parseBody(event);

    if (method === "POST" && (path === "/auth/register" || path === "/auth/register-shop")) return handleRegisterShop(body);
    if (method === "POST" && path === "/auth/login") return handleLogin(body);
    if (method === "POST" && path === "/auth/refresh") {
      const auth = verifyRefreshToken(body.refreshToken || "");
      return json(200, signTokenPair({ shopId: auth.shopId, role: auth.role }));
    }
    if (method === "POST" && path === "/auth/logout") return json(204, {});
    if (method === "GET" && path === "/subscription/status") {
      const { auth, shop } = await requireShop(event);
      const updated = await updateShop(shop.shopId, { lastValidationTime: new Date().toISOString() });
      return json(200, statusResponse(updated, auth.deviceInstallId));
    }
    if (method === "POST" && path === "/subscription/create") return handleCreateSubscription(event, false);
    if (method === "POST" && path === "/subscription/renew") return handleCreateSubscription(event, true);
    if (method === "POST" && path === "/subscription/cancel") {
      const { shop } = await requireShop(event);
      await updateShop(shop.shopId, { subscriptionStatus: "EXPIRED", paymentStatus: "CANCELLED" });
      return json(204, {});
    }
    if (method === "POST" && path === "/devices/register") {
      const { shop } = await requireShop(event);
      const deviceId = body.deviceId || body.deviceInstallId;
      const registeredDevices = registerDeviceLocally(shop, deviceId, body.deviceName);
      const updated = await updateShop(shop.shopId, { registeredDevices, registeredDeviceCount: registeredDevices.length });
      return json(200, statusResponse(updated, deviceId));
    }
    if (method === "GET" && path === "/devices") {
      const { shop } = await requireShop(event);
      return json(200, shop.registeredDevices || []);
    }
    if (method === "DELETE" && path.startsWith("/devices/")) {
      const { shop } = await requireShop(event);
      const deviceId = decodeURIComponent(path.split("/").pop());
      await updateShop(shop.shopId, { registeredDevices: (shop.registeredDevices || []).filter((device) => device.deviceId !== deviceId) });
      return json(204, {});
    }
    if (method === "POST" && path === "/admin/login") {
      const ok = body.email === process.env.ADMIN_EMAIL && await verifyPassword(body.password || "", process.env.ADMIN_PASSWORD_HASH);
      if (!ok) return json(401, { message: "Invalid admin login." });
      return json(200, { ...signTokenPair({ role: "SUPER_ADMIN" }), shopId: "admin", shopName: "Super Admin", ownerEmail: body.email, subscription: statusResponse({ shopId: "admin", subscriptionStatus: "ACTIVE", registeredDevices: [] }, null) });
    }
    if (method === "GET" && path === "/admin/dashboard") {
      requireAdmin(event);
      return handleAdminDashboard();
    }
    if (method === "GET" && path === "/admin/shops") {
      requireAdmin(event);
      return json(200, (await listShops()).map(publicShop));
    }
    if (method === "POST" && path === "/admin/shops") {
      requireAdmin(event);
      return handleRegisterShop(body);
    }
    if (method === "POST" && path.match(/^\/admin\/shops\/[^/]+\/(activate|suspend|extend)$/)) {
      requireAdmin(event);
      const parts = path.split("/");
      const shopId = parts[3];
      const action = parts[4];
      const fields = action === "suspend"
        ? { subscriptionStatus: "SUSPENDED" }
        : { subscriptionStatus: "ACTIVE", subscriptionExpiryDate: new Date(Date.now() + 31 * 24 * 60 * 60 * 1000).toISOString() };
      return json(200, publicShop(await updateShop(decodeURIComponent(shopId), fields)));
    }
    if (method === "DELETE" && path.match(/^\/admin\/shops\/[^/]+\/devices\/[^/]+$/)) {
      requireAdmin(event);
      const parts = path.split("/");
      const shopId = parts[3];
      const deviceId = parts[5];
      const shop = await findShopById(decodeURIComponent(shopId));
      await updateShop(shop.shopId, { registeredDevices: (shop.registeredDevices || []).filter((device) => device.deviceId !== decodeURIComponent(deviceId)) });
      return json(204, {});
    }
    if (method === "POST" && path === "/payments/create-subscription") return handleCreateSubscription(event, false);
    if (method === "POST" && path === "/payments/webhook") return handleWebhook(event);

    return json(404, { message: "Route not found." });
  } catch (error) {
    return json(error.statusCode || 500, { message: error.message || "Server error." });
  }
}
