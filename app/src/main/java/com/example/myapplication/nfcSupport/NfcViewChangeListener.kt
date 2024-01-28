package com.example.myapplication.nfcSupport

import android.content.Intent

interface NfcViewChangeListener{
    fun onPositiveView(isDeviceActive : Boolean)
    fun readFromIntent(intent: Intent)
}