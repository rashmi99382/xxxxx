package com.example.pharmacyinventory

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.pharmacyinventory.data.AppDatabase
import com.example.pharmacyinventory.data.SaleReportRow
import com.example.pharmacyinventory.data.SaleWithItems
import com.example.pharmacyinventory.data.StockRow
import com.example.pharmacyinventory.data.toLocalDate
import com.example.pharmacyinventory.data.toLocalDateTimeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalFileManager(private val context: Context) {
    suspend fun exportStockCsv(uri: Uri, rows: List<StockRow>) = writeText(uri, stockCsv(rows))

    suspend fun exportSalesCsv(uri: Uri, rows: List<SaleReportRow>) = writeText(uri, salesCsv(rows))

    suspend fun exportInvoicePdf(uri: Uri, sale: SaleWithItems) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri)?.use { output ->
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val titlePaint = Paint().apply {
                textSize = 20f
                isFakeBoldText = true
            }
            val normalPaint = Paint().apply { textSize = 12f }
            val boldPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
            }
            var y = 48f
            canvas.drawText("Sharma Medical Store", 40f, y, titlePaint)
            y += 28f
            canvas.drawText("Invoice: ${sale.sale.billNo}", 40f, y, normalPaint)
            y += 18f
            canvas.drawText("Date: ${sale.sale.soldAtMillis.toLocalDateTimeText()}", 40f, y, normalPaint)
            y += 18f
            canvas.drawText("Customer: ${sale.sale.customerName}", 40f, y, normalPaint)
            y += 28f
            canvas.drawText("Medicine", 40f, y, boldPaint)
            canvas.drawText("Batch", 245f, y, boldPaint)
            canvas.drawText("Qty", 350f, y, boldPaint)
            canvas.drawText("Rate", 410f, y, boldPaint)
            canvas.drawText("Total", 485f, y, boldPaint)
            y += 18f
            sale.items.forEach { item ->
                canvas.drawText(item.medicineName.take(28), 40f, y, normalPaint)
                canvas.drawText(item.batchNo.take(12), 245f, y, normalPaint)
                canvas.drawText(item.quantity.toString(), 350f, y, normalPaint)
                canvas.drawText(money(item.mrp), 410f, y, normalPaint)
                canvas.drawText(money(item.lineTotal), 485f, y, normalPaint)
                y += 18f
                if (y > 780f) return@forEach
            }
            y += 20f
            canvas.drawText("Total payable: ${money(sale.sale.total)}", 350f, y, titlePaint)
            if (sale.sale.hadExpiredWarning) {
                y += 24f
                canvas.drawText("Expired stock sold after confirmation.", 40f, y, boldPaint)
            }
            document.finishPage(page)
            document.writeTo(output)
            document.close()
        } ?: error("Unable to open PDF destination.")
    }

    suspend fun exportDatabaseBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        require(dbFile.exists()) { "Database file does not exist yet." }
        context.contentResolver.openOutputStream(uri)?.use { output ->
            dbFile.inputStream().use { input -> input.copyTo(output) }
        } ?: error("Unable to open backup destination.")
    }

    suspend fun importDatabaseBackup(uri: Uri) = withContext(Dispatchers.IO) {
        AppDatabase.closeInstance()
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        dbFile.parentFile?.mkdirs()
        val tempFile = File(dbFile.parentFile, "$DATABASE_NAME.import")
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Unable to open backup file.")
        if (dbFile.exists()) dbFile.delete()
        tempFile.renameTo(dbFile)
        File("${dbFile.path}-shm").delete()
        File("${dbFile.path}-wal").delete()
    }

    private suspend fun writeText(uri: Uri, text: String) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
            writer.write(text)
        } ?: error("Unable to open export destination.")
    }

    private fun stockCsv(rows: List<StockRow>): String {
        val header = listOf(
            "medicine_id",
            "batch_id",
            "name",
            "company",
            "category",
            "supplier",
            "batch_no",
            "expiry",
            "quantity",
            "purchase_price",
            "mrp"
        )
        return buildString {
            appendLine(header.toCsvLine())
            rows.forEach { row ->
                appendLine(
                    listOf(
                        row.medicineId,
                        row.batchId,
                        row.name,
                        row.company,
                        row.category,
                        row.supplierName.orEmpty(),
                        row.batchNo,
                        row.expiryEpochDay.toLocalDate().toString(),
                        row.quantity,
                        row.purchasePrice,
                        row.mrp
                    ).toCsvLine()
                )
            }
        }
    }

    private fun salesCsv(rows: List<SaleReportRow>): String {
        val header = listOf("sale_id", "bill_no", "date", "customer", "payment_mode", "total", "profit", "item_count")
        return buildString {
            appendLine(header.toCsvLine())
            rows.forEach { row ->
                appendLine(
                    listOf(
                        row.saleId,
                        row.billNo,
                        row.soldAtMillis.toLocalDateTimeText(),
                        row.customerName,
                        row.paymentMode,
                        row.total,
                        row.profit,
                        row.itemCount
                    ).toCsvLine()
                )
            }
        }
    }

    private fun List<Any?>.toCsvLine(): String {
        return joinToString(",") { value ->
            val text = value?.toString().orEmpty()
            "\"${text.replace("\"", "\"\"")}\""
        }
    }

    private fun money(value: Double): String = "Rs. %.2f".format(value)

    private companion object {
        const val DATABASE_NAME = "offline_pharmacy_inventory.db"
    }
}
