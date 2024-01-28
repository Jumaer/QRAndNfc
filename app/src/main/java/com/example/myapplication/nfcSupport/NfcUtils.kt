package com.example.myapplication.nfcSupport

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.example.myapplication.dialog.CardCheckNfcDialog
import com.example.myapplication.nfcSupport.BundleUtils.parcelable

object NfcUtils {

    @SuppressLint("SetTextI18n")
    private fun buildTagViews(msgs: Array<NdefMessage>?): String {
        if (msgs.isNullOrEmpty()) return ""
        var msg = ""
        for (curMsg in msgs) {
            msg = String(curMsg.records[0].payload)
        }
        return msg.substring(1)
    }

    private var myTag: Tag? = null

    /**
     * Read data from intent after call
     */

    fun processDataOfIntent(intent: Intent): String {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            myTag = intent.parcelable(NfcAdapter.EXTRA_TAG) as Tag?
            val rawMsg = BundleUtils.parcelableArray(intent, NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msg = mutableListOf<NdefMessage>()
            if (rawMsg != null) {
                for (i in rawMsg.indices) {
                    msg.add(i, rawMsg[i] as NdefMessage)
                }
                return buildTagViews(msg.toTypedArray())
            }
            return ""
        }

        return ""
    }




    interface BottomSheetNfcCancel {
        fun onCancel()
    }


    fun showScanBottomSheet(listener: BottomSheetNfcCancel, context: Context): CardCheckNfcDialog {
        return CardCheckNfcDialog(context, object : CardCheckNfcDialog.OnClickListener {
            override fun onCancel() {
                listener.onCancel()
            }
        })

    }


}