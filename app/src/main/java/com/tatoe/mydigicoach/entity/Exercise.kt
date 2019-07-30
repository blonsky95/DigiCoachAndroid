package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_table")
data class Exercise(

    //I only want name and description as constructors, so primary key id is outside
    // constructor and starts with 0 (initialization required).
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String
) {
    @PrimaryKey(autoGenerate = true)
    var exerciseId: Int = 0

}