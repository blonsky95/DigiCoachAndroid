package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.database.BlockDao
import com.tatoe.mydigicoach.database.ExerciseDao
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

class AppRepository (private val exerciseDao: ExerciseDao, private val blockDao: BlockDao) {

    val allExercises: androidx.lifecycle.LiveData<List<Exercise>> = exerciseDao.getAll()
    val allBlocks: androidx.lifecycle.LiveData<List<Block>> = blockDao.getAll()

    suspend fun insertExercise (exercise: Exercise) {
        var rowId = exerciseDao.insert(exercise)
        Timber.d("new currentExercise, row: $rowId")
    }

    suspend fun updateExercise(updatedExercise: Exercise) {
        var rowId = exerciseDao.update(updatedExercise)
        Timber.d("updated currentExercise, row: $rowId")

    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise)
        Timber.d("deleted: ${exercise.name}")

    }
}