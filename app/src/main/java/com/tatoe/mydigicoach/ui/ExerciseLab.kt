package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import kotlinx.android.synthetic.main.activity_exercise_lab.*
import timber.log.Timber

class ExerciseLab : AppCompatActivity() {

    lateinit var exerciseName: String
    lateinit var exerciseDesc: String
    lateinit var exerciseOldName: String


    lateinit var nameEditText: EditText
    lateinit var descEditText: EditText
    lateinit var saveExerciseButton: Button
    private lateinit var dataViewModel: DataViewModel

    private var BUTTON_ADD = "ADD"
    private var BUTTON_UPDATE = "UPDATE"

    companion object {
        var EXERCISE_ACTION = "exercise_new"
        var EXERCISE_NEW = "exercise_new"
        var EXERCISE_UPDATE = "exercise_action"

        var EXERCISE_NAME_KEY = "exercise_name"
        var EXERCISE_DESCRIPTION_KEY = "exercise_description"
        var EXERCISE_OLD_NAME_KEY = "exercise_old_name"


        var EXERCISE_FAIL_RESULT_CODE = 0
        var EXERCISE_NEW_RESULT_CODE = 1
        var EXERCISE_UPDATE_RESULT_CODE = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_lab)
        title = "Exercise Lab"

        nameEditText = EditText1
        descEditText = EditText2
        saveExerciseButton = addExerciseButton

        if (intent.hasExtra(EXERCISE_ACTION)) {
            var action = intent.getStringExtra(EXERCISE_ACTION)
            when (action) {
                EXERCISE_NEW -> modifyUI(BUTTON_ADD)
                EXERCISE_UPDATE -> modifyUI(
                    BUTTON_UPDATE,
                    intent.getStringExtra(EXERCISE_NAME_KEY),
                    intent.getStringExtra(EXERCISE_DESCRIPTION_KEY)
                )
            }
        }

        var view = exercise_lab_layout as View

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)


//        saveExerciseButton.setOnClickListener {
//            exerciseName = nameEditText.text.trim().toString()
//            exerciseDesc = descEditText.text.trim().toString()
//
//            var newExercise = Exercise(exerciseName, exerciseDesc)
//            var replyIntent = Intent()
//
//            Timber.d("New currentExercise - built: ${newExercise.name} ${newExercise.description}")
//
//            replyIntent.putExtra(EXERCISE_NAME_KEY, exerciseName)
//            replyIntent.putExtra(EXERCISE_DESCRIPTION_KEY, exerciseDesc)
//            setResult(EXERCISE_NEW_RESULT_CODE, replyIntent)
//
//            if (exerciseName.isEmpty()) {
//                setResult(EXERCISE_FAIL_RESULT_CODE, replyIntent)
//            }
//            finish()
//        }
    }


    private fun modifyUI(buttonText: String, nameText: String = "", descText: String = "") {
        nameEditText.text = SpannableStringBuilder(nameText)
        descEditText.text = SpannableStringBuilder(descText)
        saveExerciseButton.text = buttonText
        if (buttonText==BUTTON_ADD) {
            saveExerciseButton.setOnClickListener(addButtonListener)
        } else {
            saveExerciseButton.setOnClickListener(updateButtonListener)
        }
    }

    private val addButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        var newExercise = Exercise(exerciseName, exerciseDesc)
        var replyIntent = Intent()

        Timber.d("add currentExercise - built: ${newExercise.name} ${newExercise.description}")

        replyIntent.putExtra(EXERCISE_NAME_KEY, exerciseName)
        replyIntent.putExtra(EXERCISE_DESCRIPTION_KEY, exerciseDesc)
        setResult(EXERCISE_NEW_RESULT_CODE, replyIntent)

        if (exerciseName.isEmpty()) {
            setResult(EXERCISE_FAIL_RESULT_CODE, replyIntent)
        }
        finish()
    }

    private val updateButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        var newExercise = Exercise(exerciseName, exerciseDesc)
        var replyIntent = Intent()

        Timber.d("update currentExercise - built: ${newExercise.name} ${newExercise.description} old name was $exerciseOldName")

        replyIntent.putExtra(EXERCISE_NAME_KEY, exerciseName)
        replyIntent.putExtra(EXERCISE_OLD_NAME_KEY, exerciseOldName)
        replyIntent.putExtra(EXERCISE_DESCRIPTION_KEY, exerciseDesc)
        setResult(EXERCISE_NEW_RESULT_CODE, replyIntent)

        if (exerciseName.isEmpty()) {
            setResult(EXERCISE_FAIL_RESULT_CODE, replyIntent)
        }
        finish()
    }


}
