package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class BackupFragmentViewModel(var db: FirebaseFirestore, var application: Application) : ViewModel() {

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