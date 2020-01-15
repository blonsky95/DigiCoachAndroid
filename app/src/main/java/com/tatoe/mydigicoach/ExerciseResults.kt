package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import timber.log.Timber
import java.util.ArrayList

class ExerciseResults {

    var resultsArrayList: ArrayList<ResultSet> = arrayListOf()

    var plottableVariable:String?=null

    private var resultFieldsMap = LinkedHashMap<String, String>()


    fun addResult(date: String, result: String = "", plottableResult:String = "") {

        var newDate = Day.dashSeparatedDateFormat.parse(date)
        var resultSet = ResultSet(newDate) //check if there is a resultset with this date already(?)
        if (plottableResult.isNotEmpty()) {
            resultSet.addPlottableResult(result.toDouble())
        }
        if (result.isNotEmpty()){
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