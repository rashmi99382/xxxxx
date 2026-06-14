package com.example.pharmacyinventory.data

import androidx.room.withTransaction
import org.json.JSONArray
import org.json.JSONObject

data class SyncMergeResult(
    val suppliers: Int = 0,
    val medicines: Int = 0,
    val batches: Int = 0,
    val purchases: Int = 0,
    val sales: Int = 0
)

class LocalSyncEngine(private val database: AppDatabase) {
    private val medicineDao = database.medicineDao()
    private val batchDao = database.batchDao()
    private val supplierDao = database.supplierDao()
    private val saleDao = database.saleDao()
    private val purchaseDao = database.purchaseDao()

    suspend fun exportSnapshot(deviceId: String): String {
        val suppliers = supplierDao.getAllNow()
        val medicines = medicineDao.getAllNow()
        val batches = batchDao.getAllNow()
        val purchases = purchaseDao.getAllPurchasesNow()
        val purchaseItems = purchaseDao.getAllItemsNow()
        val sales = saleDao.getAllSalesNow()
        val saleItems = saleDao.getAllItemsNow()

        val supplierById = suppliers.associateBy { it.id }
        val medicineById = medicines.associateBy { it.id }
        val purchaseById = purchases.associateBy { it.id }
        val saleById = sales.associateBy { it.id }
        val batchById = batches.associateBy { it.id }

        return JSONObject()
            .put("schema", 1)
            .put("deviceId", deviceId)
            .put("generatedAtMillis", System.currentTimeMillis())
            .put("suppliers", JSONArray().also { array ->
                suppliers.forEach { supplier ->
                    array.put(JSONObject()
                        .put("name", supplier.name)
                        .put("contactPerson", supplier.contactPerson)
                        .put("phone", supplier.phone)
                        .put("address", supplier.address)
                        .put("balance", supplier.balance)
                        .put("createdAtMillis", supplier.createdAtMillis)
                        .put("updatedAtMillis", supplier.updatedAtMillis))
                }
            })
            .put("medicines", JSONArray().also { array ->
                medicines.forEach { medicine ->
                    array.put(JSONObject()
                        .put("key", medicineKey(medicine.name, medicine.company, medicine.genericName))
                        .put("name", medicine.name)
                        .put("company", medicine.company)
                        .put("genericName", medicine.genericName)
                        .put("category", medicine.category)
                        .put("supplierName", supplierById[medicine.supplierId]?.name)
                        .put("minStock", medicine.minStock)
                        .put("createdAtMillis", medicine.createdAtMillis)
                        .put("updatedAtMillis", medicine.updatedAtMillis))
                }
            })
            .put("batches", JSONArray().also { array ->
                batches.forEach { batch ->
                    val medicine = medicineById[batch.medicineId] ?: return@forEach
                    array.put(JSONObject()
                        .put("key", batchKey(medicine, batch.batchNo, batch.expiryEpochDay))
                        .put("medicineKey", medicineKey(medicine.name, medicine.company, medicine.genericName))
                        .put("supplierName", supplierById[batch.supplierId]?.name)
                        .put("batchNo", batch.batchNo)
                        .put("expiryEpochDay", batch.expiryEpochDay)
                        .put("quantity", batch.quantity)
                        .put("purchasePrice", batch.purchasePrice)
                        .put("mrp", batch.mrp)
                        .put("createdAtMillis", batch.createdAtMillis)
                        .put("updatedAtMillis", batch.updatedAtMillis))
                }
            })
            .put("purchases", JSONArray().also { array ->
                purchases.forEach { purchase ->
                    array.put(JSONObject()
                        .put("voucherNo", purchase.voucherNo)
                        .put("supplierName", purchase.supplierName)
                        .put("purchasedAtMillis", purchase.purchasedAtMillis)
                        .put("total", purchase.total)
                        .put("note", purchase.note))
                }
            })
            .put("purchaseItems", JSONArray().also { array ->
                purchaseItems.forEach { item ->
                    val purchase = purchaseById[item.purchaseId] ?: return@forEach
                    val medicine = medicineById[item.medicineId] ?: return@forEach
                    val batch = batchById[item.batchId] ?: return@forEach
                    array.put(JSONObject()
                        .put("voucherNo", purchase.voucherNo)
                        .put("medicineKey", medicineKey(medicine.name, medicine.company, medicine.genericName))
                        .put("batchKey", batchKey(medicine, batch.batchNo, batch.expiryEpochDay))
                        .put("medicineName", item.medicineName)
                        .put("batchNo", item.batchNo)
                        .put("expiryEpochDay", item.expiryEpochDay)
                        .put("quantity", item.quantity)
                        .put("purchasePrice", item.purchasePrice)
                        .put("mrp", item.mrp)
                        .put("lineTotal", item.lineTotal))
                }
            })
            .put("sales", JSONArray().also { array ->
                sales.forEach { sale ->
                    array.put(JSONObject()
                        .put("billNo", sale.billNo)
                        .put("soldAtMillis", sale.soldAtMillis)
                        .put("customerName", sale.customerName)
                        .put("paymentMode", sale.paymentMode)
                        .put("subtotal", sale.subtotal)
                        .put("total", sale.total)
                        .put("profit", sale.profit)
                        .put("hadExpiredWarning", sale.hadExpiredWarning)
                        .put("note", sale.note))
                }
            })
            .put("saleItems", JSONArray().also { array ->
                saleItems.forEach { item ->
                    val sale = saleById[item.saleId] ?: return@forEach
                    val medicine = medicineById[item.medicineId] ?: return@forEach
                    val batch = batchById[item.batchId] ?: return@forEach
                    array.put(JSONObject()
                        .put("billNo", sale.billNo)
                        .put("medicineKey", medicineKey(medicine.name, medicine.company, medicine.genericName))
                        .put("batchKey", batchKey(medicine, batch.batchNo, batch.expiryEpochDay))
                        .put("medicineName", item.medicineName)
                        .put("batchNo", item.batchNo)
                        .put("expiryEpochDay", item.expiryEpochDay)
                        .put("quantity", item.quantity)
                        .put("mrp", item.mrp)
                        .put("purchasePrice", item.purchasePrice)
                        .put("lineTotal", item.lineTotal)
                        .put("lineProfit", item.lineProfit)
                        .put("wasExpired", item.wasExpired))
                }
            })
            .toString()
    }

