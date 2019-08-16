package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.database.BlockDao
import com.tatoe.mydigicoach.database.DayDao
import com.tatoe.mydigicoach.database.ExerciseDao
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

class AppRepository(private val exerciseDao: ExerciseDao, private val blockDao: BlockDao, private val dayDao: DayDao) {

    val allExercises: androidx.lifecycle.LiveData<List<Exercise>> = exerciseDao.getAll()
    val allBlocks: androidx.lifecycle.LiveData<List<Block>> = blockDao.getAll()
    val allDays: androidx.lifecycle.LiveData<List<Day>> = dayDao.getAll()


    suspend fun insertExercise(exercise: Exercise) {
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

    suspend fun insertBlock(block: Block) {
        var rowId = blockDao.addBlock(block)
        Timber.d("new block, row: $rowId")
    }

    suspend fun updateBlock(block: Block) {
        var rowId = blockDao.update(block)
        Timber.d("updated currentBlock, row: $rowId")
    }

    suspend fun deleteBlock(block: Block) {
        blockDao.delete(block)
        Timber.d ("deleted: ${block.name}")
    }

    suspend fun dayExists(dayId: String) {
        var day = dayDao.findByName(dayId)
        Timber.d("day exists?, row: $day")

    }
}