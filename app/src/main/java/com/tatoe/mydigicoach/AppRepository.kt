package com.tatoe.mydigicoach

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.tatoe.mydigicoach.database.BlockDao
import com.tatoe.mydigicoach.database.DayDao
import com.tatoe.mydigicoach.database.ExerciseDao
import com.tatoe.mydigicoach.database.FriendDao
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.ExercisePackage
import timber.log.Timber


class AppRepository(
    private val exerciseDao: ExerciseDao,
    private val friendDao: FriendDao,
    private val dayDao: DayDao
) {
    lateinit var allExercises: List<Exercise>
    lateinit var allDays: List<Day>

    val allFriends:LiveData<List<Friend>> = friendDao.getAllLiveData()

    val allExercisesLiveData: LiveData<List<Exercise>> = exerciseDao.getAllLiveData()
//    val allExercises: List<Exercise> = exerciseDao.getAll()

    val allDaysLiveData: LiveData<List<Day>> = dayDao.getAllLiveData()
//    val allDays: List<Day> = dayDao.getAll()

    val dayToday: LiveData<Day> = dayDao.findByName(Day.dateToDayID(Day.getTodayDate()))

    var isLoading = MutableLiveData<Boolean>()

    val receivedExercisesMediator = MediatorLiveData<ArrayList<ExercisePackage>>()

    private val ACTION_UPDATE = 1
    private val ACTION_DELETE = 2

    fun addDataSource(data: LiveData<ArrayList<ExercisePackage>>) {
        receivedExercisesMediator.addSource(data, receivedExercisesMediator::setValue)
    }

    fun removeDataSource(data: LiveData<ArrayList<ExercisePackage>>) {
        receivedExercisesMediator.removeSource(data)
    }

    suspend fun getAllExercises():List<Exercise> {
        return exerciseDao.getAll()
    }

    suspend fun getAllDays():List<Day> {
        return dayDao.getAll()
    }

    suspend fun insertExercise(exercise: Exercise) {
        var rowId = exerciseDao.insert(exercise)
        Timber.d("new activeExercise, row: $rowId")
    }

    suspend fun updateExercise(updatedExercise: Exercise) {
        exerciseDao.update(updatedExercise)
        Timber.d("updated activeExercise: $updatedExercise)")
//        updateBlocksContainingExercise(ACTION_UPDATE,updatedExercise)
        updateDaysContainingExercise(ACTION_UPDATE,updatedExercise)
    }

    suspend fun updateExerciseResult(updatedExercise: Exercise) {
        exerciseDao.update(updatedExercise)
        Timber.d("updated currentExerciseResult: $updatedExercise)")
//        updateBlocksContainingExercise(ACTION_UPDATE,updatedExercise)
        updateDaysContainingExercise(ACTION_UPDATE,updatedExercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise)
        Timber.d("deleted: ${exercise.name}")
//        updateBlocksContainingExercise(ACTION_DELETE,exercise)
        updateDaysContainingExercise(ACTION_DELETE,exercise)

    }

    suspend fun deleteExercisesTable(){
        exerciseDao.deleteTable()
    }

    suspend fun insertExercises(exercises:List<Exercise>) : List<Long>{
        return exerciseDao.insertAll(exercises)
    }

    private suspend fun updateDaysContainingExercise(actionCode:Int, exercise: Exercise) {
        val days = dayDao.getAll()

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

    suspend fun insertDay(day: Day) {
        dayDao.insert(day)
    }

    suspend fun insertDays(days:List<Day>) : List<Long> {
        return dayDao.insertAll(days)
    }

    suspend fun updateDay(day: Day) {
        dayDao.update(day)
    }

    suspend fun deleteDaysTable() {
        dayDao.deleteTable()
    }
}