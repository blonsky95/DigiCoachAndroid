package com.tatoe.mydigicoach.network

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.ui.util.DataHolder
import java.util.ArrayList

class FirebaseListenerService :
    Service() {

    private var firebaseUser: FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()
    private val binder = LocalBinder()

    var receivedExercisesLiveData = MutableLiveData<ArrayList<ExercisePackage>>(arrayListOf())
    var receivedDaysLiveData = MutableLiveData<ArrayList<DayPackage>>(arrayListOf())


//    var receivedExercises = arrayListOf<ExercisePackage>()
//    var receivedDays = arrayListOf<DayPackage>()

    companion object {
        const val SERVICE_NAME = "DATABASE_LISTENER"
        var isServiceRunning = false
    }

    init {
        isServiceRunning = true

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val docRefExes = db.collection("users").document(DataHolder.userDocId).collection("exercise_transfers")
            .whereEqualTo("mstate", TransferPackage.STATE_SENT)

        docRefExes.addSnapshotListener { snapshot, e ->
            var receivedExercises = arrayListOf<ExercisePackage>()

            if (snapshot != null) {
                receivedExercises = arrayListOf()
                for (document in snapshot.documents) {
                    val exercisePackage = document.toObject(ExercisePackage::class.java)
                    exercisePackage!!.documentPath = document.reference.path
                    receivedExercises.add(exercisePackage)
                }
            }
            receivedExercisesLiveData.value=receivedExercises
        }

        val docRefDays = db.collection("users").document(DataHolder.userDocId).collection("day_transfers")
            .whereEqualTo("mstate", TransferPackage.STATE_SENT)

        docRefDays.addSnapshotListener { snapshot, e ->
            var receivedDays = arrayListOf<DayPackage>()

            if (snapshot != null) {
                receivedDays = arrayListOf()
                for (document in snapshot.documents) {
                    val dayPackage = document.toObject(DayPackage::class.java)
                    dayPackage!!.documentPath = document.reference.path
                    receivedDays.add(dayPackage)
                }
            }
            receivedDaysLiveData.value=receivedDays
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