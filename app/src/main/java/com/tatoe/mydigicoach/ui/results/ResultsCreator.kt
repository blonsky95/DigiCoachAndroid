package com.tatoe.mydigicoach.ui.results

import android.content.Intent
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
    private var resultIndex = -1

    companion object {
        var RESULTS_DATE = "results_date"
        var RESULTS_EXE_ID = "results_exe_id"
        var RESULT_INDEX = "result_index"
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

            if (intent.hasExtra(RESULT_INDEX)) {
                resultIndex = intent.getIntExtra(RESULT_INDEX, -1)
            }

            activeExercise = DataHolder.activeExerciseHolder
            Timber.d("ACTIVE EXERCISE IN CREATOR ${activeExercise!!}")

            result_title_text_view.text = activeExercise!!.name
            Timber.d("ACTION RECEIVED AT RESULTS CREATOR: $mAction $resultDate result index: $resultIndex")

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
                rightButton.setOnClickListener(updateButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "DELETE"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == OBJECT_VIEW) {
                rightButton.visibility = View.INVISIBLE
                leftButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateBodyUI(actionType: String) {

        if (actionType == OBJECT_VIEW) {

//            Timber.d("active exercise results 3 ${activeExercise!!.results[0].sResult.toString()}")
//            Timber.d("active exercise results 4 ${activeExercise!!.results[1].sResult.toString()}")
            result_edit_text.visibility = View.GONE
            result_text_view.visibility = View.VISIBLE
            result_text_view.text = activeExercise!!.exerciseResults.resultsArrayList[resultIndex].sResult
            return
        }
        //todo generate the layout like in ExerciseCreator
        if (actionType == OBJECT_NEW) {
            result_edit_text.visibility = View.VISIBLE
            result_edit_text.hint = "How did it go?"
            result_text_view.visibility = View.GONE
        } else { //must be edit
            result_edit_text.visibility = View.VISIBLE
            result_edit_text.text =
                SpannableStringBuilder(activeExercise!!.exerciseResults.resultsArrayList[resultIndex].sResult)
            result_text_view.visibility = View.GONE
        }
    }

    private val addButtonListener = View.OnClickListener {

        //        var resultDate = TextView1.text.toString()
        var resultString = SpannableStringBuilder(result_edit_text.text.trim()).toString()

        activeExercise?.exerciseResults!!.addResult(Day.dayIDtoDashSeparator(resultDate), resultString)
        //todo add plotabble fields if any
        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
        }
        Timber.d("after adding result exercise 3 :$activeExercise ${activeExercise?.exerciseResults!!.resultsArrayList.size}")
        finish() //?
    }

    private val updateButtonListener = View.OnClickListener {
        activeExercise!!.exerciseResults.resultsArrayList[resultIndex].sResult =
            SpannableStringBuilder(result_edit_text.text.trim()).toString()
        dataViewModel.updateExerciseResult(activeExercise!!)
        DataHolder.activeExerciseHolder = activeExercise

        backToViewer()
    }

    private val deleteButtonListener = View.OnClickListener {
        activeExercise!!.exerciseResults.resultsArrayList.removeAt(resultIndex)
        dataViewModel.updateExerciseResult(activeExercise!!)
        DataHolder.activeExerciseHolder = activeExercise

        backToViewer()
    }

    private fun backToViewer() {
        val intent = Intent(this, ResultsViewer::class.java)
        intent.putExtra(RESULTS_EXE_ID, activeExercise!!.exerciseId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

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