package com.example.myapplication.dialog

import android.animation.Animator
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.LayoutNfcSuccessDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A helper class for showing place successView
 * @param[context] current context
 * @param[dialogTitle] title of dialog
 * @param[dialogTitle] subtitle of dialog
 */
class NfcCardSuccessDialog(
    context: Context,
    dialogTitle: String,
    listener: OnDismissListener, private val isPersonal : Boolean =true
) {
    private  var dialog: AlertDialog
    private val binding = LayoutNfcSuccessDialogBinding
        .inflate(LayoutInflater.from(context), null, false).apply {
            txtTitle.text = dialogTitle
            imgBg.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    onValueChange()
                    dismiss()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            })

        }

    private fun onValueChange() {
        if(isPersonal){

        }
        else{

        }
    }

    init {
        dialog = MaterialAlertDialogBuilder(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.bg_layout_border_r16)
            this.setView(binding.root)

        }.create()
        show()

        dialog.setOnDismissListener{
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