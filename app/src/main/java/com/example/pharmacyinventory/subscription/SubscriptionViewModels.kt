package com.example.pharmacyinventory.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SubscriptionGateDestination {
    Loading,
    AccountLogin,
    PharmacyDashboard,
    Renewal,
    OfflineGrace
}

data class SubscriptionGateUiState(
    val destination: SubscriptionGateDestination = SubscriptionGateDestination.Loading,
    val cache: SubscriptionCacheState? = null,
    val message: String? = null
)

data class AccountLoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val message: String? = null,
    val loggedIn: Boolean = false
)

data class RegisterShopUiState(
    val shopName: String = "",
    val ownerName: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val message: String? = null,
    val registered: Boolean = false
)

data class SubscriptionUiState(
    val cache: SubscriptionCacheState = SubscriptionCacheState(currentDeviceId = ""),
    val selectedPlan: SubscriptionPlan = SubscriptionPlan.BASIC,
    val paymentStart: PaymentStartResponse? = null,
    val devices: List<RegisteredDevice> = emptyList(),
    val loading: Boolean = false,
    val message: String? = null
)

data class AdminUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val message: String? = null,
    val dashboard: AdminDashboardResponse? = null
)

class SubscriptionGateViewModel(private val subscriptionRepository: SubscriptionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionGateUiState())
    val uiState: StateFlow<SubscriptionGateUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = SubscriptionGateUiState()
            val cache = subscriptionRepository.validate(forceRemote = true)
            val destination = when {
                !cache.isLoggedIn -> SubscriptionGateDestination.AccountLogin
                cache.subscriptionStatus == SubscriptionStatus.ACTIVE || cache.subscriptionStatus == SubscriptionStatus.TRIAL -> SubscriptionGateDestination.PharmacyDashboard
                cache.isGraceActive() -> SubscriptionGateDestination.OfflineGrace
                else -> SubscriptionGateDestination.Renewal
            }
            _uiState.value = SubscriptionGateUiState(
                destination = destination,
                cache = cache,
                message = if (destination == SubscriptionGateDestination.OfflineGrace) {
                    "Subscription could not be verified. Offline grace period active."
                } else null
            )
        }
    }
}

class AccountLoginViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountLoginUiState())
    val uiState: StateFlow<AccountLoginUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) = update { copy(email = value, message = null) }
    fun updatePassword(value: String) = update { copy(password = value, message = null) }

    fun login() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(loading = true, message = null)
            runCatching { accountRepository.login(state.email.trim(), state.password) }
                .onSuccess { _uiState.value = state.copy(loading = false, loggedIn = true) }
                .onFailure { _uiState.value = state.copy(loading = false, message = it.message ?: "Login failed.") }
        }
    }

    private fun update(block: AccountLoginUiState.() -> AccountLoginUiState) {
        _uiState.value = _uiState.value.block()
    }
}

class RegisterShopViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterShopUiState())
    val uiState: StateFlow<RegisterShopUiState> = _uiState.asStateFlow()

    fun updateShopName(value: String) = update { copy(shopName = value, message = null) }
    fun updateOwnerName(value: String) = update { copy(ownerName = value, message = null) }
    fun updateEmail(value: String) = update { copy(email = value, message = null) }
    fun updatePassword(value: String) = update { copy(password = value, message = null) }

    fun register() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(loading = true, message = null)
            runCatching {
                accountRepository.registerShop(
                    shopName = state.shopName.trim(),
                    ownerName = state.ownerName.trim(),
                    email = state.email.trim(),
                    password = state.password
                )
            }.onSuccess {
                _uiState.value = state.copy(loading = false, registered = true)
            }.onFailure {
                _uiState.value = state.copy(loading = false, message = it.message ?: "Registration failed.")
            }
        }
    }

    private fun update(block: RegisterShopUiState.() -> RegisterShopUiState) {
        _uiState.value = _uiState.value.block()
    }
}

class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionUiState(cache = subscriptionRepository.state.value))
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            subscriptionRepository.state.collect { cache ->
                _uiState.value = _uiState.value.copy(cache = cache)
            }
        }
    }

    fun selectPlan(plan: SubscriptionPlan) {
        _uiState.value = _uiState.value.copy(selectedPlan = plan, paymentStart = null, message = null)
    }

    fun validateNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null)
            runCatching { subscriptionRepository.validate(forceRemote = true) }
                .onSuccess { _uiState.value = _uiState.value.copy(loading = false, cache = it, message = "Subscription validated.") }
                .onFailure { _uiState.value = _uiState.value.copy(loading = false, message = it.message ?: "Validation failed.") }
        }
    }

    fun startPayment(renewal: Boolean = false) {
        val plan = _uiState.value.selectedPlan
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null, paymentStart = null)
            runCatching { subscriptionRepository.createPayment(plan, renewal) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        paymentStart = it,
                        message = "Payment started. After payment success, click Refresh Status."
                    )
                }
                .onFailure { _uiState.value = _uiState.value.copy(loading = false, message = it.message ?: "Payment start failed.") }
        }
    }

    fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null)
            runCatching { deviceRepository.devices() }
                .onSuccess { _uiState.value = _uiState.value.copy(loading = false, devices = it) }
                .onFailure { _uiState.value = _uiState.value.copy(loading = false, message = it.message ?: "Unable to load devices.") }
        }
    }

    fun removeDevice(deviceId: String) {
        viewModelScope.launch {
            runCatching { deviceRepository.removeDevice(deviceId) }
                .onSuccess { loadDevices() }
                .onFailure { _uiState.value = _uiState.value.copy(message = it.message ?: "Unable to remove device.") }
        }
    }
}

class AdminViewModel(private val adminRepository: AdminRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) = update { copy(email = value, message = null) }
    fun updatePassword(value: String) = update { copy(password = value, message = null) }

    fun login() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(loading = true, message = null)
            runCatching { adminRepository.login(state.email.trim(), state.password) }
                .onSuccess { _uiState.value = state.copy(loading = false, dashboard = it) }
                .onFailure { _uiState.value = state.copy(loading = false, message = it.message ?: "Admin login failed.") }
        }
    }

    private fun update(block: AdminUiState.() -> AdminUiState) {
        _uiState.value = _uiState.value.block()
    }
}
