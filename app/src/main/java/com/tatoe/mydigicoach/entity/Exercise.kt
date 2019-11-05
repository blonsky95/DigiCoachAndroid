package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tatoe.mydigicoach.ResultSet
import timber.log.Timber
import java.util.*
import kotlin.collections.LinkedHashMap

@Entity(tableName = "exercise_table")

data class Exercise(
    @PrimaryKey(autoGenerate = true)
    @field: SerializedName("id")
    var exerciseId: Int = 0,
    @ColumnInfo(name = "name") @field: SerializedName("name") var name: String,
    @ColumnInfo(name = "description") @field: SerializedName("description") var description: String
) {
    @ColumnInfo(name = "result")
    @field: SerializedName("result")
    var results: ArrayList<ResultSet> = arrayListOf()

    @ColumnInfo(name = "fieldsHashMap")
    @field: SerializedName("fieldsHashMap")
    var fieldsHashMap: LinkedHashMap<String,String> = LinkedHashMap() //todo eventually this will get rid of saving name and description in database

    //todo find a way of making the constructor, and instance data retrieval more efficient by
    //todo finding synergy between the LinkedHashMap including name and description and having a method here


    //I only want name and description as constructors, so primary key id is outside
    // constructor and starts with 0 (initialization required).
    constructor(name: String, description: String) : this(0, name, description)

    //returns linked hash map with name, description + extra fieldsHashMap
    fun getFieldsMap():LinkedHashMap<String,String> {
//        val linkedHashMap = LinkedHashMap<String,String>()
//        linkedHashMap["Name"]=name
//        linkedHashMap["Description"]=description
//        for (field in fieldsHashMap){
//            linkedHashMap[field.key]=field.value
//        }
        return fieldsHashMap
    }

    fun addResult(date: String, result: String) {

        var newDate = Day.dashSeparatedDateFormat.parse(date)
        var resultSet = ResultSet(newDate) //check if there is a resultset with this date already(?)
        resultSet.addResult(result)
        Timber.d("RESULT SET: ${resultSet.sResult}")
//        results.add(resultSet)
        addToArrayByDate(resultSet)
    }

    fun clearResults() {
        results= arrayListOf()
    }

    private fun addToArrayByDate(newResultSet: ResultSet) {
        var i = 0
        while (i<results.size) {
            if (newResultSet.sDate.after(results[i].sDate)) {
                results.add(i, newResultSet)
                return
            }
            i++
        }

        results.add(i, newResultSet)
        Timber.d("RESULT SET 2: ${results.size}")

    }


}