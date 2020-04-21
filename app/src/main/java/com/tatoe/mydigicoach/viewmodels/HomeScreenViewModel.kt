package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Day
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.HashMap

class HomeScreenViewModel(var application: Application, var db: FirebaseFirestore) : ViewModel() {

    lateinit var mUser: HashMap<String, Any>
//    val allDays: LiveData<List<Day>>
    val dayToday: LiveData<Day>
    private val repository: AppRepository

    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)

        dayToday = repository.dayToday
    }

    fun checkInUserFirestore(user: HashMap<String, Any>) {
        //todo run this in non UI thread
        mUser=user

        viewModelScope.launch {
            addUser(user["email"] as String)
        }
    }


    private fun addUser(userEmail: String) {
        // check if in users there is document
        val docRef: DocumentReference = db.collection("users").document(userEmail)
        docRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                Timber.d("document was not found")

                createUsersDocument()
            } else {
                Timber.d("document was found")

            }
        }


    }

    private fun createUsersDocument() {
        db.collection("users").document(mUser["email"] as String)
            .set(mUser)
            .addOnSuccessListener {
                Timber.d("DocumentSnapshot added!")
            }
            .addOnFailureListener { e ->
                Timber.d("Error adding document: $e")
            }
    }
}