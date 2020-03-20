package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.ResultSet
import timber.log.Timber
import java.util.*
import kotlin.collections.LinkedHashMap

@Entity(tableName = "exercise_table")

data class Exercise(
    @ColumnInfo(name = "name") @field: SerializedName("name") var name: String="",
    @ColumnInfo(name = "description") @field: SerializedName("description") var description: String=""
) {

    @PrimaryKey(autoGenerate = true)
    @field: SerializedName("id")
    var exerciseId: Int = 0

    //ROOM and PRIMARY KEY - If you set the id to 0, Room will assume this class instance hasn't been
    //inserted into the db. So if it's 0 and you insert, it will assign a new ID (autogenerate).

    @ColumnInfo(name = "result")
    @field: SerializedName("result")
//    var results: ArrayList<ResultSet> = arrayListOf()
    var exerciseResults: ExerciseResults = ExerciseResults()

    @ColumnInfo(name = "fieldsHashMap")
    @field: SerializedName("fieldsHashMap")
    var fieldsHashMap: HashMap<String,String> = HashMap() //todo eventually this will get rid of saving name and description in database

    //todo find a way of making the constructor, and instance data retrieval more efficient by
    //todo finding synergy between the LinkedHashMap including name and description and having a method here

    //todo think about constructor, should I always use the LinkedHashMap, and not the other one?


    //I only want name and description as constructors, so primary key id is outside
    // constructor and starts with 0 (initialization required).
//    constructor(name: String, description: String) : this(name, description)
    constructor() :this("","") {
//        fieldsHashMap= HashMap()
//        exerciseResults = ExerciseResults()
    }

    constructor(mFieldsHashMap:HashMap<String,String>) : this (mFieldsHashMap["Name"]!!,mFieldsHashMap["Description"]!!) {
        fieldsHashMap=mFieldsHashMap
    }


    //returns linked hash map with name, description + extra fieldsHashMap
    fun getFieldsMap():HashMap<String,String> {
        return fieldsHashMap
    }


}