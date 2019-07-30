package com.tatoe.mydigicoach.database

import androidx.room.TypeConverter
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise

class DataConverter {

    @TypeConverter
    fun stringToListExercises(data: String?): List<Exercise>? {
        //todo do this shit
        return null
    }

    @TypeConverter
    fun ListExerciseToString(listExercise: List<Exercise>): String? {
        return null
    }
}