# AWS Subscription Backend Scaffold

This folder is an additive backend scaffold for account, subscription, device, and Razorpay payment metadata only.

It must not receive or store medicines, batches, stock, expiry dates, sales, purchases, bills, reports, backups, or any other pharmacy operating data. Those records remain in the Android Room database.

## Architecture

- API Gateway HTTP API
- AWS Lambda on Node.js 20
- DynamoDB table keyed by `shopId`
- JWT access tokens
- Razorpay Checkout starts in Android
- Razorpay Subscription API and webhook handling stay on the backend

Use AWS Secrets Manager or secure Lambda environment variables for secrets. Do not put Razorpay secrets or JWT secrets in Android.

## Endpoints

- `POST /auth/register`
- `POST /auth/register-shop` legacy alias
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `GET /subscription/status`
- `POST /subscription/create`
- `POST /subscription/renew`
- `POST /subscription/cancel`
- `POST /devices/register`
- `GET /devices`
- `DELETE /devices/{deviceId}`
- `POST /admin/login`
- `GET /admin/dashboard`
- `POST /admin/shops`
- `GET /admin/shops`
- `POST /admin/shops/{shopId}/activate`
- `POST /admin/shops/{shopId}/suspend`
- `POST /admin/shops/{shopId}/extend`
- `DELETE /admin/shops/{shopId}/devices/{deviceId}`
- `POST /payments/create-subscription`
- `POST /payments/webhook`

## Local Checks

```bash
npm install
npm run lint
```

## Deploy Sketch

```bash
sam build
sam deploy --guided
```

After deploy, set Android `BASE_API_URL` to the API Gateway URL and `RAZORPAY_KEY_ID` to the public Razorpay key. Keep `RAZORPAY_SECRET` only in AWS.

## Register Shop Behavior

`POST /auth/register` creates a shop account with a 7-day local subscription trial:

- `subscriptionStatus = TRIAL`
- `subscriptionPlan = NONE`
- `allowedDevices = 1`
- `registeredDeviceCount = 1` after the current device is registered

It uses the DynamoDB `email-index` to reject duplicate emails with `409 Email already registered.`
