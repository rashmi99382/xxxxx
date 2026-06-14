package com.example.pharmacyinventory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pharmacyinventory.data.AppDatabase
import com.example.pharmacyinventory.data.LocalSyncEngine
import com.example.pharmacyinventory.data.PharmacyRepository
import com.example.pharmacyinventory.subscription.AccountLoginViewModel
import com.example.pharmacyinventory.subscription.AccountRepository
import com.example.pharmacyinventory.subscription.AdminRepository
import com.example.pharmacyinventory.subscription.AdminViewModel
import com.example.pharmacyinventory.subscription.DeviceRepository
import com.example.pharmacyinventory.subscription.RegisterShopViewModel
import com.example.pharmacyinventory.subscription.SubscriptionApiClient
import com.example.pharmacyinventory.subscription.SubscriptionCache
import com.example.pharmacyinventory.subscription.SubscriptionGateViewModel
import com.example.pharmacyinventory.subscription.SubscriptionRepository
import com.example.pharmacyinventory.subscription.SubscriptionViewModel
import com.example.pharmacyinventory.viewmodel.AddBatchViewModel
import com.example.pharmacyinventory.viewmodel.AddMedicineViewModel
import com.example.pharmacyinventory.viewmodel.BackupRestoreViewModel
import com.example.pharmacyinventory.viewmodel.CartBillViewModel
import com.example.pharmacyinventory.viewmodel.DashboardViewModel
import com.example.pharmacyinventory.viewmodel.ExpiryAlertViewModel
import com.example.pharmacyinventory.viewmodel.InvoicePreviewViewModel
import com.example.pharmacyinventory.viewmodel.LowStockViewModel
import com.example.pharmacyinventory.viewmodel.MedicineDetailViewModel
import com.example.pharmacyinventory.viewmodel.PinLoginViewModel
import com.example.pharmacyinventory.viewmodel.PurchaseReportViewModel
import com.example.pharmacyinventory.viewmodel.SalesReportViewModel
import com.example.pharmacyinventory.viewmodel.SellMedicineViewModel
import com.example.pharmacyinventory.viewmodel.SettingsViewModel
import com.example.pharmacyinventory.viewmodel.SplashViewModel
import com.example.pharmacyinventory.viewmodel.StockListViewModel
import com.example.pharmacyinventory.viewmodel.SupplierViewModel

class PharmacyApplication : Application() {
    val container: AppContainer by lazy {
        val database = AppDatabase.getInstance(this)
        val repository = PharmacyRepository(database)
        val localPeerSyncManager = LocalPeerSyncManager(this, LocalSyncEngine(database))
        val subscriptionCache = SubscriptionCache(this)
        val subscriptionApi = SubscriptionApiClient.create(subscriptionCache)
        val accountRepository = AccountRepository(subscriptionApi, subscriptionCache)
        val subscriptionRepository = SubscriptionRepository(subscriptionApi, subscriptionCache)
        val deviceRepository = DeviceRepository(subscriptionApi, subscriptionCache)
        val adminRepository = AdminRepository(subscriptionApi, subscriptionCache)
        AppContainer(
            repository = repository,
            pinManager = PinManager(this),
            localFileManager = LocalFileManager(this),
            localPeerSyncManager = localPeerSyncManager,
            accountRepository = accountRepository,
            subscriptionRepository = subscriptionRepository,
            deviceRepository = deviceRepository,
            adminRepository = adminRepository
        )
    }

    override fun onCreate() {
        super.onCreate()
        ExpiryReminderScheduler.schedule(this)
        container.localPeerSyncManager.start()
    }
}

class AppContainer(
    val repository: PharmacyRepository,
    val pinManager: PinManager,
    val localFileManager: LocalFileManager,
    val localPeerSyncManager: LocalPeerSyncManager,
    val accountRepository: AccountRepository,
    val subscriptionRepository: SubscriptionRepository,
    val deviceRepository: DeviceRepository,
    val adminRepository: AdminRepository
) {
    val saleDraftStore = SaleDraftStore()
    val viewModelFactory: PharmacyViewModelFactory by lazy {
        PharmacyViewModelFactory(
            repository,
            saleDraftStore,
            pinManager,
            localFileManager,
            localPeerSyncManager,
            accountRepository,
            subscriptionRepository,
            deviceRepository,
            adminRepository
        )
    }
}

class PharmacyViewModelFactory(
    private val repository: PharmacyRepository,
    private val saleDraftStore: SaleDraftStore,
    private val pinManager: PinManager,
    private val localFileManager: LocalFileManager,
    private val localPeerSyncManager: LocalPeerSyncManager,
    private val accountRepository: AccountRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val deviceRepository: DeviceRepository,
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SplashViewModel::class.java -> SplashViewModel() as T
            PinLoginViewModel::class.java -> PinLoginViewModel(pinManager) as T
            DashboardViewModel::class.java -> DashboardViewModel(repository) as T
            AddMedicineViewModel::class.java -> AddMedicineViewModel(repository) as T
            AddBatchViewModel::class.java -> AddBatchViewModel(repository) as T
            StockListViewModel::class.java -> StockListViewModel(repository) as T
            MedicineDetailViewModel::class.java -> MedicineDetailViewModel(repository) as T
            SellMedicineViewModel::class.java -> SellMedicineViewModel(repository, saleDraftStore) as T
            CartBillViewModel::class.java -> CartBillViewModel(repository, saleDraftStore) as T
            InvoicePreviewViewModel::class.java -> InvoicePreviewViewModel(repository, saleDraftStore, localFileManager) as T
            ExpiryAlertViewModel::class.java -> ExpiryAlertViewModel(repository) as T
            LowStockViewModel::class.java -> LowStockViewModel(repository) as T
            SalesReportViewModel::class.java -> SalesReportViewModel(repository, localFileManager) as T
            PurchaseReportViewModel::class.java -> PurchaseReportViewModel(repository) as T
            SupplierViewModel::class.java -> SupplierViewModel(repository) as T
            BackupRestoreViewModel::class.java -> BackupRestoreViewModel(repository, localFileManager, localPeerSyncManager) as T
            SettingsViewModel::class.java -> SettingsViewModel(repository) as T
            SubscriptionGateViewModel::class.java -> SubscriptionGateViewModel(subscriptionRepository) as T
            AccountLoginViewModel::class.java -> AccountLoginViewModel(accountRepository) as T
            RegisterShopViewModel::class.java -> RegisterShopViewModel(accountRepository) as T
            SubscriptionViewModel::class.java -> SubscriptionViewModel(subscriptionRepository, deviceRepository) as T
            AdminViewModel::class.java -> AdminViewModel(adminRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
