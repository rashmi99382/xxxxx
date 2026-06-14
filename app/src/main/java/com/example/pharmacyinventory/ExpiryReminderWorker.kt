package com.example.pharmacyinventory

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.pharmacyinventory.data.AppDatabase
import com.example.pharmacyinventory.data.ExpiryTab
import com.example.pharmacyinventory.data.PharmacyRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class ExpiryReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return runCatching {
            val repository = PharmacyRepository(AppDatabase.getInstance(applicationContext))
            val rows = repository.observeExpiryRows(ExpiryTab.ThirtyDays).first()
            if (rows.isNotEmpty()) {
                NotificationHelper.showExpiryNotification(applicationContext, rows.size)
            }
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}

object ExpiryReminderScheduler {
    private const val WORK_NAME = "expiry-reminder-work"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ExpiryReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}

private object NotificationHelper {
    private const val CHANNEL_ID = "expiry_alerts"
    private const val NOTIFICATION_ID = 3001

    @SuppressLint("MissingPermission")
    fun showExpiryNotification(context: Context, count: Int) {
        createChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Expiry alert")
            .setContentText("$count stock batch(es) expire within 30 days.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Expiry alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
