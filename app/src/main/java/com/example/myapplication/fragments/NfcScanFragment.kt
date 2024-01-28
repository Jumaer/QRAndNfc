package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.activities.NfcQrScanActivity
import com.example.myapplication.databinding.FragmentNfcScanBinding
import com.example.myapplication.nfcSupport.NfcViewChangeListener


class NfcScanFragment : Fragment() {

    private lateinit var binding : FragmentNfcScanBinding
    private lateinit var mContext : Context
    private lateinit var TAG : String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        TAG = "NFC CARD SCAN"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
                findNavController().navigate(R.id.action_nfcScanFragment_to_qrCardScanFragment)
            }
        }
    }

    private fun nfcOperations() {
        (activity as NfcQrScanActivity).tryForNfc(object : NfcViewChangeListener{
            override fun onPositiveView(isDeviceActive: Boolean) {

            }

            override fun readFromIntent(intent: Intent) {

            }

        })
    }
}