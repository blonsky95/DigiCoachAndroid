package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber
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

    var resultsArrayList: ArrayList<HashMap<Int, Pair<String, String>>> = arrayListOf()

    var resultFieldsMap = HashMap<Int, Pair<String, String>>()
    //contains all the names of fields and the first time text/hints

    //todo ResultSet should be based on the resultFieldsMap
    //so result set should be modified to accord the LinkedHashMap - think about this - make an array of linked hash maps?

    companion object {

        const val NOTE_KEY = "Note"
        const val PLOTTABLE_KEY = "Plottable value"
        const val PLOTTABLE_VALUE = "plottable"
        const val DATE_KEY = "Date"

        fun getGenericFields(): HashMap<Int, Pair<String, String>> {
            val genericResultFields = HashMap<Int, Pair<String, String>>()

            genericResultFields[0] = Pair(DATE_KEY, "04-03-1995")
            genericResultFields[1] = Pair(NOTE_KEY, "String")
            genericResultFields[2] = Pair(PLOTTABLE_KEY, PLOTTABLE_VALUE)

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

    fun addResult(resultFieldsMap: LinkedHashMap<String, String>) {
        addToArrayByDate(resultFieldsMap)
    }

    fun updateResult(resultFieldsMap: LinkedHashMap<String, String>, position: Int) {
//        resultFieldsMap[DATE_KEY] = resultsArrayList[position][0]!!.second //getting the date (index 0), and the value of the pair (second)
        resultsArrayList[position] = Exercise.linkedToPairHashMap(resultFieldsMap)
    }

    fun getPlottableArrays(): ArrayList<PlottableBundle> {
        var plottableBundleArray = arrayListOf<PlottableBundle>()
        for (i in 0 until resultFieldsMap.size) {
            var arrayX = arrayListOf<Date>()
            var arrayY = arrayListOf<Double>()
            var nameVariable = ""
            if (resultFieldsMap[i]!!.second == PLOTTABLE_VALUE) {
                nameVariable = resultFieldsMap[i]!!.first
                var containsEmptyValues = false
                for (result in resultsArrayList) {
                    try {
                        Timber.d("NUTS: $result")
                        if (result[i] != null) {
                            arrayX.add(stringToDate(result[0]!!.second))
                            arrayY.add(result[i]!!.second.toDouble())
                        }
                    } catch (e: NumberFormatException) {
                        arrayY.add(0.toDouble())
                        containsEmptyValues = true
                    }

                }
                plottableBundleArray.add(
                    PlottableBundle(
                        nameVariable,
                        arrayX,
                        arrayY,
                        containsEmptyValues
                    )
                )
            }
        }

        return plottableBundleArray
    }

    private fun addToArrayByDate(newResultMap: LinkedHashMap<String, String>) {

        var i = 0
        while (i < resultsArrayList.size) {
            try {
                val newDate = stringToDate(newResultMap[DATE_KEY]!!)
                val oldDate =
                    stringToDate(resultsArrayList[i][0]!!.second) //getting the value (second) of DATE field (0) of the result you are iterating through (i)
                if (newDate.after(oldDate)) {
                    resultsArrayList.add(i, Exercise.linkedToPairHashMap(newResultMap))
                    return
                }
                i++
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        resultsArrayList.add(i, Exercise.linkedToPairHashMap(newResultMap))

    }

    fun containsResult(date: String): Boolean {
        for (result in resultsArrayList) {
            if (result[0]?.second == Day.dayIDtoDashSeparator(date)) {
                return true
            }
        }
        return false
    }

    fun getResultPosition(date: String): Int {

        for (result in resultsArrayList) {
            if (result[0]?.second == Day.dayIDtoDashSeparator(date)) {
                return resultsArrayList.indexOf(result)
            }
        }
        return -1
    }

    fun getResultDate(position: Int): String {
        return resultsArrayList[position][0]!!.second
    }

    fun getPlottableNames(): ArrayList<String> {
        var array = arrayListOf<String>()
        for (i in 0 until resultFieldsMap.size) {
            if (resultFieldsMap[i]!!.second == PLOTTABLE_VALUE) {
                array.add(resultFieldsMap[i]!!.first)
            }
        }
        return array
    }
}