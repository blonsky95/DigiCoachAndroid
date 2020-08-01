package com.tatoe.mydigicoach.network

abstract class TransferPackage ( var mRemoveResults: Boolean = false,
                                 var mSender: String? = null,
                                 var mReceiver: String? = null) {

    companion object {
        val STATE_SENT = "sent"
//        val STATE_RECEIVED = "received"
        val STATE_SAVED = "saved"
        val STATE_REJECTED = "rejected"
        const val STATE_ACCEPTED="accepted"
        const val STATE_SOLVED="solved"

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