package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class CollapsibleItemViewHolder (v: View) :
    RecyclerView.ViewHolder(v) {

    var expanded:Boolean = false

    val resultDate: TextView = v.findViewById(R.id.result_date)
    val resultResult: TextView = v.findViewById(R.id.result_result)

//    val collapsibleLayout: LinearLayout = v.findViewById(R.id.collapsible_layout)

}