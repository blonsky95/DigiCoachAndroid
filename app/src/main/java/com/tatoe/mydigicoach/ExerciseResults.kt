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

    //when you want to access this use the getter, as this data type is string and not int - to store in firestore
    var resultsArrayList: ArrayList<HashMap<String, Pair<String, String>>> = arrayListOf()

    //when you want to access this use the getter, as this data type is string and not int - to store in firestore
    //contains all the names of fields and the first time text/hints
    var resultFieldsMap = HashMap<String, Pair<String, String>>()

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

    fun getFieldsMap(): HashMap<Int, Pair<String, String>> {
        return Exercise.stringMapToIntMap(resultFieldsMap)
    }

    fun setFieldsMap(mFieldsHashMap: HashMap<Int, Pair<String, String>>) {
        resultFieldsMap = Exercise.intMapToStringMap(mFieldsHashMap)
    }

    fun getArrayListOfResults(): ArrayList<HashMap<Int, Pair<String, String>>> {
        var arrayList = ArrayList<HashMap<Int, Pair<String, String>>>()
        resultsArrayList.forEach{
            arrayList.add(Exercise.stringMapToIntMap(it))
        }
        return arrayList
    }

    fun setArrayListOfResults( arrayList: ArrayList<HashMap<Int, Pair<String, String>>>) {
        var stringArrayList = ArrayList<HashMap<String, Pair<String, String>>>()
        arrayList.forEach{
            stringArrayList.add(Exercise.intMapToStringMap(it))
        }
        resultsArrayList=stringArrayList
    }

    fun addResult(resultFieldsMap: HashMap<Int, Pair<String, String>>) {
        addToArrayByDate(resultFieldsMap)
    }

    fun updateResult(resultFieldsMap: HashMap<Int, Pair<String, String>>, position: Int) {
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
            if (intResultsFieldsMap[i]!!.second == PLOTTABLE_VALUE) {
                nameVariable = intResultsFieldsMap[i]!!.first
                var containsEmptyValues = false
                for (result in intResultsArrayList) {
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

    private fun addToArrayByDate(newResultMap: HashMap<Int, Pair<String, String>>) {

        var intResultsArrayList = getArrayListOfResults()

        var i = 0
        while (i < intResultsArrayList.size) {
            try {
                val newDate = stringToDate(newResultMap[0]!!.second)
                val oldDate =
                    stringToDate(intResultsArrayList[i][0]!!.second) //getting the value (second) of DATE field (0) of the result you are iterating through (i)
                if (newDate.after(oldDate)) {
                    intResultsArrayList.add(i, newResultMap)
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

    fun containsResult(date: String): Boolean {
        for (result in getArrayListOfResults()) {
            if (result[0]?.second == Day.dayIDtoDashSeparator(date)) {
                return true
            }
        }
        return false
    }

    fun getResultPosition(date: String): Int {
        var intResultsArrayList = getArrayListOfResults()

        for (result in intResultsArrayList) {
            if (result[0]?.second == Day.dayIDtoDashSeparator(date)) {
                return intResultsArrayList.indexOf(result)
            }
        }
        return -1
    }

    fun getResultDate(position: Int): String {
        return getArrayListOfResults()[position][0]!!.second
    }

    fun getPlottableNames(): ArrayList<String> {
        var intResultsFieldsMap = Exercise.stringMapToIntMap(resultFieldsMap)

        var array = arrayListOf<String>()
        for (i in 0 until intResultsFieldsMap.size) {
            if (intResultsFieldsMap[i]!!.second == PLOTTABLE_VALUE) {
                array.add(intResultsFieldsMap[i]!!.first)
            }
        }
        return array
    }


}