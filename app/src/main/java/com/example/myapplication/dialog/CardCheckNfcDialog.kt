package com.example.myapplication.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.myapplication.R
import com.example.myapplication.databinding.LayoutNfcDetectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CardCheckNfcDialog (context: Context, private val listener: OnClickListener) {
    private var binding: LayoutNfcDetectionBinding
    private var bottomSheet: BottomSheetDialog

    init {
        binding =
            LayoutNfcDetectionBinding.inflate(
                LayoutInflater.from(context),
                null,
                false
            ).apply {

                btnCancel.setOnClickListener {
                    dismiss()
                    listener.onCancel()
                }

            }
        bottomSheet = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme).apply {
            setContentView(binding.root)
        }
    }


    fun show() {
        if (!bottomSheet.isShowing) bottomSheet.show()
    }

    fun dismiss() {
        if (bottomSheet.isShowing) bottomSheet.dismiss()
    }




    interface OnClickListener {
        fun onCancel()
    }
}