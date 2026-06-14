package com.example.pharmacyinventory.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicines",
    indices = [
        Index("name"),
        Index("category"),
        Index("supplierId")
    ]
)
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String,
    val genericName: String,
    val category: String,
    val supplierId: Long?,
    val minStock: Int,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "batches",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("medicineId"),
        Index("supplierId"),
        Index("batchNo"),
        Index("expiryEpochDay")
    ]
)
data class BatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val supplierId: Long?,
    val batchNo: String,
    val expiryEpochDay: Long,
    val quantity: Int,
    val purchasePrice: Double,
    val mrp: Double,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "suppliers",
    indices = [
        Index(value = ["name"], unique = true),
        Index("phone")
    ]
)
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contactPerson: String,
    val phone: String,
    val address: String,
    val balance: Double = 0.0,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sales",
    indices = [
        Index(value = ["billNo"], unique = true),
        Index("soldAtMillis")
    ]
)
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val billNo: String,
    val soldAtMillis: Long,
    val customerName: String,
    val paymentMode: String,
    val subtotal: Double,
    val total: Double,
    val profit: Double,
    val hadExpiredWarning: Boolean,
    val note: String = ""
)

@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("saleId"),
        Index("medicineId"),
        Index("batchId")
    ]
)
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long,
    val medicineId: Long,
    val batchId: Long,
    val medicineName: String,
    val batchNo: String,
    val expiryEpochDay: Long,
    val quantity: Int,
    val mrp: Double,
    val purchasePrice: Double,
    val lineTotal: Double,
    val lineProfit: Double,
    val wasExpired: Boolean
)

@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["voucherNo"], unique = true),
        Index("supplierId"),
        Index("purchasedAtMillis")
    ]
)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val voucherNo: String,
    val supplierId: Long?,
    val supplierName: String,
    val purchasedAtMillis: Long,
    val total: Double,
    val note: String = ""
)

@Entity(
    tableName = "purchase_items",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["purchaseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("purchaseId"),
        Index("medicineId"),
        Index("batchId")
    ]
)
data class PurchaseItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchaseId: Long,
    val medicineId: Long,
    val batchId: Long,
    val medicineName: String,
    val batchNo: String,
    val expiryEpochDay: Long,
    val quantity: Int,
    val purchasePrice: Double,
    val mrp: Double,
    val lineTotal: Double
)

@Entity(
    tableName = "stock_adjustments",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("medicineId"),
        Index("batchId"),
        Index("createdAtMillis")
    ]
)
data class StockAdjustmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val batchId: Long,
    val quantityDelta: Int,
    val reason: String,
    val referenceType: String,
    val referenceId: Long?,
    val createdAtMillis: Long = System.currentTimeMillis()
)
