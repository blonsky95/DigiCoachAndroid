package com.tatoe.mydigicoach.database

import androidx.room.*
import com.tatoe.mydigicoach.entity.Block

@Dao
interface BlockDao {

    @Query("SELECT * FROM block_table ORDER BY name ASC")
    fun getAll(): androidx.lifecycle.LiveData<List<Block>>

    @Query("SELECT * FROM block_table ORDER BY name ASC")
    suspend fun getBlocks(): List<Block>

    @Update
    suspend fun update(block : Block)

    @Insert
    suspend fun addBlock(block: Block) : Long

    @Delete
    suspend fun delete(block: Block)
}