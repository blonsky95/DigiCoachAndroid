package com.tatoe.mydigicoach.network

class DayPackage(
    mFirestoreTransferDay: MyCustomFirestoreTransferDay? = null,
    removeResults: Boolean = false,
    sender: String? = null,
    receiver: String? = null
) : TransferPackage(removeResults, sender, receiver) {

//    constructor():this(null,"")
//    var mSender: String? = sender
//    var mReceiver: String? = receiver
//    var mState: String = STATE_SENT
//    var documentPath: String? = null

    val firestoreDay = mFirestoreTransferDay

    override var documentPath: String? = null
    override var mState: String = STATE_SENT

    init {
        if (mSender == null) {
            mSender = "no_sender"
        }
        if (mReceiver == null) {
            mReceiver = "no_receiver"
        }
        this.checkIfAndRemoveResults()
    }

    override fun removeResults() {
        for (i in 0 until firestoreDay!!.mDayExercises.size) {
            firestoreDay.mDayExercises[i].resultsArrayList= arrayListOf()
        }
    }
}