package com.example.pharmacyinventory.subscription

import com.google.gson.annotations.SerializedName
import java.time.Instant

enum class SubscriptionStatus {
    UNKNOWN,
    TRIAL,
    ACTIVE,
    EXPIRED,
    SUSPENDED,
    PAYMENT_FAILED
}

enum class SubscriptionPlan(val label: String, val price: String, val devices: String) {
    NONE("None", "Trial", "1 device"),
    BASIC("Basic", "Rs. 99/month", "3 devices"),
    STANDARD("Standard", "Rs. 199/month", "10 devices"),
    PREMIUM("Premium", "Rs. 299/month", "Unlimited devices")
}

data class SubscriptionCacheState(
    val shopId: String? = null,
    val shopName: String? = null,
    val ownerName: String? = null,
    val ownerEmail: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus.UNKNOWN,
    val subscriptionPlan: SubscriptionPlan? = null,
    val expiryDate: String? = null,
    val allowedDevices: Int = 0,
    val registeredDeviceCount: Int = 0,
    val currentDeviceId: String,
    val lastValidationTime: Long = 0L,
    val gracePeriodEnd: Long = 0L,
    val offlineAccessAllowed: Boolean = false
) {
    val isLoggedIn: Boolean = !accessToken.isNullOrBlank() && !shopId.isNullOrBlank()

    fun canUsePharmacy(nowMillis: Long = System.currentTimeMillis()): Boolean {
        return subscriptionStatus == SubscriptionStatus.ACTIVE ||
            subscriptionStatus == SubscriptionStatus.TRIAL ||
            (offlineAccessAllowed && gracePeriodEnd >= nowMillis)
    }

    fun isGraceActive(nowMillis: Long = System.currentTimeMillis()): Boolean {
        return subscriptionStatus != SubscriptionStatus.ACTIVE && offlineAccessAllowed && gracePeriodEnd >= nowMillis
    }
}

data class RegisterShopRequest(
    val shopName: String,
    val ownerName: String,
    val email: String,
    val password: String,
    val deviceId: String,
    val deviceName: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String,
    val deviceName: String
)

data class RefreshRequest(val refreshToken: String)

data class AuthResponse(
    val token: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val shopId: String? = null,
    val shopName: String? = null,
    val ownerName: String? = null,
    val email: String? = null,
    val ownerEmail: String? = null,
    val subscriptionStatus: String? = null,
    val subscriptionPlan: String? = null,
    val expiryDate: String? = null,
    val allowedDevices: Int? = null,
    val registeredDeviceCount: Int? = null,
    val deviceAllowed: Boolean? = null,
    val subscription: SubscriptionStatusResponse? = null
)

data class SubscriptionStatusResponse(
    val shopId: String? = null,
    val shopName: String? = null,
    val ownerName: String? = null,
    val email: String? = null,
    val ownerEmail: String? = null,
    val status: String? = null,
    val subscriptionStatus: String? = null,
    val plan: String? = null,
    val subscriptionPlan: String? = null,
    val expiryDate: String? = null,
    val allowedDevices: Int? = null,
    val registeredDeviceCount: Int? = null,
    val deviceAllowed: Boolean? = null,
    val serverTime: String? = null
)

data class CreateSubscriptionRequest(
    val plan: String
)

data class PaymentStartResponse(
    val shopId: String? = null,
    val keyId: String? = null,
    val razorpayKeyId: String? = null,
    val subscriptionId: String? = null,
    val razorpaySubscriptionId: String? = null,
    val razorpayOrderId: String? = null,
    val amount: Int? = null,
    val currency: String = "INR",
    val plan: String? = null,
    val status: String? = null,
    val shortUrl: String? = null
)

data class RegisteredDevice(
    val deviceId: String,
    val deviceName: String,
    val registeredAt: String?,
    val lastSeenAt: String?,
    val lastLoginAt: String? = null,
    val status: String? = null
)

data class DeviceListResponse(
    val devices: List<RegisteredDevice> = emptyList()
)

data class DeviceRegisterRequest(
    @SerializedName("deviceId")
    val deviceInstallId: String,
    val deviceName: String
)

data class AdminLoginRequest(
    val email: String,
    val password: String
)

