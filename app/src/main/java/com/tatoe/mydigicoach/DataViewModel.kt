package com.tatoe.mydigicoach

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.tatoe.mydigicoach.database.AppDatabase
import com.tatoe.mydigicoach.entity.Exercise

class DataViewModel(application: Application) : AndroidViewModel(application) {
        //todo add the coroutine scope
    private val repository: ExerciseRepository

    val allExercises: LiveData<List<Exercise>>

    init {
        val exerciseDao = AppDatabase.buildDatabase(application).ExercisesDao()
        repository = ExerciseRepository(exerciseDao)
        allExercises = repository.allExercises
    }

}