package com.tatoe.mydigicoach.database

import androidx.room.*
import com.tatoe.mydigicoach.entity.Day

@Dao
interface DayDao {
    @Query("SELECT * FROM day_table ORDER BY dayId ASC")
    fun getAll(): androidx.lifecycle.LiveData<List<Day>>

    @Query("SELECT * FROM day_table WHERE dayId LIKE :dayId")
    fun findByName(dayId: String): Day

    @Update
    suspend fun update(exercise: Day)

    @Insert
    fun insertAll(vararg exercise: Day)

    @Insert
    suspend fun insert(exercise: Day) : Long

    @Delete
    suspend fun delete(exercise: Day)

    @Update
    fun updateTodo(vararg exercises: Day)
}