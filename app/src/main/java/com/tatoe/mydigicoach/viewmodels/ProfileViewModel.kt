package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import android.widget.Toast
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
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.MyCustomFirestoreFriend
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferDay
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferExercise
import com.tatoe.mydigicoach.network.TransferPackage
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
    var receivedRequestsNumber = MutableLiveData(0)


    private val repository: AppRepository
//    val allExercises: List<Exercise>
//    val allDays: List<Day>


    init {
        getUsername()

        val appDB = AppRoomDatabase.getInstance(application, DataHolder.userName)
//        Timber.d("Database has been created")
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        Timber.d("Dataviewmodel initialised")

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        repository.isLoading.value = false

        observeLastUploadValue()
        loadFriendRequests()
    }

    private fun loadFriendRequests() {
        val docRef =
            db.collection("users").document(DataHolder.userDocId).collection("f_requests_in")
                .whereEqualTo("mstate", TransferPackage.STATE_SENT)

        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                var i = 0
                if (snapshot.documents.isNotEmpty()) {
                    i = snapshot.documents.size
                }
                receivedRequestsNumber.value = i
            }
        }
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
//        val docRef = db.collection("users").whereEqualTo("email", userEmail.value)
//        docRef.get().addOnSuccessListener { docs ->
//            if (docs.isEmpty) {
//                return@addOnSuccessListener
//            } else {
//                val doc = docs.documents[0]
//                userName.postValue(doc["username"].toString())
//            }
//        }

        userName.postValue(DataHolder.userName)
    }

    fun uploadBackup() = viewModelScope.launch {

        repository.isLoading.value = true

        //using asynchronous functions, so by using this you create a deffered class which you can call the
        //.await() method, once its done it will carry out that code
        val allExes = async { repository.getAllExercises() }
        val allDays = async { repository.getAllDays() }
        val allFriends = async { repository.getAllFriends() }

        if (!allExes.await().isNullOrEmpty()) {
            postExercisesToFirestore(allExes.await())
        }

        if (!allDays.await().isNullOrEmpty()) {
            postDaysToFirestore(allDays.await())
        }

        if (!allFriends.await().isNullOrEmpty()) {
            postFriendsToFirestore(allFriends.await())
        }

        val docRef = db.collection("users").whereEqualTo("email", userEmail.value)
        docRef.get().addOnSuccessListener { docs ->
            if (!docs.isEmpty) {
                val doc = docs.documents[0]
                doc.reference.update(
                    "last_upload",
                    Day.hoursMinutesDateFormat.format(Date(System.currentTimeMillis()))
                )
                    .addOnSuccessListener {
                        Toast.makeText(application, "last upload updated", Toast.LENGTH_SHORT)
                            .show()

                        repository.isLoading.value = false
                        Timber.d("Yay worked")
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            application,
                            "ups something wrong updating",
                            Toast.LENGTH_SHORT
                        ).show()
                        repository.isLoading.value = false
                    }
            } else {
                Toast.makeText(application, "No Internet - action denied", Toast.LENGTH_SHORT)
                    .show()
                repository.isLoading.value = false
            }

        }.addOnFailureListener {
            Toast.makeText(application, "ups something wrong fetching email", Toast.LENGTH_SHORT)
                .show()

            Timber.d("Something went wrong - this user reference doesnt exist")
            repository.isLoading.value = false

        }


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

    private fun postFriendsToFirestore(friends: List<Friend>) {
        val docRef = db.collection("users").document(DataHolder.userDocId).collection("my_friends")
        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }

                for (friend in friends) {
                    docRef.add(MyCustomFirestoreFriend(friend))
                }

            }
            .addOnFailureListener { exception ->
                Timber.d("get failed with: $exception ")
            }
    }

    fun downloadBackup() {
        repository.isLoading.value = true

        getExercisesFromFirestore()
        getDaysFromFirestore()
        getFriendsFromFirestore()

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

                modifyLocalExercisesTable(exercises)
            }
            .addOnFailureListener { exception ->
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

                modifyLocalDaysTable(days)

            }
            .addOnFailureListener { exception ->
                //                repository.isLoading.value = false
                Timber.d("get failed with: $exception ")
            }
    }

    private fun getFriendsFromFirestore() {
        val docRef = db.collection("users").document(DataHolder.userDocId)
            .collection("my_friends")
        val friends = mutableListOf<Friend>()
        docRef.get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    for (document in documents) {
                        Timber.d("DocumentSnapshot data: ${document.data}")
                        friends.add(document.toObject(MyCustomFirestoreFriend::class.java).toFriend())
                    }
                } else {
                    Timber.d("documents is empty or null")
                }

                modifyLocalFriendsTable(friends)

            }
            .addOnFailureListener { exception ->
                //                repository.isLoading.value = false
                Timber.d("get failed with: $exception ")
            }
    }



    private fun modifyLocalExercisesTable(exercises: MutableList<Exercise>) =
        viewModelScope.launch {
            Timber.d("About to delete exercises table")
            repository.deleteExercisesTable()
            Timber.d("About to insert firestore exercises")
            repository.insertExercises(exercises)

            repository.isLoading.value = false
        }

    private fun modifyLocalDaysTable(days: MutableList<Day>) = viewModelScope.launch {

        Timber.d("About to delete days table")
        repository.deleteDaysTable()
        Timber.d("About to insert firestore days")
        repository.insertDays(days)
        repository.isLoading.value = false

    }

    private fun modifyLocalFriendsTable(friends: MutableList<Friend>)= viewModelScope.launch {
        Timber.d("About to delete friends table")
        repository.deleteFriendsTable()
        Timber.d("About to insert firestore days")
        repository.insertFriends(friends)
        repository.isLoading.value = false

    }

    private fun firestoreFormatToExercise(myCustomFirestoreTransferExercise: MyCustomFirestoreTransferExercise): Exercise {
        return myCustomFirestoreTransferExercise.toExercise()
    }


}