package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(
    tableName = "block_table"
)
data class Block(
    @PrimaryKey(autoGenerate = true)
    @field: SerializedName("id")
    var blockId: Int = 0,
    @ColumnInfo(name = "name") @field: SerializedName("name") var name: String,
    @ColumnInfo(name = "components") @field: SerializedName("components") var components: ArrayList<Exercise> //todo should change components to exercises (requires destroy db)
) {
    constructor(name: String, components: ArrayList<Exercise>) : this(0, name, components)

}