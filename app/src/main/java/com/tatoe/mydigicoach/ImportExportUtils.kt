package com.tatoe.mydigicoach

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

    private val digicoachFolder =
        File("${Environment.getExternalStorageDirectory()}/$DIGICOACH_FOLDER_NAME")

//    const val FOLDER_DIR =

    init {
        if (!digicoachFolder.exists()) {
            digicoachFolder.mkdir()
        }
    }

    fun exportExercises(
        allExercises: List<Exercise>,
        selectedIndexes: ArrayList<Int>,
        exportFileName: String
    ) {
        val selectedExercises = arrayListOf<Exercise>()

        if (selectedIndexes.isNotEmpty()) {
            for (index in selectedIndexes) {
                selectedExercises.add(allExercises[index])
//            Gson().toJson(exerciseList)
            }
            Timber.d("Exercises to be exported: $selectedExercises")


            convertExercises(selectedExercises, exportFileName)
        } else {
            Timber.d("no exercises selected")
        }

    }

    private fun convertExercises(selectedExercises: ArrayList<Exercise>, exportFileName: String) {


        var success = true
        if (!digicoachFolder.exists()) {
            success = digicoachFolder.mkdir()
        }
        if (success) {
            val sd = File(digicoachFolder, "$exportFileName.txt")
            //todo check if overwriting
            if (!sd.exists()) {
                success = sd.createNewFile()
            } else {
                Timber.d("Will override file or append")
            }
            if (success) {
                val writer = BufferedWriter(
                    FileWriter(
                        sd,
                        true
                    )
                ) //im using this to be able to jump to new lines
                // directory exists or already created
                var fieldsArray = ArrayList<LinkedHashMap<String, String>>()
                for (exercise in selectedExercises) {
//                   exercise.clearResults()
                    fieldsArray.add(exercise.fieldsHashMap)
                }

//                writer.write(Gson().toJson(selectedExercises))
                writer.write(Gson().toJson(fieldsArray))

                writer.close()


            } else {
                // directory creation is not successful
                Timber.d("NO")
            }
        }
    }

    //gets the field Linked hash map and converts it into an array list of exercises ready to be inserted as new exercises
    fun importExercises(importExercisesFile: File): ArrayList<Exercise> {
        var exercises = arrayListOf<Exercise>()
        var fullTextInFile = importExercisesFile.readText(Charsets.UTF_8)
        var exercisesFieldsHashMap = Gson().fromJson<ArrayList<LinkedHashMap<String, String>>>(
            fullTextInFile,
            object : TypeToken<ArrayList<LinkedHashMap<String, String>>>() {}.type
        )
        for (exerciseFields in exercisesFieldsHashMap) {
            exercises.add(Exercise(exerciseFields))
        }
        Timber.d("IMPORTED EXERCISES: $exercises")
        return exercises
    }

    fun getFilesList(): ArrayList<File> {
        var filesList = arrayListOf<File>()

        if (!digicoachFolder.exists()) {

        }

        return filesList
    }
}