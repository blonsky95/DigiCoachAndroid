package com.tatoe.mydigicoach.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise

class DataConverter {

    @TypeConverter
    fun stringToListExercises(data: String?): List<Exercise>? {
        return Gson().fromJson(data, object : TypeToken<ArrayList<Exercise>>() {}.type)
    }

    @TypeConverter
    fun ListExerciseToString(listExercise: List<Exercise>): String? {
        return Gson().toJson(listExercise)
    }
}