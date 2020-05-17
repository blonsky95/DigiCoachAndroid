package com.tatoe.mydigicoach.network

import com.tatoe.mydigicoach.entity.Exercise

class MyCustomStoreExercise (exercise: Exercise, category:String) {
    var mExercise=exercise
    var mCategory =category
    var isOwned=false
}