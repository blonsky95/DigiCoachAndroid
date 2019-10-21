package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import java.util.*

class ResultSet (date:Date) {

    var sDate:Date = date
    var sResult:String? = null

    fun addResult(result:String) {
        sResult= result
    }

    fun getReadableDate():String {
        return Day.presentableDateFormat.format(sDate)
    }

    //in the future add here other functions or result fieldsHashMap

}