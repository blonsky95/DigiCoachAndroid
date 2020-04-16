package com.tatoe.mydigicoach.ui.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class DayExerciseViewHolder (v: View) :
    RecyclerView.ViewHolder(v) {

    var expanded:Boolean = false

    val mainLinearLayout:LinearLayout? = v.findViewById(R.id.mainLinearLayout)
    val exerciseTextView:TextView? = v.findViewById(R.id.exercise_name)
    val collapsibleLinearLayout:LinearLayout? = v.findViewById(R.id.collapsibleLinearLayout)
    val exerciseResultButton:TextView? = v.findViewById(R.id.resultsBtn)

    fun toggleExpand(){
        if (!expanded) {
            collapsibleLinearLayout!!.visibility=View.VISIBLE
            exerciseResultButton!!.visibility=View.VISIBLE
            expanded=true
        } else {
            collapsibleLinearLayout!!.visibility=View.GONE
            exerciseResultButton!!.visibility=View.GONE
            expanded=false
        }
    }

}