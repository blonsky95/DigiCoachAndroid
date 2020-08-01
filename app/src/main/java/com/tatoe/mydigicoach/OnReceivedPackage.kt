package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import java.util.ArrayList

interface OnReceivedPackage<T> {
    fun displayReceivedPackages(listOfType:List<T>)
}