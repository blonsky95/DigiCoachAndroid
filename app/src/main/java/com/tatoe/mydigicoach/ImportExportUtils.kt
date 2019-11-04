package com.tatoe.mydigicoach

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewer
import timber.log.Timber
import java.io.File

object ImportExportUtils {

    const val FOLDER_NAME = "Digi Coach"
//    const val FOLDER_DIR =

    fun exportExercises(allExercises: List<Exercise>, selectedIndexes: ArrayList<Int>) {
        val selectedExercises = arrayListOf<Exercise>()

        if (selectedIndexes.isNotEmpty()) {
            for (index in selectedIndexes) {
                selectedExercises.add(allExercises[index])
//            Gson().toJson(exerciseList)
            }
            Timber.d("Exercises to be exported: $selectedExercises")


            convertExercises(selectedExercises)
        } else {
            Timber.d("no exercises selected")
        }

    }

    private fun convertExercises(selectedExercises: ArrayList<Exercise>) {
        if (ContextCompat.checkSelfPermission(MainApplication.applicationContext(), Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(,
//                    Manifest.permission.READ_CONTACTS)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(thisActivity,
//                    arrayOf(Manifest.permission.READ_CONTACTS),
//                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }        }
        }
        if (getPrivateAlbumStorageDir(
                MainApplication.applicationContext(),
                FOLDER_NAME
            ) != null && getPrivateAlbumStorageDir(
                MainApplication.applicationContext(),
                FOLDER_NAME
            )!!.exists()
        ) {
            var fileDestination = File(
                " ${getPrivateAlbumStorageDir(
                    MainApplication.applicationContext(),
                    FOLDER_NAME
                ).toString()}/exp_exes.txt"
            )
            if (fileDestination.exists()){
                fileDestination.writeText("hey im a file")
                Timber.d("wrote text to file")

            } else {
                fileDestination.createNewFile()
                Timber.d("File has been created")

            }
        } else {
            File(getPrivateAlbumStorageDir(
                MainApplication.applicationContext(),
                FOLDER_NAME
            )!!.toURI()).mkdirs()
            convertExercises(selectedExercises)
            Timber.d("New directory created")

        }

//        Timber.d("File destination for exporting: ${fileDestination.toString()}")


    }

    private fun folderExists(): Boolean {
        if (getPrivateAlbumStorageDir(
                MainApplication.applicationContext(),
                FOLDER_NAME
            )!!.exists()
        ) {
            return true
        }
        return false
    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun getPrivateAlbumStorageDir(context: Context, dataFolder: String): File? {
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS
            ), dataFolder
        )
        if (!file.mkdirs()) {
            Timber.d("Directory not created")
        }
        return file
    }

    //todo see whats happening with results

    //1. given an exercise array list, all exercises. And an array of Ints, select exercises, take
    //results out and write them to file
}