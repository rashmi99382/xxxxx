package com.example.pharmacyinventory.subscription

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SubscriptionCache(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_subscription_cache",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val _state = MutableStateFlow(readState())
    val state: StateFlow<SubscriptionCacheState> = _state.asStateFlow()

    fun current(): SubscriptionCacheState = _state.value

    fun save(next: SubscriptionCacheState) {
        prefs.edit()
            .putString(KEY_SHOP_ID, next.shopId)
            .putString(KEY_SHOP_NAME, next.shopName)
            .putString(KEY_OWNER_NAME, next.ownerName)
            .putString(KEY_OWNER_EMAIL, next.ownerEmail)
            .putString(KEY_ACCESS_TOKEN, next.accessToken)
            .putString(KEY_REFRESH_TOKEN, next.refreshToken)
            .putString(KEY_STATUS, next.subscriptionStatus.name)
            .putString(KEY_PLAN, next.subscriptionPlan?.name)
            .putString(KEY_EXPIRY_DATE, next.expiryDate)
            .putInt(KEY_ALLOWED_DEVICES, next.allowedDevices)
            .putInt(KEY_REGISTERED_DEVICE_COUNT, next.registeredDeviceCount)
            .putLong(KEY_LAST_VALIDATION_TIME, next.lastValidationTime)
            .putLong(KEY_GRACE_PERIOD_END, next.gracePeriodEnd)
            .putBoolean(KEY_OFFLINE_ACCESS_ALLOWED, next.offlineAccessAllowed)
            .apply()
        _state.value = next
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        save(current().copy(accessToken = accessToken, refreshToken = refreshToken))
    }

    fun logout() {
        val keepDeviceId = current().currentDeviceId
        prefs.edit()
            .remove(KEY_SHOP_ID)
            .remove(KEY_SHOP_NAME)
            .remove(KEY_OWNER_NAME)
            .remove(KEY_OWNER_EMAIL)
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_STATUS)
            .remove(KEY_PLAN)
            .remove(KEY_EXPIRY_DATE)
            .remove(KEY_ALLOWED_DEVICES)
            .remove(KEY_REGISTERED_DEVICE_COUNT)
            .remove(KEY_LAST_VALIDATION_TIME)
            .remove(KEY_GRACE_PERIOD_END)
            .remove(KEY_OFFLINE_ACCESS_ALLOWED)
            .apply()
        _state.value = SubscriptionCacheState(currentDeviceId = keepDeviceId)
    }

    private fun readState(): SubscriptionCacheState {
        val deviceId = prefs.getString(KEY_DEVICE_INSTALL_ID, null)
            ?: UUID.randomUUID().toString().also { prefs.edit().putString(KEY_DEVICE_INSTALL_ID, it).apply() }
        val status = prefs.getString(KEY_STATUS, null)
            ?.let { runCatching { SubscriptionStatus.valueOf(it) }.getOrNull() }
            ?: SubscriptionStatus.UNKNOWN
        val plan = prefs.getString(KEY_PLAN, null)
            ?.let { runCatching { SubscriptionPlan.valueOf(it) }.getOrNull() }
        return SubscriptionCacheState(
            shopId = prefs.getString(KEY_SHOP_ID, null),
            shopName = prefs.getString(KEY_SHOP_NAME, null),
            ownerName = prefs.getString(KEY_OWNER_NAME, null),
            ownerEmail = prefs.getString(KEY_OWNER_EMAIL, null),
            accessToken = prefs.getString(KEY_ACCESS_TOKEN, null),
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null),
            subscriptionStatus = status,
            subscriptionPlan = plan,
            expiryDate = prefs.getString(KEY_EXPIRY_DATE, null),
            allowedDevices = prefs.getInt(KEY_ALLOWED_DEVICES, 0),
            registeredDeviceCount = prefs.getInt(KEY_REGISTERED_DEVICE_COUNT, 0),
            currentDeviceId = deviceId,
            lastValidationTime = prefs.getLong(KEY_LAST_VALIDATION_TIME, 0L),
            gracePeriodEnd = prefs.getLong(KEY_GRACE_PERIOD_END, 0L),
            offlineAccessAllowed = prefs.getBoolean(KEY_OFFLINE_ACCESS_ALLOWED, false)
        )
    }

    fun deviceName(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}".trim().ifBlank { "Android device" }
    }

    private companion object {
        const val KEY_DEVICE_INSTALL_ID = "device_install_id"
        const val KEY_SHOP_ID = "shop_id"
        const val KEY_SHOP_NAME = "shop_name"
        const val KEY_OWNER_NAME = "owner_name"
        const val KEY_OWNER_EMAIL = "owner_email"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_STATUS = "subscription_status"
        const val KEY_PLAN = "subscription_plan"
        const val KEY_EXPIRY_DATE = "expiry_date"
        const val KEY_ALLOWED_DEVICES = "allowed_devices"
        const val KEY_REGISTERED_DEVICE_COUNT = "registered_device_count"
        const val KEY_LAST_VALIDATION_TIME = "last_validation_time"
        const val KEY_GRACE_PERIOD_END = "grace_period_end"
        const val KEY_OFFLINE_ACCESS_ALLOWED = "offline_access_allowed"
    }
}
