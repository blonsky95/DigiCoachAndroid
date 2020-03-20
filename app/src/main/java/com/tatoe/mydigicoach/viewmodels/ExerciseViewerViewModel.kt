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
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.*
import timber.log.Timber

class ExerciseViewerViewModel(application: Application, var db: FirebaseFirestore) :
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

    fun getExercisesFromFirestore() = viewModelScope.launch {

        val docRef = db.collection("users").document(DataHolder.userEmail!!).collection("exercises")
        var exercises = mutableListOf<Exercise>()
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    for (document in documents) {
                        Timber.d("DocumentSnapshot data: ${document.data}")
                        exercises.add(document.toObject(Exercise::class.java))
                    }
                } else {
                    Timber.d ("documents is empty or null")
                }
                modifyLocalTable(exercises)
            }
            .addOnFailureListener { exception ->
                Timber.d("get failed with: $exception ")
            }


    }

    private fun modifyLocalTable(exercises: MutableList<Exercise>)= viewModelScope.launch {
        //collect exercises from here

        withContext(Dispatchers.Default)
        {
            Timber.d("About to delete exercises table")
            repository.deleteExercisesTable()
            Timber.d("About to insert firestore exercises")
            repository.insertExercises(exercises)
        }

    }

    fun postExercisesToFirestore(listExercises: List<Exercise>) = viewModelScope.launch {
        val docRef = db.collection("users").document(DataHolder.userEmail!!).collection("exercises")
        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }

                for (exercise in listExercises) {
                    docRef.add(exercise)
                }
//                if (documents == null || documents.isEmpty) {
//                    db.collection("users").document(DataHolder.userEmail!!).collection("exercises").
//                }
//                for (document in documents) {
//                    if (document != null) {
//                        Timber.d("DocumentSnapshot data: ${document.data}")
//                    }
//                }

            }
            .addOnFailureListener { exception ->
                Timber.d("get failed with: $exception ")
            }
    }

    fun insertBlock(block: Block) = viewModelScope.launch {
        Timber.d("ptg - data view model - insert block called")
        repository.insertBlock(block)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}