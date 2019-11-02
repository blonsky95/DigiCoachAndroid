package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

object ImportExportUtils {

    fun exportExercises(allExercises: List<Exercise>, selectedIndexes: ArrayList<Int>) {
        val selectedExercises = arrayListOf<Exercise>()
        for (index in selectedIndexes) {
            selectedExercises.add(allExercises[index])
        }
        Timber.d("Exercises to be exported: $selectedExercises")
    }

    //todo see whats happening with results

    //1. given an exercise array list, all exercises. And an array of Ints, select exercises, take
    //results out and write them to file
}