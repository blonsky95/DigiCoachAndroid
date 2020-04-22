package com.tatoe.mydigicoach.network

class ExercisePackage(mFirestoreExercise: MyCustomFirestoreExercise? = null, sender: String = "") {

    constructor():this(null,"")

    companion object {
        val STATE_SENT = "sent"
        val STATE_RECEIVED = "received"
        val STATE_SAVED = "saved"
        val STATE_REJECTED = "rejected"
    }

    var firestoreExercise = mFirestoreExercise
    var mSender: String? = sender
    var mState: String = STATE_SENT

    init {
        if (mSender == null) {
            mSender = "unknown_user_ptg"
        }
    }
}