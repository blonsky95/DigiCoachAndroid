package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "block_table")
data class Block(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "components") var description: String
) {
    @PrimaryKey(autoGenerate = true)
    var blockId: Int = 0

}