import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";

const TOKEN_TTL = "1d";
const REFRESH_TTL = "30d";

function jwtSecret() {
  const secret = process.env.JWT_SECRET;
  if (!secret || secret === "YOUR_JWT_SECRET") {
    throw new Error("JWT_SECRET is not configured.");
  }
  return secret;
}

export async function hashPassword(password) {
  return bcrypt.hash(password, 12);
}

export async function verifyPassword(password, hash) {
  if (!hash) return false;
  return bcrypt.compare(password, hash);
}

export function signTokenPair(payload) {
  return {
    accessToken: jwt.sign({ ...payload, tokenType: "access" }, jwtSecret(), { expiresIn: TOKEN_TTL }),
    refreshToken: jwt.sign({ ...payload, tokenType: "refresh" }, jwtSecret(), { expiresIn: REFRESH_TTL })
  };
}

export function readAuth(event) {
  const value = event.headers?.authorization || event.headers?.Authorization || "";
  const token = value.replace(/^Bearer\s+/i, "").trim();
  if (!token) throw Object.assign(new Error("Missing token."), { statusCode: 401 });
  try {
    return jwt.verify(token, jwtSecret());
  } catch {
    throw Object.assign(new Error("Invalid token."), { statusCode: 401 });
  }
}

export function verifyRefreshToken(token) {
  try {
    const decoded = jwt.verify(token, jwtSecret());
    if (decoded.tokenType !== "refresh") throw new Error("Invalid refresh token.");
    return decoded;
  } catch {
    throw Object.assign(new Error("Invalid refresh token."), { statusCode: 401 });
  }
}
