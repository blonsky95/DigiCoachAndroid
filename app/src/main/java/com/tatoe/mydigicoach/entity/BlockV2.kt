package com.tatoe.mydigicoach.entity

class BlockV2(var name: String, var components: ArrayList<Exercise>) {

    var blockPrimaryKeyId = 0

    fun toBlock() : Block {
        val componentsArray = arrayOfNulls<Exercise>(10)
        for ((i, exercise) in components.withIndex()) {
            componentsArray[i]=exercise
        }
        val exercise1:Exercise? = componentsArray[0]
        val exercise2:Exercise? = componentsArray[1]
        val exercise3:Exercise? = componentsArray[2]
        val exercise4:Exercise? = componentsArray[3]
        val exercise5:Exercise? = componentsArray[4]
        val exercise6:Exercise? = componentsArray[5]
        val exercise7:Exercise? = componentsArray[6]
        val exercise8:Exercise? = componentsArray[7]
        val exercise9:Exercise? = componentsArray[8]
        val exercise10:Exercise? = componentsArray[9]
        return Block(name,exercise1,exercise2,exercise3,exercise4,exercise5,exercise6,exercise7,exercise8,exercise9,exercise10)
    }


}
