package com.tatoe.mydigicoach

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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

    fun setProgressDialog(context:Context, message:String):AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }

}