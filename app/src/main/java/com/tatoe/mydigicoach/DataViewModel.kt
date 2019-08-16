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
    val allBlocks: LiveData<List<Block>>
    val allDays: LiveData<List<Day>>

    init {
        val exerciseDao = AppRoomDatabase.buildDatabase(application).exercisesDao()
        val blockDao = AppRoomDatabase.buildDatabase(application).blockDao()
        val dayDao = AppRoomDatabase.buildDatabase(application).dayDao()


        repository = AppRepository(exerciseDao,blockDao,dayDao)
        allExercises = repository.allExercises
        allBlocks = repository.allBlocks
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
        Timber.d("ptg - data view model - update called")
        repository.updateExercise(exercise)
    }

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

    fun deleteBlock(block: Block) = viewModelScope.launch {
        Timber.d("ptg - data view model - delete block called")
        repository.deleteBlock(block)
    }

    fun dayExists(dayId : String) = viewModelScope.launch {
        Timber.d("ptg - data view model - dayExists called")
        repository.dayExists(dayId)

    }



}