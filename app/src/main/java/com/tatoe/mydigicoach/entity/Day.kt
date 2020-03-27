package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

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

        var dashSeparatedDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var presentableDateFormat = SimpleDateFormat("EEE MMM dd yyyy", Locale.getDefault())
        var dayOfWeekDateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        var numberAndMonthDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    }

//    var exerciseOccurencesMap = LinkedHashMap<Exercise,Int?>()

//    fun checkExistingResult(exercise: Exercise) : Boolean {
//        iterateExes(exercises)
//        for (block in blocks) {
//            iterateExes(block.components)
//        }
//
//        return if (exerciseOccurencesMap[exercise]!!<exercise.exerciseResults.numberResultsPerDate(dayIDtoDashSeparator(dayId))) {
//            exerciseOccurencesMap[exercise]=exerciseOccurencesMap[exercise]!!.plus(1)
//            true
//        } else {
//            false
//        }
//    }
//
//    fun iterateExes(exercisesArrayList: ArrayList<Exercise>) {
//        for (exe in exercisesArrayList) {
//            if (!exerciseOccurencesMap.containsKey(exe)) {
//                exerciseOccurencesMap[exe] = 0
//            }
//        }
//    }


//    private fun modifyCounter(exe: Exercise) {
//        if (exerciseOccurencesMap.containsKey(exe)) {
//            exerciseOccurencesMap[exe] = exerciseOccurencesMap[exe]?.plus(1)
//        } else {
//            exerciseOccurencesMap[exe]=0
//        }
//    }

}