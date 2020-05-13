package com.tatoe.mydigicoach

import android.content.Context
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

        const val TIME_1 = "Time (s)"
        const val TIME_2 = "Time (mins)"
        const val DISTANCE_1 = "Distance (km)"
        const val DISTANCE_2 = "Distance (m)"
        const val WEIGHT_1 = "Weight (kg)"
        const val WEIGHT_2 = "Weight 1RM"


        fun getGenericFields(): HashMap<Int, HashMap<String, String>> {
            val genericResultFields = HashMap<Int, HashMap<String, String>>()

            genericResultFields[0] = hashMapOf(DATE_KEY to "04-03-1995")
            genericResultFields[1] = hashMapOf(NOTE_KEY to "String")
//            genericResultFields[2] = hashMapOf(PLOTTABLE_KEY to PLOTTABLE_VALUE)

            return genericResultFields
        }

        fun getReadableDate(sDate: Date): String {
            return Day.presentableDateFormat.format(sDate)
        }

        fun stringToDate(sString: String): Date {
            val format = SimpleDateFormat("dd-MM-yy")
            return format.parse(sString) as Date
        }

        fun isANumericEntry(entryName: String): Boolean {
            return entryName == TIME_1 || entryName == TIME_2 || entryName == DISTANCE_1 || entryName == DISTANCE_2 || entryName == WEIGHT_1 || entryName == WEIGHT_2
        }

        fun toReadableFormat(fieldEntryValue: String, fieldEntryKey: String): String {
            var string = fieldEntryValue
            when (fieldEntryKey) {
                //todo continue here
                TIME_1 -> {
                    string = "${fieldEntryValue}s"
                }
                TIME_2 -> {
                    string = "${fieldEntryValue.substring(
                        0,
                        fieldEntryValue.indexOf("-")
                    )} mins ${fieldEntryValue.substring(
                        fieldEntryValue.indexOf("-") + 1,
                        fieldEntryValue.length
                    )} s"
                }
                DISTANCE_1 -> {
                    string = "${fieldEntryValue}km"
                }
                DISTANCE_2 -> {
                    string = "${fieldEntryValue}m"
                }
                WEIGHT_1 -> {
                    string = "${fieldEntryValue}kg"
                }
                WEIGHT_2 -> {
                    string = "${fieldEntryValue.substring(
                        0,
                        fieldEntryValue.indexOf("-")
                    )} reps ${fieldEntryValue.substring(
                        fieldEntryValue.indexOf("-") + 1,
                        fieldEntryValue.indexOf("-", fieldEntryValue.indexOf("-") + 1)
                    )}kg - 1RM = ${fieldEntryValue.substring(
                        fieldEntryValue.indexOf(
                            "-",
                            fieldEntryValue.indexOf("-") + 1
                        ) + 1, fieldEntryValue.length
                    )}kg"
                }
            }

            return string
        }

        fun toNumericFormat(fieldEntryValue: String, fieldEntryKey: String): ArrayList<Long> {
            var long = arrayListOf<Long>()
            when (fieldEntryKey) {
                TIME_1 -> {
                    long.add(fieldEntryValue.toLong())
                }
                TIME_2 -> {
                    long.add(
                        fieldEntryValue.substring(
                            0, fieldEntryValue.indexOf("-")
                        ).toLong()
                    )
                    long.add(
                        fieldEntryValue.substring(
                            fieldEntryValue.indexOf("-") + 1,
                            fieldEntryValue.length
                        ).toLong()
                    )
                }
                DISTANCE_1 -> {
                    long.add(fieldEntryValue.toLong())
                }
                DISTANCE_2 -> {
                    long.add(fieldEntryValue.toLong())
                }
                WEIGHT_1 -> {
                    long.add(fieldEntryValue.toLong())
                }
                WEIGHT_2 -> {
                    long.add(
                        fieldEntryValue.substring(
                            0,
                            fieldEntryValue.indexOf("-")
                        ).toLong())

                    long.add(fieldEntryValue.substring(
                        fieldEntryValue.indexOf("-") + 1,
                        fieldEntryValue.indexOf("-", fieldEntryValue.indexOf("-") + 1)
                    ).toLong())

                    long.add(
                        fieldEntryValue.substring(
                            fieldEntryValue.indexOf(
                                "-",
                                fieldEntryValue.indexOf("-") + 1
                            ) + 1, fieldEntryValue.length
                        ).toDouble().toLong())
                }
            }

            return long
        }

        fun getFieldTypePosition(fieldEntryKey: String, context: Context): Int {
            var typesArray = context.resources.getStringArray(R.array.units_array)
            for (i in typesArray.indices) {
                if (fieldEntryKey == typesArray[i]) {
                    return i
                }
            }
            return -1
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
        resultsArrayList.forEach {
            arrayList.add(Exercise.stringMapToIntMap(it))
        }
        return arrayList
    }

    fun setArrayListOfResults(arrayList: ArrayList<HashMap<Int, HashMap<String, String>>>) {
        var stringArrayList = ArrayList<HashMap<String, HashMap<String, String>>>()
        arrayList.forEach {
            stringArrayList.add(Exercise.intMapToStringMap(it))
        }
        resultsArrayList = stringArrayList
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

    fun getResultFromDate(date: String): HashMap<Int, HashMap<String, String>> {
        var resultsMap = hashMapOf<Int, HashMap<String, String>>()
        if (containsResult(date)) {
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