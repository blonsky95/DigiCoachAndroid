package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class DayItemViewHolder (v: View) :
    RecyclerView.ViewHolder(v) {

    var expanded:Boolean = false

    val blockName: TextView = v.findViewById(R.id.block_title)
    val collapsibleLayout: LinearLayout = v.findViewById(R.id.collapsible_layout)

}