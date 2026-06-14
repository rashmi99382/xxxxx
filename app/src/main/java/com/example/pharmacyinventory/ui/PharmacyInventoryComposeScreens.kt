@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pharmacyinventory.ui

import android.net.Uri
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.pharmacyinventory.BuildConfig
import com.example.pharmacyinventory.LocalPeerDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pharmacyinventory.data.BatchEntity
import com.example.pharmacyinventory.data.CartLine
import com.example.pharmacyinventory.data.ExpiryTab
import com.example.pharmacyinventory.data.MedicineEntity
import com.example.pharmacyinventory.data.MedicineWithBatches
import com.example.pharmacyinventory.data.PurchaseReportRow
import com.example.pharmacyinventory.data.SaleReportRow
import com.example.pharmacyinventory.data.SaleWithItems
import com.example.pharmacyinventory.data.StockRow
import com.example.pharmacyinventory.data.SupplierEntity
import com.example.pharmacyinventory.data.toLocalDate
import com.example.pharmacyinventory.data.toLocalDateTimeText
import com.example.pharmacyinventory.viewmodel.AddBatchViewModel
import com.example.pharmacyinventory.viewmodel.AddMedicineViewModel
import com.example.pharmacyinventory.viewmodel.BackupRestoreViewModel
import com.example.pharmacyinventory.viewmodel.CartBillViewModel
import com.example.pharmacyinventory.viewmodel.DashboardUiState
import com.example.pharmacyinventory.viewmodel.DashboardViewModel
import com.example.pharmacyinventory.viewmodel.ExpiryAlertViewModel
import com.example.pharmacyinventory.viewmodel.InvoicePreviewViewModel
import com.example.pharmacyinventory.viewmodel.LowStockViewModel
import com.example.pharmacyinventory.viewmodel.MedicineDetailViewModel
import com.example.pharmacyinventory.viewmodel.PinLoginViewModel
import com.example.pharmacyinventory.viewmodel.PinLoginMode
import com.example.pharmacyinventory.viewmodel.PurchaseReportViewModel
import com.example.pharmacyinventory.viewmodel.SalesReportViewModel
import com.example.pharmacyinventory.viewmodel.SellMedicineViewModel
import com.example.pharmacyinventory.viewmodel.SettingsViewModel
import com.example.pharmacyinventory.viewmodel.SplashViewModel
import com.example.pharmacyinventory.viewmodel.StockFilter
import com.example.pharmacyinventory.viewmodel.StockListViewModel
import com.example.pharmacyinventory.viewmodel.SupplierViewModel
import com.example.pharmacyinventory.viewmodel.stockFilterFor
import com.example.pharmacyinventory.subscription.AccountLoginViewModel
import com.example.pharmacyinventory.subscription.AccountLoginUiState
import com.example.pharmacyinventory.subscription.AdminDashboardResponse
import com.example.pharmacyinventory.subscription.AdminUiState
import com.example.pharmacyinventory.subscription.AdminViewModel
import com.example.pharmacyinventory.subscription.PaymentStartResponse
import com.example.pharmacyinventory.subscription.RegisterShopUiState
import com.example.pharmacyinventory.subscription.RegisterShopViewModel
import com.example.pharmacyinventory.subscription.RegisteredDevice
import com.example.pharmacyinventory.subscription.SubscriptionGateDestination
import com.example.pharmacyinventory.subscription.SubscriptionGateUiState
import com.example.pharmacyinventory.subscription.SubscriptionGateViewModel
import com.example.pharmacyinventory.subscription.SubscriptionPlan
import com.example.pharmacyinventory.subscription.SubscriptionStatus
import com.example.pharmacyinventory.subscription.SubscriptionUiState
import com.example.pharmacyinventory.subscription.SubscriptionViewModel
import com.example.pharmacyinventory.subscription.checkoutKeyId
import com.example.pharmacyinventory.subscription.checkoutSubscriptionId
import com.razorpay.Checkout
import org.json.JSONObject
import java.time.LocalDate

private object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Dashboard = "dashboard"
    const val AddMedicine = "add_medicine"
    const val AddBatch = "add_batch"
    const val StockList = "stock_list"
    const val MedicineDetail = "medicine_detail"
    const val SellMedicine = "sell_medicine"
    const val CartBill = "cart_bill"
    const val InvoicePreview = "invoice_preview"
    const val ExpiryAlert = "expiry_alert"
    const val LowStock = "low_stock"
    const val SalesReport = "sales_report"
    const val PurchaseReport = "purchase_report"
    const val Supplier = "supplier"
    const val BackupRestore = "backup_restore"
    const val Settings = "settings"
    const val SubscriptionGate = "subscription_gate"
    const val AccountLogin = "account_login"
    const val RegisterShop = "register_shop"
    const val SubscriptionPlans = "subscription_plans"
    const val Payment = "payment"
    const val SubscriptionStatus = "subscription_status"
    const val DeviceManagement = "device_management"
    const val Renewal = "renewal"
    const val OfflineGrace = "offline_grace"
    const val AdminLogin = "admin_login"

    fun medicineDetail(id: Long) = "$MedicineDetail/$id"
}

private val safeGreen = Color(0xFF147A3D)
private val lowStockAmber = Color(0xFF9A6700)
private val expiringOrange = Color(0xFFC2410C)
private val expiredRed = Color(0xFFB3261E)
private val paidSubscriptionPlans = listOf(SubscriptionPlan.BASIC, SubscriptionPlan.STANDARD, SubscriptionPlan.PREMIUM)

private val pharmacyColorScheme = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7F5EE),
    onPrimaryContainer = Color(0xFF063F39),
    secondary = Color(0xFF415F91),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9E2FF),
    error = expiredRed,
    background = Color(0xFFF7FAF8),
    surface = Color.White,
    surfaceVariant = Color(0xFFEAF1ED)
)

