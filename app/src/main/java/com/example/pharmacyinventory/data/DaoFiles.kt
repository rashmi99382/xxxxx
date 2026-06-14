package com.example.pharmacyinventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Insert
    suspend fun insert(medicine: MedicineEntity): Long

    @Update
    suspend fun update(medicine: MedicineEntity)

    @Query("SELECT * FROM medicines ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<MedicineEntity>>

    @Query(
        """
        SELECT * FROM medicines
        WHERE :query = ''
           OR name LIKE '%' || :query || '%'
           OR company LIKE '%' || :query || '%'
           OR genericName LIKE '%' || :query || '%'
        ORDER BY name COLLATE NOCASE
        """
    )
    fun search(query: String): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :medicineId LIMIT 1")
    suspend fun getById(medicineId: Long): MedicineEntity?

    @Transaction
    @Query("SELECT * FROM medicines WHERE id = :medicineId LIMIT 1")
    fun observeMedicineWithBatches(medicineId: Long): Flow<MedicineWithBatches?>

    @Query("SELECT COUNT(*) FROM medicines")
    fun observeMedicineCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM medicines")
    suspend fun countNow(): Int

    @Query("SELECT * FROM medicines ORDER BY name COLLATE NOCASE")
    suspend fun getAllNow(): List<MedicineEntity>

    @Query(
        """
        SELECT * FROM medicines
        WHERE name = :name
          AND company = :company
          AND genericName = :genericName
        LIMIT 1
        """
    )
    suspend fun getByNaturalKey(name: String, company: String, genericName: String): MedicineEntity?
}

@Dao
interface BatchDao {
    @Insert
    suspend fun insert(batch: BatchEntity): Long

    @Update
    suspend fun update(batch: BatchEntity)

    @Query("SELECT * FROM batches WHERE id = :batchId LIMIT 1")
    suspend fun getById(batchId: Long): BatchEntity?

    @Query("SELECT * FROM batches ORDER BY expiryEpochDay ASC, id ASC")
    suspend fun getAllNow(): List<BatchEntity>

    @Query(
        """
        SELECT * FROM batches
        WHERE medicineId = :medicineId
          AND batchNo = :batchNo
          AND expiryEpochDay = :expiryEpochDay
        LIMIT 1
        """
    )
    suspend fun getByNaturalKey(medicineId: Long, batchNo: String, expiryEpochDay: Long): BatchEntity?

    @Query(
        """
        SELECT * FROM batches
        WHERE medicineId = :medicineId
          AND quantity > 0
          AND (:includeExpired = 1 OR expiryEpochDay >= :todayEpochDay)
        ORDER BY expiryEpochDay ASC, id ASC
        """
    )
    suspend fun getFefoBatchesForSale(
        medicineId: Long,
        todayEpochDay: Long,
        includeExpired: Boolean
    ): List<BatchEntity>

    @Query(
        """
        UPDATE batches
        SET quantity = quantity - :quantity,
            updatedAtMillis = :updatedAtMillis
        WHERE id = :batchId
          AND quantity >= :quantity
        """
    )
    suspend fun decrementQuantity(
        batchId: Long,
        quantity: Int,
        updatedAtMillis: Long = System.currentTimeMillis()
    ): Int

    @Query(
        """
        UPDATE batches
        SET quantity = quantity + :quantity,
            updatedAtMillis = :updatedAtMillis
        WHERE id = :batchId
        """
    )
    suspend fun incrementQuantity(
        batchId: Long,
        quantity: Int,
        updatedAtMillis: Long = System.currentTimeMillis()
    ): Int

    @Query(
        """
        SELECT
            m.id AS medicineId,
            b.id AS batchId,
            m.name AS name,
            m.company AS company,
            m.genericName AS genericName,
            m.category AS category,
            s.name AS supplierName,
            m.minStock AS minStock,
            b.batchNo AS batchNo,
            b.expiryEpochDay AS expiryEpochDay,
            b.quantity AS quantity,
            b.purchasePrice AS purchasePrice,
            b.mrp AS mrp
        FROM batches b
        INNER JOIN medicines m ON m.id = b.medicineId
        LEFT JOIN suppliers s ON s.id = b.supplierId
        ORDER BY m.name COLLATE NOCASE, b.expiryEpochDay ASC
        """
    )
    fun observeStockRows(): Flow<List<StockRow>>

    @Query(
        """
        SELECT
            m.id AS medicineId,
            b.id AS batchId,
            m.name AS name,
            m.company AS company,
            m.genericName AS genericName,
            m.category AS category,
            s.name AS supplierName,
            m.minStock AS minStock,
            b.batchNo AS batchNo,
            b.expiryEpochDay AS expiryEpochDay,
            b.quantity AS quantity,
            b.purchasePrice AS purchasePrice,
            b.mrp AS mrp
        FROM batches b
        INNER JOIN medicines m ON m.id = b.medicineId
        LEFT JOIN suppliers s ON s.id = b.supplierId
        WHERE b.quantity > 0
          AND (:query = ''
              OR m.name LIKE '%' || :query || '%'
              OR m.company LIKE '%' || :query || '%'
              OR b.batchNo LIKE '%' || :query || '%')
        ORDER BY m.name COLLATE NOCASE, b.expiryEpochDay ASC
        """
    )
    fun observeAvailableStockRows(query: String): Flow<List<StockRow>>
}

