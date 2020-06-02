package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Friend


class FriendsFragmentViewModel(var db: FirebaseFirestore, var application: Application) : ViewModel() {

    private var user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    val friends:LiveData<List<Friend>>
    private val repository: AppRepository


    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        friends=repository.allFriends
        loadFriends()

//        observeLastUploadValue()
    }

    private fun loadFriends() {
        val docRef = db.collection("users").whereEqualTo("email", "www")

        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (snapshot.documents.isNotEmpty()) {
                    val doc = snapshot.documents[0]
                    if (doc["last_upload"] != null) {
//                        lastUploadTime.postValue(doc["last_upload"].toString())
                    }
                }
            }
        }
    }

}