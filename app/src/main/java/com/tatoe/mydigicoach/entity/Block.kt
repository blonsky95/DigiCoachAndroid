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
    @ColumnInfo(name = "components") @field: SerializedName("components") var components: ArrayList<Exercise>,
    @ColumnInfo(name = "type") @field:SerializedName("type") var type: Int
) {
    constructor(name: String, components: ArrayList<Exercise>, type: Int) : this(
        0,
        name,
        components,
        type
    )

    constructor(block: Block, newType: Int) : this(0, block.name, block.components, newType)

    companion object {
        const val USER_GENERATED = 0
        const val APP_PREMADE = 1
        const val IMPORT = 2
        const val EXPORT = 3

        fun getPremadeBlocks(): List<Block> {

            var exe1array = arrayListOf(
                Exercise(LinkedHashMap(mutableMapOf("Name" to "pull ups", "Description" to "Change grip to use different muscles"))),
                Exercise(LinkedHashMap(mutableMapOf("Name" to "L holds", "Description" to "Hold L position for as long as possible with straight arms"))),
                Exercise(LinkedHashMap(mutableMapOf("Name" to "Pistol squat", "Description" to "One legged squats, better if in elevation so the free leg's hip flexor doesnt seize up"))),
                Exercise(LinkedHashMap(mutableMapOf("Name" to "Muscle ups", "Description" to "Pull yourself above the bar, try and synchronize both arms together")))
            )
            var block1 = Block("Calisthenics", exe1array, APP_PREMADE)

            var exe2array = arrayListOf(
                Exercise(LinkedHashMap(mutableMapOf("Name" to "150s", "Description" to "Try 2x4x150 with 3' and 6' or 3x3x150 with 3' and 6'"))),
                Exercise(LinkedHashMap(mutableMapOf("Name" to "Pyramid", "Description" to "200 - 250 - 300 - 350 - 300 - 250 - 200 R=4-6'"))),
                Exercise(LinkedHashMap(mutableMapOf("Name" to "300s", "Description" to "Classic 4-6 * 300 with 3-6'"))),
                Exercise(LinkedHashMap(mutableMapOf("Name" to "Joni killer", "Description" to "3*300 - 3*200 - 3*100 recu 3' y 6'")))
            )
            var block2 = Block("Lactic Runs", exe2array, APP_PREMADE)

            return listOf(block1, block2)
        }

    }


}