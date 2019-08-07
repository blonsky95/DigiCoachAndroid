package com.tatoe.mydigicoach.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise

class DataConverter {

    @TypeConverter
    fun stringToExercise(data: String?): Exercise? {
        return Gson().fromJson(data, object : TypeToken<Exercise>() {}.type)
    }

    @TypeConverter
    fun exerciseToString(exercise: Exercise?): String? {
        return Gson().toJson(exercise)
    }
}