package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.arrayListOf
import kotlin.collections.set

class ExerciseResults {

//    var resultsArrayList: ArrayList<ResultSet> = arrayListOf()
    //contains all the results

    //when you want to access this use the getter, as this data type is string and not int - to store in firestore
    var resultsArrayList: ArrayList<HashMap<String, HashMap<String, String>>> = arrayListOf()

    //when you want to access this use the getter, as this data type is string and not int - to store in firestore
    //contains all the names of fields and the first time text/hints
    var resultFieldsMap = HashMap<String, HashMap<String, String>>()

    //todo ResultSet should be based on the resultFieldsMap
    //so result set should be modified to accord the LinkedHashMap - think about this - make an array of linked hash maps?

    companion object {

        const val NOTE_KEY = "Note"
        const val PLOTTABLE_KEY = "Plottable value"
        const val PLOTTABLE_VALUE = "plottable"
        const val DATE_KEY = "Date"

        fun getGenericFields(): HashMap<Int, HashMap<String, String>> {
            val genericResultFields = HashMap<Int, HashMap<String, String>>()

            genericResultFields[0] = hashMapOf(DATE_KEY to "04-03-1995")
            genericResultFields[1] = hashMapOf(NOTE_KEY to "String")
            genericResultFields[2] = hashMapOf(PLOTTABLE_KEY to PLOTTABLE_VALUE)

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

    fun getFieldsMap(): HashMap<Int, HashMap<String, String>> {
        return Exercise.stringMapToIntMap(resultFieldsMap)
    }

    fun setFieldsMap(mFieldsHashMap: HashMap<Int, HashMap<String, String>>) {
        resultFieldsMap = Exercise.intMapToStringMap(mFieldsHashMap)
    }

    fun getArrayListOfResults(): ArrayList<HashMap<Int, HashMap<String, String>>> {
        var arrayList = ArrayList<HashMap<Int, HashMap<String, String>>>()
        resultsArrayList.forEach{
            arrayList.add(Exercise.stringMapToIntMap(it))
        }
        return arrayList
    }

    fun setArrayListOfResults( arrayList: ArrayList<HashMap<Int, HashMap<String, String>>>) {
        var stringArrayList = ArrayList<HashMap<String, HashMap<String, String>>>()
        arrayList.forEach{
            stringArrayList.add(Exercise.intMapToStringMap(it))
        }
        resultsArrayList=stringArrayList
    }

    fun addResult(resultFieldsMap: HashMap<Int, HashMap<String, String>>) {
        addToArrayByDate(resultFieldsMap)
    }

    fun updateResult(resultFieldsMap: HashMap<Int, HashMap<String, String>>, position: Int) {
//        resultFieldsMap[DATE_KEY] = resultsArrayList[position][0]!!.second //getting the date (index 0), and the value of the pair (second)
        resultsArrayList[position] = Exercise.intMapToStringMap(resultFieldsMap)
    }

    fun removeResult(resultIndex: Int) {
        resultsArrayList.removeAt(resultIndex)
    }

    fun getPlottableArrays(): ArrayList<PlottableBundle> {
        var plottableBundleArray = arrayListOf<PlottableBundle>()
        var intResultsFieldsMap = Exercise.stringMapToIntMap(resultFieldsMap)
        var intResultsArrayList = getArrayListOfResults()
        for (i in 0 until intResultsFieldsMap.size) {
            var arrayX = arrayListOf<Date>()
            var arrayY = arrayListOf<Double>()
            var nameVariable = ""
            var fieldEntry = intResultsFieldsMap[i]!!.entries.iterator().next()
            if (fieldEntry.value == PLOTTABLE_VALUE) {
                nameVariable = fieldEntry.key
                var containsEmptyValues = false

                for (result in intResultsArrayList) {
                    try {
                        Timber.d("NUTS: $result")
                        if (result[i] != null) {
                            var yAxisEntry = result[i]!!.entries.iterator().next()
                            var xAxisEntry = result[0]!!.entries.iterator().next()

                            arrayX.add(stringToDate(xAxisEntry.value))
                            arrayY.add(yAxisEntry.value.toDouble())
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

    private fun addToArrayByDate(newResultMap: HashMap<Int, HashMap<String, String>>) {

        var intResultsArrayList = getArrayListOfResults()

        var i = 0
        while (i < intResultsArrayList.size) {
            try {
                val newDate = stringToDate(newResultMap[0]!![DATE_KEY]!!)
                val oldDate =
                    stringToDate(intResultsArrayList[i][0]!![DATE_KEY]!!) //getting the value (second) of DATE field (0) of the result you are iterating through (i)
                if (newDate.after(oldDate)) {
                    intResultsArrayList.add(i, newResultMap)
                    setArrayListOfResults(intResultsArrayList)
                    return
                }
                i++
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        intResultsArrayList.add(i, newResultMap)
        setArrayListOfResults(intResultsArrayList)

    }

    fun containsResult(dayId: String): Boolean {
        for (result in getArrayListOfResults()) {
            if (result[0]!![DATE_KEY]!! == Day.dayIDtoDashSeparator(dayId)) {
                return true
            }
        }
        return false
    }

    fun getResultPosition(date: String): Int {
        var intResultsArrayList = getArrayListOfResults()

        for (result in intResultsArrayList) {
            var x = result[0]!![DATE_KEY]!!
            var y = Day.dayIDtoDashSeparator(date)
            if (result[0]!![DATE_KEY]!! == Day.dayIDtoDashSeparator(date)) {
                return intResultsArrayList.indexOf(result)
            }
        }
        return -2
    }

    fun getResultDate(position: Int): String {
        return getArrayListOfResults()[position][0]!![DATE_KEY]!!
    }

    fun getResultFromDate(date:String): HashMap<Int, HashMap<String, String>> {
        var resultsMap = hashMapOf<Int, HashMap<String, String>>()
        if (containsResult(date)){
            resultsMap = getArrayListOfResults()[getResultPosition(date)]
        }
        return resultsMap

    }

    fun getPlottableNames(): ArrayList<String> {
        var intResultsFieldsMap = Exercise.stringMapToIntMap(resultFieldsMap)

        var array = arrayListOf<String>()
        for (i in 0 until intResultsFieldsMap.size) {
            var entry = intResultsFieldsMap[i]!!.entries.iterator().next()
            if (entry.value == PLOTTABLE_VALUE) {
                array.add(entry.key)
            }
        }
        return array
    }


}