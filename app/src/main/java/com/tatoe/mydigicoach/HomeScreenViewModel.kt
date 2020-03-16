package com.tatoe.mydigicoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.HashMap

class HomeScreenViewModel(var db: FirebaseFirestore) : ViewModel() {

    lateinit var mUser: HashMap<String, Any>

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