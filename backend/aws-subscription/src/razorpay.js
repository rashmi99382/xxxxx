import crypto from "crypto";

const planEnv = {
  BASIC: "RAZORPAY_PLAN_BASIC_ID",
  STANDARD: "RAZORPAY_PLAN_STANDARD_ID",
  PREMIUM: "RAZORPAY_PLAN_PREMIUM_ID"
};

export function razorpayKeyId() {
  return process.env.RAZORPAY_KEY_ID || "YOUR_KEY";
}

function razorpaySecret() {
  const secret = process.env.RAZORPAY_SECRET;
  if (!secret || secret === "YOUR_SECRET") {
    throw new Error("RAZORPAY_SECRET is not configured.");
  }
  return secret;
}

export function verifyWebhookSignature(rawBody, signature) {
  const expected = crypto.createHmac("sha256", razorpaySecret()).update(rawBody).digest("hex");
  if (!signature || expected.length !== signature.length) return false;
  return crypto.timingSafeEqual(Buffer.from(expected), Buffer.from(signature || ""));
}

export async function createRazorpaySubscription(plan) {
  const normalized = String(plan || "BASIC").toUpperCase();
  const planId = process.env[planEnv[normalized]];
  if (!planId) throw new Error(`Razorpay plan id missing for ${normalized}.`);

  const response = await fetch("https://api.razorpay.com/v1/subscriptions", {
    method: "POST",
    headers: {
      Authorization: `Basic ${Buffer.from(`${razorpayKeyId()}:${razorpaySecret()}`).toString("base64")}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      plan_id: planId,
      total_count: 12,
      quantity: 1,
      customer_notify: 1
    })
  });

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.error?.description || "Unable to create Razorpay subscription.");
  }
  return data;
}
