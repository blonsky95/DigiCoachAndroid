package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.database.ExerciseDao
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

class ExerciseRepository (private val exerciseDao: ExerciseDao) {

    val allExercises: androidx.lifecycle.LiveData<List<Exercise>> = exerciseDao.getAll()

    suspend fun insert (exercise: Exercise) {
        var rowId = exerciseDao.addExercise(exercise)
        Timber.d("new exercise, row: $rowId")
        //todo add snackbar + check if returns row id - if it does put success message
    }
}