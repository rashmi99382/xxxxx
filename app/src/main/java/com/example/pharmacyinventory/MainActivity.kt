package com.example.pharmacyinventory

import android.Manifest
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.example.pharmacyinventory.ui.PharmacyInventoryApp
import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        val factory = (application as PharmacyApplication).container.viewModelFactory
        setContent {
            PharmacyInventoryApp(factory)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                1001
            )
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) = Unit

    override fun onPaymentError(code: Int, response: String?) = Unit
}
