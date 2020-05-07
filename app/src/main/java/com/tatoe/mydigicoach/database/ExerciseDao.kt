package com.tatoe.mydigicoach.database

import androidx.room.*
import com.tatoe.mydigicoach.entity.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_table ORDER BY name ASC")
    fun getAll(): androidx.lifecycle.LiveData<List<Exercise>>

    @Query("DELETE FROM exercise_table")
    suspend fun deleteTable()

//    @Query("SELECT * FROM exercise_table WHERE exerciseId LIKE :exeId")
//    suspend fun findByName(exeId: Int): Exercise

    @Update
    suspend fun update(exercise: Exercise)

//    (onConflict = OnConflictStrategy.REPLACE)
    @Insert
    suspend fun insertAll( exercises: List<Exercise>) : List<Long>

    @Insert
    suspend fun insert(exercise: Exercise) : Long

    @Delete
    suspend fun delete(exercise: Exercise)

    @Update
    fun updateTodo(vararg exercises: Exercise)
}