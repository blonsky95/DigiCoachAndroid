package com.tatoe.mydigicoach.ui.util

import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.TransferPackage

object DataHolder {
    var activeExerciseHolder: Exercise? = null

    var activeDayHolder: Day? = null


    var receivedExercises = arrayListOf<TransferPackage>()

    var userEmail = ""
    var userDocId = ""
    var userName = ""

    var pagerPosition: Int =
        -1 //go back to right position after adding or updating a day, or pressing back




}