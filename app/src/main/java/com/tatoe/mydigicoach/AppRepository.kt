package com.tatoe.mydigicoach

import androidx.lifecycle.MutableLiveData
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

    val allUserBlocks: androidx.lifecycle.LiveData<List<Block>> = blockDao.getUserMadeLive()
    val allAppBlocks: androidx.lifecycle.LiveData<List<Block>> = blockDao.getPremadeBlocksLive()
    val allImportBlocks: androidx.lifecycle.LiveData<List<Block>> = blockDao.getImportBlocksLive()
    val allExportBlocks: androidx.lifecycle.LiveData<List<Block>> = blockDao.getExportBlocksLive()

    val allDays: androidx.lifecycle.LiveData<List<Day>> = dayDao.getAll()
    val dayToday: androidx.lifecycle.LiveData<Day> = dayDao.findByName(Day.dateToDayID(Day.getTodayDate()))

    var isLoading = MutableLiveData<Boolean>()

    private val ACTION_UPDATE = 1
    private val ACTION_DELETE = 2

    suspend fun insertExercise(exercise: Exercise) {
        var rowId = exerciseDao.insert(exercise)
        Timber.d("new activeExercise, row: $rowId")
    }

    suspend fun updateExercise(updatedExercise: Exercise) {
        exerciseDao.update(updatedExercise)
        Timber.d("updated activeExercise: $updatedExercise)")
        updateBlocksContainingExercise(ACTION_UPDATE,updatedExercise)
        updateDaysContainingExercise(ACTION_UPDATE,updatedExercise)
    }

    suspend fun updateExerciseResult(updatedExercise: Exercise) {
        exerciseDao.update(updatedExercise)
        Timber.d("updated currentExerciseResult: $updatedExercise)")
        updateBlocksContainingExercise(ACTION_UPDATE,updatedExercise)
        updateDaysContainingExercise(ACTION_UPDATE,updatedExercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise)
        Timber.d("deleted: ${exercise.name}")
        updateBlocksContainingExercise(ACTION_DELETE,exercise)
        updateDaysContainingExercise(ACTION_DELETE,exercise)

    }

    suspend fun deleteExercisesTable(){
        exerciseDao.deleteTable()
    }

    suspend fun insertExercises(exercises:List<Exercise>){
        exerciseDao.insertAll(exercises)
    }

    private suspend fun updateBlocksContainingExercise(actionCode:Int, exercise: Exercise) {
        Timber.d("updating blocks containing exercise: $exercise)")

        val blocks = blockDao.getUserMadeBlocks()

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

    private suspend fun updateDaysContainingExercise(actionCode:Int, exercise: Exercise) {
        val days = dayDao.getDays()

        if (days.isNotEmpty()) {
            for (day in days) {
                for (tmpExercise in day.exercises) {
                    if (tmpExercise.exerciseId == exercise.exerciseId) {
                        Timber.d("EXERCISE DELETE day: ${day.dayId}")
                        Timber.d("EXERCISE DELETE exercise: ${exercise.name}")

                        if (actionCode==ACTION_UPDATE) {
                            day.exercises[day.exercises.indexOf(tmpExercise)] = exercise
                            updateDay(day)
                        }
                        if (actionCode==ACTION_DELETE) {
                            day.exercises.removeAt(day.exercises.indexOf(tmpExercise))
                            updateDay(day)
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
                            updateDay(day)
                        }
                    }
                }
            }
        }
    }

//    suspend fun getDayById(dayId: String): Day? {
//        var day = dayDao.findByName(dayId)
//        Timber.d("activeDay exists?, row: $day")
//        return day
//    }

    suspend fun insertDay(day: Day) {
        dayDao.insert(day)
    }

    suspend fun updateDay(day: Day) {
        dayDao.update(day)
    }
}