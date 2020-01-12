package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import java.util.*

class ResultSet (date:Date) {

    //this is the format results are saved in the exercise in the database, ArrayList of result sets which include a date and a result (String)

    var sDate:Date = date
    var sResult:String? = null
    var sPlottableResult:Double?=null

    fun addResult(result:String) {
        sResult= result
    }

    fun addPlottableResult(plottableResult: Double) {
        sPlottableResult=plottableResult
    }

    fun getReadableDate():String {
        return Day.presentableDateFormat.format(sDate)
    }

    //in the future add here other functions or result fieldsHashMap

}