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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) :
    AndroidViewModel(application) {

    companion object {
        const val REMOVE_VERTICAL_FRAGMENT = -2
        const val NO_FRAGMENT = -1
        const val PACKAGE_DISPLAYER = 1
        const val FRIEND_SHARER = 2
        const val FRIEND_DISPLAYER = 3
        const val BACKUP_FRAGMENT = 4
    }

    private val repository: AppRepository

    private val viewModelJob = SupervisorJob()

    val allExercises: LiveData<List<Exercise>>
    val allFriends: LiveData<List<Friend>>
    val allDays: LiveData<List<Day>>

    val exercisesToSend = MutableLiveData(listOf<Exercise>())
    val daysToSend = MutableLiveData(listOf<Day>())

    var receivedFriendRequestsPackages = MutableLiveData(arrayListOf<FriendRequestPackage>())
    var receivedExercisesPackages = MutableLiveData(arrayListOf<ExercisePackage>())
    var receivedDaysPackages = MutableLiveData(arrayListOf<DayPackage>())
    var adapterTransferPackages = MutableLiveData(arrayListOf<TransferPackage>())


    var dialogBoxBundle = MutableLiveData<Utils.DialogBundle>()

    var displayPackageReceiverFragmentType =
        MutableLiveData(PackageReceivedFragment.TRANSFER_PACKAGE_NOT_VALUE)

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

    private fun startSnapshotListeners() {
        startExercisesReceivedListener()
        startDaysReceivedListener()
        startFriendRequestListener()
        startFriendRequestAcceptedListener()
    }

    private fun startDaysReceivedListener() = viewModelScope.launch {
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
                        val newFriend =
                            Friend(friendPackage!!.mReceiver!!, friendPackage.receiverDocId!!)
//                        newFriend.docId=friendPackage.receiverDocId!!
                        viewModelScope.launch {
                            insertFriend(newFriend)
                            request.reference.update("mstate", "accepted - solved")
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
        exercisesToSend.postValue(listOf())
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
                    repository.isLoading.value = false

                }
                .addOnFailureListener { exception ->
                    repository.isLoading.value = false
                    Timber.d("get failed with: $exception ")
                }
        }
        //empty the days to send
        daysToSend.postValue(listOf())
    }

    fun exerciseAlreadyInDb(
        exercisePackage: ExercisePackage,
        allExercises: List<Exercise>
    ): Boolean {
        return findExerciseInLocal(
            exercisePackage.firestoreExercise!!.toExercise(),
            allExercises
        ) != null
    }

    fun promptOverwriteExerciseDialog(
        exercisePackage: ExercisePackage,
        allExercises: List<Exercise>
    ) {

        val newExercise = exercisePackage.firestoreExercise!!.toExercise()

        val text = "You already have this exercise, do you want to overwrite it?"
        val title = "Import exercise"
        val dialogPositiveNegativeInterface = object : DialogPositiveNegativeInterface {
            override fun onPositiveButton(inputText: String) {
                super.onPositiveButton(inputText)
                removeExercise(findExerciseInLocal(newExercise, allExercises)!!)
                insertExercise(newExercise)
                updateExerciseAdapterContentAfterAction(exercisePackage)

                updateTransferPackage(exercisePackage, TransferPackage.STATE_SAVED)
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
                updateDayAdapterContentAfterAction(dayPackage)

                updateTransferPackage(
                    dayPackage,
                    TransferPackage.STATE_SAVED
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

    fun attemptAddFriend(friendPackage: FriendRequestPackage, allFriends: List<Friend>) {
        var friend = Friend(friendPackage.mSender!!, friendPackage.senderDocId)
        var stateString = ""
        if (!allFriends.contains(friend)) {
            stateString = TransferPackage.STATE_ACCEPTED
            insertFriend(friend)

        } else {
            stateString = TransferPackage.STATE_REJECTED
        }
        updateRequestStateReceiver(friendPackage, stateString)
        updateRequestStateSender(friendPackage, stateString)
        updateFriendAdapterContentAfterAction(friendPackage)
    }

    fun rejectFriendRequest(friendPackage: FriendRequestPackage) {
        updateRequestStateReceiver(friendPackage, TransferPackage.STATE_REJECTED)
        updateRequestStateSender(friendPackage, TransferPackage.STATE_REJECTED)
    }

    private fun insertFriend(friend: Friend) = viewModelScope.launch {
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

    /**
     * Updates the mstate field of the request in f_requests_out for the sender
     */
    fun updateRequestStateSender(friendRequestPackage: FriendRequestPackage, newState: String) =
        viewModelScope.launch {
            //tells sender its accepted (state changes to ACCEPTED) so it will trigger the accepted snapshot, and accept the friend

            val docRef = db.collection("users").document(friendRequestPackage.senderDocId!!)
                .collection("f_requests_out")
                .whereEqualTo("mreceiver", DataHolder.userName)
            docRef.get().addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    docs.documents[0].reference.update(
                        "mstate",
                        newState,
                        "receiverDocId",
                        DataHolder.userDocId
                    )
                }
            }
        }

    /**
     * Updates the mstate field of the request in f_requests_in for the receiver
     */
    fun updateRequestStateReceiver(friendRequestPackage: FriendRequestPackage, newState: String) =
        viewModelScope.launch {
            //receiver requests get modified so the state of the request moves to SOLVED (aka ignored)
//        val docRef = db.document(friendPackage.documentPath!!)
            val docRef =
                db.collection("users").document(DataHolder.userDocId).collection("f_requests_in")
                    .whereEqualTo("msender", friendRequestPackage.mSender)
            docRef.get().addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    docs.documents[0].reference.update("mstate", newState)
                }
            }
        }

    fun removePackage(
        transferPackagesReceived: ArrayList<TransferPackage>,
        transferPackage: TransferPackage
    ): ArrayList<TransferPackage> {
        for (tPackage in transferPackagesReceived) {
            if (tPackage.documentPath == transferPackage.documentPath) {
                transferPackagesReceived.remove(tPackage)
                return transferPackagesReceived
            }
        }
        return transferPackagesReceived
    }

    /**
    Called straight after accepting/rejecting a transfer package - removes it from the list and updates the content
     **/
    fun updateExerciseAdapterContentAfterAction(transferPackage: TransferPackage) {
        val newAdapterContent = removePackage(
            receivedExercisesPackages.value as ArrayList<TransferPackage>,
            transferPackage
        )
        adapterTransferPackages.postValue(newAdapterContent)
        receivedExercisesPackages.postValue(newAdapterContent as ArrayList<ExercisePackage>)
    }

    fun updateDayAdapterContentAfterAction(transferPackage: TransferPackage) {
        val newAdapterContent = removePackage(
            receivedDaysPackages.value as ArrayList<TransferPackage>,
            transferPackage
        )
        adapterTransferPackages.postValue(newAdapterContent)
        receivedDaysPackages.postValue(newAdapterContent as ArrayList<DayPackage>)
    }

    fun updateFriendAdapterContentAfterAction(transferPackage: TransferPackage) {
        val newAdapterContent = removePackage(
            receivedFriendRequestsPackages.value as ArrayList<TransferPackage>,
            transferPackage
        )
        adapterTransferPackages.postValue(newAdapterContent)
        receivedFriendRequestsPackages.postValue(newAdapterContent as ArrayList<FriendRequestPackage>)
    }


    /**
     * Code to upload backup
     */

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

        val docRef = db.collection("users").whereEqualTo("email", DataHolder.userEmail)
        docRef.get().addOnSuccessListener { docs ->
            if (!docs.isEmpty) {
                val doc = docs.documents[0]
                doc.reference.update(
                    "last_upload",
                    Day.hoursMinutesDateFormat.format(Date(System.currentTimeMillis()))
                )
                    .addOnSuccessListener {
                        Toast.makeText(getApplication(), "last upload updated", Toast.LENGTH_SHORT)
                            .show()

                        repository.isLoading.value = false
                        Timber.d("Yay worked")
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            getApplication(),
                            "ups something wrong updating",
                            Toast.LENGTH_SHORT
                        ).show()
                        repository.isLoading.value = false
                    }
            } else {
                Toast.makeText(getApplication(), "No Internet - action denied", Toast.LENGTH_SHORT)
                    .show()
                repository.isLoading.value = false
            }

        }.addOnFailureListener {
            Toast.makeText(
                getApplication(),
                "ups something wrong fetching email",
                Toast.LENGTH_SHORT
            )
                .show()

            Timber.d("Something went wrong - this user reference doesnt exist")
            repository.isLoading.value = false

        }


    }

    private fun postExercisesToFirestore(listExercises: List<Exercise>) = viewModelScope.launch {

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

    /**
     * Code to download backup
     */

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
                        exercises.add(
                            document.toObject(MyCustomFirestoreTransferExercise::class.java)
                                .toExercise()
                        )
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
                        days.add(
                            document.toObject(MyCustomFirestoreTransferDay::class.java).toDay()
                        )
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
                        friends.add(
                            document.toObject(MyCustomFirestoreFriend::class.java).toFriend()
                        )
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

    private fun modifyLocalFriendsTable(friends: MutableList<Friend>) = viewModelScope.launch {
        Timber.d("About to delete friends table")
        repository.deleteFriendsTable()
        Timber.d("About to insert firestore days")
        repository.insertFriends(friends)
        repository.isLoading.value = false

    }

}