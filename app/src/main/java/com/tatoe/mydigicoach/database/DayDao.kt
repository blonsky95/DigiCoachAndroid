package com.tatoe.mydigicoach.database

import androidx.room.*
import com.tatoe.mydigicoach.entity.Day

@Dao
interface DayDao {
    @Query("SELECT * FROM day_table ORDER BY dayId ASC")
    fun getAllLiveData(): androidx.lifecycle.LiveData<List<Day>>

    @Query("SELECT * FROM day_table ORDER BY dayId ASC")
    suspend fun getAll(): List<Day>

    @Query("DELETE FROM day_table")
    suspend fun deleteTable()

    @Query("SELECT * FROM day_table WHERE dayId LIKE :dayId")
    fun findByName(dayId: String): androidx.lifecycle.LiveData<Day>

    @Update
    suspend fun update(day: Day)

//    @Insert
////    fun insertAll(vararg exercise: Day)

    @Insert
    suspend fun insertAll( days: List<Day>) : List<Long>

    @Insert
    suspend fun insert(day: Day) : Long

    @Delete
    suspend fun delete(day: Day)

    @Update
    fun updateTodo(vararg days: Day)

//    @Query("SELECT * FROM day_table ORDER BY dayId ASC")
//    suspend fun getDays(): List<Day>
}