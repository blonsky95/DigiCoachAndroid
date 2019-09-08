package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import timber.log.Timber

class ExerciseCreator : AppCompatActivity() {

    lateinit var exerciseName: String
    lateinit var exerciseDesc: String
    var exerciseId: Int? = null

//    lateinit var exerciseOldName: String


    lateinit var nameEditText: EditText
    lateinit var descEditText: EditText
    lateinit var nameTextView: TextView
    lateinit var descTextView: TextView

    lateinit var rightButton: Button
    lateinit var leftButton: Button
    lateinit var centreButton: Button

    private lateinit var dataViewModel: DataViewModel

    lateinit var updatingExercise: Exercise

    private var BUTTON_ADD = "ADD"
    private var BUTTON_UPDATE = "UPDATE"

    companion object {
        var EXERCISE_ACTION = "exercise_action"
        var EXERCISE_NEW = "exercise_new"
        var EXERCISE_UPDATE = "exercise_update"
        var EXERCISE_VIEW = "exercise_view"


        var EXERCISE_ID_KEY = "exercise_id"

        var EXERCISE_FAIL_RESULT_CODE = 0
        var EXERCISE_NEW_RESULT_CODE = 1
        var EXERCISE_UPDATE_RESULT_CODE = 2
        var EXERCISE_DELETE_RESULT_CODE = 3

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_creator)
        title = "Exercise Creator"

        setSupportActionBar(findViewById(R.id.my_toolbar))

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        nameEditText = EditText1
        descEditText = EditText2
        nameTextView = TextView1
        descTextView = TextView2

        rightButton = right_button
        leftButton = left_button
        centreButton = centre_button

        if (intent.hasExtra(EXERCISE_ACTION)) { //can only reach this with an intent extra
            var action = intent.getStringExtra(EXERCISE_ACTION)
            updateButtonUI(action)
            updateBodyUI(action)

//            when (action) {
//                EXERCISE_NEW -> {modifyUI(BUTTON_ADD)
//                EXERCISE_UPDATE -> modifyUI(BUTTON_UPDATE)
//                EXERCISE_VIEW -> readModeOn()
//            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.creator_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_back -> {
            super.onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


//    private fun modifyUI(buttonText: String) {
//
//        var nameTextField = ""
//        var descTextField = ""
//
//        rightButton.text = buttonText
//        if (buttonText == BUTTON_ADD) {
//            leftButton.visibility = View.GONE
//            rightButton.setOnClickListener(addButtonListener)
//        } else {
//            rightButton.setOnClickListener(updateButtonListener)
//            leftButton.setOnClickListener(deleteButtonListener)
//            updatingExercise = DataHolder.activeExerciseHolder
//            nameTextField = updatingExercise.name
//            descTextField = updatingExercise.description
//        }
//
//        nameEditText.text = SpannableStringBuilder(nameTextField)
//        descEditText.text = SpannableStringBuilder(descTextField)
//    }

//    private fun readModeOn() {
//        nameEditText.visibility=View.GONE
//        descEditText.visibility=View.GONE
//        rightButton.visibility=View.INVISIBLE
//        leftButton.visibility=View.INVISIBLE
//        TextView1.visibility=View.VISIBLE
//        TextView2.visibility=View.VISIBLE
//
//        TextView1.text=DataHolder.activeExerciseHolder.name
//        TextView2.text=DataHolder.activeExerciseHolder.description
//        Timber.d("name ${DataHolder.activeExerciseHolder.name} and desc ${DataHolder.activeExerciseHolder.description}")
//    }

    private fun updateButtonUI(actionType: String) {
        if (actionType == EXERCISE_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "ADD"
            rightButton.setOnClickListener(addButtonListener)

            centreButton.visibility = View.INVISIBLE
            leftButton.visibility = View.INVISIBLE
            return
        } else {
            updatingExercise=DataHolder.activeExerciseHolder //todo put this somewhere
            //todo PRIORITY removes for results in update, creator will comm with dataviewmodel
            //todo next thing is edit text squish it so text doesnt go out of borders
            //todo dyanmically check blocks and days for calendar view (currently displaying old version, not dynamic to updates)
            if (actionType == EXERCISE_UPDATE) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "UPDATE"
                rightButton.setOnClickListener(updateButtonListener)

                centreButton.visibility = View.VISIBLE
                centreButton.text = "VIEW"
                centreButton.setOnClickListener(viewButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "DELETE"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == EXERCISE_VIEW) {
                rightButton.visibility = View.INVISIBLE

                centreButton.visibility = View.VISIBLE
                centreButton.text = "EDIT"
                centreButton.setOnClickListener(editButtonListener)

                leftButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateBodyUI(actionType: String) {
        if (actionType == EXERCISE_NEW) {
            nameTextView.visibility = View.GONE
            descTextView.visibility = View.GONE

            nameEditText.visibility = View.VISIBLE
            descEditText.visibility = View.VISIBLE
        }
        if (actionType == EXERCISE_UPDATE) {
            nameTextView.visibility = View.GONE
            descTextView.visibility = View.GONE

            nameEditText.visibility = View.VISIBLE
            nameEditText.text = SpannableStringBuilder(DataHolder.activeExerciseHolder.name)
            descEditText.visibility = View.VISIBLE
            descEditText.text = SpannableStringBuilder(DataHolder.activeExerciseHolder.description)

        }
        if (actionType == EXERCISE_VIEW) {
            nameEditText.visibility = View.GONE
            descEditText.visibility = View.GONE

            nameTextView.visibility = View.VISIBLE
            nameTextView.text = SpannableStringBuilder(DataHolder.activeExerciseHolder.name)
            descTextView.visibility = View.VISIBLE
            descTextView.text = SpannableStringBuilder(DataHolder.activeExerciseHolder.description)
        }
    }

    private val addButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        var newExercise = Exercise(exerciseName, exerciseDesc)
        DataHolder.newExerciseHolder = newExercise

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
        DataHolder.activeExerciseHolder = updatingExercise
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

        DataHolder.activeExerciseHolder = updatingExercise
        var replyIntent = Intent()

        Timber.d("delete currentExercise - built: ${updatingExercise.exerciseId} ${updatingExercise.name} ${updatingExercise.description} ")

        setResult(EXERCISE_DELETE_RESULT_CODE, replyIntent)

        finish()
    }

    private val viewButtonListener = View.OnClickListener {
        updateButtonUI(EXERCISE_VIEW)
        updateBodyUI(EXERCISE_VIEW)
    }

    private val editButtonListener = View.OnClickListener {
        updateButtonUI(EXERCISE_UPDATE)
        updateBodyUI(EXERCISE_UPDATE)
    }


}
