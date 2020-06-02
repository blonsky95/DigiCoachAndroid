package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

@Entity(tableName = "friend_table")
data class Friend(
    @PrimaryKey @ColumnInfo @field: SerializedName("username") var username: String
)

