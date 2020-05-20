package com.tatoe.mydigicoach.network

abstract class TransferPackage ( var mRemoveResults: Boolean = false,
                                 var mSender: String?,
                                 var mReceiver: String?) {

    companion object {
        val STATE_SENT = "sent"
        val STATE_RECEIVED = "received"
        val STATE_SAVED = "saved"
        val STATE_REJECTED = "rejected"
    }

    abstract var mState: String
    abstract var documentPath:String?

    abstract fun removeResults()

    fun checkIfAndRemoveResults() {
        if (mRemoveResults) {
            removeResults()
        }
    }


}