package com.tatoe.mydigicoach.ui.util

import android.view.View
import com.tatoe.mydigicoach.ui.calendar.DayCreator

interface ClickListenerRecyclerView {

    fun onClick(view:View,position:Int) {

    }

    fun onClick(view: View,position: Int,actionId:Int){

    }

    fun onClick(view: View,position: Int, holder:EditableItemViewHolder){

    }

    fun onClick(view: View,position: Int, holder:DayCreator.MyCheckedExerciseViewHolder){

    }

    fun onLongClick(view: View, position: Int) {

    }

}