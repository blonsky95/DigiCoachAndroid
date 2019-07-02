package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.database.ExerciseDao
import com.tatoe.mydigicoach.entity.Exercise

class ExerciseRepository (private val exerciseDao: ExerciseDao) {

    val allExercises: androidx.lifecycle.LiveData<List<Exercise>> = exerciseDao.getAll()

    suspend fun insert (exercise: Exercise) {
        exerciseDao.addExercise(exercise)
    }
}