package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentViewProfileNfcBinding


class ViewProfileNfcQrScanFragment : Fragment() {


    private lateinit var binding : FragmentViewProfileNfcBinding
    private lateinit var mContext : Context
    private lateinit var TAG : String
    private fun goBack() {
        if (parentFragmentManager.backStackEntryCount == 0) activity?.finish()
        else findNavController().popBackStack()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        TAG = "NFC CARD SCAN"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewProfileNfcBinding.inflate(inflater, container, false)

        return binding.root
    }
}
