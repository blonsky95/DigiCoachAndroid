package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import timber.log.Timber
import java.util.ArrayList

class ExerciseResults {

    var resultsArrayList: ArrayList<ResultSet> = arrayListOf()
    //contains all the results

    var resultFieldsMap = LinkedHashMap<String, String>()
    //contains all the names of fields and the first time text/hints

    //todo ResultSet should be based on the resultFieldsMap
    //so result set should be modified to accord the LinkedHashMap - think about this - make an array of linked hash maps?

    companion object {

        const val NOTE_KEY = "Note"
        const val PLOTTABLE_KEY = "Plottable value"

        fun getGenericFields() : LinkedHashMap<String, String> {
            val genericResultFields = LinkedHashMap<String, String>()

            genericResultFields[NOTE_KEY]="Type a note here"
            genericResultFields[PLOTTABLE_KEY]="e.g. 1000"
            return genericResultFields
        }
    }



    fun addResult(date: String, result: String = "", plottableResult:String = "") {

        var newDate = Day.dashSeparatedDateFormat.parse(date)
        var resultSet = ResultSet(newDate) //check if there is a resultset with this date already(?)
        if (plottableResult.isNotEmpty()) {
            resultSet.addPlottableResult(plottableResult.toDouble())
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