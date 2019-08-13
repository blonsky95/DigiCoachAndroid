package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class ItemViewHolder(v: View, var listener: ClickListenerRecyclerView, deletableItem: Boolean = false) :
    RecyclerView.ViewHolder(v),
    View.OnClickListener, View.OnLongClickListener {

    //this class can have 2 usages
    // 1. In a read only mode, where clicking takes you to a exerciseCreator activity where you can delete, update...
    // 2. In a BlockCreator activity, where it is temporarily added until block saved: in this usage, delete button is the listener and there is no listener in the textview

    override fun onLongClick(v: View?): Boolean {
        //todo maybe here in the future add a window with description of exercise being long clicked
//        v?.let { it ->
//            listener.onLongClick(it, adapterPosition)
//        }
        return true
    }

    override fun onClick(v: View) {
        listener.onClick(v, adapterPosition)
    }

    val exerciseItemView: TextView = v.findViewById(R.id.textView)
    private val exerciseItemDeleteBtn: ImageView = v.findViewById(R.id.deleteButton)


    init {
        if (deletableItem) {
            exerciseItemDeleteBtn.visibility=View.VISIBLE
            exerciseItemDeleteBtn.setOnClickListener(this)
        } else {
            v.setOnClickListener(this)
        }
    }

}