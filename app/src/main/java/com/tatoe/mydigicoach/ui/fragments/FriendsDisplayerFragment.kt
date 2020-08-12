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
import com.tatoe.mydigicoach.DialogPositiveNegativeInterface
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.FriendRequestPackage
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.viewmodels.FriendsDisplayerFragmentViewModel
import com.tatoe.mydigicoach.viewmodels.MyFriendsFragmentViewModelFactory
import kotlinx.android.synthetic.main.fragment_friends_screen.*
import kotlinx.android.synthetic.main.fragment_friends_screen.view.*
import kotlinx.android.synthetic.main.item_holder_friends.view.*

class FriendsDisplayerFragment : Fragment() {

    private lateinit var friendsDisplayerFragmentViewModel: FriendsDisplayerFragmentViewModel
    private var db = FirebaseFirestore.getInstance()
    private lateinit var friendsAdapter: MyCustomFriendsAdapter
    private lateinit var recyclerView:RecyclerView
    private var receivedRequestsArray = arrayListOf<FriendRequestPackage>()
    private var allFriends = listOf<Friend>()

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

        friendsDisplayerFragmentViewModel = ViewModelProviders.of(
            this,
            MyFriendsFragmentViewModelFactory(db, activity!!.application)
        ).get(
            FriendsDisplayerFragmentViewModel::
            class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView=view.friendsRecyclerView
        friendsAdapter = MyCustomFriendsAdapter(activity!!)
        recyclerView.layoutManager=LinearLayoutManager(activity)
        recyclerView.adapter = friendsAdapter
        initObservers()

        add_friend_view_btn.setOnClickListener {
            var editTextText=add_friend_edit_text.text.trim().toString()
            val title = "New friend request"
            val text = "Send friend request to user $editTextText?"
            val dialogPositiveNegativeHandler = object : DialogPositiveNegativeInterface {
                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    for (friend in allFriends) {
                        if (friend.username==editTextText) {
                            Toast.makeText(activity, "$editTextText is already a friend", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    friendsDisplayerFragmentViewModel.sendFriendRequest(editTextText)
                }
            }
            Utils.getInfoDialogView(activity!!,title,text,dialogPositiveNegativeHandler)
        }

    }

    private fun initObservers() {
        friendsDisplayerFragmentViewModel.friends.observe(this, Observer {
            allFriends=it
            friendsAdapter.setContent(it)
        })

        friendsDisplayerFragmentViewModel.receivedRequestsLiveData.observe(this, Observer {
            receivedRequestsArray = it
        })
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