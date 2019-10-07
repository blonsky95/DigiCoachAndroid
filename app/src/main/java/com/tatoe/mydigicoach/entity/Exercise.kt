package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tatoe.mydigicoach.ResultSet
import java.util.*

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

    fun addResult(date: String, result: String) {

        var newDate = Day.dashSeparatedDateFormat.parse(date)
        var resultSet = ResultSet(newDate) //check if there is a resultset with this date already(?)
        resultSet.addResult(result)

//        results.add(resultSet)
        addToArrayByDate(resultSet)
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

    }

//    fun getResults() : ArrayList<com.tatoe.mydigicoach.ResultSet> {
//
//    }

    //I only want name and description as constructors, so primary key id is outside
    // constructor and starts with 0 (initialization required).
    constructor(name: String, description: String) : this(0, name, description)

}