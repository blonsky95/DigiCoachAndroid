package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import kotlinx.android.synthetic.main.activity_exercise_lab.*
import timber.log.Timber

class ExerciseLab : AppCompatActivity() {

    lateinit var exerciseName:String
    lateinit var exerciseDesc:String
    private lateinit var dataViewModel:DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_lab)
        title = "Exercise Lab"

        Timber.d("How did i get to exercise lab")


        var view = exercise_lab_layout as View

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)


        addExerciseButton.setOnClickListener {
            exerciseName=EditText1.text.trim().toString()
            exerciseDesc=editText2.text.trim().toString()

            var newExercise = Exercise(exerciseName,exerciseDesc)

            Timber.d("New exercise - built: ${newExercise.name} ${newExercise.description}")
            dataViewModel.insert(newExercise)
            val mySnackbar = Snackbar.make(view, "adding new exercise", Snackbar.LENGTH_LONG)
            //todo update snackbar when you get a succesfull exercise added
            //todo SOON fix - notify adapter to update when pressing back button (doesnt go through on create) look android room with a view project
            mySnackbar.show()


        }
    }
}
