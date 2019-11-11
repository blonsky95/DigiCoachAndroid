package com.tatoe.mydigicoach.ui.util

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class FileViewHolder(
    v: View,
    private var listener: ClickListenerRecyclerView?
) :
    RecyclerView.ViewHolder(v),
    View.OnClickListener {

    private val importBtn: ImageView = v.findViewById(R.id.import_btn)
    private val exportBtn: ImageView = v.findViewById(R.id.export_btn)
    private val deleteBtn: ImageView = v.findViewById(R.id.delete_btn)
    val fileNameTextView: TextView = v.findViewById(R.id.file_name)

    override fun onClick(v: View) {
        //view id can be used to specify the action/click listener I want to use
        listener?.onClick(v, adapterPosition,v.id)
    }

    init {
        importBtn.setOnClickListener(this)
        exportBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
    }

}