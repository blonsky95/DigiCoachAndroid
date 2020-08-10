package com.tatoe.mydigicoach

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_window_edittext.view.*
import kotlinx.android.synthetic.main.dialog_window_info.view.*
import timber.log.Timber
import java.io.File

object Utils {

    //if I want to used default parameters, but not in order, use named of the parameter

    fun getInfoDialogView(
        context: Context,
        dialogTitle: String = "",
        dialogText: String = "",
        dialogPositiveNegativeInterface: DialogPositiveNegativeInterface? = null
    ) {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_window_info, null)
        mDialogView.dialog_text.text = dialogText
        val mBuilder = AlertDialog.Builder(context).setView(mDialogView).setTitle(dialogTitle)
        if (dialogPositiveNegativeInterface != null) {
            mBuilder.setPositiveButton("Yes") { _, _ ->
                dialogPositiveNegativeInterface.onPositiveButton()
            }
            mBuilder.setNegativeButton("No") { _, _ ->
                dialogPositiveNegativeInterface.onNegativeButton()
            }
        }

        mBuilder.show()
    }

    fun getDialogViewWithEditText(
        context: Context,
        dialogTitle: String? = "",
        dialogText: String? = "",
        editTextHint: String? = "Type here",
        dialogPositiveNegativeInterface: DialogPositiveNegativeInterface
    ) {

        val mDialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_window_edittext, null)

        if (dialogText == null) {
            mDialogView.dialog_info.visibility = View.GONE
        } else {
            mDialogView.dialog_info.text = dialogText
        }

        mDialogView.dialog_edittext.hint = editTextHint

        val mBuilder = AlertDialog.Builder(context).setView(mDialogView)

        if (dialogTitle != null) {
            mBuilder.setTitle(dialogTitle)
        }

        val alertDialog = mBuilder.show()
        alertDialog.setCancelable(true)

        mDialogView.dialog_btn_left.setOnClickListener {
            val userInput = mDialogView.dialog_edittext.text.trim().toString()
            dialogPositiveNegativeInterface.onPositiveButton(userInput)
            alertDialog.dismiss()
        }

//        mDialogView.dialog_btn_right.setOnClickListener {
//            dialogPositiveNegativeHandler.onNegativeButton()
//        }

//        mBuilder.setPositiveButton("OK") { _, _ ->
//            val exportFileName = mDialogView.dialog_edittext.text.trim().toString()
//            dialogPositiveNegativeHandler.onPositiveButton(exportFileName)
//        }

    }

    fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Timber.d("Internet - NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Timber.d("Internet - NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Timber.d("Internet - NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun purgeExtStorageDirectoryPath(filePath: String): String {
        val extStorageDirKeyWords="emulated/0"
        var finalString = ""
        if (filePath.contains(extStorageDirKeyWords.toRegex())) {
            //now remove anything preceeding /storage/emulated/0
            val fileLocationInExtStorage = filePath.substring(filePath.indexOf(extStorageDirKeyWords)+extStorageDirKeyWords.length, filePath.length)
            finalString = "${Environment.getExternalStorageDirectory()}$fileLocationInExtStorage"
        }
        return finalString
    }

    fun getDataTypeBasedOnExt(substring: String): String {
        if (substring.contains("mp4")) {
            return "video/*"
        }
        if (substring.contains("jpg")) {
            return "image/*"
        }
        return ""
    }

    fun getFileName(fieldEntryValue: String): String {
//        fieldEntryValue.lastIndexOf(File.separatorChar)
        val file = File(fieldEntryValue)
        return file.name
    }

    class DialogBundle(title:String = "", text:String = "", positiveNegativeInterface: DialogPositiveNegativeInterface) {
        var mTitle=title
        var mText=text
        var mPositiveNegativeInterface=positiveNegativeInterface
    }

}