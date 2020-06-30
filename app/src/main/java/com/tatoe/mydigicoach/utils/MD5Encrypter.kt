package com.tatoe.mydigicoach.utils

import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import timber.log.Timber
import java.lang.StringBuilder
import java.security.MessageDigest
import java.util.*
import kotlin.math.ceil

class MD5Encrypter {

    companion object {

        //generates a "unique" id at creation time for an exercise. This way exercises that come and go are recognisable and arent double imported
        fun getMD5(exercise: Exercise):String {

            val md5 = MessageDigest.getInstance("MD5")
            val composedString = "${DataHolder.userName}-${exercise.name}-${ceil(100*Math.random()).toInt()}"
            md5.update(composedString.toByteArray())
            val md5Result = md5.digest()

            val sb = StringBuilder()
            for (i in md5Result.indices) {
                sb.append(String.format("%02X",md5Result[i]))
            }
            return sb.toString().toLowerCase(Locale.getDefault())
        }
    }

//    private fun testMD5() {
//
//        var exename1 = "Hang Clean"
//        var exename2 = "3k"
//        var exename3 = "300000"
//        var exename4 = "Hang Clean"
//        var exeNames = arrayListOf(exename1,exename2,exename3,exename4)
//
//        for (name in exeNames) {
//            var finalS = "$username-$name-${ceil(100*Math.random()).toInt()}"
////             = finalS.
//
//            var md5Result = md5.digest()
//            var sb = StringBuilder()
//            for (i in md5Result.indices) {
//                sb.append(String.format("%02X",md5Result[i]))
//            }
//            var md5String = sb.toString().toLowerCase()
//            Timber.d("Result for $finalS: $md5String")
//        }
//    }
}