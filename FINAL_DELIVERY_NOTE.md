# Final Delivery Note

## Project Folder

```text
/Users/rashmiranjan/Documents/Codex/2026-06-13/design-all-jetpack-compose-screens-for/outputs/OfflinePharmacyInventoryApp
```

## APK Build Command

```bash
./gradlew clean
./gradlew assembleDebug
```

Debug APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Android Subscription Build Values

Current default backend values:

```text
BASE_API_URL=https://qz8gcambkc.execute-api.ap-south-1.amazonaws.com/default/
RAZORPAY_KEY_ID=YOUR_KEY
```

Override example:

```bash
./gradlew assembleDebug \
  -PBASE_API_URL=https://qz8gcambkc.execute-api.ap-south-1.amazonaws.com/default/ \
  -PRAZORPAY_KEY_ID=YOUR_RAZORPAY_KEY_ID
```

## Important Files

- `app/src/main/java/com/example/pharmacyinventory/ui/PharmacyInventoryComposeScreens.kt`
- `app/src/main/java/com/example/pharmacyinventory/subscription/SubscriptionModels.kt`
- `app/src/main/java/com/example/pharmacyinventory/subscription/SubscriptionCache.kt`
- `app/src/main/java/com/example/pharmacyinventory/subscription/SubscriptionApi.kt`
- `app/src/main/java/com/example/pharmacyinventory/subscription/SubscriptionRepositories.kt`
- `app/src/main/java/com/example/pharmacyinventory/subscription/SubscriptionViewModels.kt`
- `app/src/main/java/com/example/pharmacyinventory/PharmacyApplication.kt`
- `app/src/main/java/com/example/pharmacyinventory/MainActivity.kt`
- `app/src/main/java/com/example/pharmacyinventory/data/Entities.kt`
- `app/src/main/java/com/example/pharmacyinventory/data/DaoFiles.kt`
- `app/src/main/java/com/example/pharmacyinventory/data/AppDatabase.kt`
- `app/src/main/java/com/example/pharmacyinventory/data/PharmacyRepository.kt`
- `app/src/main/java/com/example/pharmacyinventory/data/LocalSyncEngine.kt`
- `app/src/main/java/com/example/pharmacyinventory/LocalPeerSyncManager.kt`
- `app/src/main/java/com/example/pharmacyinventory/LocalFileManager.kt`
- `app/src/main/java/com/example/pharmacyinventory/PinManager.kt`
- `app/src/main/java/com/example/pharmacyinventory/ExpiryReminderWorker.kt`
- `backend/aws-subscription/template.yaml`
- `backend/aws-subscription/src/handlers.js`
- `backend/aws-subscription/src/auth.js`
- `backend/aws-subscription/src/razorpay.js`
- `backend/aws-subscription/src/store.js`
- `backend/aws-subscription/.env.example`

## Features Completed

- Room database persistence for medicines, batches, suppliers, purchases, sales, sale items, and stock adjustments.
- Material 3 Compose screens with Navigation Compose.
- Add medicine and stock-in write to Room.
- Stock list, low stock, expiry alerts, and reports read from Room.
- Sell medicine creates a Room transaction and reduces stock with FEFO.
- Guarded SQL decrement blocks negative stock.
- Expired stock is blocked by default and requires explicit confirmation.
- PDF invoice export.
- Stock CSV export.
- Sales CSV export.
- Local database backup export and import.
- Encrypted PIN lock with `EncryptedSharedPreferences`.
- WorkManager local expiry reminder worker.
- Trusted peer-to-peer local sync over nearby Wi-Fi, hotspot, Wi-Fi Direct group, or LAN.
- Shop login, register shop, subscription plan, payment, status, device management, renewal, offline grace, and Super Admin dashboard Compose screens.
- Local subscription cache in `EncryptedSharedPreferences`.
- Retrofit subscription/account API layer with JWT auth interceptor.
- Razorpay payment launch through backend `shortUrl`, with Android Checkout fallback using backend-returned `keyId` and subscription id.
- Existing AWS backend integration for shop login, subscription status, payment creation, and device management.
- AWS backend scaffold remains as reference code only.

## Data Boundary

Local Room database only:

- Medicines
- Batches
- Stock
- Expiry dates
- Sales
- Purchases
- Customers/billing records
- Reports
- Stock adjustments
- PDF/CSV/backup data

AWS backend only:

- Shop/account metadata
- Subscription status and plan
- Allowed and registered device metadata
- Razorpay customer/subscription/payment metadata

No pharmacy operating data is uploaded to AWS by the subscription module.

## Local Sync Notes

Local sync uses Android NSD plus direct local sockets. Android requires network socket permissions for this, including `INTERNET`.

Devices are discovered automatically, but data is not exchanged automatically. The shop owner must open `Backup`, choose a nearby device, and tap `Trust & sync`. Unknown devices on the same Wi-Fi are blocked and cannot read or write pharmacy data. For two-way sync, approve the devices on both phones.

Merge keys:

- Suppliers: supplier name.
- Medicines: medicine name + company + generic name.
- Batches: medicine key + batch number + expiry date.
- Purchases: voucher number.
- Sales: bill number.

Known limitation: if two devices edit the same batch independently before syncing, the newest batch update wins during merge. Keep regular backups for audit safety.

## Verification

- Backend JavaScript syntax check passed with `node --check`.
- Live AWS smoke test passed with the provided test shop: login returned token, subscription status returned `ACTIVE BASIC`, and create-subscription returned a `sub_...` id plus `shortUrl`.
- Live AWS register route now exists as `POST /auth/register`; duplicate email returns `409`, and a temporary new registration returned `TRIAL`, `NONE`, 7-day expiry, and one registered device.
- Gradle wrapper is present.
- `./gradlew clean assembleDebug` passes.
- Debug APK path: `app/build/outputs/apk/debug/app-debug.apk`

## Known Limitations

- The app now targets your existing AWS API Gateway URL. The local `backend/aws-subscription` folder is not required for that deployment.
- Razorpay secret, webhook secret, and JWT secret must remain only on the backend.
- The current subscription gate disables dashboard entry when subscription is expired, but deeper per-screen read-only enforcement should be expanded after the live backend policy is finalized.
