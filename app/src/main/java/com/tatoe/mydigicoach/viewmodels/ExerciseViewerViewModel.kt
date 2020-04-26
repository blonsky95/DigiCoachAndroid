package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.network.MyCustomFirestoreExercise
import kotlinx.coroutines.*
import timber.log.Timber

class ExerciseViewerViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: AppRepository

    private val viewModelJob = SupervisorJob()

    val allExercises: LiveData<List<Exercise>>

    private var db = FirebaseFirestore.getInstance()

    var receivedExercises = MutableLiveData<ArrayList<ExercisePackage>>(arrayListOf())

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

        repository.isLoading.value = false

        receivedExercises=repository.receivedExercisesMediator

    }

    fun postExercisesToFirestore(listExercises: List<Exercise>) = viewModelScope.launch {
        repository.isLoading.value = true

        val docRef = db.collection("users").document(DataHolder.userEmail!!).collection("exercises")
        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }

                for (exercise in listExercises) {
//                    docRef.add(exercise.exerciseResults.resultsArrayList.toList())
//                    var list = mutableListOf<Map<String,Pair<String,String>>>()
//                    var theMap = exerciseToFirestoreMap(exercise)
//                    var pair=Pair("1","POWER")
//                    var map1 = hashMapOf("pair1" to pair)
//                    var map2 = hashMapOf("pair2" to pair)
//                    var arrayList = arrayListOf(map1,map2)
//                    var map = HashMap<String,ArrayList<HashMap<String,Pair<String,String>>>>()
//                    map["da_list"] = arrayList

//                    list.add(map)
                    docRef.add(exerciseToFirestoreFormat(exercise))
                }
                repository.isLoading.value = false

            }
            .addOnFailureListener { exception ->
                repository.isLoading.value = false
                Timber.d("get failed with: $exception ")
            }
    }

    fun getExercisesFromFirestore() = viewModelScope.launch {
        repository.isLoading.value = true
        val docRef = db.collection("users").document(DataHolder.userEmail!!).collection("exercises")
        var exercises = mutableListOf<Exercise>()
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    for (document in documents) {
                        Timber.d("DocumentSnapshot data: ${document.data}")
                        exercises.add(document.toObject(MyCustomFirestoreExercise::class.java).toExercise())
                    }
                } else {
                    Timber.d("documents is empty or null")
                }
                modifyLocalTable(exercises)
                repository.isLoading.value = false
            }
            .addOnFailureListener { exception ->
                repository.isLoading.value = false
                Timber.d("get failed with: $exception ")
            }


    }

    fun getIsLoading(): MutableLiveData<Boolean> {
        return repository.isLoading
    }

    private fun modifyLocalTable(exercises: MutableList<Exercise>) = viewModelScope.launch {
        //collect exercises from here

        withContext(Dispatchers.Default)
        {
            Timber.d("About to delete exercises table")
            repository.deleteExercisesTable()
            Timber.d("About to insert firestore exercises")
            repository.insertExercises(exercises)
        }

    }

    private fun exerciseToFirestoreFormat(exercise: Exercise): MyCustomFirestoreExercise {
        return MyCustomFirestoreExercise(exercise)
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

    private fun firestoreFormatToExercise(myCustomFirestoreExercise: MyCustomFirestoreExercise): Exercise {
        return myCustomFirestoreExercise.toExercise()
    }

    fun insertBlock(block: Block) = viewModelScope.launch {
        Timber.d("ptg - data view model - insert block called")
        repository.insertBlock(block)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun sendExerciseToUser(exercise: Exercise?, username: String) {
        repository.isLoading.value = true

        val docRef = db.collection("users").document(username).collection("transfers")
        docRef.get()
            .addOnSuccessListener { documents ->
                docRef.add(
                    ExercisePackage(
                        exerciseToFirestoreFormat(exercise!!),
                        FirebaseAuth.getInstance().currentUser!!.email!!,
                        username,
                        true
                    )
                )
                repository.isLoading.value = false

            }
            .addOnFailureListener { exception ->
                repository.isLoading.value = false
                Timber.d("get failed with: $exception ")
            }
    }

    fun updateTransferExercise(exercisePackage: ExercisePackage, newState: String) {
        exercisePackage.mState = ExercisePackage.STATE_SAVED
        val docRef = db.document(exercisePackage.documentPath!!)
//        val docRef = db.collection("users").document(exercisePackage.mReceiver!!).collection("transfers")
//            .whereEqualTo("mstate", ExercisePackage.STATE_SENT)

        docRef
            .update("mstate", newState)
            .addOnSuccessListener { Timber.d("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.w("Error updating document: $e") }
    }
}