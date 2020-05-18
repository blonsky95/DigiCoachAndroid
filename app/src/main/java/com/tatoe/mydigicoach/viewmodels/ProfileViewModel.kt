package com.tatoe.mydigicoach.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel(var db: FirebaseFirestore) : ViewModel() {

    private var user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    val userName = MutableLiveData<String>("")
    val userEmail = MutableLiveData<String>(user.email)

    init {
        getUsername()
    }

    private fun getUsername() {
        val docRef = db.collection("users").whereEqualTo("email", userEmail.value)
        docRef.get().addOnSuccessListener { docs ->
            if (docs.isEmpty) {
                return@addOnSuccessListener
            } else {
                val doc = docs.documents[0]
                userName.postValue(doc["username"].toString())
            }
        }
    }


}