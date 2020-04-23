package com.tatoe.mydigicoach.network

import com.google.firebase.firestore.DocumentReference

class ExercisePackage(mFirestoreExercise: MyCustomFirestoreExercise? = null, sender: String = "", receiver: String = "", removeResults:Boolean = false) {

//    constructor():this(null,"")

    companion object {
        val STATE_SENT = "sent"
        val STATE_RECEIVED = "received"
        val STATE_SAVED = "saved"
        val STATE_REJECTED = "rejected"
    }

    val firestoreExercise = mFirestoreExercise
    var mSender: String? = sender
    var mReceiver: String? = receiver
    var mState: String = STATE_SENT
    var documentPath:String? = null

    init {
        if (mSender == null) {
            mSender = "unknown_user_ptg"
        }
        if (removeResults){
            firestoreExercise?.resultsArrayList= arrayListOf()
        }
    }
}