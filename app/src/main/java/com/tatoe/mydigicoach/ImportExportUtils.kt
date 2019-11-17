package com.tatoe.mydigicoach

import android.os.Environment
import android.widget.Toast
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

    init {
        if (!digicoachFolder.exists()) {
            digicoachFolder.mkdir()
            Timber.d("OH MA GOOOD Directory has been created")
            //todo seems like files in folder get deleted when uninstalling installing? hmm unsure

        } else {
            Timber.d("Directory exists")
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
            }
            convertExercises(selectedExercises, exportFileName)
        } else {
            Timber.d("selected exercises array is empty, no file was created")
        }

    }

    private fun convertExercises(selectedExercises: ArrayList<Exercise>, exportFileName: String) {

        if (digicoachFolder.exists()) {
            val exportFile = File(digicoachFolder, "$exportFileName.txt")
            //overwrites files with the same name, creates file if non existing
            if (exportFile.exists() || exportFile.createNewFile()) {
                val writer = BufferedWriter(
                    FileWriter(
                        exportFile,
                        false
                    )
                )
                val fieldsArray = ArrayList<LinkedHashMap<String, String>>()
                for (exercise in selectedExercises) {
                    fieldsArray.add(exercise.fieldsHashMap)
                }

                writer.write(Gson().toJson(fieldsArray))
                writer.close()
                Timber.d("File ${exportFile.name} was succesfully created and added")

            } else {
                Timber.d("export file was not created - something went wrong")
            }


        } else {
            Timber.d("Directory doesn't exist (and hasn't been created)")

        }
    }

    //gets the field Linked hash map and converts it into an array list of exercises ready to be inserted as new exercises
    fun importExercises(importExercisesFile: File): ArrayList<Exercise> {
        val exercises = arrayListOf<Exercise>()
        val fullTextInFile = importExercisesFile.readText(Charsets.UTF_8)
        val exercisesFieldsHashMap = Gson().fromJson<ArrayList<LinkedHashMap<String, String>>>(
            fullTextInFile,
            object : TypeToken<ArrayList<LinkedHashMap<String, String>>>() {}.type
        )
        for (exerciseFields in exercisesFieldsHashMap) {
            exercises.add(Exercise(exerciseFields))
        }
        Timber.d("IMPORTED EXERCISES: $exercises")
        return exercises
    }

    fun getFilesList(): List<File>? {
        var filesList = listOf<File>()
        Timber.d("getFilesList called, directory: $digicoachFolder")

        if (!digicoachFolder.exists()) {
            if (digicoachFolder.mkdir()) {
                Timber.d("OH MA GOOOD Directory has been created")

                return getFilesList()
            } else {
                Timber.d("Failed creating directory")
            }
        } else {
            //if no storage write permission - listFiles will return null so filesList will be empty - if empty show in adapter why
            if (digicoachFolder.listFiles()!=null) {
                filesList = digicoachFolder.listFiles().toList()
            } else {
                return null
            }
        }
        Timber.d("files in Digicoach 2: $filesList")

        return filesList
    }

    fun moveToPrivateFolder(fileToMove: File): Boolean {
        //when importing into library, user selects a file they want in Library, which
        //has to be moved to digicoach folder.

        //todo move fileToMove to folder

        return false
    }
}