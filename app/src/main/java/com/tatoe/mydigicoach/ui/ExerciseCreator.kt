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
import com.tatoe.mydigicoach.ui.util.ExerciseHolder
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import timber.log.Timber

class ExerciseCreator : AppCompatActivity() {

    lateinit var exerciseName: String
    lateinit var exerciseDesc: String
    var exerciseId: Int? = null

//    lateinit var exerciseOldName: String


    lateinit var nameEditText: EditText
    lateinit var descEditText: EditText
    lateinit var saveExerciseButton: Button
    lateinit var deleteButton: Button

    private lateinit var dataViewModel: DataViewModel

    lateinit var updatingExercise: Exercise

    private var BUTTON_ADD = "ADD"
    private var BUTTON_UPDATE = "UPDATE"

    companion object {
        var EXERCISE_ACTION = "exercise_new"
        var EXERCISE_NEW = "exercise_new"
        var EXERCISE_UPDATE = "exercise_action"

        var EXERCISE_NAME_KEY = "exercise_name"
        var EXERCISE_DESCRIPTION_KEY = "exercise_description"
        //        var EXERCISE_OLD_NAME_KEY = "exercise_old_name"
        var EXERCISE_ID_KEY = "exercise_id"

        var EXERCISE_FAIL_RESULT_CODE = 0
        var EXERCISE_NEW_RESULT_CODE = 1
        var EXERCISE_UPDATE_RESULT_CODE = 2
        var EXERCISE_DELETE_RESULT_CODE = 3

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_creator)
        title = "Exercise Lab"

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        nameEditText = EditText1
        descEditText = EditText2
        saveExerciseButton = addExerciseButton
        deleteButton = delete_button

        if (intent.hasExtra(EXERCISE_ACTION)) {
            var action = intent.getStringExtra(EXERCISE_ACTION)

            if (action == EXERCISE_UPDATE) {
                exerciseId = intent.getIntExtra(EXERCISE_ID_KEY, -1)
            }

            when (action) {
                EXERCISE_NEW -> modifyUI(BUTTON_ADD)
                EXERCISE_UPDATE -> modifyUI(BUTTON_UPDATE)
            }
        }

//        var view = exercise_lab_layout as View


    }


    private fun modifyUI(buttonText: String) {

        var nameTextField = ""
        var descTextField = ""

        saveExerciseButton.text = buttonText
        if (buttonText == BUTTON_ADD) {
            deleteButton.visibility = View.GONE
            saveExerciseButton.setOnClickListener(addButtonListener)
        } else {
            saveExerciseButton.setOnClickListener(updateButtonListener)
            deleteButton.setOnClickListener(deleteButtonListener)
            updatingExercise = ExerciseHolder.activeExerciseHolder
            nameTextField = updatingExercise.name
            descTextField = updatingExercise.description
        }

        nameEditText.text = SpannableStringBuilder(nameTextField)
        descEditText.text = SpannableStringBuilder(descTextField)
    }

    private val addButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        var newExercise = Exercise(exerciseName, exerciseDesc)
        ExerciseHolder.newExerciseHolder = newExercise

        var replyIntent = Intent()

        Timber.d("add currentExercise - built: ${newExercise.name} ${newExercise.description}")
//

        if (exerciseName.isEmpty()) {
            setResult(EXERCISE_FAIL_RESULT_CODE, replyIntent)
        } else {
            setResult(EXERCISE_NEW_RESULT_CODE, replyIntent)
        }
        finish()
    }

    private val updateButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        updatingExercise.name = exerciseName
        updatingExercise.description = exerciseDesc
        ExerciseHolder.activeExerciseHolder = updatingExercise
        var replyIntent = Intent()

        Timber.d("update currentExercise - built: ${updatingExercise.exerciseId} ${updatingExercise.name} ${updatingExercise.description} ")


        if (exerciseName.isEmpty()) {
            setResult(EXERCISE_FAIL_RESULT_CODE, replyIntent)
        } else {
            setResult(EXERCISE_UPDATE_RESULT_CODE, replyIntent)
        }
        finish()
    }

    private val deleteButtonListener = View.OnClickListener {

        ExerciseHolder.activeExerciseHolder = updatingExercise
        var replyIntent = Intent()

        Timber.d("delete currentExercise - built: ${updatingExercise.exerciseId} ${updatingExercise.name} ${updatingExercise.description} ")

        setResult(EXERCISE_DELETE_RESULT_CODE, replyIntent)

        finish()
    }


}
