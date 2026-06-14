package com.example.pharmacyinventory

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

class PinManager(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_pin_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun hasPin(): Boolean = prefs.contains(KEY_PIN_HASH)

    fun savePin(pin: String) {
        require(pin.length == 4 && pin.all(Char::isDigit)) { "PIN must be 4 digits." }
        prefs.edit().putString(KEY_PIN_HASH, hash(pin)).apply()
    }

    fun verifyPin(pin: String): Boolean {
        return prefs.getString(KEY_PIN_HASH, null) == hash(pin)
    }

    fun clearPin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }

    private fun hash(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("$HASH_PREFIX$pin".toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val KEY_PIN_HASH = "pin_hash"
        const val HASH_PREFIX = "offline-pharmacy-pin-v1:"
    }
}
