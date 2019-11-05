package com.tatoe.mydigicoach

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

object ImportExportUtils {

    const val DIGICOACH_FOLDER_NAME = "DigiCoach"
//    const val FOLDER_DIR =

    fun exportExercises(allExercises: List<Exercise>, selectedIndexes: ArrayList<Int>, exportFileName:String) {
        val selectedExercises = arrayListOf<Exercise>()

        if (selectedIndexes.isNotEmpty()) {
            for (index in selectedIndexes) {
                selectedExercises.add(allExercises[index])
//            Gson().toJson(exerciseList)
            }
            Timber.d("Exercises to be exported: $selectedExercises")


            convertExercises(selectedExercises,exportFileName)
        } else {
            Timber.d("no exercises selected")
        }

    }

    private fun convertExercises(selectedExercises: ArrayList<Exercise>,exportFileName:String) {

        val sd_main = File("${Environment.getExternalStorageDirectory()}/$DIGICOACH_FOLDER_NAME")
        var success = true
        if (!sd_main.exists()) {
            success = sd_main.mkdir()
        }
        if (success) {
            val sd = File(sd_main,"$exportFileName.txt")
            //todo check if overwriting
            if (!sd.exists()) {
                success = sd.createNewFile()
            } else {
                Timber.d("Will override file or append")
            }
            if (success) {
                val writer = BufferedWriter(FileWriter(sd,true)) //im using this to be able to jump to new lines
                // directory exists or already created
                for (exercise in selectedExercises){
                    exercise.clearResults()
                }
                writer.write(Gson().toJson(selectedExercises))

                writer.close()


            } else {
                // directory creation is not successful
                Timber.d("NO")
            }
        }
    }

    fun importExercises (importExercisesFile:File) : ArrayList<Exercise>{
        var fullTextInFile = importExercisesFile.readText(Charsets.UTF_8)
        var exercises = Gson().fromJson<ArrayList<Exercise>>(fullTextInFile, object : TypeToken<ArrayList<Exercise>>() {}.type)
        Timber.d("IMPORTED EXERCISES: $exercises")
        return exercises
    }
}