@Composable
fun PharmacyInventoryApp(factory: ViewModelProvider.Factory) {
    MaterialTheme(colorScheme = pharmacyColorScheme) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Routes.Splash) {
            composable(Routes.Splash) {
                val vm: SplashViewModel = viewModel(factory = factory)
                val ready by vm.ready.collectAsState()
                SplashScreen(ready = ready) {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            }
            composable(Routes.Login) {
                val vm: PinLoginViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                PinLoginScreen(
                    state = state,
                    onDigit = vm::appendDigit,
                    onClear = vm::clear,
                    onBackspace = vm::backspace,
                    onLogin = {
                        navController.navigate(Routes.SubscriptionGate) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.SubscriptionGate) {
                val vm: SubscriptionGateViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                SubscriptionGateScreen(
                    state = state,
                    onNavigateLogin = {
                        navController.navigate(Routes.AccountLogin) {
                            popUpTo(Routes.SubscriptionGate) { inclusive = true }
                        }
                    },
                    onNavigateDashboard = {
                        navController.navigate(Routes.Dashboard) {
                            popUpTo(Routes.SubscriptionGate) { inclusive = true }
                        }
                    },
                    onNavigateRenewal = {
                        navController.navigate(Routes.Renewal) {
                            popUpTo(Routes.SubscriptionGate) { inclusive = true }
                        }
                    },
                    onNavigateGrace = {
                        navController.navigate(Routes.OfflineGrace) {
                            popUpTo(Routes.SubscriptionGate) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.AccountLogin) {
                val vm: AccountLoginViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                AccountLoginScreen(
                    state = state,
                    onEmail = vm::updateEmail,
                    onPassword = vm::updatePassword,
                    onLogin = vm::login,
                    onRegister = { navController.navigate(Routes.RegisterShop) },
                    onAdmin = { navController.navigate(Routes.AdminLogin) },
                    onLoggedIn = {
                        navController.navigate(Routes.SubscriptionGate) {
                            popUpTo(Routes.AccountLogin) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.RegisterShop) {
                val vm: RegisterShopViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                RegisterShopScreen(
                    state = state,
                    onShopName = vm::updateShopName,
                    onOwnerName = vm::updateOwnerName,
                    onEmail = vm::updateEmail,
                    onPassword = vm::updatePassword,
                    onRegister = vm::register,
                    onBack = navController::popBackStack,
                    onRegistered = {
                        navController.navigate(Routes.SubscriptionGate) {
                            popUpTo(Routes.RegisterShop) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.SubscriptionPlans) {
                val vm: SubscriptionViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                SubscriptionPlanScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onSelect = vm::selectPlan,
                    onStartPayment = { vm.startPayment(false) },
                    onOpenStatus = { navController.navigate(Routes.SubscriptionStatus) }
                )
            }
            composable(Routes.Payment) {
                val vm: SubscriptionViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                PaymentScreen(state = state, onBack = navController::popBackStack, onValidate = vm::validateNow)
            }
            composable(Routes.SubscriptionStatus) {
                val vm: SubscriptionViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                SubscriptionStatusScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onValidate = vm::validateNow,
                    onPlans = { navController.navigate(Routes.SubscriptionPlans) },
                    onDevices = {
                        vm.loadDevices()
                        navController.navigate(Routes.DeviceManagement)
                    }
                )
            }
            composable(Routes.DeviceManagement) {
                val vm: SubscriptionViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                LaunchedEffect(Unit) { vm.loadDevices() }
                DeviceManagementScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onRemove = vm::removeDevice
                )
            }
            composable(Routes.Renewal) {
                val vm: SubscriptionViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                RenewalScreen(
                    state = state,
                    onSelect = vm::selectPlan,
                    onRenew = { vm.startPayment(true) },
                    onReadOnly = { navController.navigate(Routes.StockList) },
                    onStatus = { navController.navigate(Routes.SubscriptionStatus) }
                )
            }
            composable(Routes.OfflineGrace) {
                val vm: SubscriptionViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                OfflineGraceScreen(
                    state = state,
                    onValidate = vm::validateNow,
                    onContinue = {
                        navController.navigate(Routes.Dashboard) {
                            popUpTo(Routes.OfflineGrace) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.AdminLogin) {
                val vm: AdminViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                AdminLoginScreen(
                    state = state,
                    onEmail = vm::updateEmail,
                    onPassword = vm::updatePassword,
                    onLogin = vm::login,
                    onBack = navController::popBackStack
                )
            }
            composable(Routes.Dashboard) {
                val vm: DashboardViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                DashboardScreen(state = state, onNavigate = navController::navigate)
            }
            composable(Routes.AddMedicine) {
                val vm: AddMedicineViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                AddMedicineScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onName = vm::updateName,
                    onCompany = vm::updateCompany,
                    onGeneric = vm::updateGenericName,
                    onCategory = vm::updateCategory,
                    onSupplier = vm::updateSupplier,
                    onMinStock = vm::updateMinStock,
                    onSave = vm::save
                )
            }
            composable(Routes.AddBatch) {
                val vm: AddBatchViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                val medicines by vm.medicines.collectAsState()
                LaunchedEffect(medicines, state.medicineId) {
                    if (state.medicineId == null && medicines.isNotEmpty()) {
                        vm.selectMedicine(medicines.first().id)
                    }
                }
                AddBatchStockInScreen(
                    state = state,
                    medicines = medicines,
                    onBack = navController::popBackStack,
                    onMedicine = vm::selectMedicine,
                    onSupplier = vm::updateSupplier,
                    onBatch = vm::updateBatchNo,
                    onExpiry = vm::updateExpiry,
                    onQuantity = vm::updateQuantity,
                    onPurchase = vm::updatePurchasePrice,
                    onMrp = vm::updateMrp,
                    onVoucher = vm::updateVoucherNo,
                    onSave = vm::save,
                    onAddMedicine = { navController.navigate(Routes.AddMedicine) }
                )
            }
            composable(Routes.StockList) {
                val vm: StockListViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                StockListScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onQuery = vm::updateQuery,
                    onCategory = vm::selectCategory,
                    onStatus = vm::selectStatus,
                    onAddMedicine = { navController.navigate(Routes.AddMedicine) },
                    onOpenMedicine = { navController.navigate(Routes.medicineDetail(it)) }
                )
            }
            composable(
                route = "${Routes.MedicineDetail}/{medicineId}",
                arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
            ) { entry ->
                val vm: MedicineDetailViewModel = viewModel(factory = factory)
                val id = entry.arguments?.getLong("medicineId") ?: 0L
                LaunchedEffect(id) { vm.load(id) }
                val detail by vm.detail.collectAsState()
                MedicineDetailScreen(
                    detail = detail,
                    onBack = navController::popBackStack,
                    onStockIn = { navController.navigate(Routes.AddBatch) },
                    onSell = { navController.navigate(Routes.SellMedicine) }
                )
            }
            composable(Routes.SellMedicine) {
                val vm: SellMedicineViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                SellMedicineScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onQuery = vm::updateQuery,
                    onSelectBatch = vm::selectBatch,
                    onQuantity = vm::changeQuantity,
                    onAddToCart = vm::addSelectedToCart,
                    onOpenCart = { navController.navigate(Routes.CartBill) }
                )
            }
            composable(Routes.CartBill) {
                val vm: CartBillViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                CartBillScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onCustomer = vm::updateCustomer,
                    onPayment = vm::updatePaymentMode,
                    onAllowExpired = vm::setAllowExpiredSale,
                    onRemoveLine = vm::removeLine,
                    onClear = vm::clearCart,
                    onAddMore = { navController.navigate(Routes.SellMedicine) },
                    onGenerate = { vm.generateBill { navController.navigate(Routes.InvoicePreview) } }
                )
            }
            composable(Routes.InvoicePreview) {
                val vm: InvoicePreviewViewModel = viewModel(factory = factory)
                val sale by vm.sale.collectAsState()
                val message by vm.message.collectAsState()
                InvoicePreviewScreen(
                    sale = sale,
                    message = message,
                    onBack = navController::popBackStack,
                    onSavePdf = vm::exportPdf
                )
            }
            composable(Routes.ExpiryAlert) {
                val vm: ExpiryAlertViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                ExpiryAlertScreen(state = state, onBack = navController::popBackStack, onTab = vm::selectTab)
            }
            composable(Routes.LowStock) {
                val vm: LowStockViewModel = viewModel(factory = factory)
                val rows by vm.rows.collectAsState()
                LowStockScreen(rows = rows, onBack = navController::popBackStack, onStockIn = { navController.navigate(Routes.AddBatch) })
            }
            composable(Routes.SalesReport) {
                val vm: SalesReportViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                SalesReportScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onToday = vm::showToday,
                    onMonth = vm::showCurrentMonth,
                    onAll = vm::showAllTime,
                    onExportCsv = vm::exportCsv
                )
            }
            composable(Routes.PurchaseReport) {
                val vm: PurchaseReportViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                PurchaseReportScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onToday = vm::showToday,
                    onMonth = vm::showCurrentMonth,
                    onAll = vm::showAllTime
                )
            }
            composable(Routes.Supplier) {
                val vm: SupplierViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                SupplierScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onName = vm::updateName,
                    onContact = vm::updateContact,
                    onPhone = vm::updatePhone,
                    onAddress = vm::updateAddress,
                    onSave = vm::save
                )
            }
            composable(Routes.BackupRestore) {
                val vm: BackupRestoreViewModel = viewModel(factory = factory)
                val message by vm.message.collectAsState()
                val peers by vm.localPeers.collectAsState()
                val syncStatus by vm.localSyncStatus.collectAsState()
                BackupRestoreScreen(
                    message = message,
                    syncStatus = syncStatus,
                    peers = peers,
                    onBack = navController::popBackStack,
                    onExportBackup = vm::exportBackup,
                    onImportBackup = vm::importBackup,
                    onExportStockCsv = vm::exportStockCsv,
                    onExportSalesCsv = vm::exportSalesCsv,
                    onSeed = vm::seedDemoData,
                    onSyncPeer = vm::trustAndSync
                )
            }
            composable(Routes.Settings) {
                val vm: SettingsViewModel = viewModel(factory = factory)
                val message by vm.message.collectAsState()
                SettingsScreen(
                    message = message,
                    onBack = navController::popBackStack,
                    onSeed = vm::seedDemoData
                )
            }
        }
    }
}

@Composable
private fun SplashScreen(ready: Boolean, onFinished: () -> Unit) {
    LaunchedEffect(ready) {
        if (ready) onFinished()
    }
    Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("Rx", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(20.dp))
            Text("MediStock Offline", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            Text("Room database, no internet required", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f))
        }
    }
}

@Composable
private fun PinLoginScreen(
    state: com.example.pharmacyinventory.viewmodel.PinLoginUiState,
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onLogin: () -> Unit
) {
    LaunchedEffect(state.authenticated) {
        if (state.authenticated) onLogin()
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (state.mode == PinLoginMode.CreatePin) "Create PIN" else "Enter PIN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (state.mode == PinLoginMode.CreatePin) "This encrypted PIN protects local pharmacy data." else "Device-only access for local pharmacy data",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (index < state.pin.length) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
                MessageText(state.message)
                Spacer(Modifier.height(28.dp))
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "Clear", "0", "Back").chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { key ->
                            OutlinedButton(
                                onClick = {
                                    when (key) {
                                        "Clear" -> onClear()
                                        "Back" -> onBackspace()
                                        else -> onDigit(key)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text(key) }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            StatusNote("All stock and sales are saved locally on this device.")
        }
    }
}

@Composable
private fun SubscriptionGateScreen(
    state: SubscriptionGateUiState,
    onNavigateLogin: () -> Unit,
    onNavigateDashboard: () -> Unit,
    onNavigateRenewal: () -> Unit,
    onNavigateGrace: () -> Unit
) {
    LaunchedEffect(state.destination) {
        when (state.destination) {
            SubscriptionGateDestination.AccountLogin -> onNavigateLogin()
            SubscriptionGateDestination.PharmacyDashboard -> onNavigateDashboard()
            SubscriptionGateDestination.Renewal -> onNavigateRenewal()
            SubscriptionGateDestination.OfflineGrace -> onNavigateGrace()
            SubscriptionGateDestination.Loading -> Unit
        }
    }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Checking subscription", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(state.message ?: "Pharmacy data remains local on this device.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AccountLoginScreen(
    state: AccountLoginUiState,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onAdmin: () -> Unit,
    onLoggedIn: () -> Unit
) {
    LaunchedEffect(state.loggedIn) { if (state.loggedIn) onLoggedIn() }
    FormScaffold("Shop login", onBack = {}) {
        StatusNote("Login validates only account, subscription, and device access. Medicine, stock, sales, bills, and reports stay offline in Room.")
        FormTextField("Owner email", state.email, onEmail)
        PasswordTextField("Password", state.password, onPassword)
        MessageText(state.message)
        Button(onClick = onLogin, enabled = !state.loading, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Text(if (state.loading) "Signing in..." else "Login")
        }
        OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Register shop") }
        TextButton(onClick = onAdmin, modifier = Modifier.fillMaxWidth()) { Text("Super Admin login") }
    }
}

@Composable
private fun RegisterShopScreen(
    state: RegisterShopUiState,
    onShopName: (String) -> Unit,
    onOwnerName: (String) -> Unit,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit,
    onRegistered: () -> Unit
) {
    LaunchedEffect(state.registered) { if (state.registered) onRegistered() }
    FormScaffold("Register shop", onBack) {
        FormTextField("Shop name", state.shopName, onShopName)
        FormTextField("Owner name", state.ownerName, onOwnerName)
        FormTextField("Email", state.email, onEmail)
        PasswordTextField("Password", state.password, onPassword)
        MessageText(state.message)
        Button(onClick = onRegister, enabled = !state.loading, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Text(if (state.loading) "Registering..." else "Create shop account")
        }
    }
}

@Composable
private fun SubscriptionPlanScreen(
    state: SubscriptionUiState,
    onBack: () -> Unit,
    onSelect: (SubscriptionPlan) -> Unit,
    onStartPayment: () -> Unit,
    onOpenStatus: () -> Unit
) {
    LaunchRazorpayIfReady(payment = state.paymentStart)
    FormScaffold("Subscription plans", onBack) {
        StatusNote("Choose a plan. Payment starts through Razorpay Checkout; subscription confirmation comes from the AWS webhook and validate API.")
        paidSubscriptionPlans.forEach { plan ->
            SubscriptionPlanCard(plan = plan, selected = state.selectedPlan == plan, onClick = { onSelect(plan) })
        }
        MessageText(state.message)
        Button(onClick = onStartPayment, enabled = !state.loading, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Text(if (state.loading) "Preparing..." else "Start payment")
        }
        OutlinedButton(onClick = onOpenStatus, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Subscription status") }
        Text("Razorpay key: ${BuildConfig.RAZORPAY_KEY_ID}", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PaymentScreen(state: SubscriptionUiState, onBack: () -> Unit, onValidate: () -> Unit) {
    LaunchRazorpayIfReady(payment = state.paymentStart)
    FormScaffold("Payment", onBack) {
        StatusNote("After payment, Razorpay webhook updates AWS. Tap Validate after payment completion.")
        MessageText(state.message)
        Button(onClick = onValidate, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Validate subscription") }
    }
}

@Composable
private fun SubscriptionStatusScreen(
    state: SubscriptionUiState,
    onBack: () -> Unit,
    onValidate: () -> Unit,
    onPlans: () -> Unit,
    onDevices: () -> Unit
) {
    val cache = state.cache
    FormScaffold("Subscription status", onBack) {
        InfoCard {
            KeyValue("Shop", cache.shopName ?: cache.shopId.orEmpty().ifBlank { "-" })
            KeyValue("Owner", cache.ownerName ?: "-")
            KeyValue("Email", cache.ownerEmail ?: "-")
            KeyValue("Status", cache.subscriptionStatus.name)
            KeyValue("Plan", cache.subscriptionPlan?.label ?: "-")
            KeyValue("Expiry", cache.expiryDate ?: "-")
            KeyValue("Allowed devices", if (cache.allowedDevices <= 0) "-" else cache.allowedDevices.toString())
            KeyValue("Registered", cache.registeredDeviceCount.toString())
            KeyValue("Device ID", cache.currentDeviceId.take(12))
        }
        if (cache.isGraceActive()) WarningText("Subscription could not be verified. Offline grace period active.")
        MessageText(state.message)
        Button(onClick = onValidate, enabled = !state.loading, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Validate now") }
        OutlinedButton(onClick = onPlans, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Renew / change plan") }
        OutlinedButton(onClick = onDevices, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Device management") }
    }
}

@Composable
private fun DeviceManagementScreen(state: SubscriptionUiState, onBack: () -> Unit, onRemove: (String) -> Unit) {
    FormScaffold("Device management", onBack) {
        StatusNote("Device limits are checked by AWS. Premium allows unlimited devices.")
        MessageText(state.message)
        if (state.devices.isEmpty()) EmptyCard("No registered devices loaded.")
        state.devices.forEach { device ->
            RegisteredDeviceCard(device = device, onRemove = { onRemove(device.deviceId) })
        }
    }
}

@Composable
private fun RenewalScreen(
    state: SubscriptionUiState,
    onSelect: (SubscriptionPlan) -> Unit,
    onRenew: () -> Unit,
    onReadOnly: () -> Unit,
    onStatus: () -> Unit
) {
    LaunchRazorpayIfReady(payment = state.paymentStart)
    FormScaffold("Renew subscription", onBack = {}) {
        WarningBox("Subscription required", "Your local pharmacy data is safe. Renew to add stock, create bills, export reports, and continue full usage.")
        paidSubscriptionPlans.forEach { plan ->
            SubscriptionPlanCard(plan = plan, selected = state.selectedPlan == plan, onClick = { onSelect(plan) })
        }
        MessageText(state.message)
        Button(onClick = onRenew, enabled = !state.loading, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Renew with Razorpay") }
        OutlinedButton(onClick = onReadOnly, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("View local stock read-only") }
        TextButton(onClick = onStatus, modifier = Modifier.fillMaxWidth()) { Text("Subscription status") }
    }
}

@Composable
private fun OfflineGraceScreen(state: SubscriptionUiState, onValidate: () -> Unit, onContinue: () -> Unit) {
    FormScaffold("Offline grace active", onBack = {}) {
        WarningBox("Subscription could not be verified", "You can continue for the local 7-day grace period after last successful validation.")
        KeyValue("Status", state.cache.subscriptionStatus.name)
        KeyValue("Grace until", state.cache.gracePeriodEnd.toString())
        MessageText(state.message)
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Continue to pharmacy") }
        OutlinedButton(onClick = onValidate, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Try validation") }
    }
}

@Composable
private fun AdminLoginScreen(
    state: AdminUiState,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit
) {
    FormScaffold("Super Admin", onBack) {
        if (state.dashboard == null) {
            FormTextField("Admin email", state.email, onEmail)
            PasswordTextField("Password", state.password, onPassword)
            MessageText(state.message)
            Button(onClick = onLogin, enabled = !state.loading, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Text(if (state.loading) "Loading..." else "Login")
            }
        } else {
            AdminDashboardCard(state.dashboard)
        }
    }
}

@Composable
private fun DashboardScreen(state: DashboardUiState, onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = { PharmacyTopBar("Sharma Medical Store") },
        bottomBar = { DashboardBottomBar(onNavigate) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                MetricGrid(
                    listOf(
                        Metric("Total medicines", state.metrics.medicineCount.toString(), "Saved in Room"),
                        Metric("Today sales", money(state.metrics.todaySales), "From invoices"),
                        Metric("Monthly sales", money(state.metrics.monthlySales), "Current month"),
                        Metric("Low stock", state.metrics.lowStockCount.toString(), "Needs reorder"),
                        Metric("Expired", state.metrics.expiredCount.toString(), "Remove or return"),
                        Metric("Expiring soon", state.metrics.expiringSoonCount.toString(), "Next 30 days")
                    )
                )
            }
            item {
                SectionTitle("Quick actions")
                QuickActionGrid(
                    actions = listOf(
                        "Sell medicine" to Routes.SellMedicine,
                        "Add medicine" to Routes.AddMedicine,
                        "Stock-in batch" to Routes.AddBatch,
                        "Stock list" to Routes.StockList,
                        "Expiry alerts" to Routes.ExpiryAlert,
                        "Sales report" to Routes.SalesReport,
                        "Suppliers" to Routes.Supplier,
                        "Subscription" to Routes.SubscriptionStatus,
                        "Backup" to Routes.BackupRestore
                    ),
                    onNavigate = onNavigate
                )
            }
            item { SectionTitle("Recent sales") }
            if (state.recentSales.isEmpty()) {
                item { EmptyCard("No sales yet. Use Sell medicine to create the first bill.") }
            } else {
                items(state.recentSales) { sale -> RecentSaleCard(sale) }
            }
            item { SectionTitle("Expiry warning") }
            if (state.expiryWarnings.isEmpty()) {
                item { EmptyCard("No medicines expiring in the selected warning window.") }
            } else {
                items(state.expiryWarnings) { row -> StockMiniCard(row, onClick = { onNavigate(Routes.ExpiryAlert) }) }
            }
        }
    }
}

@Composable
private fun AddMedicineScreen(
    state: com.example.pharmacyinventory.viewmodel.AddMedicineUiState,
    onBack: () -> Unit,
    onName: (String) -> Unit,
    onCompany: (String) -> Unit,
    onGeneric: (String) -> Unit,
    onCategory: (String) -> Unit,
    onSupplier: (String) -> Unit,
    onMinStock: (String) -> Unit,
    onSave: () -> Unit
) {
    FormScaffold("Add medicine", onBack) {
        FormTextField("Medicine name", state.name, onName)
        FormTextField("Company", state.company, onCompany)
        FormTextField("Generic name", state.genericName, onGeneric)
        FormTextField("Category", state.category, onCategory)
        FormTextField("Supplier", state.supplierName, onSupplier)
        FormTextField("Low stock alert quantity", state.minStock, onMinStock)
        MessageText(state.message)
        Button(onClick = onSave, enabled = !state.saving, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Text(if (state.saving) "Saving..." else "Save medicine")
        }
    }
}

@Composable
private fun AddBatchStockInScreen(
    state: com.example.pharmacyinventory.viewmodel.AddBatchUiState,
    medicines: List<MedicineEntity>,
    onBack: () -> Unit,
    onMedicine: (Long) -> Unit,
    onSupplier: (String) -> Unit,
    onBatch: (String) -> Unit,
    onExpiry: (String) -> Unit,
    onQuantity: (String) -> Unit,
    onPurchase: (String) -> Unit,
    onMrp: (String) -> Unit,
    onVoucher: (String) -> Unit,
    onSave: () -> Unit,
    onAddMedicine: () -> Unit
) {
    FormScaffold("Add batch / stock-in", onBack) {
        if (medicines.isEmpty()) {
            EmptyCard("Add a medicine before adding stock.")
            OutlinedButton(onClick = onAddMedicine, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Text("Add medicine")
            }
        } else {
            SectionTitle("Medicine")
            ChipRow(
                labels = medicines.map { it.name },
                selected = medicines.firstOrNull { it.id == state.medicineId }?.name ?: medicines.first().name,
                onSelected = { label -> medicines.firstOrNull { it.name == label }?.let { onMedicine(it.id) } }
            )
            FormTextField("Supplier", state.supplierName, onSupplier)
            FormTextField("Batch number", state.batchNo, onBatch)
            FormTextField("Expiry date yyyy-MM-dd", state.expiryDate, onExpiry)
            FormTextField("Quantity received", state.quantity, onQuantity)
            FormTextField("Purchase price", state.purchasePrice, onPurchase)
            FormTextField("MRP", state.mrp, onMrp)
            FormTextField("Voucher number", state.voucherNo, onVoucher)
            MessageText(state.message)
            Button(onClick = onSave, enabled = !state.saving, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Text(if (state.saving) "Saving..." else "Save stock-in")
            }
        }
    }
}

@Composable
private fun StockListScreen(
    state: com.example.pharmacyinventory.viewmodel.StockListUiState,
    onBack: () -> Unit,
    onQuery: (String) -> Unit,
    onCategory: (String) -> Unit,
    onStatus: (StockFilter) -> Unit,
    onAddMedicine: () -> Unit,
    onOpenMedicine: (Long) -> Unit
) {
    Scaffold(
        topBar = { PharmacyTopBar("Stock list", onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMedicine, shape = RoundedCornerShape(16.dp)) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQuery,
                    label = { Text("Search medicine, company, batch") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
            item {
                ChipRow(state.categories, state.selectedCategory, onCategory)
                Spacer(Modifier.height(8.dp))
                FilterChipRow(
                    values = StockFilter.entries,
                    selected = state.selectedStatus,
                    label = { it.label },
                    onSelected = onStatus
                )
            }
            if (state.rows.isEmpty()) {
                item { EmptyCard("No stock batches found. Add stock-in entries to see inventory here.") }
            } else {
                items(state.rows) { row ->
                    StockCard(row = row, onClick = { onOpenMedicine(row.medicineId) })
                }
            }
        }
    }
}

@Composable
private fun MedicineDetailScreen(
    detail: MedicineWithBatches?,
    onBack: () -> Unit,
    onStockIn: () -> Unit,
    onSell: () -> Unit
) {
    Scaffold(
        topBar = { PharmacyTopBar(detail?.medicine?.name ?: "Medicine detail", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val current = detail
            if (current == null) {
                item { EmptyCard("Loading medicine details...") }
            } else {
                item {
                    InfoCard {
                        Text(current.medicine.company, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(current.medicine.genericName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(10.dp))
                        KeyValue("Category", current.medicine.category)
                        KeyValue("Low stock alert", current.medicine.minStock.toString())
                        KeyValue("Total quantity", current.batches.sumOf { it.quantity }.toString())
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onStockIn, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Stock-in") }
                        OutlinedButton(onClick = onSell, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Sell") }
                    }
                }
                item { SectionTitle("Batches") }
                items(current.batches.sortedBy { it.expiryEpochDay }) { batch ->
                    BatchCard(current.medicine, batch)
                }
            }
        }
    }
}

@Composable
private fun SellMedicineScreen(
    state: com.example.pharmacyinventory.viewmodel.SellMedicineUiState,
    onBack: () -> Unit,
    onQuery: (String) -> Unit,
    onSelectBatch: (Long) -> Unit,
    onQuantity: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onOpenCart: () -> Unit
) {
    Scaffold(
        topBar = { PharmacyTopBar("Sell medicine", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQuery,
                    label = { Text("Search medicine") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
            item {
                SectionTitle("Available batches")
                Text("Nearest non-expired expiry is auto-selected. Expired stock requires bill confirmation.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (state.availableRows.isEmpty()) {
                item { EmptyCard("No available stock found.") }
            } else {
                items(state.availableRows.take(8)) { row ->
                    BatchSelectCard(
                        row = row,
                        selected = state.selectedBatchId == row.batchId,
                        onClick = { onSelectBatch(row.batchId) }
                    )
                }
            }
            item {
                QuantitySelector(quantity = state.quantity, onChange = onQuantity)
                MessageText(state.warning)
            }
            item {
                Button(
                    onClick = onAddToCart,
                    enabled = (state.selectedRow?.quantity ?: 0) > 0,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Add to bill") }
            }
            item {
                CartSummary(cartLines = state.cartLines, onOpenCart = onOpenCart)
            }
        }
    }
}

@Composable
private fun CartBillScreen(
    state: com.example.pharmacyinventory.viewmodel.CartBillUiState,
    onBack: () -> Unit,
    onCustomer: (String) -> Unit,
    onPayment: (String) -> Unit,
    onAllowExpired: (Boolean) -> Unit,
    onRemoveLine: (Long) -> Unit,
    onClear: () -> Unit,
    onAddMore: () -> Unit,
    onGenerate: () -> Unit
) {
    Scaffold(
        topBar = { PharmacyTopBar("Cart / bill", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FormTextField("Customer", state.customerName, onCustomer)
                FormTextField("Payment mode", state.paymentMode, onPayment)
            }
            if (state.cartLines.isEmpty()) {
                item { EmptyCard("Cart is empty. Add medicines to generate a bill.") }
            } else {
                items(state.cartLines) { line ->
                    CartLineCard(line = line, onRemove = { onRemoveLine(line.medicineId) })
                }
            }
            item {
                InfoCard {
                    KeyValue("Items", state.cartLines.sumOf { it.requestedQuantity }.toString())
                    KeyValue("Payable", money(state.cartLines.sumOf { it.lineTotal }), strong = true)
                    HorizontalDivider(Modifier.padding(vertical = 10.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text("Allow expired sale", fontWeight = FontWeight.SemiBold)
                            Text("Use only after shop owner confirms warning.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = state.allowExpiredSale, onCheckedChange = onAllowExpired)
                    }
                }
            }
            item { MessageText(state.message) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onAddMore, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Add more") }
                    OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Clear") }
                }
            }
            item {
                Button(
                    onClick = onGenerate,
                    enabled = state.cartLines.isNotEmpty() && !state.saving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(if (state.saving) "Saving bill..." else "Generate bill") }
            }
        }
    }
}

@Composable
private fun InvoicePreviewScreen(
    sale: SaleWithItems?,
    message: String?,
    onBack: () -> Unit,
    onSavePdf: (Uri) -> Unit
) {
    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri -> if (uri != null) onSavePdf(uri) }

    Scaffold(
        topBar = { PharmacyTopBar("Invoice preview", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (sale == null) {
                item { EmptyCard("No generated invoice selected.") }
            } else {
                item {
                    InfoCard {
                        Text("Sharma Medical Store", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Bill ${sale.sale.billNo}  ${sale.sale.soldAtMillis.toLocalDateTimeText()}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Customer: ${sale.sale.customerName}")
                        HorizontalDivider(Modifier.padding(vertical = 10.dp))
                        sale.items.forEach { item ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.medicineName, fontWeight = FontWeight.SemiBold)
                                    Text("Batch ${item.batchNo}  Exp ${item.expiryEpochDay.toLocalDate()}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("${item.quantity} x ${money(item.mrp)}", textAlign = TextAlign.End)
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        KeyValue("Total", money(sale.sale.total), strong = true)
                        if (sale.sale.hadExpiredWarning) {
                            WarningText("Expired stock was sold after confirmation.")
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Print") }
                        OutlinedButton(
                            onClick = { pdfLauncher.launch("${sale.sale.billNo}.pdf") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Save PDF") }
                    }
                }
                item { MessageText(message) }
            }
        }
    }
}

@Composable
private fun ExpiryAlertScreen(
    state: com.example.pharmacyinventory.viewmodel.ExpiryAlertUiState,
    onBack: () -> Unit,
    onTab: (ExpiryTab) -> Unit
) {
    val tabs = listOf(
        ExpiryTab.All to "All",
        ExpiryTab.Expired to "Expired",
        ExpiryTab.SevenDays to "7 Days",
        ExpiryTab.ThirtyDays to "30 Days",
        ExpiryTab.NinetyDays to "90 Days"
    )
    Scaffold(
        topBar = { PharmacyTopBar("Expiry alerts", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.padding(padding)) {
            ScrollableTabRow(selectedTabIndex = tabs.indexOfFirst { it.first == state.selectedTab }.coerceAtLeast(0)) {
                tabs.forEach { (tab, label) ->
                    Tab(selected = state.selectedTab == tab, onClick = { onTab(tab) }, text = { Text(label, maxLines = 1) })
                }
            }
            LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.rows.isEmpty()) {
                    item { EmptyCard(if (state.selectedTab == ExpiryTab.All) "No stocked medicine batches found." else "No medicines in this expiry window.") }
                } else {
                    items(state.rows) { row -> ExpiryCard(row) }
                }
            }
        }
    }
}

@Composable
private fun LowStockScreen(rows: List<StockRow>, onBack: () -> Unit, onStockIn: () -> Unit) {
    Scaffold(
        topBar = { PharmacyTopBar("Low stock", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { StatusNote("Low stock is calculated from persisted batch quantities in Room.") }
            if (rows.isEmpty()) {
                item { EmptyCard("No low-stock medicines right now.") }
            } else {
                items(rows) { row ->
                    InfoCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(row.name, fontWeight = FontWeight.Bold)
                                Text("Batch ${row.batchNo}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StatusBadge(StockFilter.LowStock)
                        }
                        Spacer(Modifier.height(8.dp))
                        KeyValue("Available", row.quantity.toString())
                        KeyValue("Minimum", row.minStock.toString())
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onStockIn, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Stock-in") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalesReportScreen(
    state: com.example.pharmacyinventory.viewmodel.SalesReportUiState,
    onBack: () -> Unit,
    onToday: () -> Unit,
    onMonth: () -> Unit,
    onAll: () -> Unit,
    onExportCsv: (Uri) -> Unit
) {
    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> if (uri != null) onExportCsv(uri) }

    Scaffold(topBar = { PharmacyTopBar("Sales report", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { DateFilterSummary("Selected range", state.range.startMillis.toLocalDateTimeText(), state.range.endMillis.toLocalDateTimeText(), onToday, onMonth, onAll) }
            item {
                MetricGrid(
                    listOf(
                        Metric("Total revenue", money(state.revenue), "Room sales"),
                        Metric("Total profit", money(state.profit), "MRP - purchase"),
                        Metric("Total bills", state.billCount.toString(), "Invoices")
                    )
                )
            }
            item { Button(onClick = { csvLauncher.launch("sales-report.csv") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Export sales CSV") } }
            item { MessageText(state.message) }
            item { SectionTitle("Sales") }
            if (state.rows.isEmpty()) item { EmptyCard("No sales in selected date range.") }
            else items(state.rows) { row -> SaleReportCard(row) }
        }
    }
}

@Composable
private fun PurchaseReportScreen(
    state: com.example.pharmacyinventory.viewmodel.PurchaseReportUiState,
    onBack: () -> Unit,
    onToday: () -> Unit,
    onMonth: () -> Unit,
    onAll: () -> Unit
) {
    Scaffold(topBar = { PharmacyTopBar("Purchase report", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { DateFilterSummary("Selected range", state.range.startMillis.toLocalDateTimeText(), state.range.endMillis.toLocalDateTimeText(), onToday, onMonth, onAll) }
            item {
                MetricGrid(
                    listOf(
                        Metric("Total purchase", money(state.totalPurchase), "Room purchases"),
                        Metric("Purchase bills", state.rows.size.toString(), "Stock-in vouchers")
                    )
                )
            }
            item { Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Export purchase CSV") } }
            if (state.rows.isEmpty()) item { EmptyCard("No purchases in selected date range.") }
            else items(state.rows) { row -> PurchaseReportCard(row) }
        }
    }
}

@Composable
private fun SupplierScreen(
    state: com.example.pharmacyinventory.viewmodel.SupplierUiState,
    onBack: () -> Unit,
    onName: (String) -> Unit,
    onContact: (String) -> Unit,
    onPhone: (String) -> Unit,
    onAddress: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(topBar = { PharmacyTopBar("Suppliers", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                SectionTitle("Add supplier")
                FormTextField("Name", state.name, onName)
                FormTextField("Contact person", state.contact, onContact)
                FormTextField("Phone", state.phone, onPhone)
                FormTextField("Address", state.address, onAddress)
                MessageText(state.message)
                Button(onClick = onSave, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Save supplier") }
            }
            item { SectionTitle("Supplier list") }
            if (state.suppliers.isEmpty()) item { EmptyCard("No suppliers saved yet.") }
            else items(state.suppliers) { supplier -> SupplierCard(supplier) }
        }
    }
}

@Composable
private fun BackupRestoreScreen(
    message: String?,
    syncStatus: String,
    peers: List<LocalPeerDevice>,
    onBack: () -> Unit,
    onExportBackup: (Uri) -> Unit,
    onImportBackup: (Uri) -> Unit,
    onExportStockCsv: (Uri) -> Unit,
    onExportSalesCsv: (Uri) -> Unit,
    onSeed: () -> Unit,
    onSyncPeer: (String) -> Unit
) {
    val backupExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> if (uri != null) onExportBackup(uri) }
    val backupImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) onImportBackup(uri) }
    val stockCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> if (uri != null) onExportStockCsv(uri) }
    val salesCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> if (uri != null) onExportSalesCsv(uri) }

    Scaffold(topBar = { PharmacyTopBar("Backup and restore", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { WarningBox("Local data safety", "Data is stored in the Room database on this phone. Create regular local backups before changing phones or resetting the device.") }
            item { WarningBox("Trusted local sync", "Nearby devices can be discovered, but data is shared only after you tap Trust & sync. For two-way sync, approve the devices on both phones.") }
            item { StatusNote(syncStatus) }
            item { SectionTitle("Nearby devices") }
            if (peers.isEmpty()) {
                item { EmptyCard("No nearby pharmacy devices found. Keep both apps open on the same Wi-Fi, hotspot, or Wi-Fi Direct group.") }
            } else {
                items(peers) { peer -> LocalPeerCard(peer = peer, onSync = { onSyncPeer(peer.deviceId) }) }
            }
            item { BackupAction("Create backup", "Save a copy of the local Room database.", { backupExportLauncher.launch("offline-pharmacy-backup.db") }) }
            item { BackupAction("Restore backup", "Restore from a database backup selected on this device.", { backupImportLauncher.launch(arrayOf("application/octet-stream", "*/*")) }) }
            item { BackupAction("Export stock CSV", "Export current stock from local database.", { stockCsvLauncher.launch("stock-export.csv") }) }
            item { BackupAction("Export sales CSV", "Export sales report from local database.", { salesCsvLauncher.launch("sales-export.csv") }) }
            item { BackupAction("Seed optional demo data", "Adds demo rows only if the database is empty.", onSeed) }
            item { MessageText(message) }
        }
    }
}

@Composable
private fun SettingsScreen(message: String?, onBack: () -> Unit, onSeed: () -> Unit) {
    Scaffold(topBar = { PharmacyTopBar("Settings", onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                InfoCard {
                    Text("Offline settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("No internet permission is used. Room keeps data after closing the app.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item { SettingSwitch("PIN lock", "Ask PIN when app opens", true) }
            item { SettingSwitch("FEFO selling", "First Expiry First Out batch usage", true) }
            item { SettingSwitch("Expiry warning", "Warn before expired stock sale", true) }
            item { BackupAction("Seed optional demo data", "Use only for testing a fresh local database.", onSeed) }
            item { MessageText(message) }
        }
    }
}

@Composable
private fun PharmacyTopBar(title: String, onBack: (() -> Unit)? = null) {
    CenterAlignedTopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold) },
        navigationIcon = { if (onBack != null) TextButton(onClick = onBack) { Text("Back") } },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
private fun DashboardBottomBar(onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(true, { onNavigate(Routes.Dashboard) }, icon = { Text("Home") }, label = { Text("Home") })
        NavigationBarItem(false, { onNavigate(Routes.StockList) }, icon = { Text("Stock") }, label = { Text("Stock") })
        NavigationBarItem(false, { onNavigate(Routes.SellMedicine) }, icon = { Text("Sell") }, label = { Text("Sell") })
        NavigationBarItem(false, { onNavigate(Routes.Settings) }, icon = { Text("Set") }, label = { Text("Settings") })
    }
}

private data class Metric(val title: String, val value: String, val caption: String)

@Composable
private fun MetricGrid(metrics: List<Metric>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        metrics.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { metric ->
                    InfoCard(modifier = Modifier.weight(1f).height(104.dp)) {
                        Text(metric.title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(metric.value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 1)
                        Spacer(Modifier.height(4.dp))
                        Text(metric.caption, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionGrid(actions: List<Pair<String, String>>, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
        actions.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { action ->
                    OutlinedButton(
                        onClick = { onNavigate(action.second) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text(action.first, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FormScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(topBar = { PharmacyTopBar(title, onBack) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
            }
        }
    }
}

@Composable
private fun FormTextField(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), content = content)
    }
}

@Composable
private fun EmptyCard(message: String) {
    InfoCard {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatusNote(message: String) {
    InfoCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Text(message, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun MessageText(message: String?) {
    if (!message.isNullOrBlank()) {
        Text(message, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun WarningText(message: String) {
    Text(message, color = expiredRed, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun WarningBox(title: String, message: String) {
    InfoCard(containerColor = Color(0xFFFFF4E5)) {
        Text(title, color = expiringOrange, fontWeight = FontWeight.Bold)
        Text(message, color = Color(0xFF6B3F00))
    }
}

@Composable
private fun KeyValue(label: String, value: String, strong: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = if (strong) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.End)
    }
}

@Composable
private fun ChipRow(labels: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
    ) {
        labels.forEach { label ->
            FilterChip(selected = selected == label, onClick = { onSelected(label) }, label = { Text(label, maxLines = 1) })
        }
    }
}

@Composable
private fun <T> FilterChipRow(values: List<T>, selected: T, label: (T) -> String, onSelected: (T) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
    ) {
        values.forEach { value ->
            FilterChip(selected = selected == value, onClick = { onSelected(value) }, label = { Text(label(value), maxLines = 1) })
        }
    }
}

@Composable
private fun StatusBadge(status: StockFilter) {
    val color = when (status) {
        StockFilter.All -> MaterialTheme.colorScheme.primary
        StockFilter.Safe -> safeGreen
        StockFilter.LowStock -> lowStockAmber
        StockFilter.ExpiringSoon -> expiringOrange
        StockFilter.Expired -> expiredRed
    }
    Surface(color = color.copy(alpha = 0.12f), contentColor = color, shape = RoundedCornerShape(50)) {
        Text(status.label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

private val StockFilter.label: String
    get() = when (this) {
        StockFilter.All -> "All"
        StockFilter.Safe -> "Safe"
        StockFilter.LowStock -> "Low Stock"
        StockFilter.ExpiringSoon -> "Expiring Soon"
        StockFilter.Expired -> "Expired"
    }

@Composable
private fun StockCard(row: StockRow, onClick: () -> Unit) {
    InfoCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(row.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(row.company, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(stockFilterFor(row))
        }
        Spacer(Modifier.height(10.dp))
        KeyValue("Batch", row.batchNo)
        KeyValue("Expiry", row.expiryEpochDay.toLocalDate().toString())
        KeyValue("Quantity", row.quantity.toString())
        KeyValue("Price", money(row.mrp))
    }
}

@Composable
private fun StockMiniCard(row: StockRow, onClick: () -> Unit) {
    InfoCard(modifier = Modifier.clickable(onClick = onClick), containerColor = Color(0xFFFFF4E5)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(row.name, fontWeight = FontWeight.Bold)
                Text("Batch ${row.batchNo}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("${row.expiryEpochDay - LocalDate.now().toEpochDay()} days", color = expiringOrange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BatchCard(medicine: MedicineEntity, batch: BatchEntity) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(batch.batchNo, fontWeight = FontWeight.Bold)
                Text(medicine.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Qty ${batch.quantity}", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        KeyValue("Expiry", batch.expiryEpochDay.toLocalDate().toString())
        KeyValue("MRP", money(batch.mrp))
        KeyValue("Purchase", money(batch.purchasePrice))
    }
}

@Composable
private fun BatchSelectCard(row: StockRow, selected: Boolean, onClick: () -> Unit) {
    val expired = row.expiryEpochDay < LocalDate.now().toEpochDay()
    val container = when {
        selected -> MaterialTheme.colorScheme.primaryContainer
        expired -> Color(0xFFFFEDEA)
        else -> MaterialTheme.colorScheme.surface
    }
    InfoCard(modifier = Modifier.clickable(onClick = onClick), containerColor = container) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(row.name, fontWeight = FontWeight.Bold)
                Text("Batch ${row.batchNo}  Exp ${row.expiryEpochDay.toLocalDate()}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Qty ${row.quantity}", fontWeight = FontWeight.SemiBold)
                Text(money(row.mrp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (expired) WarningText("Expired. Confirmation required before sale.")
    }
}

@Composable
private fun QuantitySelector(quantity: Int, onChange: (Int) -> Unit) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Quantity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { onChange(quantity - 1) }, shape = RoundedCornerShape(8.dp)) { Text("-") }
                Text(quantity.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Button(onClick = { onChange(quantity + 1) }, shape = RoundedCornerShape(8.dp)) { Text("+") }
            }
        }
    }
}

@Composable
private fun CartSummary(cartLines: List<CartLine>, onOpenCart: () -> Unit) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Cart summary", fontWeight = FontWeight.Bold)
                Text("${cartLines.sumOf { it.requestedQuantity }} items  ${money(cartLines.sumOf { it.lineTotal })}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = onOpenCart, enabled = cartLines.isNotEmpty(), shape = RoundedCornerShape(8.dp)) { Text("Bill") }
        }
    }
}

@Composable
private fun CartLineCard(line: CartLine, onRemove: () -> Unit) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(line.medicineName, fontWeight = FontWeight.Bold)
                Text("Qty ${line.requestedQuantity}  ${line.batchNo ?: "FEFO"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(money(line.lineTotal), fontWeight = FontWeight.Bold)
                TextButton(onClick = onRemove) { Text("Remove") }
            }
        }
    }
}

@Composable
private fun ExpiryCard(row: StockRow) {
    val days = row.expiryEpochDay - LocalDate.now().toEpochDay()
    val expired = days < 0
    InfoCard(containerColor = if (expired) Color(0xFFFFEDEA) else Color(0xFFFFF4E5)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(row.name, fontWeight = FontWeight.Bold)
                Text("${row.company}  Batch ${row.batchNo}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(if (expired) "Expired" else "$days days", color = if (expired) expiredRed else expiringOrange, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        KeyValue("Expiry", row.expiryEpochDay.toLocalDate().toString())
        KeyValue("Quantity", row.quantity.toString())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Remove") }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Return") }
            Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Edit") }
        }
    }
}

@Composable
private fun RecentSaleCard(sale: SaleWithItems) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(sale.sale.billNo, fontWeight = FontWeight.Bold)
                Text(sale.items.joinToString { it.medicineName }.ifBlank { "No items" }, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(money(sale.sale.total), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DateFilterSummary(
    label: String,
    from: String,
    to: String,
    onToday: () -> Unit,
    onMonth: () -> Unit,
    onAll: () -> Unit
) {
    InfoCard {
        Text("Date filters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("$label: $from to $to", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onToday, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Today") }
            OutlinedButton(onClick = onMonth, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Month") }
            OutlinedButton(onClick = onAll, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("All") }
        }
    }
}

@Composable
private fun SaleReportCard(row: SaleReportRow) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(row.billNo, fontWeight = FontWeight.Bold)
                Text("${row.customerName}  ${row.soldAtMillis.toLocalDateTimeText()}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(money(row.total), fontWeight = FontWeight.Bold)
                Text("Profit ${money(row.profit)}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PurchaseReportCard(row: PurchaseReportRow) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(row.voucherNo, fontWeight = FontWeight.Bold)
                Text("${row.supplierName}  ${row.purchasedAtMillis.toLocalDateTimeText()}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(money(row.total), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SupplierCard(supplier: SupplierEntity) {
    InfoCard {
        Text(supplier.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        KeyValue("Contact", supplier.contactPerson.ifBlank { "-" })
        KeyValue("Phone", supplier.phone.ifBlank { "-" })
        KeyValue("Address", supplier.address.ifBlank { "-" })
        KeyValue("Balance", money(supplier.balance))
    }
}

@Composable
private fun BackupAction(title: String, body: String, onClick: () -> Unit) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = onClick, shape = RoundedCornerShape(8.dp)) { Text("Start") }
        }
    }
}

@Composable
private fun PasswordTextField(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun SubscriptionPlanCard(plan: SubscriptionPlan, selected: Boolean, onClick: () -> Unit) {
    InfoCard(
        modifier = Modifier.clickable(onClick = onClick),
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(plan.label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(plan.devices, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(plan.price, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RegisteredDeviceCard(device: RegisteredDevice, onRemove: () -> Unit) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(device.deviceName, fontWeight = FontWeight.Bold)
                Text(device.deviceId.take(16), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Registered: ${device.registeredAt ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Last login: ${device.lastLoginAt ?: device.lastSeenAt ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Status: ${device.status ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = onRemove, shape = RoundedCornerShape(8.dp)) { Text("Remove") }
        }
    }
}

@Composable
private fun AdminDashboardCard(dashboard: AdminDashboardResponse?) {
    val data = dashboard ?: return
    InfoCard {
        Text("Dashboard", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        KeyValue("Total shops", data.totalShops.toString())
        KeyValue("Active shops", data.activeShops.toString())
        KeyValue("Expired shops", data.expiredShops.toString())
        KeyValue("Suspended shops", data.suspendedShops.toString())
        KeyValue("Monthly revenue", money(data.monthlyRevenue))
        KeyValue("Device usage", data.deviceUsage.toString())
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Text("Plan-wise shops", fontWeight = FontWeight.SemiBold)
        data.planWiseShops.forEach { (plan, count) -> KeyValue(plan, count.toString()) }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Text("Recent renewals", fontWeight = FontWeight.SemiBold)
        data.recentRenewals.take(5).forEach { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        Text("Payment failures", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        data.paymentFailures.take(5).forEach { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun LaunchRazorpayIfReady(payment: PaymentStartResponse?) {
    val context = LocalContext.current
    LaunchedEffect(payment?.shortUrl, payment?.checkoutSubscriptionId(), payment?.razorpayOrderId) {
        val currentPayment = payment ?: return@LaunchedEffect
        val activity = context.findActivity() ?: return@LaunchedEffect
        if (!currentPayment.shortUrl.isNullOrBlank()) {
            runCatching {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(currentPayment.shortUrl)))
            }
            return@LaunchedEffect
        }
        val checkout = Checkout()
        checkout.setKeyID(currentPayment.checkoutKeyId().ifBlank { BuildConfig.RAZORPAY_KEY_ID })
        val options = JSONObject()
            .put("name", "MediStock Offline")
            .put("description", "${currentPayment.plan ?: "Pharmacy"} subscription")
            .put("currency", currentPayment.currency)
        currentPayment.amount?.let { options.put("amount", it) }
        currentPayment.razorpayOrderId?.let { options.put("order_id", it) }
        currentPayment.checkoutSubscriptionId()?.let { options.put("subscription_id", it) }
        checkout.open(activity, options)
    }
}

private fun android.content.Context.findActivity(): Activity? {
    var current = this
    while (current is android.content.ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

@Composable
private fun LocalPeerCard(peer: LocalPeerDevice, onSync: () -> Unit) {
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(peer.name, fontWeight = FontWeight.Bold)
                Text(
                    "ID ${peer.deviceId.take(8)}  ${if (peer.trusted) "Trusted" else "Not trusted"}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onSync, shape = RoundedCornerShape(8.dp)) {
                Text(if (peer.trusted) "Sync now" else "Trust & sync")
            }
        }
    }
}

@Composable
private fun SettingSwitch(title: String, subtitle: String, initial: Boolean) {
    var checked by remember { mutableStateOf(initial) }
    InfoCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = { checked = it })
        }
    }
}

private fun money(value: Double): String = "Rs. %.2f".format(value)
