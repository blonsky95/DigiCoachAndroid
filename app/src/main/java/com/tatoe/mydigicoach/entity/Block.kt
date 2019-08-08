package com.tatoe.mydigicoach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(
    tableName = "block_table",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = arrayOf("exerciseId"),
        childColumns = arrayOf("exercise1"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)


//todo IMPORTANT FIX THE FOREIGN KEY SHIT
//todo implement the activity for result so pressing back is an intent and updates block viewer
//todo add update and delete - do its magic
//todo calendar!!!!!

data class Block(
    @PrimaryKey(autoGenerate = true)
    @field: SerializedName("id")
    var blockId: Int = 0,
    @ColumnInfo(name = "name") @field: SerializedName("name") var name: String,
    @ColumnInfo(name = "exercise1") @field: SerializedName("components") var exercise1: Exercise?,
    @ColumnInfo(name = "exercise2") @field: SerializedName("components") var exercise2: Exercise?,
    @ColumnInfo(name = "exercise3") @field: SerializedName("components") var exercise3: Exercise?,
    @ColumnInfo(name = "exercise4") @field: SerializedName("components") var exercise4: Exercise?,
    @ColumnInfo(name = "exercise5") @field: SerializedName("components") var exercise5: Exercise?,
    @ColumnInfo(name = "exercise6") @field: SerializedName("components") var exercise6: Exercise?,
    @ColumnInfo(name = "exercise7") @field: SerializedName("components") var exercise7: Exercise?,
    @ColumnInfo(name = "exercise8") @field: SerializedName("components") var exercise8: Exercise?,
    @ColumnInfo(name = "exercise9") @field: SerializedName("components") var exercise9: Exercise?,
    @ColumnInfo(name = "exercise10") @field: SerializedName("components") var exercise10: Exercise?
) {
    constructor(
        name: String,
        exercise1: Exercise?,
        exercise2: Exercise?,
        exercise3: Exercise?,
        exercise4: Exercise?,
        exercise5: Exercise?,
        exercise6: Exercise?,
        exercise7: Exercise?,
        exercise8: Exercise?,
        exercise9: Exercise?,
        exercise10: Exercise?
    )
            : this(
        0,
        name,
        exercise1,
        exercise2,
        exercise3,
        exercise4,
        exercise5,
        exercise6,
        exercise7,
        exercise8,
        exercise9,
        exercise10
    )

//    companion object {
//        fun createBlock(block: BlockV2): Block {
//
//        }
//    }

}