    suspend fun mergeSnapshot(snapshot: String): SyncMergeResult {
        val root = JSONObject(snapshot)
        if (root.optInt("schema") != 1) return SyncMergeResult()

        return database.withTransaction {
            var suppliersMerged = 0
            var medicinesMerged = 0
            var batchesMerged = 0
            var purchasesMerged = 0
            var salesMerged = 0

            root.optJSONArray("suppliers").forEachObject { supplier ->
                val name = supplier.optString("name").trim()
                if (name.isNotBlank() && supplierDao.getByName(name) == null) {
                    supplierDao.insert(
                        SupplierEntity(
                            name = name,
                            contactPerson = supplier.optString("contactPerson"),
                            phone = supplier.optString("phone"),
                            address = supplier.optString("address"),
                            balance = supplier.optDouble("balance", 0.0),
                            createdAtMillis = supplier.optLong("createdAtMillis", System.currentTimeMillis()),
                            updatedAtMillis = supplier.optLong("updatedAtMillis", System.currentTimeMillis())
                        )
                    )
                    suppliersMerged++
                }
            }

            val medicineIdByKey = mutableMapOf<String, Long>()
            root.optJSONArray("medicines").forEachObject { medicine ->
                val name = medicine.optString("name").trim()
                val company = medicine.optString("company").trim()
                val genericName = medicine.optString("genericName").trim()
                val key = medicine.optString("key", medicineKey(name, company, genericName))
                if (name.isBlank()) return@forEachObject
                val existing = medicineDao.getByNaturalKey(name, company, genericName)
                val localId = existing?.id ?: medicineDao.insert(
                    MedicineEntity(
                        name = name,
                        company = company,
                        genericName = genericName,
                        category = medicine.optString("category", "General"),
                        supplierId = medicine.optStringOrNull("supplierName")?.let { supplierDao.getByName(it)?.id },
                        minStock = medicine.optInt("minStock", 0),
                        createdAtMillis = medicine.optLong("createdAtMillis", System.currentTimeMillis()),
                        updatedAtMillis = medicine.optLong("updatedAtMillis", System.currentTimeMillis())
                    )
                ).also { medicinesMerged++ }
                medicineIdByKey[key] = localId
            }

            val batchIdByKey = mutableMapOf<String, Long>()
            root.optJSONArray("batches").forEachObject { batch ->
                val medicineId = medicineIdByKey[batch.optString("medicineKey")] ?: return@forEachObject
                val batchNo = batch.optString("batchNo").trim().uppercase()
                val expiryEpochDay = batch.optLong("expiryEpochDay")
                if (batchNo.isBlank()) return@forEachObject
                val existing = batchDao.getByNaturalKey(medicineId, batchNo, expiryEpochDay)
                val localId = if (existing == null) {
                    batchDao.insert(
                        BatchEntity(
                            medicineId = medicineId,
                            supplierId = batch.optStringOrNull("supplierName")?.let { supplierDao.getByName(it)?.id },
                            batchNo = batchNo,
                            expiryEpochDay = expiryEpochDay,
                            quantity = batch.optInt("quantity", 0).coerceAtLeast(0),
                            purchasePrice = batch.optDouble("purchasePrice", 0.0),
                            mrp = batch.optDouble("mrp", 0.0),
                            createdAtMillis = batch.optLong("createdAtMillis", System.currentTimeMillis()),
                            updatedAtMillis = batch.optLong("updatedAtMillis", System.currentTimeMillis())
                        )
                    ).also { batchesMerged++ }
                } else {
                    val remoteUpdated = batch.optLong("updatedAtMillis", existing.updatedAtMillis)
                    if (remoteUpdated > existing.updatedAtMillis) {
                        batchDao.update(
                            existing.copy(
                                supplierId = batch.optStringOrNull("supplierName")?.let { supplierDao.getByName(it)?.id } ?: existing.supplierId,
                                quantity = batch.optInt("quantity", existing.quantity).coerceAtLeast(0),
                                purchasePrice = batch.optDouble("purchasePrice", existing.purchasePrice),
                                mrp = batch.optDouble("mrp", existing.mrp),
                                updatedAtMillis = remoteUpdated
                            )
                        )
                        batchesMerged++
                    }
                    existing.id
                }
                batchIdByKey[batch.optString("key")] = localId
            }

            val purchaseIdByVoucher = mutableMapOf<String, Long>()
            val newPurchaseVouchers = mutableSetOf<String>()
            root.optJSONArray("purchases").forEachObject { purchase ->
                val voucherNo = purchase.optString("voucherNo").trim()
                if (voucherNo.isBlank()) return@forEachObject
                val existing = purchaseDao.getByVoucherNo(voucherNo)
                val localId = existing?.id ?: purchaseDao.insertPurchase(
                    PurchaseEntity(
                        voucherNo = voucherNo,
                        supplierId = purchase.optStringOrNull("supplierName")?.let { supplierDao.getByName(it)?.id },
                        supplierName = purchase.optString("supplierName", "Unknown supplier"),
                        purchasedAtMillis = purchase.optLong("purchasedAtMillis", System.currentTimeMillis()),
                        total = purchase.optDouble("total", 0.0),
                        note = purchase.optString("note")
                    )
                ).also {
                    purchasesMerged++
                    newPurchaseVouchers += voucherNo
                }
                purchaseIdByVoucher[voucherNo] = localId
            }

            root.optJSONArray("purchaseItems").forEachObject { item ->
                val voucherNo = item.optString("voucherNo")
                if (voucherNo !in newPurchaseVouchers) return@forEachObject
                val purchaseId = purchaseIdByVoucher[voucherNo] ?: return@forEachObject
                val medicineId = medicineIdByKey[item.optString("medicineKey")] ?: return@forEachObject
                val batchId = batchIdByKey[item.optString("batchKey")] ?: return@forEachObject
                purchaseDao.insertItems(
                    listOf(
                        PurchaseItemEntity(
                            purchaseId = purchaseId,
                            medicineId = medicineId,
                            batchId = batchId,
                            medicineName = item.optString("medicineName"),
                            batchNo = item.optString("batchNo"),
                            expiryEpochDay = item.optLong("expiryEpochDay"),
                            quantity = item.optInt("quantity"),
                            purchasePrice = item.optDouble("purchasePrice"),
                            mrp = item.optDouble("mrp"),
                            lineTotal = item.optDouble("lineTotal")
                        )
                    )
                )
            }

            val saleIdByBill = mutableMapOf<String, Long>()
            val newSaleBills = mutableSetOf<String>()
            root.optJSONArray("sales").forEachObject { sale ->
                val billNo = sale.optString("billNo").trim()
                if (billNo.isBlank()) return@forEachObject
                val existing = saleDao.getByBillNo(billNo)
                val localId = existing?.id ?: saleDao.insertSale(
                    SaleEntity(
                        billNo = billNo,
                        soldAtMillis = sale.optLong("soldAtMillis", System.currentTimeMillis()),
                        customerName = sale.optString("customerName", "Walk-in customer"),
                        paymentMode = sale.optString("paymentMode", "Cash"),
                        subtotal = sale.optDouble("subtotal", 0.0),
                        total = sale.optDouble("total", 0.0),
                        profit = sale.optDouble("profit", 0.0),
                        hadExpiredWarning = sale.optBoolean("hadExpiredWarning", false),
                        note = sale.optString("note")
                    )
                ).also {
                    salesMerged++
                    newSaleBills += billNo
                }
                saleIdByBill[billNo] = localId
            }

            root.optJSONArray("saleItems").forEachObject { item ->
                val billNo = item.optString("billNo")
                if (billNo !in newSaleBills) return@forEachObject
                val saleId = saleIdByBill[billNo] ?: return@forEachObject
                val medicineId = medicineIdByKey[item.optString("medicineKey")] ?: return@forEachObject
                val batchId = batchIdByKey[item.optString("batchKey")] ?: return@forEachObject
                saleDao.insertItems(
                    listOf(
                        SaleItemEntity(
                            saleId = saleId,
                            medicineId = medicineId,
                            batchId = batchId,
                            medicineName = item.optString("medicineName"),
                            batchNo = item.optString("batchNo"),
                            expiryEpochDay = item.optLong("expiryEpochDay"),
                            quantity = item.optInt("quantity"),
                            mrp = item.optDouble("mrp"),
                            purchasePrice = item.optDouble("purchasePrice"),
                            lineTotal = item.optDouble("lineTotal"),
                            lineProfit = item.optDouble("lineProfit"),
                            wasExpired = item.optBoolean("wasExpired", false)
                        )
                    )
                )
            }

            SyncMergeResult(
                suppliers = suppliersMerged,
                medicines = medicinesMerged,
                batches = batchesMerged,
                purchases = purchasesMerged,
                sales = salesMerged
            )
        }
    }
}

private fun medicineKey(name: String, company: String, genericName: String): String {
    return listOf(name, company, genericName).joinToString("|") { it.trim().lowercase() }
}

private fun batchKey(medicine: MedicineEntity, batchNo: String, expiryEpochDay: Long): String {
    return "${medicineKey(medicine.name, medicine.company, medicine.genericName)}|${batchNo.trim().uppercase()}|$expiryEpochDay"
}

private suspend fun JSONArray?.forEachObject(block: suspend (JSONObject) -> Unit) {
    if (this == null) return
    for (index in 0 until length()) {
        val item = optJSONObject(index)
        if (item != null) block(item)
    }
}

private fun JSONObject.optStringOrNull(name: String): String? {
    if (!has(name) || isNull(name)) return null
    return optString(name).takeIf { it.isNotBlank() }
}
