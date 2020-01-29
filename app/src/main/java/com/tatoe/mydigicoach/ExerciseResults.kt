package com.tatoe.mydigicoach

import android.widget.Toast
import com.tatoe.mydigicoach.entity.Day
import java.lang.NumberFormatException
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

        fun getGenericFields(): LinkedHashMap<String, String> {
            val genericResultFields = LinkedHashMap<String, String>()

            genericResultFields[NOTE_KEY] = "String"
            genericResultFields[PLOTTABLE_KEY] = PLOTTABLE_VALUE
            return genericResultFields
        }

        fun getReadableDate(sDate: Date): String {
            return Day.presentableDateFormat.format(sDate)
        }

        fun stringToDate(sString: String): Date {
            val format = SimpleDateFormat("dd-MM-yy")
            return format.parse(sString) as Date
        }
    }

    fun addResult(date: String, resultFieldsMap: LinkedHashMap<String, String>) {
        resultFieldsMap[DATE_KEY] = date
        addToArrayByDate(resultFieldsMap)
    }

    fun updateResult(resultFieldsMap: LinkedHashMap<String, String>, position: Int) {
        resultFieldsMap[DATE_KEY] = resultsArrayList[position][DATE_KEY]!!
        resultsArrayList[position] = resultFieldsMap
    }

    fun getPlottableArrays(): ArrayList<PlottableBundle> {
        var plottableBundleArray = arrayListOf<PlottableBundle>()

        var arrayX = arrayListOf<Date>()
        var arrayY = arrayListOf<Double>()
        var nameVariable = ""

        for (entry in resultFieldsMap) {
            if (entry.value == PLOTTABLE_VALUE) {
                nameVariable = entry.key
                var containsEmptyValues = false
                for (result in resultsArrayList) {
                    try {
                        arrayX.add(stringToDate(result[DATE_KEY]!!))
                        arrayY.add(result[entry.key]!!.toDouble())
                    } catch (e: NumberFormatException) {
                        arrayY.add(0.toDouble())
                        containsEmptyValues=true
                    }

                }
                plottableBundleArray.add(PlottableBundle(nameVariable, arrayX, arrayY,containsEmptyValues))
            }
        }

        return plottableBundleArray
    }

    private fun addToArrayByDate(newResultMap: LinkedHashMap<String, String>) {

        var i = 0
        while (i < resultsArrayList.size) {
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
    fun resultsPerDate(date: String): Int {
        var counter = 0
        for (result in resultsArrayList){
            if (result[DATE_KEY]==Day.dayIDtoDashSeparator(date)){
                counter++
            }
        }
        return counter
    }

    fun containsResult(date: String): Boolean {
        //todo make date unique + this returns boolean
//        var linkedHashMap = LinkedHashMap<String, String>()
//        linkedHashMap[DATE_KEY] = Day.dayIDtoDashSeparator(date)
//        resultsArrayList.contains(linkedHashMap)
        for (result in resultsArrayList){
            if (result[DATE_KEY]==Day.dayIDtoDashSeparator(date)){
                return true
            }
        }
//        var ccc = resultsArrayList.contains(linkedHashMap)
        return false
    }

    fun getResultPosition(date: String): Int {
//        var linkedHashMap = LinkedHashMap<String, String>()
//        linkedHashMap[DATE_KEY] = Day.dayIDtoDashSeparator(date)

        for (result in resultsArrayList){
            if (result[DATE_KEY]==Day.dayIDtoDashSeparator(date)){
                return resultsArrayList.indexOf(result)
            }
        }

        return -1
    }
}