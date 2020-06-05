package com.tatoe.mydigicoach.network

class FriendPackage(
    removeResults: Boolean = false,
    sender: String? = null,
    receiver: String? = null
) : TransferPackage(removeResults, sender, receiver) {

//    constructor():this(null,"")
//    var mSender: String? = sender
//    var mReceiver: String? = receiver
//    var mState: String = STATE_SENT
//    var documentPath: String? = null

    override var documentPath: String? = null


    override var mState: String = STATE_SENT

    var senderDocId: String? = null

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
//friend package never calls removeResults
    }

}