package com.tatoe.mydigicoach.ui.results

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.EXERCISE_EDIT
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.EXERCISE_NEW
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.EXERCISE_VIEW
import kotlinx.android.synthetic.main.activity_results_creator.*

class ResultsCreator : AppCompatActivity() {

    private lateinit var rightButton: Button
    private lateinit var leftButton: Button

    private lateinit var dataViewModel: DataViewModel

    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_creator)
        title = "Exercise Result"

        setSupportActionBar(findViewById(R.id.my_toolbar))

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        rightButton = right_button
        leftButton = left_button

//        if (intent.hasExtra(EXERCISE_ACTION)) { //can only reach this with an intent extra
//            mAction = intent.getStringExtra(EXERCISE_ACTION)
//            if (mAction == EXERCISE_EDIT || mAction == EXERCISE_VIEW) {
////                if (DataHolder.activeExerciseHolder != null) {
//                updatingExercise = DataHolder.activeExerciseHolder
//                if (updatingExercise != null) {
//                    exerciseFieldsMap = updatingExercise!!.getFieldsMap()
//                }
////                }
//            } else {
//                exerciseFieldsMap["Name"] = ""
//                exerciseFieldsMap["Description"] = ""
//            }
//            updateBodyUI(mAction)
//            updateButtonUI(mAction)
//
//        }
    }

    private fun updateButtonUI(actionType: String) {
        if (actionType == EXERCISE_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "ADD"
//            rightButton.setOnClickListener(addButtonListener)

            return
        } else {

            if (actionType == EXERCISE_EDIT) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "UPDATE"
//                rightButton.setOnClickListener(updateButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "DELETE"
//                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == EXERCISE_VIEW) {
                rightButton.visibility = View.INVISIBLE
                leftButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateBodyUI(actionType: String) {

        if (actionType == EXERCISE_VIEW) {
            result_edit_text.visibility = View.GONE
            result_text_view.visibility = View.VISIBLE
        } else {
            result_edit_text.visibility = View.VISIBLE
            result_text_view.visibility = View.GONE
        }
    }

//    private val addButtonListener = View.OnClickListener {
//
//        var newExerciseFields = getFieldContents()
//
//        var newExercise = Exercise(newExerciseFields["Name"]!!, newExerciseFields["Description"]!!)
//        newExercise.fieldsHashMap = newExerciseFields
//
//        dataViewModel.insertExercise(newExercise)
//        backToViewer()
//    }
//    private val updateButtonListener = View.OnClickListener {
//
//        var updatingExerciseFields = getFieldContents()
//
//        updatingExercise!!.name = updatingExerciseFields["Name"]!!
//        updatingExercise!!.description = updatingExerciseFields["Description"]!!
//        updatingExercise!!.fieldsHashMap = updatingExerciseFields
//
//        dataViewModel.updateExercise(updatingExercise!!)
//
//        backToViewer()
//    }
//
//    private val deleteButtonListener = View.OnClickListener {
//        dataViewModel.deleteExercise(updatingExercise!!)
//        backToViewer()
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.exercise_creator_toolbar, menu)
        menuItemEdit = menu?.findItem(R.id.action_edit)
        menuItemRead = menu?.findItem(R.id.action_read)
        when (mAction) {
            EXERCISE_EDIT -> {
                updateToolbarItemVisibility(menuItemEdit, false)
                updateToolbarItemVisibility(menuItemRead, true)
            }
            EXERCISE_VIEW -> {
                updateToolbarItemVisibility(menuItemEdit, true)
                updateToolbarItemVisibility(menuItemRead, false)
            }
            EXERCISE_NEW -> {
                updateToolbarItemVisibility(menuItemEdit, false)
                updateToolbarItemVisibility(menuItemRead, false)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_back -> {
            super.onBackPressed()
            true
        }
        R.id.action_edit -> {
            updateButtonUI(EXERCISE_EDIT)
            updateBodyUI(EXERCISE_EDIT)
            updateToolbarItemVisibility(menuItemEdit, false)
            updateToolbarItemVisibility(menuItemRead, true)
            true
        }
        R.id.action_read -> {
            updateButtonUI(EXERCISE_VIEW)
            updateBodyUI(EXERCISE_VIEW)
            updateToolbarItemVisibility(menuItemEdit, true)
            updateToolbarItemVisibility(menuItemRead, false)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun updateToolbarItemVisibility(menuItem: MenuItem?, isVisible: Boolean) {
        menuItem?.isVisible = isVisible
    }

}