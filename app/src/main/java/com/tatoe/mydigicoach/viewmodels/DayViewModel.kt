package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.DayPackage
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferDay
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.collections.ArrayList

class DayViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: AppRepository
    private val viewModelJob = SupervisorJob()
    val allDays: LiveData<List<Day>>
    val allExercises: LiveData<List<Exercise>>
    val allFriends: LiveData<List<Friend>>

    val activeDayIdStr: MutableLiveData<String> = MutableLiveData()
    var selectedExercises = arrayListOf<Exercise>()

    private var db = FirebaseFirestore.getInstance()

    init {
        val appDB = AppRoomDatabase.getInstance(application, DataHolder.userName)
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        allDays = repository.allDaysLiveData
        allExercises = repository.allExercisesLiveData
        allFriends = repository.allFriends

    }

//    fun addToSelectedExerciseIndexes(index:Int) {
//
//    }

    fun insertExercise(newExercise: Exercise) = viewModelScope.launch {
        repository.insertExercise(newExercise)
    }

    fun updateExercise(newExercise: Exercise) = viewModelScope.launch {
        repository.updateExercise(newExercise)
    }

    fun insertDay(day: Day) = viewModelScope.launch {
        repository.insertDay(day)
    }

    fun updateDay(day: Day) = viewModelScope.launch {
        if (repository.allDaysLiveData.value != null && !repository.allDaysLiveData.value!!.contains(
                day
            )
        ) {
            insertDay(day)
        } else {
            repository.updateDay(day)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun changeActiveDay(dayId: String) {
//        activePosition.value=Day.dayIdToPosition(dayId)
        activeDayIdStr.value = dayId
    }

    fun sendDaysToFriend(
        calendarDatesToShare: ArrayList<String>,
        allDays: ArrayList<Day>,
        friend: Friend
    ) {

        var daysToSend = arrayListOf<Day>()
        for (dayId in calendarDatesToShare) {
            for (day in allDays) {
                if (day.dayId == dayId) {
                    daysToSend.add(day)
                }
            }
        }

        val docRef = db.collection("users").document(friend.docId!!).collection("day_transfers")
        for (dayToSend in daysToSend) {
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

    }

    fun updateTransferDay(dayPackage: DayPackage, newState: String) {
        dayPackage.mState = TransferPackage.STATE_SAVED
        val docRef = db.document(dayPackage.documentPath!!)
        docRef
            .update("mstate", newState)
            .addOnSuccessListener { Timber.d("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.w("Error updating document: $e") }
    }
}