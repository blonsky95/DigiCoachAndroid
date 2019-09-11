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

    private val ACTION_UPDATE = 1
    private val ACTION_DELETE = 2

    suspend fun insertExercise(exercise: Exercise) {
        var rowId = exerciseDao.insert(exercise)
        Timber.d("new currentExercise, row: $rowId")
    }

    suspend fun updateExercise(updatedExercise: Exercise) {
        exerciseDao.update(updatedExercise)
        Timber.d("updated currentExercise: $updatedExercise)")
        updateBlocksContainingExercise(ACTION_UPDATE,updatedExercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise)
        Timber.d("deleted: ${exercise.name}")
        updateBlocksContainingExercise(ACTION_DELETE,exercise)

    }

    private suspend fun updateBlocksContainingExercise(actionCode:Int, exercise: Exercise) {
        val blocks = blockDao.getBlocks()

        if (blocks.isNotEmpty()) {
            for (block in blocks) { //todo make here a contains function in block class, and return position
                for (tmpExercise in block.components) {
                    Timber.d("looking for ${exercise.exerciseId} and this is ${tmpExercise.exerciseId}")

                    if (tmpExercise.exerciseId == exercise.exerciseId) {
//                        Timber.d("MATCH FOUND bef block: $block")
                        if (actionCode==ACTION_UPDATE) {
                            block.components[block.components.indexOf(tmpExercise)] = exercise
                            updateBlock(block)
                        }
                        if (actionCode==ACTION_DELETE) {
                            block.components.removeAt(block.components.indexOf(tmpExercise))
                            updateBlock(block)
                        }
                    }
                }
            }
        }
    }

    suspend fun insertBlock(block: Block) {
        var rowId = blockDao.addBlock(block)
        Timber.d("new block, row: $rowId")
    }

    suspend fun updateBlock(block: Block) {
        blockDao.update(block)
        updateDaysContainingBlocks(ACTION_UPDATE,block)
        Timber.d("updated currentBlock")
    }

    suspend fun deleteBlock(block: Block) {
        blockDao.delete(block)
        updateDaysContainingBlocks(ACTION_DELETE,block)
        Timber.d("deleted: ${block.name}")
    }

    private suspend fun updateDaysContainingBlocks(actionCode:Int, block: Block) {
        val days = dayDao.getDays()

        if (days.isNotEmpty()) {
            for (day in days) {
                for (tmpBlock in day.blocks) {
                    if (tmpBlock.blockId == block.blockId) {

                        if (actionCode==ACTION_UPDATE) {
                            day.blocks[day.blocks.indexOf(tmpBlock)] = block
                            updateDay(day)
                        }
                        if (actionCode==ACTION_DELETE) {
                            day.blocks.removeAt(day.blocks.indexOf(tmpBlock))
                            updateBlock(block)
                        }
                    }
                }
            }
        }
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