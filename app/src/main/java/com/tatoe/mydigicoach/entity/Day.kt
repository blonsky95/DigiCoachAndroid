package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "day_table")
data class Day(
    @PrimaryKey @ColumnInfo @field: SerializedName("id") var dayId: String, //number of day of year
    @ColumnInfo(name = "blocks") @field:SerializedName("blocks") var blocks:List<Block>,
    @ColumnInfo(name = "exercises") @field:SerializedName("exercises") var exercises:List<Exercise>
) {

}