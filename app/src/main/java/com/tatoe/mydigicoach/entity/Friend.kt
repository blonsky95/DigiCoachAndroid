package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "friend_table")
data class Friend(
    @PrimaryKey @ColumnInfo @field: SerializedName("username") var username: String,
    @ColumnInfo(name = "doc_id")
    @field: SerializedName("doc_id")
    var docId: String? = "empty_xxx"
    )

