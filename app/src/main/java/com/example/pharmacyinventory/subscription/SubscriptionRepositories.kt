package com.example.pharmacyinventory.subscription

import kotlinx.coroutines.flow.StateFlow

class AccountRepository(
    private val api: SubscriptionApiService,
    private val cache: SubscriptionCache
) {
    val state: StateFlow<SubscriptionCacheState> = cache.state

    suspend fun registerShop(shopName: String, ownerName: String, email: String, password: String): SubscriptionCacheState {
        val response = api.registerShop(
            RegisterShopRequest(
                shopName = shopName,
                ownerName = ownerName,
                email = email,
                password = password,
                deviceId = cache.current().currentDeviceId,
                deviceName = cache.deviceName()
            )
        )
            .bodyOrError("Unable to register shop.")
        val next = response.toCache(cache.current())
        cache.save(next)
        return next
    }

    suspend fun login(email: String, password: String): SubscriptionCacheState {
        val response = api.login(
            LoginRequest(
                email = email,
                password = password,
                deviceId = cache.current().currentDeviceId,
                deviceName = cache.deviceName()
            )
        ).bodyOrError("Unable to login.")
        val next = response.toCache(cache.current())
        if (response.deviceAllowed == false || response.subscription?.deviceAllowed == false) {
            cache.save(next.copy(offlineAccessAllowed = false))
            error("Device limit exceeded for this subscription plan.")
        }
        cache.save(next)
        return next
    }

    suspend fun logout() {
        val token = cache.current().accessToken
        if (!token.isNullOrBlank()) runCatching { api.logout("Bearer $token") }
        cache.logout()
    }
}

class SubscriptionRepository(
    private val api: SubscriptionApiService,
    private val cache: SubscriptionCache
) {
    val state: StateFlow<SubscriptionCacheState> = cache.state

    suspend fun validate(forceRemote: Boolean = false): SubscriptionCacheState {
        val current = cache.current()
        if (!current.isLoggedIn) return current
        val now = System.currentTimeMillis()
        if (!forceRemote && now - current.lastValidationTime < VALIDATION_CACHE_MILLIS) return current

        return runCatching {
            val token = current.accessToken ?: error("Login required.")
            val response = api.subscriptionStatus("Bearer $token").bodyOrError("Unable to validate subscription.")
            val next = response.toCache(current)
            if (response.deviceAllowed == false) next.copy(offlineAccessAllowed = false) else next
        }.onSuccess { cache.save(it) }.getOrElse {
            if (it is UnauthorizedException) {
                cache.logout()
                return cache.current()
            }
            val offlineState = current.copy(
                offlineAccessAllowed = current.gracePeriodEnd >= now,
                subscriptionStatus = if (current.gracePeriodEnd >= now) current.subscriptionStatus else SubscriptionStatus.EXPIRED
            )
            cache.save(offlineState)
            offlineState
        }
    }

    suspend fun createPayment(plan: SubscriptionPlan, renewal: Boolean = false): PaymentStartResponse {
        val token = cache.current().accessToken ?: error("Login required.")
        val request = CreateSubscriptionRequest(plan.name)
        return if (renewal) {
            api.renewSubscription("Bearer $token", request).bodyOrError("Unable to create renewal.")
        } else {
            api.createSubscription("Bearer $token", request).bodyOrError("Unable to create subscription.")
        }
    }

    suspend fun cancel() {
        val token = cache.current().accessToken ?: error("Login required.")
        api.cancelSubscription("Bearer $token").bodyOrError("Unable to cancel subscription.")
    }
}

class DeviceRepository(
    private val api: SubscriptionApiService,
    private val cache: SubscriptionCache
) {
    suspend fun registerCurrentDevice(): SubscriptionCacheState {
        val token = cache.current().accessToken ?: error("Login required.")
        val response = api.registerDevice(
            "Bearer $token",
            DeviceRegisterRequest(cache.current().currentDeviceId, cache.deviceName())
        ).bodyOrError("Unable to register device.")
        val next = response.toCache(cache.current())
        cache.save(next)
        return next
    }

    suspend fun devices(): List<RegisteredDevice> {
        val token = cache.current().accessToken ?: error("Login required.")
        return api.devices("Bearer $token").bodyOrError("Unable to load devices.").devices
    }

    suspend fun removeDevice(deviceId: String) {
        val token = cache.current().accessToken ?: error("Login required.")
        api.deleteDevice("Bearer $token", deviceId).bodyOrError("Unable to remove device.")
    }
}

class AdminRepository(private val api: SubscriptionApiService, private val cache: SubscriptionCache) {
    suspend fun login(email: String, password: String): AdminDashboardResponse {
        val response = api.adminLogin(AdminLoginRequest(email, password)).bodyOrError("Unable to login admin.")
        val token = response.token ?: response.accessToken ?: error("Admin token missing.")
        cache.saveTokens(token, response.refreshToken.orEmpty())
        return api.adminDashboard("Bearer $token").bodyOrError("Unable to load admin dashboard.")
    }

    suspend fun dashboard(): AdminDashboardResponse {
        val token = cache.current().accessToken ?: error("Admin login required.")
        return api.adminDashboard("Bearer $token").bodyOrError("Unable to load admin dashboard.")
    }
}
