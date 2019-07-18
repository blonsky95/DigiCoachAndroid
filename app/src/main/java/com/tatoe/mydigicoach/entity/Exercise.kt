package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "exercise_table")
data class Exercise(
    //todo add primary key from example so not needed in every exercise constructyor

    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String
)