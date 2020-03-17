package com.tatoe.mydigicoach.ui.util

import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise

object DataHolder {

    var activeExerciseHolder: Exercise? = null

    var activeBlockHolder: Block? = null

    var activeDayHolder: Day? = null

    var userEmail: String? = null

//    lateinit var updatedDayHolder: Day

    var pagerPosition: Int =
        -1 //go back to right position after adding or updating a day, or pressing back

//    fun getBlock(): Block {
//        if (activeBlockHolder != null) {
//            return activeBlockHolder
//        }
//    }


}