package com.tatoe.mydigicoach.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import kotlinx.android.synthetic.main.fragment_friends_screen.view.*
import kotlinx.android.synthetic.main.fragment_share_to_friends_screen.*
import kotlinx.android.synthetic.main.item_holder_friends.view.friend_username_textview
import kotlinx.android.synthetic.main.item_holder_share_friends.view.*
import java.lang.ClassCastException

class ShareToFriendsFragment : Fragment() {

    private lateinit var shareFriendsAdapter: MyCustomShareFriendsAdapter
    private lateinit var recyclerView: RecyclerView
    private var allFriends = listOf<Friend>()

    var friendSelectorListener: OnFriendSelectedListenerInterface? = null

    companion object {

        const val BUNDLE_ALLFRIENDS_KEY = "allfriends_object"

        fun newInstance(
            allFriends: List<Friend>
        ): ShareToFriendsFragment {
            val shareToFriendsFragment = ShareToFriendsFragment()

            shareToFriendsFragment.arguments = Bundle().apply {
                putString(BUNDLE_ALLFRIENDS_KEY, Gson().toJson(allFriends))
            }

            return shareToFriendsFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_share_to_friends_screen, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        friendSelectorListener = context as? OnFriendSelectedListenerInterface
        if (friendSelectorListener == null) {
            throw ClassCastException("$context must implement OnFriendSelectedListenerInterface")
        }

        arguments?.getString(BUNDLE_ALLFRIENDS_KEY)?.let {
            allFriends = Gson().fromJson(it, object : TypeToken<List<Friend>>() {}.type)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.friendsRecyclerView
        shareFriendsAdapter = MyCustomShareFriendsAdapter(activity!!)
        shareFriendsAdapter.setContent(allFriends)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = shareFriendsAdapter
        cancel_btn.setOnClickListener {
            friendSelectorListener?.onCancelSelected()
        }

    }

    inner class MyCustomShareFriendsAdapter(context: Context) :
        RecyclerView.Adapter<MyFriendViewHolder>() {

        var allFriends = listOf<Friend>()

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFriendViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_share_friends, parent, false)
            return MyFriendViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return allFriends.size
        }

        override fun onBindViewHolder(holder: MyFriendViewHolder, position: Int) {
            holder.usernameTextView.text = allFriends[position].username
            holder.shareImageView.setOnClickListener {
                friendSelectorListener?.onFriendSelected(allFriends[position])
            }
            //do listener on share icon
        }

        fun setContent(friends: List<Friend>) {
            allFriends = friends
            notifyDataSetChanged()
        }

    }

    class MyFriendViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var usernameTextView = v.friend_username_textview
        var shareImageView = v.share_image_view
    }

    interface OnFriendSelectedListenerInterface {
        fun onFriendSelected(friend: Friend)

        fun onCancelSelected()
    }
}