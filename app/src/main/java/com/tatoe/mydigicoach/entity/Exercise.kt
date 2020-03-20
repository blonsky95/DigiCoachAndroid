package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.ResultSet
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap
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
    var fieldsHashMap: HashMap<Int,Pair<String,String>> = HashMap() //todo eventually this will get rid of saving name and description in database

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

    constructor(mFieldsHashMap:HashMap<Int,Pair<String,String>>) : this (mFieldsHashMap[0]!!.second,mFieldsHashMap[1]!!.second) {
        fieldsHashMap=mFieldsHashMap
    }


    //returns linked hash map with name, description + extra fieldsHashMap
    fun getFieldsMap():HashMap<Int,Pair<String,String>> {
        return fieldsHashMap
    }

    companion object {

        fun pairHashMapToLinked(exerciseFieldsMap: HashMap<Int,Pair<String,String>>): LinkedHashMap<String,String> {
            var linkedHashMap = LinkedHashMap<String,String>()

            for (i in 0 until exerciseFieldsMap.size){
                linkedHashMap[exerciseFieldsMap[i]!!.first]=exerciseFieldsMap[i]!!.second
            }
            return linkedHashMap
        }

        fun linkedToPairHashMap(linkedHashMap: LinkedHashMap<String,String>) : HashMap<Int,Pair<String,String>> {
            var pairHashMap=HashMap<Int,Pair<String,String>>()

            var i =0
            linkedHashMap.forEach{
                pairHashMap[i] = Pair(it.key,it.value)
                i++
            }
//            for (i in 0 until linkedHashMap.size) {
//                pairHashMap.put(i,Pair<String,String>(linkedHashMap.))
//            }
            return pairHashMap
        }
    }


}