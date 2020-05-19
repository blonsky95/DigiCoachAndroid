package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
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
    val allUserBlocks: LiveData<List<Block>>

    val activeDayIdStr: MutableLiveData<String> =  MutableLiveData()
    val activePosition: MutableLiveData<Int> =  MutableLiveData()
//    val oldActivePosition: MutableLiveData<Int> =  MutableLiveData()

    private var db = FirebaseFirestore.getInstance()


    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)

        allDays = repository.allDays
        allExercises = repository.allExercises
        allUserBlocks = repository.allUserBlocks

    }

    fun insertDay(day: Day) = viewModelScope.launch{
        Timber.d("ptg - data view model - insert day called $day")
        repository.insertDay(day)
    }

    fun updateDay(day: Day) = viewModelScope.launch {
        Timber.d("ptg - data view model - update day called $day")
        repository.updateDay(day)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun changeActiveDay(dayId:String) {
//        activePosition.value=Day.dayIdToPosition(dayId)
        activeDayIdStr.value= dayId
    }

    fun changeActivePosition(position:Int) {
        activePosition.value=position
//        activeDay.value= Day.positionToDayId(position)
    }

    fun sendDaysToUser(
        calendarDatesToShare: ArrayList<String>,
        allDays: ArrayList<Day>,
        username: String
    ) {

        //todo continue here next
        var daysToSend = arrayListOf<Day>()
        for (dayId in calendarDatesToShare) {
            for (day in allDays) {
                if (day.dayId==dayId) {
                    daysToSend.add(day)
                }
            }
        }

        val docRef2 = db.collection("users").whereEqualTo("username", username)
        //do shit

        Timber.d("Size of the tpack: ${calendarDatesToShare.size}")
    }
}