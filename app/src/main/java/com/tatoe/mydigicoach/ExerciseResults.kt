package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import timber.log.Timber
import java.util.ArrayList

class ExerciseResults {

    var resultsArrayList: ArrayList<ResultSet> = arrayListOf()

    var plottableVariable:String?=null

    fun addResult(date: String, result: String, isPlottable:Boolean = false) {

        var newDate = Day.dashSeparatedDateFormat.parse(date)
        var resultSet = ResultSet(newDate) //check if there is a resultset with this date already(?)
        if (isPlottable) {
            //todo check if valid to double here
            resultSet.addPlottableResult(result.toDouble())
        } else {
            resultSet.addResult(result)
        }
        Timber.d("RESULT SET: ${resultSet.sResult}")
//        results.add(resultSet)
        addToArrayByDate(resultSet)
    }

    private fun addToArrayByDate(newResultSet: ResultSet) {
        var i = 0
        while (i<resultsArrayList.size) {
            if (newResultSet.sDate.after(resultsArrayList[i].sDate)) {
                resultsArrayList.add(i, newResultSet)
                return
            }
            i++
        }

        resultsArrayList.add(i, newResultSet)
        Timber.d("RESULT SET 2: ${resultsArrayList.size}")

    }

}