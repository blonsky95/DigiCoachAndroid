package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.day.CustomAdapterFragment

class CollapsibleItemViewHolderDay (v: View, itemHolderType:Int) :
    RecyclerView.ViewHolder(v) {

//    companion object {
//        const val BLOCK_ITEM_HOLDER = CustomAdapterFragment.BLOCK_TYPE_ADAPTER
//        const val EXERCISE_WITH_RESULT_ITEM_HOLDER = CustomAdapterFragment.EXERCISE_TYPE_ADAPTER
//    }


    var expanded:Boolean? = false

    val itemTitle: TextView? = v.findViewById(R.id.block_title)
    val collapsibleLayout: LinearLayout? = v.findViewById(R.id.collapsible_layout)

    val exerciseTextView:TextView? = v.findViewById(R.id.exercise_name)
    val exerciseResultButton:ImageView? = v.findViewById(R.id.result_button)

}