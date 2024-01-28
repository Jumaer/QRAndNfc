package com.example.myapplication.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.LayoutFailureDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CardFailureDialog (
    context: Context,
    listener: OnDismissListener, title : String? = null, hideHint : Boolean? = null
) {

    private lateinit var dialog: AlertDialog
    private val binding = LayoutFailureDialogBinding
        .inflate(LayoutInflater.from(context), null, false).apply {

            if(!title.isNullOrBlank()){
                txtTitle.text = title
                if(title == context.getString(R.string.nfcNotSupported)  ){
                    txtHint.visibility = View.GONE
                }
                if(hideHint != null){
                    if(hideHint == true) txtHint.visibility = View.GONE
                }
            }
        }

    init {
        dialog = MaterialAlertDialogBuilder(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.bg_layout_border_r16)
            this.setView(binding.root)

        }.create()

        show()


        dialog.setOnDismissListener {
            listener.onDismiss()
        }
    }





    fun show() {
        if (!dialog.isShowing) dialog.show()
    }

    fun dismiss() {
        if (dialog.isShowing) dialog.dismiss()
    }



    interface OnDismissListener {
        fun onDismiss()
    }


}