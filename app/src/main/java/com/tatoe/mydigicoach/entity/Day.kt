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

    //todo move this big chunky companion object to a utils file

    companion object {
        fun intDatetoDayId(day: Int, month: Int, year: Int): String {
            val format = DecimalFormat("00")
            return "${format.format(day)}${format.format(month)}$year"
        }

        fun dayIDtoDashSeparator(dayId: String): String {
            return "${dayId.substring(0, 2)}-${dayId.substring(2, 4)}-${dayId.substring(4, 8)}"
        }

        @SuppressLint("SimpleDateFormat")
        fun dayIDToDate(dayId: String): Date? {
            return try {
                dayIdFormat.parse(dayId)
            } catch (e: ParseException) {
                Timber.d("parse exception: $e")
                null
            }
        }

        fun dateToDayID(date: Date): String {
            return dayIdFormat.format(date)
        }

        fun getTodayDate(): Date {
            return Calendar.getInstance().time
        }

        fun getDayDifference(date1: Date, date2: Date): Int {
            var difference = date1.time - date2.time
            return (difference.toDouble() / MS_IN_DAY).roundToInt()

        }

        fun isLeapYear(newCalendar: Calendar?): Boolean {
            return newCalendar!!.get(Calendar.YEAR) % 4 == 0
        }

//        const val MAX_DAYS = 7
        const val DEFAULT_POS = 1

        fun dayIdToPosition(dayId: String): Int {
            //make day id date and find day difference between day id and now and then use default pos
            val dayDiff = Day.getDayDifference(dayIDToDate(dayId)!!, Day.getTodayDate())
            return DEFAULT_POS + dayDiff
        }

        //calendar of week
        //position you want dayid from, so if position is 0, returns calendar day id
        fun calendarAndPositionToDayId(calendar: Calendar, position: Int): String {
            val myCalendar = getDifferentCalendar(calendar, position)
            return dateToDayID(myCalendar.time)
        }

        private fun getDifferentCalendar(calendar: Calendar, position: Int): Calendar {

            var dayOfWeek0To6Is = getDayOfWeek0to6(calendar)
            //this is like getting current calendar and adding/subtracting millis to set the new calendar

            //THIS HAS TO BE LONG TYPE ELSE IT RUNS OUT OF INT RANGE
            calendar.timeInMillis += ((position - dayOfWeek0To6Is) * MS_IN_DAY)

            return calendar
        }

        fun getDayOfWeek0to6(nonCurrentCalendar: Calendar): Int {
            var mDayOfWeek = nonCurrentCalendar.get(Calendar.DAY_OF_WEEK)

            //checks leap year - gergorian calendars don't do leap years and thats default calendar (?)
            if (isLeapYear(nonCurrentCalendar)) {
                if (mDayOfWeek == 1) {
                    mDayOfWeek = 7
                } else {
                    mDayOfWeek--
                }
            }
            //day of week varies 1-7 but pager positions are 0-6 so subtract 1:
            return mDayOfWeek -1
        }


        var dayIdFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        var dashSeparatedDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var presentableDateFormat = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
        var dayOfWeekDateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        var numberAndMonthDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        var dayFragmentFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        const val MS_IN_DAY: Long = 86400000
        const val MONDAY=0
        const val TUESDAY=1
        const val WEDNESDAY=2
        const val THURSDAY=3
        const val FRIDAY=4
        const val SATURDAY=5
        const val SUNDAY=6


    }
}