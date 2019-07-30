package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_table")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    var exerciseId: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String
) {
    //I only want name and description as constructors, so primary key id is outside
    // constructor and starts with 0 (initialization required).
    constructor(name: String, description: String) : this(0, name, description)

}