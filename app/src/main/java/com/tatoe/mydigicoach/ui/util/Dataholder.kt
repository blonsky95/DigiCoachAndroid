package com.tatoe.mydigicoach.ui.util

import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

object Dataholder {

    lateinit var activeExerciseHolder: Exercise
    lateinit var newExerciseHolder: Exercise

    fun updateClickedExercise(clickedExercise: Exercise) {

            activeExerciseHolder = clickedExercise

    }

    fun storeNewExercise(mNewExercise :Exercise) {
        newExerciseHolder = mNewExercise
        Timber.d("new exercise is now: ${newExerciseHolder.exerciseId} ${newExerciseHolder.name}")

    }

}