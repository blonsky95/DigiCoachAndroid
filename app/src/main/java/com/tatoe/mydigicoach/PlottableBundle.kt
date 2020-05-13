package com.tatoe.mydigicoach

import java.util.*
import kotlin.collections.ArrayList

class PlottableBundle(
    name: String,
    valuesX: ArrayList<Date>,
    valuesY: ArrayList<Float>,
    containsZeros: Boolean = false
) {
    var sName = name
    var sValuesX = valuesX
    var sValuesy = valuesY
    var sContainsZeros = containsZeros


    fun datesToFloats(listDates: ArrayList<Date>): ArrayList<Float> {
        var floatArrayList = arrayListOf<Float>()
        for (date in listDates) {
            floatArrayList.add(date.time.toFloat())
        }
        return floatArrayList
    }
}