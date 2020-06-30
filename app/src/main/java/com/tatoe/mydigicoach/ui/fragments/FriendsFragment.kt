package com.tatoe.mydigicoach.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.FriendRequestPackage
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.viewmodels.FriendsFragmentViewModel
import com.tatoe.mydigicoach.viewmodels.MyFriendsFragmentViewModelFactory
import kotlinx.android.synthetic.main.fragment_friends_screen.*
import kotlinx.android.synthetic.main.fragment_friends_screen.view.*
import kotlinx.android.synthetic.main.item_holder_friends.view.*

class FriendsFragment : Fragment() {

    //    private lateinit var cloudActionInterface: HandleCloudActionsInterface
    private lateinit var friendsFragmentViewModel: FriendsFragmentViewModel
    private var db = FirebaseFirestore.getInstance()
    private lateinit var friendsAdapter: MyCustomFriendsAdapter
    private lateinit var recyclerView:RecyclerView
    private var receivedRequestsArray = arrayListOf<FriendRequestPackage>()
    private var allFriends = listOf<Friend>()


    //So I have the profile view model here, meaning there was no need for me to implement the interface
    //however I'd rather have the fragment specify on UI logic, and the activity on other stuff.
    //Plus, I also use some interfaces.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends_screen, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //so because the attaching activity implements the interface I can use its context directly to
//        initialise the interface im using when the buttons selected!!!!!!!!

        friendsFragmentViewModel = ViewModelProviders.of(
            this,
            MyFriendsFragmentViewModelFactory(db, activity!!.application)
        ).get(
            FriendsFragmentViewModel::
            class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        myExercisesAdapter = Library.MyCustomExercisesAdapter(this)
//        friendsAdapter = MyCustomFriendsAdapter(activity!!)
//        friendsRecyclerView.adapter = friendsAdapter
        recyclerView=view.friendsRecyclerView
        friendsAdapter = MyCustomFriendsAdapter(activity!!)
        recyclerView.layoutManager=LinearLayoutManager(activity)
        recyclerView.adapter = friendsAdapter
        initObservers()

        add_friend_btn.setOnClickListener {
            val title = "New friend request"
            val text = "Type friend username"
            val dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    for (friend in allFriends) {
                        if (friend.username==inputText) {
                            Toast.makeText(activity, "$inputText is already a friend", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    friendsFragmentViewModel.sendFriendRequest(inputText)
                }
            }
            Utils.getDialogViewWithEditText(activity!!,title,text,"username",dialogPositiveNegativeHandler)
        }

    }

    private fun initObservers() {
        friendsFragmentViewModel.friends.observe(this, Observer {
            allFriends=it
            friendsAdapter.setContent(it)
        })

        friendsFragmentViewModel.receivedRequestsLiveData.observe(this, Observer {
            receivedRequestsArray = it
            updateAcceptButton()
        })
    }

    private fun updateAcceptButton() {
        if (receivedRequestsArray.isEmpty()) {
            request_received_btn.text = "No friend requests"
        } else {
            request_received_btn.text = "New Friend Request!"

            var currentFriendRequest = receivedRequestsArray[0]
            var requestFriendName = currentFriendRequest.mSender
            val title = "New friend request"
            val text = "accept request from $requestFriendName"
            val dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    val newFriend = Friend(currentFriendRequest.mSender!!,currentFriendRequest.senderDocId!!)
//                    newFriend.docId=currentFriendRequest.senderDocId!!
                    friendsFragmentViewModel.insertFriend(newFriend) //GOOD
                    friendsFragmentViewModel.updateRequestStateReceiver(currentFriendRequest, TransferPackage.STATE_ACCEPTED)
                    friendsFragmentViewModel.updateRequestStateSender(currentFriendRequest,TransferPackage.STATE_ACCEPTED)
//                    friendsFragmentViewModel.popFriendRequest()
                }

                override fun onNegativeButton() {
                    super.onNegativeButton()
                    friendsFragmentViewModel.updateRequestStateReceiver(currentFriendRequest,TransferPackage.STATE_REJECTED)
                    friendsFragmentViewModel.updateRequestStateSender(currentFriendRequest,TransferPackage.STATE_REJECTED)
                }
            }
            request_received_btn.setOnClickListener {
                Utils.getInfoDialogView(activity!!, title ,text,dialogPositiveNegativeHandler)
            }
        }
    }


    class MyCustomFriendsAdapter(context: Context) : RecyclerView.Adapter<MyFriendViewHolder>() {

        var allFriends = listOf<Friend>()

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFriendViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_friends, parent, false)
            return MyFriendViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return allFriends.size
        }

        override fun onBindViewHolder(holder: MyFriendViewHolder, position: Int) {
            holder.usernameTextView.text = allFriends[position].username
        }

        fun setContent(friends: List<Friend>) {
            allFriends = friends
            notifyDataSetChanged()
        }

    }

    class MyFriendViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var usernameTextView = v.friend_username_textview
    }


}