package com.tatoe.mydigicoach

import java.util.*
import kotlin.collections.ArrayList

class PlottableBundle(name: String, valuesX:ArrayList<Date>,valuesY:ArrayList<Double>,containsZeros :Boolean =false) {
    var sName = name
    var sValuesX = valuesX
    var sValuesy = valuesY
    var sContainsZeros = containsZeros
}