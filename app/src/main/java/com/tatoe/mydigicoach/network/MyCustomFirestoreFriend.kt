package com.tatoe.mydigicoach.network

import com.tatoe.mydigicoach.entity.Friend

class MyCustomFirestoreFriend(var friend: Friend) {

    constructor():this(Friend("unknown_friend"))

    var mFriendDocId=friend.docId
    var mFriendUsername=friend.username

    fun toFriend():Friend {
        return Friend(mFriendUsername,mFriendDocId)
    }
}
