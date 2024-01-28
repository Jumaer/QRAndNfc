package com.example.myapplication.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.myapplication.R
import com.example.myapplication.databinding.LayoutScanDetectDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class CardScanDetectDialog(context: Context, listener : OnScanSuccess) {
    private var binding: LayoutScanDetectDialogBinding
    private var bottomSheet: BottomSheetDialog

    init {
        binding =
            LayoutScanDetectDialogBinding.inflate(
                LayoutInflater.from(context),
                null,
                false
            ).apply {

                imgBack.setOnClickListener {

                }
            }
        bottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialog).apply {
            setContentView(binding.root)
            setOnDismissListener {
                listener.onSuccess()
            }
            setCancelable(false)
            setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val parentLayout = bottomSheetDialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                parentLayout?.let { bottomSheet ->
                    val behaviour = BottomSheetBehavior.from(bottomSheet)
                    val layoutParams = bottomSheet.layoutParams
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                    bottomSheet.layoutParams = layoutParams
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }




    fun show() {
        if (!bottomSheet.isShowing) bottomSheet.show()
    }

    fun dismiss() {
        if (bottomSheet.isShowing) bottomSheet.dismiss()
    }




    interface OnScanSuccess {
        fun onSuccess()

    }



}