package com.tatoe.mydigicoach

import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

object ImportExportUtils {

//    const val DIGICOACH_FOLDER_NAME = "DigiCoach"
//
//    private val digicoachFolder =
//        File("${Environment.getExternalStorageDirectory()}/$DIGICOACH_FOLDER_NAME")
//
//    init {
//        if (!digicoachFolder.exists()) {
//            digicoachFolder.mkdir()
//            Timber.d("OH MA GOOOD Directory has been created")
//
//        } else {
//            Timber.d("Directory exists")
//        }
//    }

    fun makeExportBlock(
        allExercises: List<Exercise>, selectedIndexes: ArrayList<Int>, exportBlockName: String
    ): Block {

        val selectedExercises = arrayListOf<Exercise>()
        val exportBlock = Block(exportBlockName, arrayListOf(), Block.EXPORT)
        if (selectedIndexes.isNotEmpty()) {
            for (index in selectedIndexes) {
                allExercises[index].exerciseId =
                    0 //when imported + inserted, exercises will be assigned a new id
                allExercises[index].exerciseResults.resultsArrayList= arrayListOf()
                selectedExercises.add(allExercises[index])
            }
            exportBlock.components = selectedExercises


        } else {
            Timber.d("selected exercises array is empty, no block was created")
        }
        return exportBlock

    }

    //gets the field Linked hash map and converts it into an array list of exercises ready to be inserted as new exercises
//    fun importExercises(importExercisesFile: File): ArrayList<Exercise> {
//        val exercises = arrayListOf<Exercise>()
//        val fullTextInFile = importExercisesFile.readText(Charsets.UTF_8)
//        val exercisesFieldsHashMap = Gson().fromJson<ArrayList<LinkedHashMap<String, String>>>(
//            fullTextInFile,
//            object : TypeToken<ArrayList<LinkedHashMap<String, String>>>() {}.type
//        )
//        for (exerciseFields in exercisesFieldsHashMap) {
//            exercises.add(Exercise(exerciseFields))
//        }
//        Timber.d("IMPORTED EXERCISES: $exercises")
//        return exercises
//    }

//    fun getFilesList(): List<File>? {
//        var filesList = listOf<File>()
//        Timber.d("getFilesList called, directory: $digicoachFolder")
//
//        if (!digicoachFolder.exists()) {
//            if (digicoachFolder.mkdir()) {
//                Timber.d("OH MA GOOOD Directory has been created")
//
//                return getFilesList()
//            } else {
//                Timber.d("Failed creating directory")
//            }
//        } else {
//            //if no storage write permission - listFiles will return null so filesList will be empty - if empty show in adapter why
//            if (digicoachFolder.listFiles() != null) {
//                filesList = digicoachFolder.listFiles().toList()
//            } else {
//                return null
//            }
//        }
//        Timber.d("files in Digicoach 2: $filesList")
//
//        return filesList
//    }

}