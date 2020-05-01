package com.tatoe.mydigicoach.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.MyCustomFirestoreExercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.HashMap

class LibraryViewModel(var application: Application, var db: FirebaseFirestore) : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    val isDoingBackgroundTask = MutableLiveData<Boolean>(false)
    private val repository: AppRepository

    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)
    }

    //todo observe the online stored_exercises, and have a list of exercises of live data , actually make it a subclass of exercise that contains the category??
    // also store it so its offline, and not every time you log in

    fun addBunchOfStubStoreExercises() {
        val olympicLiftingExes = arrayListOf<Exercise>()
        olympicLiftingExes.add(Exercise("Snatch","Lift bar from ground, hit at hips and gain control over bar either as power snatch or normal snatch (full depth)"))
        olympicLiftingExes.add(Exercise("Hang snatch","Lift bar from hips, gain control over bar either as power snatch or normal snatch (full depth)"))
        olympicLiftingExes.add(Exercise("Clean","Lift bar from ground, hit at hips and gain control over bar either as power clean or normal clean (full depth)"))
        olympicLiftingExes.add(Exercise("Hang Clean","Lift bar from hips, gain control over bar either as power snatch or normal snatch (full depth)"))
        olympicLiftingExes.add(Exercise("Clean & jerk","Lift bar from ground, hit at hips and gain control over bar"))

        val longDistRuns = arrayListOf<Exercise>()
        longDistRuns.add(Exercise("3km","3km optimal time 11:00"))
        longDistRuns.add(Exercise("5km","5km optimal time 20:00"))
        longDistRuns.add(Exercise("10km","10km optimal time 45:00"))
        longDistRuns.add(Exercise("Half-marathon","21.098km"))
        longDistRuns.add(Exercise("Marathon","42.196km lol have fun"))

        val sprints = arrayListOf<Exercise>()
        sprints.add(Exercise("30m","between 5 and 8 reps, recovery of 3/4 minutes minimum"))
        sprints.add(Exercise("100m","2 sets of 10 runs at 85% intensity, with 1-2 min recovery, full rest between sets"))
        sprints.add(Exercise("Lactic pyramid","100-120-150-180-150-120-100 recovery of 5-6 min"))

        val powerlifting = arrayListOf<Exercise>()
        powerlifting.add(Exercise("Squats","Full depth squats"))
        powerlifting.add(Exercise("Bench Press","Touch chest for max gains"))
        powerlifting.add(Exercise("Deadlift","Technique-wise make sure straight back, and engaging posterior chain when starting lift, start with legs not with back"))

        for (exercise in olympicLiftingExes) {
            db.collection("store_exercises").document("olympic_lifting").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreExercise(exercise))
                .addOnSuccessListener {
                    Timber.d("DocumentSnapshot successfully written!")
                }
                .addOnFailureListener {
                        e -> Timber.d("Error writing document: $e")
                }
        }

        for (exercise in longDistRuns) {
            db.collection("store_exercises").document("long_distance_running").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }

        for (exercise in sprints) {
            db.collection("store_exercises").document("sprinting").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }

        for (exercise in powerlifting) {
            db.collection("store_exercises").document("powerlifting").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }


    }
}