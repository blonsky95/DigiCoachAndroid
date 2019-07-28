package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class ExerciseViewHolder(v: View, var listener: ClickListenerRecyclerView) : RecyclerView.ViewHolder(v),
    View.OnClickListener {

    override fun onClick(v: View) {
        listener.onClick(v,adapterPosition)
    }

    val exerciseItemView: TextView = v.findViewById(R.id.textView)

    init {
        v.setOnClickListener(this)
    }

}