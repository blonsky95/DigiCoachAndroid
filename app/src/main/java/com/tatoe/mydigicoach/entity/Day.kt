package com.tatoe.mydigicoach.entity

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import timber.log.Timber
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

@Entity(tableName = "day_table")
data class Day(
    @PrimaryKey @ColumnInfo @field: SerializedName("id") var dayId: String, //DDMMYYYY
    @ColumnInfo(name = "blocks") @field:SerializedName("blocks") var blocks: ArrayList<Block>,
    @ColumnInfo(name = "exercises") @field:SerializedName("exercises") var exercises: ArrayList<Exercise>
) {
    companion object {
        fun intDatetoDayId(day: Int, month: Int, year: Int): String {
            val format = DecimalFormat("00")
            return "${format.format(day)}${format.format(month)}$year"
        }

        fun dayIDtoDashSeparator(dayId:String) : String {
            return " ${dayId.substring(0,2)}-${dayId.substring(2,4)}-${dayId.substring(4,8)}"
        }

        @SuppressLint("SimpleDateFormat")
        fun dayIDToDate(dayId: String) : Date? {
            return try {
                dayIdFormat.parse(dayId)
            } catch (e : ParseException){
                Timber.d("parse exception: $e")
                null
            }
        }

        fun dateToDayID(date:Date):String{
            return dayIdFormat.format(date)
        }

        fun getTodayDate():Date {
            return Calendar.getInstance().time
        }

        fun getDayDifference(date1:Date, date2:Date) :Int {
            var difference = date1.time - date2.time
            return (difference.toDouble()/ MS_IN_DAY).roundToInt()

        }

        fun isLeapYear(newCalendar: Calendar?): Boolean {
            return newCalendar!!.get(Calendar.YEAR)%4==0
        }


        var dayIdFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        var dashSeparatedDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var presentableDateFormat = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
        var dayOfWeekDateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        var numberAndMonthDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        const val MS_IN_DAY:Long = 86400000


    }
}