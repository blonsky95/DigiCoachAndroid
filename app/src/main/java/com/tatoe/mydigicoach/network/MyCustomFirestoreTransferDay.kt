package com.tatoe.mydigicoach.network

import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise

class MyCustomFirestoreTransferDay (day: Day) {

    //empty constructor needed to deserialize object and have it in Firestore
    constructor() : this(Day("040395", arrayListOf()))


    var mDayId = day.dayId
    var mDayExercises = arrayListOf<MyCustomFirestoreTransferExercise>()

    init {
        for (exe in day.exercises) {
            mDayExercises.add(MyCustomFirestoreTransferExercise(exe))
        }
    }

    fun toDay() :Day {
        var exercises = arrayListOf<Exercise>()
        for (firestoreExe in mDayExercises){
            exercises.add(firestoreExe.toExercise())
        }
        return Day(mDayId,exercises)
    }
}