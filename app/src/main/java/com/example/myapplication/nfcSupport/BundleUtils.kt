package com.example.myapplication.nfcSupport

import android.content.Intent
import android.os.Build
import android.os.Parcelable

object BundleUtils {

    fun parcelableArray(intent: Intent, msg: String): Array<out Parcelable>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra(msg, Parcelable::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayExtra(msg)
        }
    }
    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}