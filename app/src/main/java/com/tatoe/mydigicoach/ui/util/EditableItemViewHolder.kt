package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class EditableItemViewHolder(
    v: View,
    private var listener: ClickListenerRecyclerView?,
    deletableItem: Boolean = false
) :
    RecyclerView.ViewHolder(v),
    View.OnClickListener, View.OnLongClickListener {

    val itemInfoView: TextView = v.findViewById(R.id.textView)
    private val itemDeleteBtn: ImageView = v.findViewById(R.id.deleteButton)

    // 2. In a BlockCreator activity, where it is temporarily added until block saved: in this usage, delete button is the listener and there is no listener in the textview

    //this class can have 2 usages
    // 1. In a read only mode, where clicking takes you to a exerciseCreator activity where you can delete, update...

    override fun onLongClick(v: View): Boolean {
        listener?.onLongClick(v, adapterPosition)
        return true
    }

    override fun onClick(v: View) {
        listener?.onClick(v, adapterPosition)
    }

    init {
        if (deletableItem) {
            itemDeleteBtn.visibility = View.VISIBLE
            itemDeleteBtn.setOnClickListener(this)
        } else {
            v.setOnClickListener(this)
            v.setOnLongClickListener(this)
        }
    }

}