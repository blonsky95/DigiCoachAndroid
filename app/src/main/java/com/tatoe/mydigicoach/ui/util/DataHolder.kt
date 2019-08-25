package com.tatoe.mydigicoach.ui.util

import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise

object DataHolder {

    lateinit var activeExerciseHolder: Exercise
    lateinit var newExerciseHolder: Exercise

    lateinit var activeBlockHolder: Block
    lateinit var newBlockHolder: Block

    var oldDayHolder: Day? = null
    lateinit var updatedDayHolder: Day

    var pagerPosition: Int = -1 //go back to right position after adding or updating a day, or pressing back


}