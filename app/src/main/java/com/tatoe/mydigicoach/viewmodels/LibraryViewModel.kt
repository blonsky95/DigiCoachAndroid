package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.MyCustomFirestoreExercise
import kotlinx.coroutines.launch
import timber.log.Timber

class LibraryViewModel(var application: Application, var db: FirebaseFirestore) : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    val isDoingBackgroundTask = MutableLiveData<Boolean>(true)
    val isInsertingExercises = MutableLiveData<Boolean>(false)

    private val repository: AppRepository
    val categoriesList = MutableLiveData<ArrayList<String>>(arrayListOf())
    val exercisesPairsList = MutableLiveData<ArrayList<Pair<String, Exercise>>>(arrayListOf())


    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)

        getLibraryExercises()
    }

    private fun getLibraryExercises() {
        isDoingBackgroundTask.postValue(true)
        var docRef = db.collection("store_exercises")

        docRef.get().addOnSuccessListener { docs ->

            var categoryList = arrayListOf<String>()
            var exercisePairList = arrayListOf<Pair<String, Exercise>>()

            for (document in docs) {
                var exerciseCategory = document["name"] as String
                categoryList.add(exerciseCategory)
                var docRef2 =
                    db.collection("store_exercises").document(document.id).collection("exercises")
                docRef2.get().addOnSuccessListener { docs2 ->
                    for (doc in docs2) {
                        exercisePairList.add(
                            Pair(
                                exerciseCategory,
                                doc.toObject(MyCustomFirestoreExercise::class.java).toExercise()
                            )
                        )
                    }
                    //little cheat to only trigger observers when all categories are present
                    if (exercisePairList.size == 16) {
                        Timber.d("posting to observers ")
                        categoriesList.value = categoryList
                        exercisesPairsList.value = exercisePairList
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
                .set(MyCustomFirestoreExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }


    }

    fun importExercises(toImportExercises: java.util.ArrayList<Exercise>) = viewModelScope.launch {
        isInsertingExercises.value=true

        repository.insertExercises(toImportExercises.toList()).let {
            isInsertingExercises.value=false
        }

    }
}