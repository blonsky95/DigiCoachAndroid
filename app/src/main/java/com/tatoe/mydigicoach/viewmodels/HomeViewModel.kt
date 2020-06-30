package com.tatoe.mydigicoach.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferExercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.utils.MD5Encrypter
import timber.log.Timber
import java.util.HashMap

class HomeViewModel(var application: Application, var db: FirebaseFirestore) : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    val dayToday: LiveData<Day>
    val hasAccess = MutableLiveData<Boolean>(false)
    val isDoingBackgroundTask = MutableLiveData<Boolean>(false)
    private val repository: AppRepository

    init {
        val appDB = AppRoomDatabase.getInstance(application,DataHolder.userName)
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        dayToday = repository.dayToday
        if (auth.currentUser!=null){
            hasAccess.value=true
        }
    }

    fun saveUserToDataholder() {
        val docRef1 = db.collection("users").whereEqualTo("email", auth.currentUser!!.email)
        docRef1.get().addOnSuccessListener { docs ->
            if (!docs.isEmpty) {
                DataHolder.userDocId=docs.documents[0].id
                DataHolder.userName=docs.documents[0]["username"] as String
            }
        }

        DataHolder.userEmail = auth.currentUser!!.email!!
    }

    fun closeDbInstance() {
        AppRoomDatabase.destroyInstance()
    }


}