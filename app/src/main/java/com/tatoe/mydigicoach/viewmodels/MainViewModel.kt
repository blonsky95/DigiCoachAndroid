package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.*
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) :
    AndroidViewModel(application) {

    companion object {
        const val NO_FRAGMENT=0
        const val PACKAGE_DISPLAYER=1
        const val FRIEND_SHARER=2

    }

    private val repository: AppRepository

    private val viewModelJob = SupervisorJob()

    //    val allExercises: LiveData<List<Exercise>>
    val allFriends: LiveData<List<Friend>>

    val exercisesToSend = MutableLiveData(listOf<Exercise>())
    val daysToSend = MutableLiveData(listOf<Day>())

    var receivedFriendRequestsPackages = MutableLiveData(listOf<FriendRequestPackage>())
    var receivedExercisesPackages = MutableLiveData(listOf<ExercisePackage>())
    var receivedDaysPackages = MutableLiveData(listOf<DayPackage>())

    var displayFragmentTriggerAndType = MutableLiveData(-1)

    var displayFragmentById = MutableLiveData(NO_FRAGMENT)

    lateinit var friendsSnapshot: ListenerRegistration
    lateinit var exesSnapshot: ListenerRegistration
    lateinit var daysSnapshot: ListenerRegistration


    private var db = FirebaseFirestore.getInstance()

    init {
        val appDB = AppRoomDatabase.getInstance(application, DataHolder.userName)
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        allFriends = repository.allFriends

        startSnapshotListeners()

    }

    fun displayFragmentByID(id:Int){
        displayFragmentById.postValue(id)
    }


    private fun startSnapshotListeners() {
        startFriendRequestListener()
        startExercisesReceivedListener()
        startDaysReceivedListener()
    }

    private fun startDaysReceivedListener() {
        val docRefDays =
            db.collection("users").document(DataHolder.userDocId).collection("day_transfers")
                .whereEqualTo("mstate", TransferPackage.STATE_SENT)

        daysSnapshot = docRefDays.addSnapshotListener { snapshot, e ->
            var receivedDays = arrayListOf<DayPackage>()

            if (snapshot != null) {
                receivedDays = arrayListOf()
                for (document in snapshot.documents) {
                    val dayPackage = document.toObject(DayPackage::class.java)
                    dayPackage!!.documentPath = document.reference.path
                    receivedDays.add(dayPackage)
                }
            }
            receivedDaysPackages.value = receivedDays
        }
    }

    private fun startExercisesReceivedListener() {
        val docRefExes =
            db.collection("users").document(DataHolder.userDocId).collection("exercise_transfers")
                .whereEqualTo("mstate", TransferPackage.STATE_SENT)

        exesSnapshot = docRefExes.addSnapshotListener { snapshot, e ->
            var receivedExercises = arrayListOf<ExercisePackage>()

            if (snapshot != null) {
                receivedExercises = arrayListOf()
                for (document in snapshot.documents) {
                    val exercisePackage = document.toObject(ExercisePackage::class.java)
                    exercisePackage!!.documentPath = document.reference.path
                    receivedExercises.add(exercisePackage)
                }
            }
            receivedExercisesPackages.value = receivedExercises
        }
    }

    private fun startFriendRequestListener() {
        val docRef =
            db.collection("users").document(DataHolder.userDocId).collection("f_requests_in")
                .whereEqualTo("mstate", TransferPackage.STATE_SENT)

        friendsSnapshot = docRef.addSnapshotListener { snapshot, e ->
            var receivedFriendRequests = arrayListOf<FriendRequestPackage>()
            if (snapshot != null) {
                receivedFriendRequests = arrayListOf()
                for (docu in snapshot.documents) {
                    val friendRequestPackage = docu.toObject(FriendRequestPackage::class.java)
                    friendRequestPackage!!.documentPath = docu.reference.path
                    receivedFriendRequests.add(friendRequestPackage)
                }
                receivedFriendRequestsPackages.value = receivedFriendRequests
            }
        }
    }

    fun stopSnapshotListeners() {
        friendsSnapshot.remove()
        daysSnapshot.remove()
        exesSnapshot.remove()
    }

    fun sendExercisesToFriend(exes: List<Exercise>, friend: Friend) {

        val docRef =
            db.collection("users").document(friend.docId!!).collection("exercise_transfers")
        for (exerciseToSend in exes) {
            docRef.get()
                .addOnSuccessListener {
                    docRef.add(
                        ExercisePackage(
                            MyCustomFirestoreTransferExercise(exerciseToSend),
                            true,
                            DataHolder.userName,
                            friend.username
                        )
                    )
                }
                .addOnFailureListener { exception ->
                    Timber.d("get failed with: $exception ")
                }
        }
        //empty the exercises to send
        exercisesToSend.value = listOf()
    }

    fun sendDaysToFriend(
        days: List<Day>,
        friend: Friend
    ) {

        val docRef = db.collection("users").document(friend.docId!!).collection("day_transfers")
        for (dayToSend in days) {
            docRef.get()
                .addOnSuccessListener {
                    docRef.add(
                        DayPackage(
                            MyCustomFirestoreTransferDay(dayToSend),
                            true,
                            DataHolder.userName,
                            friend.username
                        )
                    )
                    Toast.makeText(
                        getApplication(), "Day sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    repository.isLoading.value = false

                }
                .addOnFailureListener { exception ->
                    repository.isLoading.value = false
                    Timber.d("get failed with: $exception ")
                }
        }
        //empty the days to send
        daysToSend.value = listOf()
    }

    fun attemptImportExercise(exercise: Exercise) {
        //if exists - update live data for dialog - do the dialog object here? - the yes calls the insert
        //to check if exists need to to run an async operation to get non live data allexercises, allfriends and alldays here and then use them


    }

    fun insertExercise(newExercise:Exercise) = viewModelScope.launch {
        repository.insertExercise(newExercise)
    }

    fun insertDay(day: Day) {

    }

    fun insertFriend(friend: Friend) {

    }
}