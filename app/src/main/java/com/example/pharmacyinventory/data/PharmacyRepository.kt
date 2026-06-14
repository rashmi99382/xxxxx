package com.example.pharmacyinventory.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class PharmacyRepository(private val database: AppDatabase) {
    private val medicineDao = database.medicineDao()
    private val batchDao = database.batchDao()
    private val supplierDao = database.supplierDao()
    private val saleDao = database.saleDao()
    private val purchaseDao = database.purchaseDao()
    private val stockAdjustmentDao = database.stockAdjustmentDao()

    fun observeMedicines(): Flow<List<MedicineEntity>> = medicineDao.observeAll()

    fun searchMedicines(query: String): Flow<List<MedicineEntity>> = medicineDao.search(query.trim())

    fun observeSuppliers(): Flow<List<SupplierEntity>> = supplierDao.observeAll()

    fun observeMedicineDetail(medicineId: Long): Flow<MedicineWithBatches?> {
        return medicineDao.observeMedicineWithBatches(medicineId)
    }

    fun observeStockRows(): Flow<List<StockRow>> = batchDao.observeStockRows()

    fun observeAvailableStockRows(query: String): Flow<List<StockRow>> {
        return batchDao.observeAvailableStockRows(query.trim())
    }

    fun observeLowStockRows(): Flow<List<StockRow>> {
        return observeStockRows().map { rows ->
            rows.groupBy { it.medicineId }
                .filter { (_, batches) ->
                    batches.sumOf { it.quantity } <= batches.first().minStock
                }
                .values
                .flatten()
                .sortedWith(compareBy<StockRow> { it.name.lowercase() }.thenBy { it.expiryEpochDay })
        }
    }

    fun observeExpiryRows(tab: ExpiryTab, today: LocalDate = LocalDate.now()): Flow<List<StockRow>> {
        val todayDay = today.toEpochDay()
        return observeStockRows().map { rows ->
            rows.filter { row ->
                row.quantity > 0 && (tab == ExpiryTab.All || expiryTabFor(row.expiryEpochDay, todayDay) == tab)
            }.sortedBy { it.expiryEpochDay }
        }
    }

    fun observeDashboardMetrics(today: LocalDate = LocalDate.now()): Flow<DashboardMetrics> {
        val todayRange = DateRange.today(today)
        val monthRange = DateRange.currentMonth(today)
        val todayDay = today.toEpochDay()

        return combine(
            medicineDao.observeMedicineCount(),
            saleDao.observeRevenue(todayRange.startMillis, todayRange.endMillis),
            saleDao.observeRevenue(monthRange.startMillis, monthRange.endMillis),
            observeStockRows()
        ) { medicineCount, todaySales, monthlySales, rows ->
            val lowStock = rows.groupBy { it.medicineId }
                .count { (_, batches) -> batches.sumOf { it.quantity } <= batches.first().minStock }
            val expired = rows.count { it.expiryEpochDay < todayDay && it.quantity > 0 }
            val expiringSoon = rows.count { (it.expiryEpochDay - todayDay) in 0..30 && it.quantity > 0 }
            DashboardMetrics(
                medicineCount = medicineCount,
                todaySales = todaySales,
                monthlySales = monthlySales,
                lowStockCount = lowStock,
                expiredCount = expired,
                expiringSoonCount = expiringSoon
            )
        }
    }

    fun observeRecentSales(limit: Int = 5): Flow<List<SaleWithItems>> = saleDao.observeRecentSales(limit)

    fun observeSaleWithItems(saleId: Long): Flow<SaleWithItems?> = saleDao.observeSaleWithItems(saleId)

    fun observeSalesReport(range: DateRange): Flow<List<SaleReportRow>> {
        return saleDao.observeReport(range.startMillis, range.endMillis)
    }

    fun observeSalesRevenue(range: DateRange): Flow<Double> {
        return saleDao.observeRevenue(range.startMillis, range.endMillis)
    }

    fun observeSalesProfit(range: DateRange): Flow<Double> {
        return saleDao.observeProfit(range.startMillis, range.endMillis)
    }

    fun observeSalesBillCount(range: DateRange): Flow<Int> {
        return saleDao.observeBillCount(range.startMillis, range.endMillis)
    }

    fun observePurchaseReport(range: DateRange): Flow<List<PurchaseReportRow>> {
        return purchaseDao.observeReport(range.startMillis, range.endMillis)
    }

    suspend fun addMedicine(
        name: String,
        company: String,
        genericName: String,
        category: String,
        supplierName: String,
        minStock: Int
    ): Long {
        require(name.isNotBlank()) { "Medicine name is required." }
        require(minStock >= 0) { "Low stock quantity cannot be negative." }
        val supplierId = findOrCreateSupplierId(supplierName)
        return medicineDao.insert(
            MedicineEntity(
                name = name.trim(),
                company = company.trim(),
                genericName = genericName.trim(),
                category = category.ifBlank { "General" }.trim(),
                supplierId = supplierId,
                minStock = minStock
            )
        )
    }

    suspend fun addSupplier(
        name: String,
        contactPerson: String,
        phone: String,
        address: String
    ): Long {
        require(name.isNotBlank()) { "Supplier name is required." }
        return supplierDao.insert(
            SupplierEntity(
                name = name.trim(),
                contactPerson = contactPerson.trim(),
                phone = phone.trim(),
                address = address.trim()
            )
        )
    }

    suspend fun stockIn(request: StockInRequest): Long {
        require(request.quantity > 0) { "Stock-in quantity must be greater than zero." }
        require(request.purchasePrice >= 0.0) { "Purchase price cannot be negative." }
        require(request.mrp >= 0.0) { "MRP cannot be negative." }
        require(request.batchNo.isNotBlank()) { "Batch number is required." }

        return database.withTransaction {
            val medicine = medicineDao.getById(request.medicineId)
                ?: throw IllegalArgumentException("Medicine not found.")
            val supplierId = request.supplierId ?: findOrCreateSupplierId(request.supplierName)
            val batchId = batchDao.insert(
                BatchEntity(
                    medicineId = request.medicineId,
                    supplierId = supplierId,
                    batchNo = request.batchNo.trim().uppercase(),
                    expiryEpochDay = request.expiryEpochDay,
                    quantity = request.quantity,
                    purchasePrice = request.purchasePrice,
                    mrp = request.mrp
                )
            )
            val lineTotal = request.quantity * request.purchasePrice
            val purchaseId = purchaseDao.insertPurchase(
                PurchaseEntity(
                    voucherNo = request.voucherNo.ifBlank { "P-${System.currentTimeMillis()}" },
                    supplierId = supplierId,
                    supplierName = request.supplierName.ifBlank { "Unknown supplier" },
                    purchasedAtMillis = System.currentTimeMillis(),
                    total = lineTotal,
                    note = request.note
                )
            )
            purchaseDao.insertItems(
                listOf(
                    PurchaseItemEntity(
                        purchaseId = purchaseId,
                        medicineId = medicine.id,
                        batchId = batchId,
                        medicineName = medicine.name,
                        batchNo = request.batchNo.trim().uppercase(),
                        expiryEpochDay = request.expiryEpochDay,
                        quantity = request.quantity,
                        purchasePrice = request.purchasePrice,
                        mrp = request.mrp,
                        lineTotal = lineTotal
                    )
                )
            )
            stockAdjustmentDao.insert(
                StockAdjustmentEntity(
                    medicineId = medicine.id,
                    batchId = batchId,
                    quantityDelta = request.quantity,
                    reason = "Stock-in",
                    referenceType = "purchase",
                    referenceId = purchaseId
                )
            )
            purchaseId
        }
    }

    suspend fun createSale(
        customerName: String,
        paymentMode: String,
        lines: List<SaleRequestLine>,
        allowExpired: Boolean = false,
        today: LocalDate = LocalDate.now()
    ): SaleResult {
        require(lines.isNotEmpty()) { "Add at least one item to bill." }
        require(lines.all { it.quantity > 0 }) { "Sale quantity must be greater than zero." }
        val groupedLines = lines
            .groupBy { it.medicineId }
            .map { (medicineId, grouped) ->
                SaleRequestLine(
                    medicineId = medicineId,
                    medicineName = grouped.first().medicineName,
                    quantity = grouped.sumOf { it.quantity }
                )
            }

        return database.withTransaction {
            val now = System.currentTimeMillis()
            val todayEpochDay = today.toEpochDay()
            val plannedItems = mutableListOf<SaleItemEntity>()
            var saleTotal = 0.0
            var saleProfit = 0.0
            var hadExpiredWarning = false

            groupedLines.forEach { request ->
                val sellableBatches = batchDao.getFefoBatchesForSale(
                    medicineId = request.medicineId,
                    todayEpochDay = todayEpochDay,
                    includeExpired = allowExpired
                )
                val nonExpiredAvailable = sellableBatches.filter { it.expiryEpochDay >= todayEpochDay }
                if (!allowExpired && nonExpiredAvailable.sumOf { it.quantity } < request.quantity) {
                    val allStock = batchDao.getFefoBatchesForSale(
                        medicineId = request.medicineId,
                        todayEpochDay = todayEpochDay,
                        includeExpired = true
                    )
                    if (allStock.sumOf { it.quantity } >= request.quantity && allStock.any { it.expiryEpochDay < todayEpochDay }) {
                        throw ExpiredStockRequiresConfirmationException(
                            "${request.medicineName} has expired stock. Confirm before selling expired medicine."
                        )
                    }
                }

                var remaining = request.quantity
                sellableBatches.forEach { batch ->
                    if (remaining <= 0) return@forEach
                    val take = minOf(remaining, batch.quantity)
                    val medicine = medicineDao.getById(batch.medicineId)
                        ?: throw IllegalStateException("Medicine missing for batch ${batch.batchNo}.")
                    val wasExpired = batch.expiryEpochDay < todayEpochDay
                    if (wasExpired) hadExpiredWarning = true
                    val lineTotal = take * batch.mrp
                    val lineProfit = take * (batch.mrp - batch.purchasePrice)
                    plannedItems += SaleItemEntity(
                        saleId = 0,
                        medicineId = medicine.id,
                        batchId = batch.id,
                        medicineName = medicine.name,
                        batchNo = batch.batchNo,
                        expiryEpochDay = batch.expiryEpochDay,
                        quantity = take,
                        mrp = batch.mrp,
                        purchasePrice = batch.purchasePrice,
                        lineTotal = lineTotal,
                        lineProfit = lineProfit,
                        wasExpired = wasExpired
                    )
                    saleTotal += lineTotal
                    saleProfit += lineProfit
                    remaining -= take
                }
                if (remaining > 0) {
                    throw InsufficientStockException(
                        "${request.medicineName} has only ${request.quantity - remaining} units available."
                    )
                }
            }

            val billNo = "B-$now"
            val saleId = saleDao.insertSale(
                SaleEntity(
                    billNo = billNo,
                    soldAtMillis = now,
                    customerName = customerName.ifBlank { "Walk-in customer" },
                    paymentMode = paymentMode.ifBlank { "Cash" },
                    subtotal = saleTotal,
                    total = saleTotal,
                    profit = saleProfit,
                    hadExpiredWarning = hadExpiredWarning
                )
            )
            val saleItems = plannedItems.map { it.copy(saleId = saleId) }
            saleDao.insertItems(saleItems)

            saleItems.forEach { item ->
                val changed = batchDao.decrementQuantity(item.batchId, item.quantity, now)
                if (changed != 1) {
                    throw InsufficientStockException("${item.medicineName} stock changed. Please retry sale.")
                }
                stockAdjustmentDao.insert(
                    StockAdjustmentEntity(
                        medicineId = item.medicineId,
                        batchId = item.batchId,
                        quantityDelta = -item.quantity,
                        reason = "Sale ${billNo}",
                        referenceType = "sale",
                        referenceId = saleId,
                        createdAtMillis = now
                    )
                )
            }

            SaleResult(
                saleId = saleId,
                billNo = billNo,
                total = saleTotal,
                profit = saleProfit,
                hadExpiredWarning = hadExpiredWarning
            )
        }
    }

    suspend fun seedDemoDataIfEmpty() {
        if (medicineDao.countNow() > 0) return
        database.withTransaction {
            val supplierId = supplierDao.insert(
                SupplierEntity(
                    name = "City Pharma Distributors",
                    contactPerson = "Neeraj Jain",
                    phone = "98765 43210",
                    address = "Main Market Road"
                )
            )
            val today = LocalDate.now()
            val paracetamol = medicineDao.insert(
                MedicineEntity(
                    name = "Paracetamol 500",
                    company = "CalCare Labs",
                    genericName = "Paracetamol",
                    category = "Tablet",
                    supplierId = supplierId,
                    minStock = 20
                )
            )
            val amoxicillin = medicineDao.insert(
                MedicineEntity(
                    name = "Amoxicillin 250",
                    company = "HealthCure",
                    genericName = "Amoxicillin",
                    category = "Capsule",
                    supplierId = supplierId,
                    minStock = 15
                )
            )
            batchDao.insert(
                BatchEntity(
                    medicineId = paracetamol,
                    supplierId = supplierId,
                    batchNo = "PCM24A",
                    expiryEpochDay = today.plusDays(120).toEpochDay(),
                    quantity = 86,
                    purchasePrice = 9.2,
                    mrp = 15.0
                )
            )
            batchDao.insert(
                BatchEntity(
                    medicineId = paracetamol,
                    supplierId = supplierId,
                    batchNo = "PCM23X",
                    expiryEpochDay = today.plusDays(18).toEpochDay(),
                    quantity = 14,
                    purchasePrice = 8.7,
                    mrp = 14.0
                )
            )
            batchDao.insert(
                BatchEntity(
                    medicineId = amoxicillin,
                    supplierId = supplierId,
                    batchNo = "AMX80",
                    expiryEpochDay = today.minusDays(12).toEpochDay(),
                    quantity = 4,
                    purchasePrice = 36.0,
                    mrp = 55.0
                )
            )
        }
    }

    private suspend fun findOrCreateSupplierId(name: String): Long? {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return null
        supplierDao.getByName(cleanName)?.let { return it.id }
        val inserted = supplierDao.insert(
            SupplierEntity(
                name = cleanName,
                contactPerson = "",
                phone = "",
                address = ""
            )
        )
        if (inserted > 0) return inserted
        return supplierDao.getByName(cleanName)?.id
    }
}

enum class ExpiryTab {
    All,
    Expired,
    SevenDays,
    ThirtyDays,
    NinetyDays
}

fun daysUntilExpiry(expiryEpochDay: Long, today: LocalDate = LocalDate.now()): Long {
    return expiryEpochDay - today.toEpochDay()
}

fun expiryTabFor(expiryEpochDay: Long, todayEpochDay: Long = LocalDate.now().toEpochDay()): ExpiryTab? {
    val days = expiryEpochDay - todayEpochDay
    return when {
        days < 0 -> ExpiryTab.Expired
        days in 0..7 -> ExpiryTab.SevenDays
        days in 8..30 -> ExpiryTab.ThirtyDays
        days in 31..90 -> ExpiryTab.NinetyDays
        else -> null
    }
}

class InsufficientStockException(message: String) : IllegalStateException(message)

class ExpiredStockRequiresConfirmationException(message: String) : IllegalStateException(message)

fun LocalDate.toStartOfDayMillis(): Long {
    return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
