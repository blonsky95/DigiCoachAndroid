package com.tatoe.mydigicoach

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_window_export.view.*
import kotlinx.android.synthetic.main.dialog_window_info.view.*

object Utils {

    //if I want to used default parameters, but not in order, use named of the parameter

    fun getInfoDialogView(context: Context, dialogTitle:String = "", dialogText: String = "", dialogPositiveNegativeHandler: DialogPositiveNegativeHandler?=null) {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_window_info, null)
        mDialogView.dialog_text.text=dialogText
        val mBuilder = AlertDialog.Builder(context).setView(mDialogView).setTitle(dialogTitle)
        if (dialogPositiveNegativeHandler!=null){
            mBuilder.setPositiveButton("Yes") { _, _ ->
                dialogPositiveNegativeHandler.onPositiveButton()
            }
            mBuilder.setNegativeButton("No") { _, _ ->
                dialogPositiveNegativeHandler.onNegativeButton()
            }
        }

        mBuilder.show()
    }

    fun getDialogViewWithEditText(context: Context, dialogTitle:String = "", dialogText: String = "", dialogPositiveNegativeHandler: DialogPositiveNegativeHandler) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_window_export, null)
        mDialogView.text_info.text = dialogText

        val mBuilder = AlertDialog.Builder(context).setView(mDialogView).setTitle(dialogTitle)
        mBuilder.setPositiveButton("OK") { _, _ ->
            val exportFileName = mDialogView.export_name_edittext.text.trim().toString()
            dialogPositiveNegativeHandler.onPositiveButton(exportFileName)
        }
        mBuilder.show()

    }

}