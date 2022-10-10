package com.example.optitrip.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.optitrip.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs

/**
 * Dialog for the userto entert its token if its account is not yet activated
 *
 * @property context context of the fragment/activity
 * @property customAlertDialogView the view of the dialog
 */
class DialogToken(val context : Context) {

    private lateinit var customAlertDialogView : View

    /**
     * Build the dialog on the fragment
     *
     * @param callback function to call after dialog ended
     * @param token the token to enter used for validation
     * @return the dialog itself
     */
    fun  createDialog(callback : () -> Unit,token: String) : androidx.appcompat.app.AlertDialog {

        customAlertDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_token,null,false)
        val code  = customAlertDialogView.findViewById<EditText>(R.id.et_dialog_code)

        val dialog = MaterialAlertDialogBuilder(context)
                .setView(customAlertDialogView)
                .setTitle("Enter activation code")
                .setNegativeButton(context.getString(R.string.cancel)) { dialog,_ ->
                    dialog.dismiss()
                }
                .setPositiveButton(context.getString(R.string.validate),null)
                .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener{
                if(code.text.toString() != token)  code.error =  context.getString(R.string.token_wrong)
                else {
                    callback()
                    dialog.dismiss()
                }
            }
        }

        return dialog

    }


}