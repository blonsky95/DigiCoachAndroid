package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferDay
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferExercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class ProfileViewModel(var db: FirebaseFirestore, var application: Application) : ViewModel() {

    private var user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    val userName = MutableLiveData<String>("")
    val userEmail = MutableLiveData<String>(user.email)
    val lastUploadTime = MutableLiveData<String>("-")

    private val repository: AppRepository
//    val allExercises: List<Exercise>
//    val allDays: List<Day>


    init {
        getUsername()

        val appDB = AppRoomDatabase.getInstance(application)
//        Timber.d("Database has been created")
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        Timber.d("Dataviewmodel initialised")

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        repository.isLoading.value = false

        observeLastUploadValue()
    }

    private fun observeLastUploadValue() {
        val docRef = db.collection("users").whereEqualTo("email", userEmail.value)

        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (snapshot.documents.isNotEmpty()) {
                    val doc = snapshot.documents[0]
                    if (doc["last_upload"] != null) {
                        lastUploadTime.postValue(doc["last_upload"].toString())
                    }
                }
            }
        }
    }

    fun getIsLoading(): MutableLiveData<Boolean> {
        return repository.isLoading
    }

    private fun getUsername() {
        val docRef = db.collection("users").whereEqualTo("email", userEmail.value)
        docRef.get().addOnSuccessListener { docs ->
            if (docs.isEmpty) {
                return@addOnSuccessListener
            } else {
                val doc = docs.documents[0]
                userName.postValue(doc["username"].toString())
            }
        }
    }

    fun uploadBackup() = viewModelScope.launch {
        repository.isLoading.value = true
        Timber.d("time upload reset is 0 ")

        //using asynchronous functions, so by using this you create a deffered class which you can call the
        //.await() method, once its done it will carry out that code
        var allExes = async { repository.getAllExercises() }
        var allDays = async { repository.getAllDays() }

        if (!allExes.await().isNullOrEmpty()) {
            postExercisesToFirestore(allExes.await())
        }

        if (!allDays.await().isNullOrEmpty()) {
            postDaysToFirestore(allDays.await())
        }

        val docRef = db.collection("users").whereEqualTo("email", userEmail.value)
        docRef.get().addOnSuccessListener { docs ->
            if (docs.isEmpty) {
                return@addOnSuccessListener
            } else {
                val doc = docs.documents[0]
                doc.reference.update("last_upload",Day.hoursMinutesDateFormat.format(Date(System.currentTimeMillis())))
                    .addOnSuccessListener {
                        Timber.d("time X is ${System.currentTimeMillis()-timenow} ")

                        repository.isLoading.value = false
                        Timber.d("Yay worked") }
                    .addOnFailureListener { Timber.d("damnnnn didnt") }

//                doc.update("capital", true)
//                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
//                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
//                if (doc["last_upload"] != null) {
//                    lastUploadTime.postValue(doc["last_upload"].toString())
//                }
            }
        }
              Timber.d("time W is ${System.currentTimeMillis()-timenow} ")

    }

    fun postExercisesToFirestore(listExercises: List<Exercise>) = viewModelScope.launch {

        val docRef =
            db.collection("users").document(DataHolder.userDocId).collection("my_exercises")
        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }

                for (exercise in listExercises) {
                    docRef.add(MyCustomFirestoreTransferExercise(exercise))
                }

            }
            .addOnFailureListener { exception ->
                Timber.d("get failed with: $exception ")
            }
    }

    private fun postDaysToFirestore(days: List<Day>) = viewModelScope.launch {
        val docRef = db.collection("users").document(DataHolder.userDocId).collection("my_days")
        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }

                for (day in days) {
                    docRef.add(MyCustomFirestoreTransferDay(day))
                }

            }
            .addOnFailureListener { exception ->
                Timber.d("get failed with: $exception ")
            }
    }
    var timenow= 555L
    fun downloadBackup() {
        repository.isLoading.value = true
        Timber.d("time downlaod reset is 0 ")
        timenow = System.currentTimeMillis()

        getExercisesFromFirestore()
        getDaysFromFirestore()
        Timber.d("time F is ${System.currentTimeMillis()-timenow} ")


    }

    fun getExercisesFromFirestore() = viewModelScope.launch {
        val docRef = db.collection("users").document(DataHolder.userDocId)
            .collection("my_exercises")
        var exercises = mutableListOf<Exercise>()
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    for (document in documents) {
                        Timber.d("DocumentSnapshot data: ${document.data}")
                        exercises.add(document.toObject(MyCustomFirestoreTransferExercise::class.java).toExercise())
                    }
                } else {
                    Timber.d("documents is empty or null")
                }
                Timber.d("time D is ${System.currentTimeMillis()-timenow} ")

                modifyLocalExercisesTable(exercises)
                Timber.d("time E is ${System.currentTimeMillis()-timenow} ")

//                repository.isLoading.value = false
            }
            .addOnFailureListener { exception ->
//                repository.isLoading.value = false
                Timber.d("get failed with: $exception ")
            }
    }

    private fun getDaysFromFirestore() = viewModelScope.launch {
        val docRef = db.collection("users").document(DataHolder.userDocId)
            .collection("my_days")
        var days = mutableListOf<Day>()
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    for (document in documents) {
                        Timber.d("DocumentSnapshot data: ${document.data}")
                        days.add(document.toObject(MyCustomFirestoreTransferDay::class.java).toDay())
                    }
                } else {
                    Timber.d("documents is empty or null")
                }
                Timber.d("time B is ${System.currentTimeMillis()-timenow} ")

                modifyLocalDaysTable(days)
//                repository.isLoading.value = false
                Timber.d("time C is ${System.currentTimeMillis()-timenow} ")

            }
            .addOnFailureListener { exception ->
//                repository.isLoading.value = false
                Timber.d("get failed with: $exception ")
            }
    }

    private fun modifyLocalExercisesTable(exercises: MutableList<Exercise>) =
        viewModelScope.launch {
            //        withContext(Dispatchers.Default)
//        {
            Timber.d("time N is ${System.currentTimeMillis()-timenow} ")

            Timber.d("About to delete exercises table")
            repository.deleteExercisesTable()
            Timber.d("About to insert firestore exercises")
            repository.insertExercises(exercises)

            Timber.d("time NN is ${System.currentTimeMillis()-timenow} ")
            repository.isLoading.value = false

//        }
        }

    private fun modifyLocalDaysTable(days: MutableList<Day>) = viewModelScope.launch {
        //        withContext(Dispatchers.Default)
//        {
        Timber.d("time M is ${System.currentTimeMillis()-timenow} ")

        Timber.d("About to delete days table")
        repository.deleteDaysTable()
        Timber.d("About to insert firestore days")
        repository.insertDays(days)
        repository.isLoading.value = false

//        }
    }

    private fun firestoreFormatToExercise(myCustomFirestoreTransferExercise: MyCustomFirestoreTransferExercise): Exercise {
        return myCustomFirestoreTransferExercise.toExercise()
    }


}