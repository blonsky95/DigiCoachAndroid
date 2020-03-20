package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
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

    var resultsArrayList: ArrayList<HashMap<String, String>> = arrayListOf()

    var resultFieldsMap = HashMap<String, String>()
    //contains all the names of fields and the first time text/hints

    //todo ResultSet should be based on the resultFieldsMap
    //so result set should be modified to accord the LinkedHashMap - think about this - make an array of linked hash maps?

    companion object {

        const val NOTE_KEY = "Note"
        const val PLOTTABLE_KEY = "Plottable value"
        const val PLOTTABLE_VALUE = "plottable"
        const val DATE_KEY = "Date"

        fun getGenericFields(): HashMap<String, String> {
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

    fun addResult(date: String, resultFieldsMap: HashMap<String, String>) {
        resultFieldsMap[DATE_KEY] = date
        addToArrayByDate(resultFieldsMap)
    }

    fun updateResult(resultFieldsMap: HashMap<String, String>, position: Int) {
        resultFieldsMap[DATE_KEY] = resultsArrayList[position][DATE_KEY]!!
        resultsArrayList[position] = resultFieldsMap
    }

    fun getPlottableArrays(): ArrayList<PlottableBundle> {
        var plottableBundleArray = arrayListOf<PlottableBundle>()



        for (entry in resultFieldsMap) {
            var arrayX = arrayListOf<Date>()
            var arrayY = arrayListOf<Double>()
            var nameVariable = ""
            if (entry.value == PLOTTABLE_VALUE) {
                nameVariable = entry.key
                var containsEmptyValues = false
                for (result in resultsArrayList) {
                    try {
                        Timber.d("NUTS: $result")
                        if (result[nameVariable]!=null) {
                            arrayX.add(stringToDate(result[DATE_KEY]!!))
                            arrayY.add(result[nameVariable]!!.toDouble())
                        }
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

    private fun addToArrayByDate(newResultMap: HashMap<String, String>) {

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
//    fun numberResultsPerDate(date: String): Int {
//        var counter = 0
//        for (result in resultsArrayList){
//            if (result[DATE_KEY]==date){
//                counter++
//            }
//        }
//        return counter
//    }

//    fun getResultsPerDate(date: String) : ArrayList<LinkedHashMap<String,String>> {
//        var output = arrayListOf<LinkedHashMap<String,String>>()
//        var datePositionInResults = getResultPosition(date)
//        var resultsPerDate=numberResultsPerDate(date)
//        for (i in 0 until resultsPerDate) {
//            output.add(resultsArrayList[datePositionInResults+i])
//        }
//        return output
//    }

    fun containsResult(date: String): Boolean {
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

    fun getPlottableNames(): ArrayList<String> {
        var array = arrayListOf<String>()
        for (entry in resultFieldsMap) {
            if (entry.value == PLOTTABLE_VALUE) {
                array.add(entry.key)
            }
        }
        return array
    }
}