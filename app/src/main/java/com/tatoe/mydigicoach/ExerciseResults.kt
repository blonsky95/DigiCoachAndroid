package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.arrayListOf
import kotlin.collections.set

class ExerciseResults {

//    var resultsArrayList: ArrayList<ResultSet> = arrayListOf()
    //contains all the results

    var resultsArrayList: ArrayList<LinkedHashMap<String, String>> = arrayListOf()

    var resultFieldsMap = LinkedHashMap<String, String>()
    //contains all the names of fields and the first time text/hints

    //todo ResultSet should be based on the resultFieldsMap
    //so result set should be modified to accord the LinkedHashMap - think about this - make an array of linked hash maps?

    companion object {

        const val NOTE_KEY = "Note"
        const val PLOTTABLE_KEY = "Plottable value"
        const val PLOTTABLE_VALUE = "plottable"
        const val DATE_KEY = "Date"

        fun getGenericFields() : LinkedHashMap<String, String> {
            val genericResultFields = LinkedHashMap<String, String>()

            genericResultFields[NOTE_KEY]="String"
            genericResultFields[PLOTTABLE_KEY]=PLOTTABLE_VALUE
            return genericResultFields
        }

        fun getReadableDate(sDate : Date):String {
            return Day.presentableDateFormat.format(sDate)
        }

        fun stringToDate(sString:String) :Date {
            val format = SimpleDateFormat("dd-MM-yy")
            return format.parse(sString) as Date
        }
    }

    fun addResult(date: String, resultFieldsMap:LinkedHashMap<String, String>) {
        resultFieldsMap[DATE_KEY]=date
        addToArrayByDate(resultFieldsMap)
    }

    fun updateResult(resultFieldsMap:LinkedHashMap<String, String>,position :Int) {
        resultFieldsMap[DATE_KEY] = resultsArrayList[position][DATE_KEY]!!
        resultsArrayList[position]=resultFieldsMap
    }

    fun getPlottableArrays():ArrayList<PlottableBundle> {
        var plottableBundleArray = arrayListOf<PlottableBundle>()

        var arrayX = arrayListOf<Date>()
        var arrayY = arrayListOf<Double>()
        var nameVariable = ""

        for (entry in resultFieldsMap) {
            if (entry.value==PLOTTABLE_VALUE) {
                nameVariable=entry.key
                for (result in resultsArrayList) {
                    arrayX.add(stringToDate(result[DATE_KEY]!!))
                    arrayY.add(result[entry.key]!!.toDouble())
                }
                plottableBundleArray.add(PlottableBundle(nameVariable,arrayX,arrayY))
            }
        }

        return plottableBundleArray
    }

//    fun addResult(date: String, result: String = "", plottableResult:String = "") {
//
//        var newDate = Day.dashSeparatedDateFormat.parse(date)
//        var resultSet = ResultSet(newDate) //check if there is a resultset with this date already(?)
//        if (plottableResult.isNotEmpty()) {
//            resultSet.addPlottableResult(plottableResult.toDouble())
//        }
//        if (result.isNotEmpty()){
//            resultSet.addResult(result)
//        }
//
//        Timber.d("RESULT SET: ${resultSet.sResult}")
////        results.add(resultSet)
//        addToArrayByDate(resultSet)
//    }

//    private fun addToArrayByDate(newResultSet: ResultSet) {
//        var i = 0
//        while (i<resultsArrayList.size) {
//            if (newResultSet.sDate.after(resultsArrayList[i].sDate)) {
//                resultsArrayList.add(i, newResultSet)
//                return
//            }
//            i++
//        }
//
//        resultsArrayList.add(i, newResultSet)
//        Timber.d("RESULT SET 2: ${resultsArrayList.size}")
//
//    }

    private fun addToArrayByDate(newResultMap: LinkedHashMap<String, String>) {

        var i = 0
        while (i<resultsArrayList.size) {
            try {
                val newDate = stringToDate(newResultMap[DATE_KEY]!!)
                val oldDate = stringToDate(resultsArrayList[i][DATE_KEY]!!)
                if (newDate.after(oldDate)) {
                    resultsArrayList.add(i, newResultMap)
                    return
                }
                i++
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        resultsArrayList.add(i, newResultMap)

    }
}