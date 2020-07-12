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
import com.tatoe.mydigicoach.DialogPositiveNegativeInterface
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.*
import com.tatoe.mydigicoach.ui.fragments.PackageReceivedFragment
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) :
    AndroidViewModel(application) {

    companion object {
        const val NO_FRAGMENT = -1
        const val PACKAGE_DISPLAYER = 1
        const val FRIEND_SHARER = 2
        const val FRIEND_DISPLAYER=3
    }

    private val repository: AppRepository

    private val viewModelJob = SupervisorJob()

    val allExercises: LiveData<List<Exercise>>
    val allFriends: LiveData<List<Friend>>
    val allDays: LiveData<List<Day>>

    val exercisesToSend = MutableLiveData(listOf<Exercise>())
    val daysToSend = MutableLiveData(listOf<Day>())

    var receivedFriendRequestsPackages = MutableLiveData(listOf<FriendRequestPackage>())
    var receivedExercisesPackages = MutableLiveData(listOf<ExercisePackage>())
    var receivedDaysPackages = MutableLiveData(listOf<DayPackage>())

    var dialogBoxBundle = MutableLiveData<Utils.DialogBundle>()

    var displayPackageReceiverFragmentType = MutableLiveData(PackageReceivedFragment.TRANSFER_PACKAGE_NOT_VALUE)

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
        allDays = repository.allDaysLiveData
        allExercises = repository.allExercisesLiveData

        startSnapshotListeners()

    }

    fun displayFragmentByID(id: Int) {
        displayFragmentById.postValue(id)
    }


    private fun startSnapshotListeners() {
        startExercisesReceivedListener()
        startDaysReceivedListener()
        startFriendRequestListener()
        startFriendRequestAcceptedListener()
    }

    private fun startDaysReceivedListener()  = viewModelScope.launch{
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

    private fun startExercisesReceivedListener() = viewModelScope.launch {
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

    private fun startFriendRequestListener() = viewModelScope.launch {
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

    private fun startFriendRequestAcceptedListener() = viewModelScope.launch {
        val docRefAccepted =
            db.collection("users").document(DataHolder.userDocId).collection("f_requests_out")
                .whereEqualTo("mstate", TransferPackage.STATE_ACCEPTED)

        docRefAccepted.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (snapshot.documents.isNotEmpty()) {
                    for (request in snapshot.documents) {
                        val friendPackage = request.toObject(FriendRequestPackage::class.java)
                        val newFriend = Friend(friendPackage!!.mReceiver!!,friendPackage.receiverDocId!!)
//                        newFriend.docId=friendPackage.receiverDocId!!
                        viewModelScope.launch {
                            insertFriend(newFriend)
                            request.reference.update("mstate","accepted - solved")
                        }
                    }
                }
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

    fun attemptImportExercise(
        exercisePackage: ExercisePackage,
        allExercises: List<Exercise>
    ) {

        val newExercise = exercisePackage.firestoreExercise!!.toExercise()

        if (findExerciseInLocal(newExercise, allExercises) != null) {
            val text = "You already have this exercise, do you want to overwrite it?"
            val title = "Import exercise"
            val dialogPositiveNegativeInterface = object : DialogPositiveNegativeInterface {
                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    removeExercise(findExerciseInLocal(newExercise, allExercises)!!)
                    insertExercise(newExercise)
                    updateTransferPackage(exercisePackage, TransferPackage.STATE_SAVED)
                }

                override fun onNegativeButton() {
                    super.onNegativeButton()
                    updateTransferPackage(exercisePackage, TransferPackage.STATE_REJECTED)
                }
            }
            dialogBoxBundle.postValue(
                Utils.DialogBundle(
                    title,
                    text,
                    dialogPositiveNegativeInterface
                )
            )
        } else {
            insertExercise(newExercise)
        }

    }

    private fun findExerciseInLocal(exe: Exercise, fullListExes: List<Exercise>): Exercise? {
        for (exercise in fullListExes) {
            if (exe.md5 == exercise.md5) {
                return exercise
            }
        }
        return null
    }

    fun insertExercise(newExercise: Exercise) = viewModelScope.launch {
        repository.insertExercise(newExercise)
    }

    private fun removeExercise(theSameExercise: Exercise) = viewModelScope.launch {
        repository.deleteExercise(theSameExercise)
    }


    fun attemptImportDay(dayPackage: DayPackage, allExercises: List<Exercise>, allDays: List<Day>) {
        val title = "Import Days"
        val text =
            "Import ${Day.toReadableFormat(Day.dayIDToDate(dayPackage.firestoreDay!!.mDayId)!!)} from your friend ${dayPackage.mSender}?" +
                    "\n new exercises will be imported, existing exercises won't be replaced, your days will be overwritten"
        val dialogPositiveNegativeInterface = object : DialogPositiveNegativeInterface {
            override fun onPositiveButton(inputText: String) {
                super.onPositiveButton(inputText)
                var toImportDay = dayPackage.firestoreDay.toDay()
                toImportDay.exercises = replaceExistingExercises(toImportDay, allExercises)
                updateDay(toImportDay, allDays)
                updateTransferPackage(
                    dayPackage,
                    TransferPackage.STATE_SAVED
                )
            }

            override fun onNegativeButton() {
                super.onNegativeButton()
                updateTransferPackage(
                    dayPackage,
                    TransferPackage.STATE_REJECTED
                )
            }

        }
        dialogBoxBundle.postValue(
            Utils.DialogBundle(
                title,
                text,
                dialogPositiveNegativeInterface
            )
        )
    }

    private fun replaceExistingExercises(
        toImportDay: Day,
        allExercises: List<Exercise>
    ): ArrayList<Exercise> {
        var newDayExercises = arrayListOf<Exercise>()
        for (exe in toImportDay.exercises) {
            //if exe exists we add
            val existingExercise = findExerciseInLocal(exe, allExercises)
            if (existingExercise == null) {
                //it is a new exercise
                insertExercise(exe)
                newDayExercises.add(exe)
            } else {
                //replace it for your existing instance of the exercise
                newDayExercises.add(existingExercise)
            }
        }
        return newDayExercises
    }

    fun updateDay(day: Day, allDays: List<Day>) = viewModelScope.launch {
        if (!allDays.contains(day)) {
            insertDay(day)
        } else {
            repository.updateDay(day)
        }
    }

    fun insertDay(day: Day) = viewModelScope.launch {
        repository.insertDay(day)
    }

    fun attemptAddFriend(friend: Friend, allFriends: List<Friend>) {
        if (!allFriends.contains(friend)) {
            insertFriend(friend)
        }
    }

    private fun insertFriend(friend: Friend) = viewModelScope.launch{
        repository.insertFriend(friend)
    }

    fun updateTransferPackage(transferPackage: TransferPackage, newState: String) =
        viewModelScope.launch {
            transferPackage.mState = TransferPackage.STATE_SAVED
            val docRef = db.document(transferPackage.documentPath!!)

            docRef
                .update("mstate", newState)
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Timber.w("Error updating document: $e") }
        }


}