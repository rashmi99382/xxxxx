@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.pharmacyinventory.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmacyinventory.LocalFileManager
import com.example.pharmacyinventory.LocalPeerDevice
import com.example.pharmacyinventory.LocalPeerSyncManager
import com.example.pharmacyinventory.PinManager
import com.example.pharmacyinventory.SaleDraftStore
import com.example.pharmacyinventory.data.CartLine
import com.example.pharmacyinventory.data.DashboardMetrics
import com.example.pharmacyinventory.data.DateRange
import com.example.pharmacyinventory.data.ExpiredStockRequiresConfirmationException
import com.example.pharmacyinventory.data.ExpiryTab
import com.example.pharmacyinventory.data.MedicineEntity
import com.example.pharmacyinventory.data.MedicineWithBatches
import com.example.pharmacyinventory.data.PharmacyRepository
import com.example.pharmacyinventory.data.PurchaseReportRow
import com.example.pharmacyinventory.data.SaleReportRow
import com.example.pharmacyinventory.data.SaleRequestLine
import com.example.pharmacyinventory.data.SaleWithItems
import com.example.pharmacyinventory.data.StockInRequest
import com.example.pharmacyinventory.data.StockRow
import com.example.pharmacyinventory.data.SupplierEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val dateInputFormats = listOf(
    DateTimeFormatter.ISO_LOCAL_DATE,
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("dd-MM-yyyy")
)

enum class StockFilter {
    All,
    Safe,
    LowStock,
    ExpiringSoon,
    Expired
}

data class DashboardUiState(
    val metrics: DashboardMetrics = DashboardMetrics(),
    val recentSales: List<SaleWithItems> = emptyList(),
    val expiryWarnings: List<StockRow> = emptyList()
)

enum class PinLoginMode {
    CreatePin,
    Unlock
}

data class PinLoginUiState(
    val pin: String = "",
    val mode: PinLoginMode = PinLoginMode.Unlock,
    val message: String? = null,
    val authenticated: Boolean = false
)

data class AddMedicineUiState(
    val name: String = "",
    val company: String = "",
    val genericName: String = "",
    val category: String = "Tablet",
    val supplierName: String = "",
    val minStock: String = "10",
    val saving: Boolean = false,
    val message: String? = null,
    val savedMedicineId: Long? = null
)

data class AddBatchUiState(
    val medicineId: Long? = null,
    val supplierName: String = "",
    val batchNo: String = "",
    val expiryDate: String = LocalDate.now().plusMonths(12).toString(),
    val quantity: String = "",
    val purchasePrice: String = "",
    val mrp: String = "",
    val voucherNo: String = "",
    val message: String? = null,
    val saving: Boolean = false
)

data class StockListUiState(
    val rows: List<StockRow> = emptyList(),
    val categories: List<String> = listOf("All"),
    val query: String = "",
    val selectedCategory: String = "All",
    val selectedStatus: StockFilter = StockFilter.All
)

data class SellMedicineUiState(
    val query: String = "",
    val quantity: Int = 1,
    val availableRows: List<StockRow> = emptyList(),
    val selectedBatchId: Long? = null,
    val selectedRow: StockRow? = null,
    val cartLines: List<CartLine> = emptyList(),
    val warning: String? = null
)

private data class SellFormState(
    val query: String = "",
    val quantity: Int = 1,
    val selectedBatchId: Long? = null,
    val warning: String? = null
)

data class CartBillUiState(
    val customerName: String = "Walk-in customer",
    val paymentMode: String = "Cash",
    val allowExpiredSale: Boolean = false,
    val cartLines: List<CartLine> = emptyList(),
    val saving: Boolean = false,
    val message: String? = null,
    val generatedSaleId: Long? = null
)

data class ExpiryAlertUiState(
    val selectedTab: ExpiryTab = ExpiryTab.All,
    val rows: List<StockRow> = emptyList()
)

data class SalesReportUiState(
    val range: DateRange = DateRange.currentMonth(),
    val rows: List<SaleReportRow> = emptyList(),
    val revenue: Double = 0.0,
    val profit: Double = 0.0,
    val billCount: Int = 0,
    val message: String? = null
)

data class PurchaseReportUiState(
    val range: DateRange = DateRange.currentMonth(),
    val rows: List<PurchaseReportRow> = emptyList(),
    val totalPurchase: Double = 0.0
)

