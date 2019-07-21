package com.tatoe.mydigicoach

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class DataViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExerciseRepository

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    val allExercises: LiveData<List<Exercise>>

    init {
        val exerciseDao = AppRoomDatabase.buildDatabase(application).exercisesDao()
        repository = ExerciseRepository(exerciseDao)
        allExercises = repository.allExercises
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun insert(newExercise: Exercise) = viewModelScope.launch {
        Timber.d("data view model - insert called")
        repository.insert(newExercise)
    }


}