package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class ResultsViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: AppRepository
    private val viewModelJob = SupervisorJob()

    init {
        val appDB = AppRoomDatabase.getInstance(application, DataHolder.userName)
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        Timber.d("Dataviewmodel initialised")

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun updateExerciseResult(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - update result called $exercise")
        repository.updateExerciseResult(exercise)
    }
}