package com.tatoe.mydigicoach.ui.results

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_ACTION
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_EDIT
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_NEW
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_VIEW
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_results_creator.*
import timber.log.Timber

class ResultsCreator : AppCompatActivity() {

    private lateinit var rightButton: Button
    private lateinit var leftButton: Button

    private lateinit var dataViewModel: DataViewModel

    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var activeExercise: Exercise? = null
     private var resultDate = "unknown date"

    companion object {
        var RESULTS_DATE = "results_date"
        var RESULTS_EXE_ID = "results_exe_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_creator)
        title = "Exercise Result"

        setSupportActionBar(findViewById(R.id.my_toolbar))

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        rightButton = right_button
        leftButton = left_button

        if (intent.hasExtra(OBJECT_ACTION)) { //can only reach this with an intent extra
            mAction = intent.getStringExtra(OBJECT_ACTION)

            if (intent.hasExtra(RESULTS_DATE)) {
                resultDate = intent.getStringExtra(RESULTS_DATE)
            }

            activeExercise = DataHolder.activeExerciseHolder
            result_title_text_view.text=activeExercise!!.name
            Timber.d("ACTION RECEIVED AT RESULTS CREATOR: $mAction $resultDate")

            updateBodyUI(mAction)
            updateButtonUI(mAction)

        }
    }

    private fun updateButtonUI(actionType: String) {
        if (actionType == OBJECT_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "ADD"
            rightButton.setOnClickListener(addButtonListener)

            return
        } else {

            if (actionType == OBJECT_EDIT) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "UPDATE"
//                rightButton.setOnClickListener(updateButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "DELETE"
//                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == OBJECT_VIEW) {
                rightButton.visibility = View.INVISIBLE
                leftButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateBodyUI(actionType: String) {

        if (actionType == OBJECT_VIEW) {
            result_edit_text.visibility = View.GONE
            result_text_view.visibility = View.VISIBLE
        } else {
            result_edit_text.visibility = View.VISIBLE
            result_text_view.visibility = View.GONE
        }
    }

    private val addButtonListener = View.OnClickListener {

//        var resultDate = TextView1.text.toString()
        var resultString = SpannableStringBuilder(result_edit_text.text.trim().toString()).toString()

        activeExercise?.addResult(Day.dayIDtoDashSeparator(resultDate), resultString)
        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
        }
        Timber.d("after adding result exercise 3 :$activeExercise ${activeExercise?.results!!.size}")
        finish() //?
    }

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
            OBJECT_EDIT -> {
                updateToolbarItemVisibility(menuItemEdit, false)
                updateToolbarItemVisibility(menuItemRead, true)
            }
            OBJECT_VIEW -> {
                updateToolbarItemVisibility(menuItemEdit, true)
                updateToolbarItemVisibility(menuItemRead, false)
            }
            OBJECT_NEW -> {
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
            updateButtonUI(OBJECT_EDIT)
            updateBodyUI(OBJECT_EDIT)
            updateToolbarItemVisibility(menuItemEdit, false)
            updateToolbarItemVisibility(menuItemRead, true)
            true
        }
        R.id.action_read -> {
            updateButtonUI(OBJECT_VIEW)
            updateBodyUI(OBJECT_VIEW)
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