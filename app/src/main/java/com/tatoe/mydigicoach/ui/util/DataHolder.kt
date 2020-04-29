package com.tatoe.mydigicoach.ui.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.ExercisePackage

object DataHolder {
    var activeExerciseHolder: Exercise? = null

    var activeBlockHolder: Block? = null

    var activeDayHolder: Day? = null


    var receivedExercises = arrayListOf<ExercisePackage>()

    var userEmail = ""
    var userDocId = ""
    var userName = ""

    fun addReceivedExercise(newExercise: ExercisePackage) {
        receivedExercises.add(newExercise)
    }

    fun emptyReceivedExercises() {
        receivedExercises = arrayListOf()
    }

//    lateinit var updatedDayHolder: Day

    var pagerPosition: Int =
        -1 //go back to right position after adding or updating a day, or pressing back

//    fun getBlock(): Block {
//        if (activeBlockHolder != null) {
//            return activeBlockHolder
//        }
//    }


}