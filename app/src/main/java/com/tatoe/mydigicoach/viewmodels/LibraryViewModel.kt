package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferExercise
import com.tatoe.mydigicoach.network.MyCustomStoreExercise
import kotlinx.coroutines.launch
import timber.log.Timber

class LibraryViewModel(var application: Application, var db: FirebaseFirestore) : ViewModel() {

    val isDoingBackgroundTask = MutableLiveData<Boolean>(true)
    val isInsertingExercises = MutableLiveData<Boolean>(false)

    private val repository: AppRepository
    val categoriesList = MutableLiveData<ArrayList<String>>(arrayListOf())
    val storeExercisesList = MutableLiveData<ArrayList<MyCustomStoreExercise>>(arrayListOf())
    val myExercises: LiveData<List<Exercise>>

    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)

        myExercises = repository.allExercises

        getLibraryExercises()
    }

    fun getLibraryExercises() {
        isDoingBackgroundTask.postValue(true)
        var docRef = db.collection("store_exercises")

        docRef.get().addOnSuccessListener { docs ->

            var categoryList = arrayListOf<String>()
            var exercisePairList = arrayListOf<MyCustomStoreExercise>()

            for (document in docs) {
                var exerciseCategory = document["name"] as String
                categoryList.add(exerciseCategory)
                var docRef2 =
                    db.collection("store_exercises").document(document.id).collection("exercises")
                docRef2.get().addOnSuccessListener { docs2 ->
                    for (doc in docs2) {
                        exercisePairList.add(
                            MyCustomStoreExercise(doc.toObject(MyCustomFirestoreTransferExercise::class.java).toExercise(),exerciseCategory)
                        )
                    }
                    //little cheat to only trigger observers when all categories are present
                    if (exercisePairList.size == 16) {
                        Timber.d("posting to observers ")
                        categoriesList.value = categoryList
                        storeExercisesList.postValue(exercisePairList)
                        isDoingBackgroundTask.postValue(false)
                    }
                }
            }

        }.addOnFailureListener { e ->
            Timber.d("Error recovering doc: $e")
        }

    }

    fun addBunchOfStubStoreExercises() {
        val powerlifting = arrayListOf<Exercise>()
        powerlifting.add(Exercise("Squats", "Full depth squats"))
        powerlifting.add(Exercise("Bench Press", "Touch chest for max gains"))
        powerlifting.add(
            Exercise(
                "Deadlift",
                "Technique-wise make sure straight back, and engaging posterior chain when starting lift, start with legs not with back"
            )
        )

        for (exercise in powerlifting) {
            db.collection("store_exercises").document("powerlifting").collection("exercises")
                .document(exercise.name)
                .set(MyCustomFirestoreTransferExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }


    }

    fun importExercises(toImportStoreExercises: java.util.ArrayList<MyCustomStoreExercise>) = viewModelScope.launch {
        isInsertingExercises.postValue(true)
        val toImportExercises = arrayListOf<Exercise>()
        for (storeExe in toImportStoreExercises) {
            toImportExercises.add(storeExe.mExercise)
        }
        repository.insertExercises(toImportExercises.toList()).let {
            //when operation is done post that inserting is finiished --- isinserting = false
            isInsertingExercises.postValue(false)
        }

    }
}