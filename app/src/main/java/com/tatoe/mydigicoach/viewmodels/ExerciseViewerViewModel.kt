package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import kotlinx.coroutines.*
import timber.log.Timber

class ExerciseViewerViewModel(application: Application, db: FirebaseFirestore) :
    AndroidViewModel(application) {

    private val repository: AppRepository

    private val viewModelJob = SupervisorJob()

    val allExercises: LiveData<List<Exercise>>

    init {
        val appDB = AppRoomDatabase.getInstance(application)
//        Timber.d("Database has been created")
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        Timber.d("Dataviewmodel initialised")

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)

        allExercises = repository.allExercises
    }

    fun getExercisesFromFirestore(exercises:List<Exercise>) = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            Timber.d("About to delete exercises table")
            repository.deleteExercisesTable()
        }
        Timber.d("About to insert firestore exercises")
        repository.insertExercises(exercises)
    }

    fun insertBlock(block: Block) = viewModelScope.launch{
        Timber.d("ptg - data view model - insert block called")
        repository.insertBlock(block)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}