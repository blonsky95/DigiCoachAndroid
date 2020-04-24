package com.tatoe.mydigicoach.network

import android.app.IntentService
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.ui.util.DataHolder
import timber.log.Timber

class DatabaseListener :
    IntentService(SERVICE_NAME) {

    private var firebaseUser: FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()

    companion object {
        const val SERVICE_NAME = "DATABASE_LISTENER"
        var isServiceRunning = false
    }

    override fun onHandleIntent(intent: Intent?) {
        isServiceRunning=true

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val docRef = db.collection("users").document(firebaseUser!!.email!!).collection("transfers")
            .whereEqualTo("mstate", ExercisePackage.STATE_SENT)

        docRef.addSnapshotListener { snapshot, e ->

            if (snapshot!=null){
                DataHolder.emptyReceivedExercises()
                for (document in snapshot.documents) {
                    val exercisePackage = document.toObject(ExercisePackage::class.java)
                    exercisePackage!!.documentPath=document.reference.path
                    DataHolder.addReceivedExercise(exercisePackage)
                }
            }


//            if (e != null) {
//                return@addSnapshotListener
//            }

        }
    }

    private fun updateExeState(documentReference: DocumentReference, exercisePackage: ExercisePackage) {
        exercisePackage.mState = ExercisePackage.STATE_RECEIVED
        documentReference
            .update("mstate", ExercisePackage.STATE_RECEIVED)
            .addOnSuccessListener { Timber.d( "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.w("Error updating document: $e") }
    }

    override fun onDestroy() {
        isServiceRunning=false
        super.onDestroy()

    }
}