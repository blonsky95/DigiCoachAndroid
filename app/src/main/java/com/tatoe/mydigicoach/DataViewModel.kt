package com.tatoe.mydigicoach

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class DataViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    val allExercises: LiveData<List<Exercise>>

    val allUserBlocks: LiveData<List<Block>>
    val allAppBlocks: LiveData<List<Block>>
    val allImportBlocks: LiveData<List<Block>>
    val allExportBlocks: LiveData<List<Block>>

    val allDays: LiveData<List<Day>>

    init {

        val appDB=AppRoomDatabase.getInstance(application)
        Timber.d("Database has been created")
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        Timber.d("Dataviewmodel initialised")

        repository = AppRepository(exerciseDao,blockDao,dayDao)

        allExercises = repository.allExercises

        allUserBlocks = repository.allUserBlocks
        allAppBlocks = repository.allAppBlocks
        allImportBlocks=repository.allImportBlocks
        allExportBlocks=repository.allExportBlocks

        allDays = repository.allDays
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun insertExercise(newExercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - insert called")
        repository.insertExercise(newExercise)
    }

    fun updateExercise(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - update called $exercise")
        repository.updateExercise(exercise)
    }

    fun updateExerciseResult(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - update result called $exercise")
        repository.updateExerciseResult(exercise)
    }

//    fun getExerciseById(exerciseId: Int) = viewModelScope.launch {
//        repository.getExerciseById(exerciseId)
//    }

    fun deleteExercise(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - delete called")
        repository.deleteExercise(exercise)
    }

    fun insertBlock(block: Block) = viewModelScope.launch{
        Timber.d("ptg - data view model - insert block called")
        repository.insertBlock(block)
    }

    fun updateBlock(block: Block) = viewModelScope.launch {
        Timber.d("ptg - data view model - update block called")
        repository.updateBlock(block)
    }

    fun deleteBlock(block: Block, deleteExercises:Boolean = false) = viewModelScope.launch {
        Timber.d("ptg - data view model - delete block called")
        if (deleteExercises) {
            for (exercise in block.components) {
                deleteExercise(exercise)
            }
        }
        repository.deleteBlock(block)
    }

    fun getDayById(currentDayId : String) : Day? {
//        Timber.d("ptg - data view model - getDayById called")
        if (allDays.value==null) {
            return null
        }
        for (day in allDays.value!!) {
            if (day.dayId==currentDayId) {
                return day
            }
        }
        return null
    }

    fun insertDay(day: Day) = viewModelScope.launch{
        Timber.d("ptg - data view model - insert day called $day")
        repository.insertDay(day)
    }

    fun updateDay(day: Day) = viewModelScope.launch {
        Timber.d("ptg - data view model - update day called $day")
        repository.updateDay(day)
    }

//    fun deleteDay(day: Day) = viewModelScope.launch {
//        Timber.d("ptg - data view model - delete block called")
//        repository.deleteBlock(day)
//    }



}