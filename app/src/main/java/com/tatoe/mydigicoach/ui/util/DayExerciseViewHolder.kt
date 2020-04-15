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

    var expanded:Boolean? = false

    val exerciseTextView:TextView? = v.findViewById(R.id.exercise_name)
    val exerciseResultButton:ImageView? = v.findViewById(R.id.result_button)

}