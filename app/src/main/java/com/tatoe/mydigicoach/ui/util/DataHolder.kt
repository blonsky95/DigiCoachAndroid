package com.tatoe.mydigicoach.ui.util

import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise

object DataHolder {

    lateinit var activeExerciseHolder: Exercise
    lateinit var newExerciseHolder: Exercise

    var activeBlockHolder: Block? = null

    lateinit var newBlockHolder: Block

    var oldDayHolder: Day? = null
    lateinit var updatedDayHolder: Day

    var pagerPosition: Int =
        -1 //go back to right position after adding or updating a day, or pressing back

//    fun getBlock(): Block {
//        if (activeBlockHolder != null) {
//            return activeBlockHolder
//        }
//    }


}