package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "block_table",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = arrayOf("exerciseId"),
        childColumns = arrayOf("components"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Block(
    @PrimaryKey(autoGenerate = true)
    var blockId: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "components") var components: List<Exercise>
) {
    constructor(name: String,components: List<Exercise>) : this (0, name,components)

}