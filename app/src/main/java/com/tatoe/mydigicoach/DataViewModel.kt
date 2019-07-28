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

//    lateinit var activeExerciseHolder: Exercise
//    lateinit var newExerciseHolder: Exercise


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
        Timber.d("ptg - data view model - insert called")
        repository.insert(newExercise)
    }

    fun update(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - update called")
        repository.update(exercise)
    }

    fun delete(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - delete called")
        repository.delete(exercise)
    }

//    fun updateClickedExercise(position: Int) {
//
//        val listExercises = allExercises.value
//
//        if (listExercises != null && listExercises.isNotEmpty()) {
//            activeExerciseHolder = listExercises[position]
//            Timber.d("updating exercise is now: ${activeExerciseHolder.exerciseId} ${activeExerciseHolder.name}")
//        }
//    }
//
//    fun storeNewExercise(mNewExercise :Exercise) {
//        newExerciseHolder = mNewExercise
//        Timber.d("new exercise is now: ${newExerciseHolder.exerciseId} ${newExerciseHolder.name}")
//
//    }


}