package com.tatoe.mydigicoach.network

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.ui.util.DataHolder
import timber.log.Timber
import java.util.ArrayList

class FirebaseListenerService :
    Service() {

    private var firebaseUser: FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()
    private val binder = LocalBinder()

    var receivedExercisesLiveData = MutableLiveData<ArrayList<ExercisePackage>>(arrayListOf())

    var receivedExercises = arrayListOf<ExercisePackage>()

    companion object {
        const val SERVICE_NAME = "DATABASE_LISTENER"
        var isServiceRunning = false
    }

    init {
        isServiceRunning = true

        firebaseUser = FirebaseAuth.getInstance().currentUser



        val docRef = db.collection("users").document(DataHolder.userDocId).collection("transfers")
            .whereEqualTo("mstate", ExercisePackage.STATE_SENT)

        docRef.addSnapshotListener { snapshot, e ->

            if (snapshot != null) {
                receivedExercises = arrayListOf<ExercisePackage>()
                for (document in snapshot.documents) {
                    val exercisePackage = document.toObject(ExercisePackage::class.java)
                    exercisePackage!!.documentPath = document.reference.path
//                    DataHolder.addReceivedExercise(exercisePackage)
                    receivedExercises.add(exercisePackage)
                }
            }
            receivedExercisesLiveData.value=receivedExercises
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        isServiceRunning = false
        super.onDestroy()

    }

    inner class LocalBinder : Binder() {
        fun getService(): FirebaseListenerService = this@FirebaseListenerService
    }
}