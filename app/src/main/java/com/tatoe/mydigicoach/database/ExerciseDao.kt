package com.tatoe.mydigicoach.database

import android.arch.lifecycle.LiveData
import androidx.room.*
import com.tatoe.mydigicoach.entity.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_table ORDER BY name ASC")
    fun getAll(): androidx.lifecycle.LiveData<List<Exercise>>

    @Query("SELECT * FROM exercise_table WHERE name LIKE :title")
    fun findByName(title: String): Exercise

    @Insert
    fun insertAll(vararg exercise: Exercise)

    @Insert
    fun addExercise(exercise: Exercise)

    @Delete
    fun delete(exercise: Exercise)

    @Update
    fun updateTodo(vararg exercises: Exercise)
}