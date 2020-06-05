package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferExercise
import com.tatoe.mydigicoach.network.TransferPackage
import kotlinx.coroutines.*
import timber.log.Timber

class ExerciseViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: AppRepository

    private val viewModelJob = SupervisorJob()

    val allExercises: LiveData<List<Exercise>>

    private var db = FirebaseFirestore.getInstance()

    init {
        val appDB = AppRoomDatabase.getInstance(application)
//        Timber.d("Database has been created")
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        Timber.d("Dataviewmodel initialised")

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        allExercises = repository.allExercisesLiveData

        repository.isLoading.value = false
    }



    fun getIsLoading(): MutableLiveData<Boolean> {
        return repository.isLoading
    }



    private fun exerciseToFirestoreFormat(exercise: Exercise): MyCustomFirestoreTransferExercise {
        return MyCustomFirestoreTransferExercise(exercise)
    }

    fun insertExercise(newExercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - insert called")
        repository.insertExercise(newExercise)
    }

    fun updateExercise(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - update called $exercise")
        repository.updateExercise(exercise)
    }

    fun deleteExercise(exercise: Exercise) = viewModelScope.launch {
        Timber.d("ptg - data view model - delete $exercise")
        repository.deleteExercise(exercise)
    }



//    fun insertBlock(block: Block) = viewModelScope.launch {
//        Timber.d("ptg - data view model - insert block called")
//        repository.insertBlock(block)
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun sendExerciseToUser(exercise: Exercise?, username: String) {
        repository.isLoading.value = true

        var docUid: String
        val docRef2 = db.collection("users").whereEqualTo("username", username)
        docRef2.get().addOnSuccessListener { docs ->
            if (docs.isEmpty) {
                //no user with this name exists
                Toast.makeText(
                    getApplication(), " User doesn't exist",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                docUid = docs.documents[0].id

                val docRef = db.collection("users").document(docUid).collection("exercise_transfers")
                docRef.get()
                    .addOnSuccessListener { documents ->
                        docRef.add(
                            ExercisePackage(
                                exerciseToFirestoreFormat(exercise!!),
                                true,
                                FirebaseAuth.getInstance().currentUser!!.email!!,
                                username
                            )
                        )
                        Toast.makeText(
                            getApplication(), "Exercise sent",
                            Toast.LENGTH_SHORT
                        ).show()
                        repository.isLoading.value = false

                    }
                    .addOnFailureListener { exception ->
                        repository.isLoading.value = false
                        Timber.d("get failed with: $exception ")
                    }
            }

        }

    }

    fun updateTransferExercise(exercisePackage: ExercisePackage, newState: String) {
        exercisePackage.mState = TransferPackage.STATE_SAVED
        val docRef = db.document(exercisePackage.documentPath!!)
//        val docRef = db.collection("users").document(exercisePackage.mReceiver!!).collection("transfers")
//            .whereEqualTo("mstate", ExercisePackage.STATE_SENT)

        docRef
            .update("mstate", newState)
            .addOnSuccessListener { Timber.d("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.w("Error updating document: $e") }
    }
}