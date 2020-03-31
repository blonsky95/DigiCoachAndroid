package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tatoe.mydigicoach.ExerciseResults
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

@Entity(tableName = "exercise_table")

data class Exercise(
    @ColumnInfo(name = "name") @field: SerializedName("name") var name: String = "",
    @ColumnInfo(name = "description") @field: SerializedName("description") var description: String = ""
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
    var fieldsHashMap: HashMap<String, HashMap<String,String>> =
        HashMap() //todo eventually this will get rid of saving name and description in database

    //todo find a way of making the constructor, and instance data retrieval more efficient by
    //todo finding synergy between the LinkedHashMap including name and description and having a method here

    //todo think about constructor, should I always use the LinkedHashMap, and not the other one?


    //I only want name and description as constructors, so primary key id is outside
    // constructor and starts with 0 (initialization required).
//    constructor(name: String, description: String) : this(name, description)
    constructor() : this("", "") {
//        fieldsHashMap= HashMap()
//        exerciseResults = ExerciseResults()
    }

    constructor(mFieldsHashMap: HashMap<Int, HashMap<String, String>>) : this(
        mFieldsHashMap[0]!!["Name"]!!,
        mFieldsHashMap[1]!!["Description"]!!
    ) {
        setFieldsMap(mFieldsHashMap)
    }


    //returns linked hash map with name, description + extra fieldsHashMap
    fun getFieldsMap(): HashMap<Int, HashMap<String, String>> {
        return stringMapToIntMap(fieldsHashMap)
    }

    fun setFieldsMap(mFieldsHashMap: HashMap<Int, HashMap<String, String>>) {
        fieldsHashMap = intMapToStringMap(mFieldsHashMap)
    }

    companion object {

        const val DATE_ID = "0"
        const val NAME_ID = "1"
        const val DESCRIPTION_ID = "2"

//        fun pairHashMapToLinked(exerciseFieldsMap: HashMap<Int, Pair<String, String>>): LinkedHashMap<String, String> {
//            var linkedHashMap = LinkedHashMap<String, String>()
//
//            for (i in 0 until exerciseFieldsMap.size) {
//                linkedHashMap[exerciseFieldsMap[i]!!.first] = exerciseFieldsMap[i]!!.second
//            }
//            return linkedHashMap
//        }

        fun linkedToPairHashMap(linkedHashMap: LinkedHashMap<String, String>): HashMap<Int, HashMap<String, String>> {
            var hashMap = HashMap<Int, HashMap<String, String>>()

            var i = 0
            linkedHashMap.forEach {
                hashMap[i]= hashMapOf(it.key to it.value)
                i++
            }
//            for (i in 0 until linkedHashMap.size) {
//                pairHashMap.put(i,Pair<String,String>(linkedHashMap.))
//            }
            return hashMap
        }

        fun intMapToStringMap(mFieldsHashMap: HashMap<Int, HashMap<String, String>>): HashMap<String, HashMap<String, String>> {
            var stringMap = HashMap<String, HashMap<String, String>>()
            for (i in 0 until mFieldsHashMap.size){
                stringMap[i.toString()]=mFieldsHashMap[i]!!
            }
            return stringMap
        }

        fun stringMapToIntMap(fieldsHashMap: HashMap<String, HashMap<String, String>>): HashMap<Int, HashMap<String, String>> {
            var intMap = HashMap<Int, HashMap<String, String>>()
            for (i in 0 until fieldsHashMap.size){
                intMap[i]=fieldsHashMap[i.toString()]!!
            }
            return intMap
        }
    }


}