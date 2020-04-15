package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class CollapsibleItemViewHolder(
    v: View,
    private var listener: ClickListenerRecyclerView?,
    hasSecondaryButton: Boolean = false
) :
    RecyclerView.ViewHolder(v),
    View.OnClickListener, View.OnLongClickListener {

    val resultDate: TextView = v.findViewById(R.id.result_date)
    val resultResult: TextView = v.findViewById(R.id.result_result)
    private val secondaryBtn: ImageView = v.findViewById(R.id.imageRightExerciseHolder)


    override fun onLongClick(v: View?): Boolean {
        return true
    }

    override fun onClick(v: View) {
        listener?.onClick(v, adapterPosition)
    }

    var expanded: Boolean = false

    init {
        if (hasSecondaryButton) {
            secondaryBtn.visibility = View.VISIBLE
            secondaryBtn.setOnClickListener(this)
        } else {
            v.setOnClickListener(this)
            v.setOnLongClickListener(this)
        }
    }

//    val collapsibleLayout: LinearLayout = v.findViewById(R.id.collapsible_layout)

}