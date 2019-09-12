package com.tatoe.mydigicoach.ui.exercise

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_exercise_results.*
import timber.log.Timber

class ExerciseResults : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel

    lateinit var updatingExercise: Exercise

    companion object {
        var RESULTS_ACTION = "results_action"
        var RESULTS_VIEW = "results_view"
        var RESULTS_ADD = "results_add"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_results)
        title = "Exercise Results"

        setSupportActionBar(findViewById(R.id.my_toolbar))

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)


        if (intent.hasExtra(RESULTS_ACTION)) { //can only reach this with an intent extra
            val action = intent.getStringExtra(RESULTS_ACTION)
            updatingExercise = DataHolder.activeExerciseHolder
            title = updatingExercise.name
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
        if (actionType == RESULTS_VIEW) {
            right_button.visibility = View.INVISIBLE
            centre_button.visibility = View.INVISIBLE
            left_button.visibility = View.INVISIBLE
            return
        }

        if (actionType == RESULTS_ADD) {
            right_button.visibility = View.VISIBLE
            right_button.text = "ADD"
            right_button.setOnClickListener(addButtonListener)

            centre_button.visibility = View.INVISIBLE
            left_button.visibility = View.INVISIBLE
//            centreButton.visibility = View.VISIBLE
//            centreButton.text = "VIEW"
//            centreButton.setOnClickListener(viewButtonListener)
//
//            leftButton.visibility = View.VISIBLE
//            leftButton.text = "DELETE"
//            leftButton.setOnClickListener(deleteButtonListener)
        }

    }

    private fun updateBodyUI(actionType: String) {
        if (actionType == RESULTS_VIEW) {
            TextView1.visibility = View.GONE
            EditText2.visibility = View.GONE

            //add adapters from dayviewer here, well their visibulity, add them in xml
        }
        if (actionType == RESULTS_ADD) {
            TextView1.visibility = View.VISIBLE
            EditText2.visibility = View.VISIBLE

            //make adapters invisible
            //todo continue here - probably have to edit xml a bit, use textview1 as layout reference to edittext2

        }

    }

    private val addButtonListener = View.OnClickListener {
        var resultDate = TextView1.text.toString()
        var resultString = SpannableStringBuilder(EditText2.text.trim().toString()).toString()

        updatingExercise.addResult(resultDate,resultString)
        Timber.d("map of results: ${updatingExercise.results}")
        finish() //?
    }
}