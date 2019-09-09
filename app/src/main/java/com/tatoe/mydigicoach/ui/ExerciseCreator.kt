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

    lateinit var nameEditText: EditText
    lateinit var descEditText: EditText
    lateinit var nameTextView: TextView
    lateinit var descTextView: TextView

    lateinit var rightButton: Button
    lateinit var leftButton: Button
    lateinit var centreButton: Button

    private lateinit var dataViewModel: DataViewModel

    lateinit var updatingExercise: Exercise

    companion object {
        var EXERCISE_ACTION = "exercise_action"
        var EXERCISE_NEW = "exercise_new"
        var EXERCISE_UPDATE = "exercise_update"
        var EXERCISE_VIEW = "exercise_view"
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
            if (action == EXERCISE_UPDATE || action == EXERCISE_VIEW) {
                updatingExercise = DataHolder.activeExerciseHolder
            }
            updateButtonUI(action)
            updateBodyUI(action)
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


    private fun updateButtonUI(actionType: String) {
        if (actionType == EXERCISE_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "ADD"
            rightButton.setOnClickListener(addButtonListener)

            centreButton.visibility = View.INVISIBLE
            leftButton.visibility = View.INVISIBLE
            return
        } else {

            //todo dynamically check blocks and days for calendar view (currently displaying old version, not dynamic to updates)
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
        dataViewModel.insertExercise(newExercise)
        backToViewer()
    }

    private val updateButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        updatingExercise.name = exerciseName
        updatingExercise.description = exerciseDesc
        dataViewModel.updateExercise(updatingExercise)

        backToViewer()
    }

    private val deleteButtonListener = View.OnClickListener {
        dataViewModel.deleteExercise(updatingExercise)

        backToViewer()
    }

    private val viewButtonListener = View.OnClickListener {
        updateButtonUI(EXERCISE_VIEW)
        updateBodyUI(EXERCISE_VIEW)
    }

    private val editButtonListener = View.OnClickListener {
        updateButtonUI(EXERCISE_UPDATE)
        updateBodyUI(EXERCISE_UPDATE)
    }

    private fun backToViewer() {
        val intent = Intent(this, ExerciseViewer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }


}
