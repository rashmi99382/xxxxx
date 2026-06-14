package com.example.pharmacyinventory.data

import androidx.room.Embedded
import androidx.room.Relation
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class MedicineWithBatches(
    @Embedded val medicine: MedicineEntity,
    @Relation(parentColumn = "id", entityColumn = "medicineId")
    val batches: List<BatchEntity>
)

data class SaleWithItems(
    @Embedded val sale: SaleEntity,
    @Relation(parentColumn = "id", entityColumn = "saleId")
    val items: List<SaleItemEntity>
)

data class PurchaseWithItems(
    @Embedded val purchase: PurchaseEntity,
    @Relation(parentColumn = "id", entityColumn = "purchaseId")
    val items: List<PurchaseItemEntity>
)

data class StockRow(
    val medicineId: Long,
    val batchId: Long,
    val name: String,
    val company: String,
    val genericName: String,
    val category: String,
    val supplierName: String?,
    val minStock: Int,
    val batchNo: String,
    val expiryEpochDay: Long,
    val quantity: Int,
    val purchasePrice: Double,
    val mrp: Double
)

data class SaleReportRow(
    val saleId: Long,
    val billNo: String,
    val soldAtMillis: Long,
    val customerName: String,
    val paymentMode: String,
    val total: Double,
    val profit: Double,
    val itemCount: Int
)

data class PurchaseReportRow(
    val purchaseId: Long,
    val voucherNo: String,
    val purchasedAtMillis: Long,
    val supplierName: String,
    val total: Double,
    val itemCount: Int
)

data class DashboardMetrics(
    val medicineCount: Int = 0,
    val todaySales: Double = 0.0,
    val monthlySales: Double = 0.0,
    val lowStockCount: Int = 0,
    val expiredCount: Int = 0,
    val expiringSoonCount: Int = 0
)

data class CartLine(
    val medicineId: Long,
    val medicineName: String,
    val requestedQuantity: Int,
    val selectedBatchId: Long?,
    val batchNo: String?,
    val expiryEpochDay: Long?,
    val mrp: Double,
    val availableQuantity: Int
) {
    val lineTotal: Double = requestedQuantity * mrp
}

data class SaleRequestLine(
    val medicineId: Long,
    val medicineName: String,
    val quantity: Int
)

data class SaleResult(
    val saleId: Long,
    val billNo: String,
    val total: Double,
    val profit: Double,
    val hadExpiredWarning: Boolean
)

data class StockInRequest(
    val medicineId: Long,
    val supplierId: Long?,
    val supplierName: String,
    val batchNo: String,
    val expiryEpochDay: Long,
    val quantity: Int,
    val purchasePrice: Double,
    val mrp: Double,
    val voucherNo: String,
    val note: String = ""
)

data class DateRange(
    val startMillis: Long,
    val endMillis: Long
) {
    companion object {
        fun currentMonth(today: LocalDate = LocalDate.now()): DateRange {
            val zone = ZoneId.systemDefault()
            val start = today.withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
            return DateRange(start, end)
        }

        fun today(today: LocalDate = LocalDate.now()): DateRange {
            val zone = ZoneId.systemDefault()
            val start = today.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
            return DateRange(start, end)
        }
    }
}

fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)

fun Long.toLocalDateTimeText(): String {
    val instant = Instant.ofEpochMilli(this)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthValue.toString().padStart(2, '0')}/${date.year}"
}
