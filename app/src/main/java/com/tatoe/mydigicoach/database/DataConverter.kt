package com.tatoe.mydigicoach.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.ResultSet
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise

class DataConverter {

    @TypeConverter
    fun stringToExerciseList(data: String?): ArrayList<Exercise> {
        return Gson().fromJson(data, object : TypeToken<ArrayList<Exercise>>() {}.type)
    }

    @TypeConverter
    fun exerciseListToString(exerciseList: ArrayList<Exercise>): String? {
        return Gson().toJson(exerciseList)
    }

    @TypeConverter
    fun stringToExerciseResults(data: String?) : ExerciseResults {
        return Gson().fromJson(data, object :TypeToken<ExerciseResults>() {}.type)
    }

    @TypeConverter
    fun exerciseResultsToString(exerciseResults: ExerciseResults) :String? {
        return Gson().toJson(exerciseResults)
    }

    @TypeConverter
    fun stringToHashMap(data: String?) : HashMap<String,HashMap<String,String>> {
        return Gson().fromJson(data, object :TypeToken<HashMap<String,HashMap<String,String>>>() {}.type)
    }

    @TypeConverter
    fun hashMapToString(hashMap: HashMap<String,HashMap<String,String>>) :String? {
        return Gson().toJson(hashMap)
    }

    @TypeConverter
    fun stringToBlockList(data: String?): ArrayList<Block> {
        return Gson().fromJson(data, object : TypeToken<ArrayList<Block>>() {}.type)
    }

    @TypeConverter
    fun blockListToString(blockList: ArrayList<Block>): String? {
        return Gson().toJson(blockList)
    }
}