package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.database.ExerciseDao
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

class ExerciseRepository (private val exerciseDao: ExerciseDao) {

    val allExercises: androidx.lifecycle.LiveData<List<Exercise>> = exerciseDao.getAll()

    suspend fun insert (exercise: Exercise) {
        var rowId = exerciseDao.addExercise(exercise)
        Timber.d("new currentExercise, row: $rowId")
    }

    suspend fun update(updatedExercise: Exercise) {
        var rowId = exerciseDao.update(updatedExercise)
        Timber.d("updated currentExercise, row: $rowId")

    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
        Timber.d("deleted: ${exercise.name}")

    }
}