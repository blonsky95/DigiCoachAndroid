package com.tatoe.mydigicoach.database

import androidx.room.*
import com.tatoe.mydigicoach.entity.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_table ORDER BY name ASC")
    fun getAll(): androidx.lifecycle.LiveData<List<Exercise>>

//    @Query("SELECT * FROM exercise_table WHERE exerciseId LIKE :exeId")
//    suspend fun findByName(exeId: Int): Exercise

    @Update
    suspend fun update(exercise: Exercise)

    @Insert
    fun insertAll(vararg exercise: Exercise)

    @Insert
    suspend fun insert(exercise: Exercise) : Long

    @Delete
    suspend fun delete(exercise: Exercise)

    @Update
    fun updateTodo(vararg exercises: Exercise)
}