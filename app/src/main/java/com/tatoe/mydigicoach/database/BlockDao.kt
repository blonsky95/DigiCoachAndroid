package com.tatoe.mydigicoach.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.tatoe.mydigicoach.entity.Block

@Dao
interface BlockDao {

    @Update
    suspend fun update(block : Block)

    @Insert
    suspend fun addBlock(block: Block) : Long

    @Delete
    suspend fun delete(block: Block)
}