@Dao
interface SupplierDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(supplier: SupplierEntity): Long

    @Update
    suspend fun update(supplier: SupplierEntity)

    @Query("SELECT * FROM suppliers ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE id = :supplierId LIMIT 1")
    suspend fun getById(supplierId: Long): SupplierEntity?

    @Query("SELECT * FROM suppliers WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): SupplierEntity?

    @Query("SELECT * FROM suppliers ORDER BY name COLLATE NOCASE")
    suspend fun getAllNow(): List<SupplierEntity>
}

@Dao
interface SaleDao {
    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert
    suspend fun insertItems(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sales ORDER BY soldAtMillis DESC")
    suspend fun getAllSalesNow(): List<SaleEntity>

    @Query("SELECT * FROM sale_items ORDER BY id ASC")
    suspend fun getAllItemsNow(): List<SaleItemEntity>

    @Query("SELECT * FROM sales WHERE billNo = :billNo LIMIT 1")
    suspend fun getByBillNo(billNo: String): SaleEntity?

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :saleId LIMIT 1")
    fun observeSaleWithItems(saleId: Long): Flow<SaleWithItems?>

    @Transaction
    @Query("SELECT * FROM sales ORDER BY soldAtMillis DESC LIMIT :limit")
    fun observeRecentSales(limit: Int): Flow<List<SaleWithItems>>

    @Query(
        """
        SELECT
            s.id AS saleId,
            s.billNo AS billNo,
            s.soldAtMillis AS soldAtMillis,
            s.customerName AS customerName,
            s.paymentMode AS paymentMode,
            s.total AS total,
            s.profit AS profit,
            COALESCE(SUM(si.quantity), 0) AS itemCount
        FROM sales s
        LEFT JOIN sale_items si ON si.saleId = s.id
        WHERE s.soldAtMillis BETWEEN :startMillis AND :endMillis
        GROUP BY s.id
        ORDER BY s.soldAtMillis DESC
        """
    )
    fun observeReport(startMillis: Long, endMillis: Long): Flow<List<SaleReportRow>>

    @Query("SELECT COALESCE(SUM(total), 0.0) FROM sales WHERE soldAtMillis BETWEEN :startMillis AND :endMillis")
    fun observeRevenue(startMillis: Long, endMillis: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(profit), 0.0) FROM sales WHERE soldAtMillis BETWEEN :startMillis AND :endMillis")
    fun observeProfit(startMillis: Long, endMillis: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM sales WHERE soldAtMillis BETWEEN :startMillis AND :endMillis")
    fun observeBillCount(startMillis: Long, endMillis: Long): Flow<Int>
}

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insertPurchase(purchase: PurchaseEntity): Long

    @Insert
    suspend fun insertItems(items: List<PurchaseItemEntity>)

    @Query("SELECT * FROM purchases ORDER BY purchasedAtMillis DESC")
    suspend fun getAllPurchasesNow(): List<PurchaseEntity>

    @Query("SELECT * FROM purchase_items ORDER BY id ASC")
    suspend fun getAllItemsNow(): List<PurchaseItemEntity>

    @Query("SELECT * FROM purchases WHERE voucherNo = :voucherNo LIMIT 1")
    suspend fun getByVoucherNo(voucherNo: String): PurchaseEntity?

    @Transaction
    @Query("SELECT * FROM purchases WHERE id = :purchaseId LIMIT 1")
    fun observePurchaseWithItems(purchaseId: Long): Flow<PurchaseWithItems?>

    @Query(
        """
        SELECT
            p.id AS purchaseId,
            p.voucherNo AS voucherNo,
            p.purchasedAtMillis AS purchasedAtMillis,
            p.supplierName AS supplierName,
            p.total AS total,
            COALESCE(SUM(pi.quantity), 0) AS itemCount
        FROM purchases p
        LEFT JOIN purchase_items pi ON pi.purchaseId = p.id
        WHERE p.purchasedAtMillis BETWEEN :startMillis AND :endMillis
        GROUP BY p.id
        ORDER BY p.purchasedAtMillis DESC
        """
    )
    fun observeReport(startMillis: Long, endMillis: Long): Flow<List<PurchaseReportRow>>
}

@Dao
interface StockAdjustmentDao {
    @Insert
    suspend fun insert(adjustment: StockAdjustmentEntity): Long

    @Query("SELECT * FROM stock_adjustments ORDER BY createdAtMillis ASC")
    suspend fun getAllNow(): List<StockAdjustmentEntity>

    @Query(
        """
        SELECT * FROM stock_adjustments
        WHERE medicineId = :medicineId
        ORDER BY createdAtMillis DESC
        """
    )
    fun observeForMedicine(medicineId: Long): Flow<List<StockAdjustmentEntity>>
}
