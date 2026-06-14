# Offline Pharmacy Inventory Android App

An offline-first Android application for small pharmacies to manage inventory, billing, reports, backups, local sync, and subscription-based shop account access.

The app is designed to keep pharmacy business data private and local. All medicine, stock, billing, purchase, sales, expiry, and report data remains stored on the device using a local Room database. The optional online subscription module only sends shop account, device, subscription, and Razorpay payment metadata to the backend.

## Key Privacy Rule

This app does **not** upload pharmacy operating data to the backend.

The backend must not receive or store:

* Medicines
* Batches
* Stock details
* Expiry dates
* Sales records
* Purchase records
* Bills or invoices
* Reports
* Backup files
* Pharmacy business data

Only subscription-related metadata is sent online.

## Tech Stack

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose
* Room Database
* Repository + ViewModel architecture
* AndroidX Security for encrypted PIN and subscription token storage
* WorkManager for local expiry reminders
* Android Storage Access Framework for PDF, CSV, and database backup files
* Local peer-to-peer sync over Wi-Fi, hotspot, Wi-Fi Direct, or LAN
* Retrofit for subscription/account API calls
* Razorpay Android Checkout and hosted payment link support

## Project Structure

```text
OfflinePharmacyInventoryApp/
├── app/                    # Android application source code
├── backend/                # Reference AWS subscription backend scaffold
├── gradle/                 # Gradle wrapper files
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## Open in Android Studio

Open the following folder in Android Studio:

```text
OfflinePharmacyInventoryApp
```

Then:

1. Sync Gradle.
2. Select the app run configuration.
3. Run the app on an emulator or Android device.

## Gradle JDK Setup

If Android Studio or the terminal shows this error:

```text
Unable to locate a Java Runtime
```

Set the Gradle JDK inside Android Studio:

1. Open Android Studio.
2. Go to **Settings** or **Preferences**.
3. Open **Build, Execution, Deployment > Build Tools > Gradle**.
4. Set **Gradle JDK** to the Android Studio embedded JDK.

Common names are:

```text
Embedded JDK
jbr-17
Android Studio default JDK
```

A separate system Java installation is not required if Android Studio provides the embedded JDK.

## Build Check

After the JDK is available, run:

```bash
./gradlew clean
./gradlew assembleDebug
```

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Android Subscription Configuration

The app is currently configured to use the existing AWS backend:

```text
BASE_API_URL=https://qz8gcambkc.execute-api.ap-south-1.amazonaws.com/default/
RAZORPAY_KEY_ID=YOUR_KEY
```

The app uses the Razorpay public `keyId` returned from the backend payment API.

You can override build values using Gradle properties:

```bash
./gradlew assembleDebug \
  -PBASE_API_URL=https://qz8gcambkc.execute-api.ap-south-1.amazonaws.com/default/ \
  -PRAZORPAY_KEY_ID=YOUR_RAZORPAY_KEY_ID
```

## Security Notes

Do not store the following secrets inside the Android app:

```text
RAZORPAY_SECRET
JWT_SECRET
Database credentials
AWS access keys
AWS secret keys
Webhook secrets
```

All secrets must remain server-side only.

## AWS Subscription Backend

The reference backend scaffold is available at:

```text
backend/aws-subscription
```

It includes:

* API Gateway + Lambda + DynamoDB SAM template
* JWT authentication helpers
* Razorpay subscription API reference scaffold
* Razorpay webhook signature verification
* Shop admin subscription and device endpoints
* Super admin dashboard endpoints
* `.env.example` for required backend secrets

The backend stores only:

* Shop/account metadata
* Subscription status and plan
* Allowed or registered devices
* Razorpay customer, subscription, and payment metadata

It does not store pharmacy inventory, billing, sales, purchase, report, or backup records.

## Local Room Persistence

All pharmacy operating data is stored locally in:

```text
offline_pharmacy_inventory.db
```

Local tables include:

* Medicines
* Batches
* Suppliers
* Sales
* Sale items
* Purchases
* Purchase items
* Stock adjustments

Data remains available after closing and reopening the app.

## Offline Multi-Device Sync

The app supports local peer-to-peer sync over nearby networks.

Supported connection types:

* Same Wi-Fi network
* Phone hotspot
* Wi-Fi Direct group
* Reachable LAN

Local discovery starts automatically when the app opens. Devices advertise and discover a local service:

```text
_pharmacysync._tcp.
```

Devices do not exchange data automatically.

To sync data:

1. Open **Backup** on both phones.
2. Select the nearby device.
3. Tap **Trust & sync**.
4. For two-way sync, both phones must trust each other.

Unknown devices on the same network are blocked and cannot read or write pharmacy data.

Devices can continue working offline for days. When they later connect to the same nearby network, the shop owner can choose the trusted device and sync local Room data.

## Completed Local Pharmacy Features

* Add medicine to Room database
* Add batch / stock-in to Room database
* Support multiple batches for the same medicine
* Persisted stock list from Room batches
* Sales saved using Room transaction
* FEFO stock reduction: First Expiry First Out
* Negative stock prevention using guarded SQL updates
* Expired stock blocked by default
* Explicit expired-stock confirmation from bill screen
* Low-stock screen using real persisted quantities
* Expiry alert screen using real persisted expiry dates
* Sales report using real persisted sales data
* Local PDF invoice export
* Local stock CSV export
* Local sales CSV export
* Local database backup export/import
* Trusted local peer sync over nearby network connections
* PIN lock using EncryptedSharedPreferences
* WorkManager local expiry reminder checks

## Subscription Features

* Shop login screen
* Shop registration using `POST /auth/register`
* Subscription plan screen
* Razorpay payment launch through backend `shortUrl`
* Razorpay Checkout fallback using backend-returned `keyId` and subscription ID
* Subscription status screen
* Device management screen
* Renewal screen
* Offline grace warning screen
* Super admin login and dashboard scaffold
* Local subscription cache using EncryptedSharedPreferences
* Retrofit API layer with auth token interceptor
* 24-hour validation cache
* 7-day offline grace logic
* Trial subscription gate support for the first version

## Optional Demo Data

The database starts empty.

Settings and Backup/Restore include an optional demo seed action. Demo data is inserted only if the database is empty.

## Known Limitations

* The Android app is wired to the existing AWS API Gateway URL.
* The `backend/aws-subscription` folder is a reference scaffold and is not required for the already deployed backend.
* Razorpay secret, webhook secret, JWT secret, and backend credentials must remain server-side only.
* Database restore replaces the Room database file.
* After restoring a database backup, the user should close and reopen the app so Room reloads the restored file cleanly.
* Local notifications on Android 13+ require notification permission from the user.
* Local sync requires trusted devices to be reachable on the same nearby network at the same time.
* If two offline devices edit the same batch before syncing, the newest batch update is kept during merge.
* Regular backups are recommended for audit and safety.

## Build Output

After a successful debug build, the APK is available at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## License

Add your license details here.

Example:

```text
Copyright © 2026
All rights reserved.
```
