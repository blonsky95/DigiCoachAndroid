package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.tatoe.mydigicoach.database.DataConverter

@Entity(tableName = "exercise_table")

data class Exercise(
    @PrimaryKey(autoGenerate = true)
    @field: SerializedName("id")
    var exerciseId: Int = 0,
    @ColumnInfo(name = "name") @field: SerializedName("name") var name: String,
    @ColumnInfo(name = "description") @field: SerializedName("description") var description: String
) {
    @ColumnInfo(name = "result") @field: SerializedName("result") var results:MutableMap<String,String> = mutableMapOf()

    fun addResult(date:String, result:String) {
        results[date] = result
    }

    //I only want name and description as constructors, so primary key id is outside
    // constructor and starts with 0 (initialization required).
    constructor(name: String, description: String) : this(0, name, description)

}