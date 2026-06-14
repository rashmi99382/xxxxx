# Offline Pharmacy Inventory Android App

Android Studio project for a small pharmacy inventory, billing, reports, backup, local sync, and subscription-gated shop account workflow.

The pharmacy operating data remains offline-first and local-only in Room. The optional online subscription module sends only shop account, subscription, device, and Razorpay payment metadata to the backend. It must not upload medicines, batches, stock, expiry dates, sales, purchases, bills, reports, backups, or pharmacy business data.

Built with:

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room database
- Repository + ViewModel architecture
- AndroidX Security encrypted PIN and subscription token storage
- WorkManager local expiry reminders
- Android Storage Access Framework for local PDF, CSV, and database backup files
- Local peer-to-peer sync over nearby Wi-Fi, hotspot, Wi-Fi Direct group, or LAN
- Retrofit for subscription/account API calls
- Razorpay Android Checkout and hosted `shortUrl` payment launch

## Open in Android Studio

Open this folder:

```text
OfflinePharmacyInventoryApp
```

Then sync Gradle and run the `app` configuration.

## Gradle JDK Setup

If the terminal or Android Studio shows:

```text
Unable to locate a Java Runtime
```

set the Gradle JDK in Android Studio:

1. Open Android Studio.
2. Go to `Settings` or `Preferences`.
3. Open `Build, Execution, Deployment > Build Tools > Gradle`.
4. Set `Gradle JDK` to the Android Studio embedded JDK, usually named `Embedded JDK`, `jbr-17`, or `Android Studio default JDK`.
5. Sync Gradle again.

The Android Studio embedded JDK is sufficient. A separate system Java install is not required if Android Studio exposes its embedded JDK to Gradle.

## Compile Check

After a JDK is available, run:

```bash
./gradlew clean
./gradlew assembleDebug
```

The debug APK is created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Android Subscription Config

The app now points to your existing AWS backend by default:

- `BASE_API_URL=https://qz8gcambkc.execute-api.ap-south-1.amazonaws.com/default/`
- `RAZORPAY_KEY_ID=YOUR_KEY`

The app uses the Razorpay public `keyId` returned from the backend payment API. You can still override build values through Gradle properties:

```bash
./gradlew assembleDebug \
  -PBASE_API_URL=https://qz8gcambkc.execute-api.ap-south-1.amazonaws.com/default/ \
  -PRAZORPAY_KEY_ID=YOUR_RAZORPAY_KEY_ID
```

Do not put `RAZORPAY_SECRET`, JWT secrets, database credentials, or AWS secrets in Android.

## AWS Subscription Backend

Backend scaffold:

```text
backend/aws-subscription
```

It contains:

- API Gateway + Lambda + DynamoDB SAM template
- JWT authentication helpers
- Razorpay Subscription API reference scaffold
- Razorpay webhook signature verification
- Shop Admin subscription/device endpoints
- Super Admin dashboard endpoints
- `.env.example` for required backend secrets

The backend stores only:

- shop/account metadata
- subscription status and plan
- allowed/registered devices
- Razorpay customer/subscription/payment metadata

It does not store pharmacy inventory or billing records.

## Local Room Persistence

All pharmacy data is stored locally in:

```text
offline_pharmacy_inventory.db
```

Stored local tables include:

- Medicines
- Batches
- Suppliers
- Sales
- Sale items
- Purchases
- Purchase items
- Stock adjustments

Data persists after closing and reopening the app.

## Offline Multi-Device Sync

Local discovery starts automatically when the app opens. Devices advertise a local `_pharmacysync._tcp.` service and discover nearby devices using Android NSD, but they do not exchange data automatically.

To share data, open `Backup` on both phones and tap `Trust & sync` for the other device. For two-way sync, both phones must trust each other. Unknown devices on the same Wi-Fi are blocked and cannot read or write pharmacy data.

Sync works when trusted devices are on the same local Wi-Fi network, phone hotspot, Wi-Fi Direct group, or reachable LAN. Devices can continue working offline for days; when they later meet on a local network, the shop owner chooses the device and syncs local Room data.

## Completed Local Pharmacy Features

- Add medicine to Room.
- Add batch / stock-in to Room.
- Same medicine can have multiple batches.
- Stock list reads persisted Room batches.
- Sales are saved in a Room transaction.
- Sale stock reduction uses FEFO: First Expiry First Out.
- Negative stock is blocked with guarded SQL updates.
- Expired stock is blocked by default and requires explicit confirmation from the bill screen.
- Low stock screen reads real persisted quantities.
- Expiry alert screen reads real persisted expiry dates.
- Sales report reads real persisted sales.
- PDF invoice export writes a local PDF file.
- Stock CSV export writes a local CSV file.
- Sales CSV export writes a local CSV file.
- Database backup export/import uses local files through Android file picker.
- Trusted local peer sync over nearby network connections.
- PIN lock uses `EncryptedSharedPreferences`.
- WorkManager schedules local expiry reminder checks.

## Subscription Features Added

- Shop login screen.
- Register shop screen using `POST /auth/register`.
- Subscription plan screen.
- Razorpay payment launch through backend `shortUrl`, with Checkout fallback using backend-returned `keyId` and subscription id.
- Subscription status screen.
- Device management screen.
- Renewal screen.
- Offline grace warning screen.
- Super Admin login/dashboard scaffold.
- Local subscription cache in `EncryptedSharedPreferences`.
- Retrofit API layer with auth token interceptor.
- 24-hour validation cache and 7-day offline grace logic.
- `TRIAL` subscriptions are accepted by the subscription gate for the 7-day first version.

## Optional Demo Data

The database starts empty. Settings and Backup/Restore include an optional demo seed action that only inserts demo rows if the database is empty.

## Known Limitations

- The Android app is wired to your existing AWS API Gateway URL. The `backend/aws-subscription` folder remains only a reference scaffold and is not required for this deployed backend.
- The backend must continue to keep Razorpay secret, webhook secret, and JWT secret server-side only.
- Database restore replaces the Room database file and asks the user to close and reopen the app so Room reloads the restored file cleanly.
- Local notifications on Android 13+ require the user to grant notification permission.
- Local sync is direct peer-to-peer and requires trusted devices to be reachable on the same nearby network at the same time.
- If two offline devices edit the same batch before syncing, the newest batch update is kept during merge. Keep regular backups for audit safety.
