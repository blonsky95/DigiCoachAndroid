package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.FriendRequestPackage
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber


class FriendsFragmentViewModel(var db: FirebaseFirestore, var application: Application) :
    ViewModel() {

    val friends: LiveData<List<Friend>>
    var receivedRequestsLiveData = MutableLiveData<ArrayList<FriendRequestPackage>>(arrayListOf())

    private val repository: AppRepository
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        val appDB = AppRoomDatabase.getInstance(application,DataHolder.userName)
        val exerciseDao = appDB.exercisesDao()
        val friendDao = appDB.friendDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, friendDao, dayDao)

        friends = repository.allFriends
        listenIncomingRequests()
        listenOutgoingRequests()
    }

    private fun listenOutgoingRequests() {
        val docRefAccepted =
            db.collection("users").document(DataHolder.userDocId).collection("f_requests_out")
                .whereEqualTo("mstate", TransferPackage.STATE_ACCEPTED)

        docRefAccepted.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (snapshot.documents.isNotEmpty()) {
                    for (request in snapshot.documents) {
                        val friendPackage = request.toObject(FriendRequestPackage::class.java)
                        val newFriend = Friend(friendPackage!!.mReceiver!!,friendPackage.receiverDocId!!)
//                        newFriend.docId=friendPackage.receiverDocId!!
                        viewModelScope.launch {
                            insertFriend(newFriend)
                            request.reference.update("mstate","accepted - solved")
                        }



                        //WEIRD ALERT - so if there is no pause between the insert and the mstate update the insert fails?
//                        uiScope.launch {
//                            Thread.sleep(3000)
//                        }

                    }
                }
            }
        }
    }

    private fun listenIncomingRequests() {
        val docRefSent =
            db.collection("users").document(DataHolder.userDocId).collection("f_requests_in")
                .whereEqualTo("mstate", TransferPackage.STATE_SENT)

        docRefSent.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val requests = arrayListOf<FriendRequestPackage>()
                if (snapshot.documents.isNotEmpty()) {
                    for (request in snapshot.documents) {
                        val friendPackage = request.toObject(FriendRequestPackage::class.java)
                        friendPackage!!.documentPath = request.reference.path
                        requests.add(friendPackage)

                    }
                }
                receivedRequestsLiveData.value = requests
            }
        }
    }

    fun sendFriendRequest(friendUsername: String) {
        //updates the friend requests of sender and receiver
        informReceivingUserFirebase(friendUsername)
        updateSendingUserFirebase(friendUsername)
    }



    fun insertFriend(friend: Friend) = viewModelScope.launch {
//        in case both users send a request to each other and one of them accepts first
        if (!friends.value!!.contains(friend)) {
            repository.insertFriend(friend)
        }

    }

    //todo run network actions from viewmodelscope if they block ui
    /**
    * Updates the mstate field of the request in f_requests_out for the sender
     */
    fun updateRequestStateSender(friendRequestPackage: FriendRequestPackage, newState: String) {
        //tells sender its accepted (state changes to ACCEPTED) so it will trigger the accepted snapshot, and accept the friend

        val docRef = db.collection("users").document(friendRequestPackage.senderDocId!!).collection("f_requests_out")
            .whereEqualTo("mreceiver", DataHolder.userName)
        docRef.get().addOnSuccessListener { docs ->
            if (!docs.isEmpty) {
                docs.documents[0].reference.update("mstate", newState, "receiverDocId",DataHolder.userDocId)
            }
        }
    }

    /**
     * Updates the mstate field of the request in f_requests_in for the receiver
     */
    fun updateRequestStateReceiver(friendRequestPackage: FriendRequestPackage, newState: String) {
        //receiver requests get modified so the state of the request moves to SOLVED (aka ignored)
//        val docRef = db.document(friendPackage.documentPath!!)
        val docRef = db.collection("users").document(DataHolder.userDocId).collection("f_requests_in")
            .whereEqualTo("msender", friendRequestPackage.mSender)
        docRef.get().addOnSuccessListener { docs ->
            if (!docs.isEmpty) {
                docs.documents[0].reference.update("mstate", newState)
            }
        }
    }

    /**
     * Generates a request in the f_requests_in for the receiver of the request
     */
    private fun informReceivingUserFirebase(friendUsername: String) {
        //receiver gets a friend request with STATE SENT (default) which will trigger the SENT snapshot listener
        var docUid: String
        val docRef2 = db.collection("users").whereEqualTo("username", friendUsername)
        docRef2.get().addOnSuccessListener { docs ->
            if (docs.isEmpty) {
                //no user with this name exists
                Toast.makeText(
                    application, " User doesn't exist",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                docUid = docs.documents[0].id
                val docRef =
                    db.collection("users").document(docUid).collection("f_requests_in")
                docRef.get()
                    .addOnSuccessListener { documents ->
                        val friendPackage = FriendRequestPackage(
                            false, DataHolder.userName, friendUsername
                        )
                        friendPackage.senderDocId=DataHolder.userDocId
                        friendPackage.receiverDocId=docUid
                        docRef.add(
                            friendPackage
                        )
                        Toast.makeText(
                            application, "Friend request sent",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    .addOnFailureListener { exception ->
                        Timber.d("get failed with: $exception ")
                    }
            }
        }
    }

    /**
     * Generates a request in the f_requests_out for the sender
     */
    private fun updateSendingUserFirebase(friendUsername: String) {
        //updates the senders firebase to sent, this will be modified by the receiver to accepted or rejected
        val friendPackage = FriendRequestPackage(
            false, DataHolder.userName, friendUsername
        )
        friendPackage.senderDocId=DataHolder.userDocId
        db.collection("users").document(DataHolder.userDocId).collection("f_requests_out").add(friendPackage)
    }


}