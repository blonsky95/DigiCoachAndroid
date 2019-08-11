package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class ItemViewHolder(v: View, var listener: ClickListenerRecyclerView) : RecyclerView.ViewHolder(v),
    View.OnClickListener, View.OnLongClickListener {

    //todo customize this class, add something to account for the delete button, - put the click listener, but hide the visibility when its not a deletable adapter


    override fun onLongClick(v: View?): Boolean {
        v?.let { it ->
            listener.onLongClick(it, adapterPosition)
        }
        return true
    }

    override fun onClick(v: View) {
        listener.onClick(v, adapterPosition)
    }

    val exerciseItemView: TextView = v.findViewById(R.id.textView)


    init {
        v.setOnClickListener(this)
    }

}