data class SupplierUiState(
    val suppliers: List<SupplierEntity> = emptyList(),
    val name: String = "",
    val contact: String = "",
    val phone: String = "",
    val address: String = "",
    val message: String? = null
)

class SplashViewModel : ViewModel() {
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    init {
        viewModelScope.launch {
            delay(700)
            _ready.value = true
        }
    }
}

class PinLoginViewModel(private val pinManager: PinManager) : ViewModel() {
    private val _uiState = MutableStateFlow(
        PinLoginUiState(
            mode = if (pinManager.hasPin()) PinLoginMode.Unlock else PinLoginMode.CreatePin,
            message = if (pinManager.hasPin()) null else "Create a new 4 digit PIN."
        )
    )
    val uiState: StateFlow<PinLoginUiState> = _uiState.asStateFlow()

    fun appendDigit(digit: String) {
        val current = _uiState.value
        if (current.pin.length >= 4) return
        val nextPin = current.pin + digit
        _uiState.value = current.copy(pin = nextPin, message = null)
        if (nextPin.length == 4) {
            when (current.mode) {
                PinLoginMode.CreatePin -> {
                    pinManager.savePin(nextPin)
                    _uiState.value = current.copy(
                        pin = nextPin,
                        message = "PIN created.",
                        authenticated = true
                    )
                }
                PinLoginMode.Unlock -> {
                    if (pinManager.verifyPin(nextPin)) {
                        _uiState.value = current.copy(pin = nextPin, authenticated = true)
                    } else {
                        _uiState.value = current.copy(pin = "", message = "Wrong PIN. Try again.")
                    }
                }
            }
        }
    }

    fun clear() {
        _uiState.value = _uiState.value.copy(pin = "", message = null)
    }

    fun backspace() {
        _uiState.value = _uiState.value.copy(pin = _uiState.value.pin.dropLast(1), message = null)
    }
}

class DashboardViewModel(private val repository: PharmacyRepository) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> = currentDateFlow()
        .flatMapLatest { today ->
            combine(
                repository.observeDashboardMetrics(today),
                repository.observeRecentSales(),
                repository.observeExpiryRows(ExpiryTab.ThirtyDays, today)
            ) { metrics, recentSales, expiryRows ->
                DashboardUiState(
                    metrics = metrics,
                    recentSales = recentSales,
                    expiryWarnings = expiryRows.take(4)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())
}

class AddMedicineViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddMedicineUiState())
    val uiState: StateFlow<AddMedicineUiState> = _uiState.asStateFlow()
    val suppliers: StateFlow<List<SupplierEntity>> = repository.observeSuppliers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateName(value: String) = update { copy(name = value, message = null) }
    fun updateCompany(value: String) = update { copy(company = value, message = null) }
    fun updateGenericName(value: String) = update { copy(genericName = value, message = null) }
    fun updateCategory(value: String) = update { copy(category = value, message = null) }
    fun updateSupplier(value: String) = update { copy(supplierName = value, message = null) }
    fun updateMinStock(value: String) = update { copy(minStock = value.filter(Char::isDigit), message = null) }

    fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(saving = true, message = null)
            runCatching {
                repository.addMedicine(
                    name = state.name,
                    company = state.company,
                    genericName = state.genericName,
                    category = state.category,
                    supplierName = state.supplierName,
                    minStock = state.minStock.toIntOrNull() ?: 0
                )
            }.onSuccess { id ->
                _uiState.value = AddMedicineUiState(message = "Medicine saved.", savedMedicineId = id)
            }.onFailure { error ->
                _uiState.value = state.copy(saving = false, message = error.message ?: "Unable to save medicine.")
            }
        }
    }

    private fun update(block: AddMedicineUiState.() -> AddMedicineUiState) {
        _uiState.value = _uiState.value.block()
    }
}

class AddBatchViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddBatchUiState())
    val uiState: StateFlow<AddBatchUiState> = _uiState.asStateFlow()
    val medicines: StateFlow<List<MedicineEntity>> = repository.observeMedicines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val suppliers: StateFlow<List<SupplierEntity>> = repository.observeSuppliers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectMedicine(id: Long) = update { copy(medicineId = id, message = null) }
    fun updateSupplier(value: String) = update { copy(supplierName = value, message = null) }
    fun updateBatchNo(value: String) = update { copy(batchNo = value.uppercase(), message = null) }
    fun updateExpiry(value: String) = update { copy(expiryDate = value, message = null) }
    fun updateQuantity(value: String) = update { copy(quantity = value.filter(Char::isDigit), message = null) }
    fun updatePurchasePrice(value: String) = update { copy(purchasePrice = value.priceInput(), message = null) }
    fun updateMrp(value: String) = update { copy(mrp = value.priceInput(), message = null) }
    fun updateVoucherNo(value: String) = update { copy(voucherNo = value.uppercase(), message = null) }

    fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(saving = true, message = null)
            runCatching {
                val medicineId = state.medicineId ?: medicines.value.firstOrNull()?.id ?: error("Select a medicine.")
                val expiry = parseDate(state.expiryDate)
                repository.stockIn(
                    StockInRequest(
                        medicineId = medicineId,
                        supplierId = null,
                        supplierName = state.supplierName,
                        batchNo = state.batchNo,
                        expiryEpochDay = expiry.toEpochDay(),
                        quantity = state.quantity.toIntOrNull() ?: 0,
                        purchasePrice = state.purchasePrice.toDoubleOrNull() ?: 0.0,
                        mrp = state.mrp.toDoubleOrNull() ?: 0.0,
                        voucherNo = state.voucherNo
                    )
                )
            }.onSuccess {
                _uiState.value = AddBatchUiState(message = "Stock-in saved.")
            }.onFailure { error ->
                _uiState.value = state.copy(saving = false, message = error.message ?: "Unable to save stock.")
            }
        }
    }

    private fun update(block: AddBatchUiState.() -> AddBatchUiState) {
        _uiState.value = _uiState.value.block()
    }
}

class StockListViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow("All")
    private val status = MutableStateFlow(StockFilter.All)

    val uiState: StateFlow<StockListUiState> = combine(
        repository.observeStockRows(),
        query,
        category,
        status,
        currentDateFlow()
    ) { rows, queryText, selectedCategory, selectedStatus, today ->
        val categories = listOf("All") + rows.map { it.category }.distinct().sorted()
        val filtered = rows.filter { row ->
            val matchesQuery = queryText.isBlank() ||
                row.name.contains(queryText, ignoreCase = true) ||
                row.company.contains(queryText, ignoreCase = true) ||
                row.batchNo.contains(queryText, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || row.category == selectedCategory
            val matchesStatus = selectedStatus == StockFilter.All || stockFilterFor(row, today) == selectedStatus
            matchesQuery && matchesCategory && matchesStatus
        }
        StockListUiState(
            rows = filtered,
            categories = categories,
            query = queryText,
            selectedCategory = selectedCategory,
            selectedStatus = selectedStatus
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StockListUiState())

    fun updateQuery(value: String) {
        query.value = value
    }

    fun selectCategory(value: String) {
        category.value = value
    }

    fun selectStatus(value: StockFilter) {
        status.value = value
    }
}

class MedicineDetailViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val medicineId = MutableStateFlow<Long?>(null)
    val detail: StateFlow<MedicineWithBatches?> = medicineId
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.observeMedicineDetail(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun load(id: Long) {
        medicineId.value = id
    }
}

class SellMedicineViewModel(
    private val repository: PharmacyRepository,
    private val saleDraftStore: SaleDraftStore
) : ViewModel() {
    private val form = MutableStateFlow(SellFormState())
    private val today = LocalDate.now()

    private val availableRows: Flow<List<StockRow>> = form
        .map { it.query }
        .flatMapLatest { repository.observeAvailableStockRows(it) }

    val uiState: StateFlow<SellMedicineUiState> = combine(
        form,
        availableRows,
        saleDraftStore.cartLines
    ) { formState, rows, cart ->
        val sorted = rows.sortedWith(compareBy<StockRow> { it.expiryEpochDay }.thenBy { it.name.lowercase() })
        val adjustedRows = sorted.withCartReservations(cart)
        val preferred = formState.selectedBatchId?.let { id -> adjustedRows.firstOrNull { it.batchId == id && it.quantity > 0 } }
            ?: adjustedRows.firstOrNull { it.expiryEpochDay >= today.toEpochDay() && it.quantity > 0 }
            ?: adjustedRows.firstOrNull { it.quantity > 0 }
        SellMedicineUiState(
            query = formState.query,
            quantity = formState.quantity,
            availableRows = adjustedRows,
            selectedBatchId = preferred?.batchId,
            selectedRow = preferred,
            cartLines = cart,
            warning = formState.warning
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SellMedicineUiState())

    fun updateQuery(value: String) {
        form.value = form.value.copy(query = value, selectedBatchId = null, warning = null)
    }

    fun selectBatch(batchId: Long) {
        form.value = form.value.copy(selectedBatchId = batchId, warning = null)
    }

    fun changeQuantity(value: Int) {
        form.value = form.value.copy(quantity = value.coerceAtLeast(1))
    }

    fun addSelectedToCart() {
        val state = uiState.value
        val row = state.selectedRow ?: run {
            form.value = form.value.copy(warning = "Select a batch.")
            return
        }
        val availableQuantity = state.availableRows
            .filter { it.medicineId == row.medicineId }
            .sumOf { it.quantity }
        if (state.quantity > availableQuantity) {
            form.value = form.value.copy(
                warning = "Only $availableQuantity units available after the current bill cart."
            )
            return
        }
        if (row.expiryEpochDay < today.toEpochDay()) {
            form.value = form.value.copy(
                warning = "${row.name} batch ${row.batchNo} is expired. It can only be sold from bill screen after confirmation."
            )
        }
        saleDraftStore.add(
            CartLine(
                medicineId = row.medicineId,
                medicineName = row.name,
                requestedQuantity = state.quantity,
                selectedBatchId = row.batchId.takeIf { state.quantity <= row.quantity },
                batchNo = row.batchNo.takeIf { state.quantity <= row.quantity },
                expiryEpochDay = row.expiryEpochDay.takeIf { state.quantity <= row.quantity },
                mrp = row.mrp,
                availableQuantity = availableQuantity
            )
        )
        form.value = form.value.copy(warning = "Added to bill. Available batch quantity is reserved until bill generation.")
    }
}

class CartBillViewModel(
    private val repository: PharmacyRepository,
    private val saleDraftStore: SaleDraftStore
) : ViewModel() {
    private val form = MutableStateFlow(CartBillUiState())

    val uiState: StateFlow<CartBillUiState> = combine(
        form,
        saleDraftStore.cartLines
    ) { formState, cart ->
        formState.copy(cartLines = cart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartBillUiState())

    fun updateCustomer(value: String) {
        form.value = form.value.copy(customerName = value)
    }

    fun updatePaymentMode(value: String) {
        form.value = form.value.copy(paymentMode = value)
    }

    fun setAllowExpiredSale(value: Boolean) {
        form.value = form.value.copy(allowExpiredSale = value, message = null)
    }

    fun removeLine(medicineId: Long) {
        saleDraftStore.remove(medicineId)
    }

    fun clearCart() {
        saleDraftStore.clear()
    }

    fun generateBill(onSuccess: (Long) -> Unit) {
        val state = uiState.value
        viewModelScope.launch {
            form.value = form.value.copy(saving = true, message = null)
            runCatching {
                repository.createSale(
                    customerName = state.customerName,
                    paymentMode = state.paymentMode,
                    lines = state.cartLines.map {
                        SaleRequestLine(
                            medicineId = it.medicineId,
                            medicineName = it.medicineName,
                            quantity = it.requestedQuantity
                        )
                    },
                    allowExpired = state.allowExpiredSale
                )
            }.onSuccess { result ->
                form.value = form.value.copy(generatedSaleId = result.saleId)
                saleDraftStore.setLastSaleId(result.saleId)
                saleDraftStore.clear()
                form.value = form.value.copy(saving = false)
                onSuccess(result.saleId)
            }.onFailure { error ->
                form.value = form.value.copy(
                    saving = false,
                    message = when (error) {
                    is ExpiredStockRequiresConfirmationException -> error.message
                    else -> error.message ?: "Unable to generate bill."
                    }
                )
            }
        }
    }
}

class InvoicePreviewViewModel(
    repository: PharmacyRepository,
    saleDraftStore: SaleDraftStore,
    private val localFileManager: LocalFileManager
) : ViewModel() {
    val sale: StateFlow<SaleWithItems?> = saleDraftStore.lastSaleId
        .flatMapLatest { saleId -> if (saleId == null) flowOf(null) else repository.observeSaleWithItems(saleId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun exportPdf(uri: Uri) {
        val currentSale = sale.value ?: run {
            _message.value = "No invoice available to export."
            return
        }
        viewModelScope.launch {
            runCatching { localFileManager.exportInvoicePdf(uri, currentSale) }
                .onSuccess { _message.value = "PDF invoice exported." }
                .onFailure { _message.value = it.message ?: "Unable to export PDF." }
        }
    }
}

class ExpiryAlertViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val selectedTab = MutableStateFlow(ExpiryTab.All)

    val uiState: StateFlow<ExpiryAlertUiState> = combine(selectedTab, currentDateFlow()) { tab, today -> tab to today }
        .flatMapLatest { (tab, today) ->
            repository.observeExpiryRows(tab, today).map { rows -> ExpiryAlertUiState(tab, rows) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExpiryAlertUiState())

    fun selectTab(tab: ExpiryTab) {
        selectedTab.value = tab
    }
}

class LowStockViewModel(repository: PharmacyRepository) : ViewModel() {
    val rows: StateFlow<List<StockRow>> = repository.observeLowStockRows()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class SalesReportViewModel(
    private val repository: PharmacyRepository,
    private val localFileManager: LocalFileManager
) : ViewModel() {
    private val range = MutableStateFlow(DateRange.currentMonth())
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SalesReportUiState> = range.flatMapLatest { currentRange ->
        combine(
            repository.observeSalesReport(currentRange),
            repository.observeSalesRevenue(currentRange),
            repository.observeSalesProfit(currentRange),
            repository.observeSalesBillCount(currentRange),
            message
        ) { rows, revenue, profit, billCount, messageText ->
            SalesReportUiState(
                range = currentRange,
                rows = rows,
                revenue = revenue,
                profit = profit,
                billCount = billCount,
                message = messageText
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SalesReportUiState())

    fun showToday() {
        range.value = DateRange.today()
        message.value = null
    }

    fun showCurrentMonth() {
        range.value = DateRange.currentMonth()
        message.value = null
    }

    fun showAllTime() {
        range.value = DateRange(0L, System.currentTimeMillis())
        message.value = null
    }

    fun exportCsv(uri: Uri) {
        viewModelScope.launch {
            runCatching { localFileManager.exportSalesCsv(uri, uiState.value.rows) }
                .onSuccess { message.value = "Sales CSV exported." }
                .onFailure { message.value = it.message ?: "Unable to export sales CSV." }
        }
    }
}

class PurchaseReportViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val range = MutableStateFlow(DateRange.currentMonth())

    val uiState: StateFlow<PurchaseReportUiState> = range.flatMapLatest { currentRange ->
        repository.observePurchaseReport(currentRange).map { rows ->
            PurchaseReportUiState(
                range = currentRange,
                rows = rows,
                totalPurchase = rows.sumOf { it.total }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PurchaseReportUiState())

    fun showToday() {
        range.value = DateRange.today()
    }

    fun showCurrentMonth() {
        range.value = DateRange.currentMonth()
    }

    fun showAllTime() {
        range.value = DateRange(0L, System.currentTimeMillis())
    }
}

class SupplierViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val _form = MutableStateFlow(SupplierUiState())
    val uiState: StateFlow<SupplierUiState> = combine(
        repository.observeSuppliers(),
        _form
    ) { suppliers, form ->
        form.copy(suppliers = suppliers)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SupplierUiState())

    fun updateName(value: String) = update { copy(name = value, message = null) }
    fun updateContact(value: String) = update { copy(contact = value, message = null) }
    fun updatePhone(value: String) = update { copy(phone = value, message = null) }
    fun updateAddress(value: String) = update { copy(address = value, message = null) }

    fun save() {
        val state = _form.value
        viewModelScope.launch {
            runCatching {
                repository.addSupplier(state.name, state.contact, state.phone, state.address)
            }.onSuccess {
                _form.value = SupplierUiState(message = "Supplier saved.")
            }.onFailure { error ->
                _form.value = state.copy(message = error.message ?: "Unable to save supplier.")
            }
        }
    }

    private fun update(block: SupplierUiState.() -> SupplierUiState) {
        _form.value = _form.value.block()
    }
}

class BackupRestoreViewModel(
    private val repository: PharmacyRepository,
    private val localFileManager: LocalFileManager,
    private val localPeerSyncManager: LocalPeerSyncManager
) : ViewModel() {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    val localPeers: StateFlow<List<LocalPeerDevice>> = localPeerSyncManager.peers
    val localSyncStatus: StateFlow<String> = localPeerSyncManager.status

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            runCatching { localFileManager.exportDatabaseBackup(uri) }
                .onSuccess { _message.value = "Database backup exported." }
                .onFailure { _message.value = it.message ?: "Unable to export backup." }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            runCatching { localFileManager.importDatabaseBackup(uri) }
                .onSuccess { _message.value = "Backup restored. Close and reopen the app to reload the database." }
                .onFailure { _message.value = it.message ?: "Unable to restore backup." }
        }
    }

    fun exportStockCsv(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                localFileManager.exportStockCsv(uri, repository.observeStockRows().first())
            }.onSuccess {
                _message.value = "Stock CSV exported."
            }.onFailure {
                _message.value = it.message ?: "Unable to export stock CSV."
            }
        }
    }

    fun exportSalesCsv(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                localFileManager.exportSalesCsv(
                    uri,
                    repository.observeSalesReport(DateRange(0L, System.currentTimeMillis())).first()
                )
            }.onSuccess {
                _message.value = "Sales CSV exported."
            }.onFailure {
                _message.value = it.message ?: "Unable to export sales CSV."
            }
        }
    }

    fun seedDemoData() {
        viewModelScope.launch {
            repository.seedDemoDataIfEmpty()
            _message.value = "Demo data seeded if database was empty."
        }
    }

    fun trustAndSync(deviceId: String) {
        localPeerSyncManager.trustAndSync(deviceId)
    }
}

class SettingsViewModel(private val repository: PharmacyRepository) : ViewModel() {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun seedDemoData() {
        viewModelScope.launch {
            repository.seedDemoDataIfEmpty()
            _message.value = "Optional demo seed completed."
        }
    }
}

fun stockFilterFor(row: StockRow, today: LocalDate = LocalDate.now()): StockFilter {
    val days = row.expiryEpochDay - today.toEpochDay()
    return when {
        days < 0 -> StockFilter.Expired
        row.quantity <= row.minStock -> StockFilter.LowStock
        days <= 30 -> StockFilter.ExpiringSoon
        else -> StockFilter.Safe
    }
}

private fun List<StockRow>.withCartReservations(cart: List<CartLine>): List<StockRow> {
    val reservedByMedicine = cart
        .groupBy { it.medicineId }
        .mapValues { (_, lines) -> lines.sumOf { it.requestedQuantity } }
        .toMutableMap()

    return sortedWith(compareBy<StockRow> { it.medicineId }.thenBy { it.expiryEpochDay }.thenBy { it.batchId })
        .map { row ->
            val reserved = reservedByMedicine[row.medicineId].orZero()
            val reservedFromBatch = minOf(row.quantity, reserved)
            reservedByMedicine[row.medicineId] = reserved - reservedFromBatch
            row.copy(quantity = row.quantity - reservedFromBatch)
        }
        .sortedWith(compareBy<StockRow> { it.expiryEpochDay }.thenBy { it.name.lowercase() })
}

private fun Int?.orZero(): Int = this ?: 0

private fun currentDateFlow(): Flow<LocalDate> = flow {
    while (true) {
        val now = ZonedDateTime.now()
        emit(now.toLocalDate())
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
        val delayMillis = Duration.between(now, nextMidnight).toMillis().coerceAtLeast(1_000L)
        delay(delayMillis)
    }
}

private fun parseDate(input: String): LocalDate {
    val clean = input.trim()
    dateInputFormats.forEach { format ->
        try {
            return LocalDate.parse(clean, format)
        } catch (_: DateTimeParseException) {
            // Try next accepted local date format.
        }
    }
    error("Use date as yyyy-MM-dd, dd/MM/yyyy, or dd-MM-yyyy.")
}

private fun String.priceInput(): String {
    var dotSeen = false
    return filter { char ->
        when {
            char.isDigit() -> true
            char == '.' && !dotSeen -> {
                dotSeen = true
                true
            }
            else -> false
        }
    }
}
