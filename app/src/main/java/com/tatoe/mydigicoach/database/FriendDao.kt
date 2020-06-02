package com.tatoe.mydigicoach.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Friend

@Dao
interface FriendDao {

    @Query("SELECT * FROM friend_table ORDER BY username ASC")
    fun getAllLiveData(): androidx.lifecycle.LiveData<List<Friend>>

    @Insert
    suspend fun insert(friend : Friend) : Long

    @Delete
    suspend fun delete(friend: Friend)
}