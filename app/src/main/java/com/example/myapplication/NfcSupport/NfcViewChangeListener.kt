package com.example.myapplication.NfcSupport

import android.content.Intent

interface NfcViewChangeListener{
    fun onPositiveView(isDeviceActive : Boolean)
    fun readFromIntent(intent: Intent)
}