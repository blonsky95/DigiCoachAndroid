package com.tatoe.mydigicoach.ui.util

import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.BlockV2
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

object DataHolder {

    lateinit var activeExerciseHolder: Exercise
    lateinit var newExerciseHolder: Exercise

    lateinit var activeBlockHolder: BlockV2
    lateinit var newBlockHolder: BlockV2

}