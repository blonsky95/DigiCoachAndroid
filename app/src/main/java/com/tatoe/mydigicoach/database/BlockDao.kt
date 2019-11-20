package com.tatoe.mydigicoach.database

import androidx.room.*
import com.tatoe.mydigicoach.entity.Block

@Dao
interface BlockDao {

    @Query("SELECT * FROM block_table WHERE type=0 ORDER BY name ASC")
    fun getUserMadeLive(): androidx.lifecycle.LiveData<List<Block>>

    @Query("SELECT * FROM block_table WHERE type=0 ORDER BY name ASC")
    suspend fun getUserMadeBlocks(): List<Block>

    @Query("SELECT * FROM block_table WHERE type=1 ORDER BY name ASC")
    fun getPremadeBlocksLive(): androidx.lifecycle.LiveData<List<Block>>

    @Query("SELECT * FROM block_table WHERE type=2 ORDER BY name ASC")
    fun getImportBlocksLive(): androidx.lifecycle.LiveData<List<Block>>

    @Query("SELECT * FROM block_table WHERE type=3 ORDER BY name ASC")
    fun getExportBlocksLive(): androidx.lifecycle.LiveData<List<Block>>

    @Update
    suspend fun update(block: Block)

    @Insert
    suspend fun addBlock(block: Block): Long

    //no need for suspend as I know this is being called from a IO thread in AppRoomDatabase
    @Insert
    fun addInitialBlock(block: Block): Long

    @Delete
    suspend fun delete(block: Block)
}