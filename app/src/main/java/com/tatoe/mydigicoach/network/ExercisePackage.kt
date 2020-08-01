package com.tatoe.mydigicoach.network

class ExercisePackage(
    mFirestoreTransferExercise: MyCustomFirestoreTransferExercise? = null,
    removeResults: Boolean = false,
    sender: String? = null,
    receiver: String? = null
) : TransferPackage(removeResults, sender, receiver) {

//    constructor():this(null,"")
//    var mSender: String? = sender
//    var mReceiver: String? = receiver
//    var mState: String = STATE_SENT
//    var documentPath: String? = null

    val firestoreExercise = mFirestoreTransferExercise

    override var documentPath: String? = null
    override var mState: String = STATE_SENT

    init {
        if (mSender == null) {
            mSender = "unknown_user_ptg"
        }
        this.checkIfAndRemoveResults()
    }

    override fun removeResults() {
        firestoreExercise?.resultsArrayList = arrayListOf()
    }
}