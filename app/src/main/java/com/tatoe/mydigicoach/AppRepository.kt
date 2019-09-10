package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.database.BlockDao
import com.tatoe.mydigicoach.database.DayDao
import com.tatoe.mydigicoach.database.ExerciseDao
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

class AppRepository(
    private val exerciseDao: ExerciseDao,
    private val blockDao: BlockDao,
    private val dayDao: DayDao
) {

    val allExercises: androidx.lifecycle.LiveData<List<Exercise>> = exerciseDao.getAll()
    val allBlocks: androidx.lifecycle.LiveData<List<Block>> = blockDao.getAll()
    val allDays: androidx.lifecycle.LiveData<List<Day>> = dayDao.getAll()


    suspend fun insertExercise(exercise: Exercise) {
        var rowId = exerciseDao.insert(exercise)
        Timber.d("new currentExercise, row: $rowId")
    }

    suspend fun updateExercise(updatedExercise: Exercise) {
        exerciseDao.update(updatedExercise)
        Timber.d("updated currentExercise: $updatedExercise)")
        updateBlocksContainingExercise(updatedExercise)
    }

    private suspend fun updateBlocksContainingExercise(updatedExercise: Exercise) {
        val blocks = blockDao.getBlocks()

        if (blocks.isNotEmpty()) {
            for (block in blocks) {
                for (exercise in block.components) {
                    Timber.d("looking for ${updatedExercise.exerciseId} and this is ${exercise.exerciseId}")

                    if (exercise.exerciseId == updatedExercise.exerciseId) {
                        Timber.d("MATCH FOUND bef block: $block")
                        block.components[block.components.indexOf(exercise)] = updatedExercise
                        Timber.d("MATCH FOUND after block: $block")
                        updateBlock(block)
                    }
                }
            }
        }
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
        blockDao.update(block)
        updateDaysContainingBlocks(block)
        Timber.d("updated currentBlock")
    }

    private suspend fun updateDaysContainingBlocks(updatedBlock: Block) {
        val days = dayDao.getDays()

        if (days.isNotEmpty()) {
            for (day in days) {
                for (block in day.blocks) {
                    if (block.blockId == updatedBlock.blockId) {
                        Timber.d("MATCH FOUND bef day: $day")
                        day.blocks[day.blocks.indexOf(block)] = updatedBlock
                        Timber.d("MATCH FOUND after day: $day")
                        updateDay(day)
                    }
                }
            }
        }
    }

    suspend fun deleteBlock(block: Block) {
        blockDao.delete(block)
        Timber.d("deleted: ${block.name}")
    }

    suspend fun getDayById(dayId: String): Day? {
        var day = dayDao.findByName(dayId)
        Timber.d("activeDay exists?, row: $day")
        return day
    }

    suspend fun insertDay(day: Day) {
        dayDao.insert(day)
    }

    suspend fun updateDay(day: Day) {
        dayDao.update(day)
    }
}