data class AdminDashboardResponse(
    val totalShops: Int = 0,
    val activeShops: Int = 0,
    val expiredShops: Int = 0,
    val suspendedShops: Int = 0,
    val monthlyRevenue: Double = 0.0,
    val planWiseShops: Map<String, Int> = emptyMap(),
    val deviceUsage: Int = 0,
    val recentRenewals: List<String> = emptyList(),
    val paymentFailures: List<String> = emptyList(),
    val expiryList: List<String> = emptyList()
)

data class ApiErrorMessage(val message: String)

fun AuthResponse.toCache(current: SubscriptionCacheState): SubscriptionCacheState {
    val nested = subscription
    return SubscriptionStatusResponse(
        shopId = nested?.shopId ?: shopId,
        shopName = nested?.shopName ?: shopName,
        ownerName = nested?.ownerName ?: ownerName,
        email = nested?.email ?: email ?: ownerEmail,
        ownerEmail = nested?.ownerEmail ?: ownerEmail ?: email,
        status = nested?.status ?: subscriptionStatus,
        subscriptionStatus = nested?.subscriptionStatus ?: subscriptionStatus,
        plan = nested?.plan ?: subscriptionPlan,
        subscriptionPlan = nested?.subscriptionPlan ?: subscriptionPlan,
        expiryDate = nested?.expiryDate ?: expiryDate,
        allowedDevices = nested?.allowedDevices ?: allowedDevices,
        registeredDeviceCount = nested?.registeredDeviceCount ?: registeredDeviceCount,
        deviceAllowed = nested?.deviceAllowed ?: deviceAllowed,
        serverTime = nested?.serverTime
    ).toCache(
        current = current,
        accessToken = token ?: accessToken ?: current.accessToken,
        refreshToken = refreshToken ?: current.refreshToken,
        ownerEmail = ownerEmail ?: email ?: current.ownerEmail,
        ownerName = ownerName ?: current.ownerName,
        shopName = shopName ?: current.shopName
    )
}

fun SubscriptionStatusResponse.toCache(
    current: SubscriptionCacheState,
    accessToken: String? = current.accessToken,
    refreshToken: String? = current.refreshToken,
    ownerEmail: String? = current.ownerEmail,
    ownerName: String? = current.ownerName,
    shopName: String? = current.shopName
): SubscriptionCacheState {
    val normalizedStatus = subscriptionStatus ?: status
    val normalizedPlan = subscriptionPlan ?: plan
    val statusEnum = normalizedStatus
        ?.let { runCatching { SubscriptionStatus.valueOf(it.uppercase()) }.getOrNull() }
        ?: SubscriptionStatus.UNKNOWN
    val planEnum = normalizedPlan?.let { value -> runCatching { SubscriptionPlan.valueOf(value.uppercase()) }.getOrNull() }
    val now = System.currentTimeMillis()
    return current.copy(
        shopId = shopId ?: current.shopId,
        shopName = shopName ?: this.shopName ?: current.shopName,
        ownerName = ownerName ?: this.ownerName ?: current.ownerName,
        ownerEmail = ownerEmail ?: email ?: this.ownerEmail ?: current.ownerEmail,
        accessToken = accessToken,
        refreshToken = refreshToken,
        subscriptionStatus = statusEnum,
        subscriptionPlan = planEnum,
        expiryDate = expiryDate,
        allowedDevices = allowedDevices ?: current.allowedDevices,
        registeredDeviceCount = registeredDeviceCount ?: current.registeredDeviceCount,
        lastValidationTime = now,
        gracePeriodEnd = now + GRACE_PERIOD_MILLIS,
        offlineAccessAllowed = (statusEnum == SubscriptionStatus.ACTIVE || statusEnum == SubscriptionStatus.TRIAL) && (deviceAllowed ?: true)
    )
}

fun PaymentStartResponse.checkoutKeyId(): String = (keyId ?: razorpayKeyId).orEmpty()

fun PaymentStartResponse.checkoutSubscriptionId(): String? = subscriptionId ?: razorpaySubscriptionId

fun serverTimeToMillis(value: String?): Long {
    return value?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() } ?: System.currentTimeMillis()
}

const val GRACE_PERIOD_MILLIS: Long = 7L * 24L * 60L * 60L * 1000L
const val VALIDATION_CACHE_MILLIS: Long = 24L * 60L * 60L * 1000L
