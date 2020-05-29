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

class ProfileFragmentViewModel(var db: FirebaseFirestore, var application: Application) : ViewModel() {

    private var user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    val userEmail = MutableLiveData<String>(user.email)
    val lastUploadTime = MutableLiveData<String>("-")

    init {
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

}