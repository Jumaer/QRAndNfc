package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.activities.NfcQrScanActivity
import com.example.myapplication.constants.AppConstants
import com.example.myapplication.databinding.FragmentNfcScanBinding
import com.example.myapplication.dialog.CardFailureDialog
import com.example.myapplication.dialog.NfcCardSuccessDialog
import com.example.myapplication.nfcSupport.CommunicatorRefresh
import com.example.myapplication.nfcSupport.NfcUtils
import com.example.myapplication.nfcSupport.NfcViewChangeListener
import java.net.URI


class NfcScanFragment : Fragment() {

    private lateinit var binding: FragmentNfcScanBinding
    private lateinit var mContext: Context
    private lateinit var TAG: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        TAG = "NFC CARD SCAN"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNfcScanBinding.inflate(inflater, container, false)

        setListeners(binding)

        return binding.root
    }

    private fun setListeners(bind: FragmentNfcScanBinding) {
        bind.apply {
            nfcScan.setOnClickListener {
                nfcOperations()
            }
            customScan.setOnClickListener {
                moveForScan()
            }
        }
    }

    private fun moveForScan() {
        findNavController().navigate(R.id.action_nfcScanFragment_to_qrCardScanFragment)
    }

    /**
     * Bottom sheet of nfc will start from here
     * no param required
     * calling from activity
     */


    private fun nfcOperations() {

        (activity as NfcQrScanActivity).tryForNfc(object : NfcViewChangeListener {
            override fun onPositiveView(isDeviceActive: Boolean) {
                performNoNfcFound(isDeviceActive)
            }

            override fun readFromIntent(intent: Intent) {
                performReadData(intent)
            }

        })
    }

    private fun performNoNfcFound(isDeviceActive: Boolean) {
        if (!isDeviceActive) {
            moveForScan()
        }
    }



    // If NFC  found in phone
    private fun performReadData(intent: Intent) {
        val linkText = NfcUtils.processDataOfIntent(intent)
        Log.d(TAG, linkText)
        if (linkText.contains(AppConstants.LINK_OF_DATA_URL)) {
            generateCardInfo(linkText)

        } else {
            CardFailureDialog(mContext,
                object : CardFailureDialog.OnDismissListener {
                    override fun onDismiss() {

                    }
                })

        }
    }

    private fun generateCardInfo(linkText: String) {
        val uri = URI(linkText)
        val path = uri.path
        val idStr = path.substring(path.lastIndexOf('/') + 1)
        showSuccessPopUp(idStr)

    }


    private fun showSuccessPopUp(idStr: String) {

        /**
         * Show success pop up
         */
        NfcCardSuccessDialog(mContext,
            getString(R.string.successfully_get_nfc),
            object : NfcCardSuccessDialog.OnDismissListener {
                override fun onDismiss() {
                      Toast.makeText(mContext,idStr,Toast.LENGTH_SHORT).show()
                }
            })

    }
}