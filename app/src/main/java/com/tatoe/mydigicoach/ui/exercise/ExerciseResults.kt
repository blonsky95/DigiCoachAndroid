package com.tatoe.mydigicoach.ui.exercise

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ResultListAdapter
import kotlinx.android.synthetic.main.activity_exercise_results.*
import timber.log.Timber

class ExerciseResults : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel

    lateinit var activeExercise: Exercise
    lateinit var adapter: ResultListAdapter

    companion object {
        var RESULTS_ACTION = "results_action"
        var RESULTS_DATE = "results_date"

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
            Timber.d("intent extra action: $action")
            var date: String? = null
            if (intent.hasExtra(RESULTS_DATE)) {
                date = intent.getStringExtra(RESULTS_DATE)

            }
//            title = updatingExercise.name
            activeExercise = DataHolder.activeExerciseHolder
            Timber.d("exercise results open intent 2/6 : ${activeExercise} ${activeExercise.results}")


            updateButtonUI(action)
            updateBodyUI(action, date)
            //todo send intent from click in day viewer - add a button that takes you here with date as extra! then start testing

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
        }

    }

    private fun updateBodyUI(actionType: String, date: String?) {
        if (actionType == RESULTS_VIEW) {
            TextView1.visibility = View.GONE
            EditText2.visibility = View.GONE

            ResultsRecyclerView.visibility = View.VISIBLE
            adapter = ResultListAdapter(this)
            ResultsRecyclerView.adapter = adapter
            ResultsRecyclerView.layoutManager = LinearLayoutManager(this)
            Timber.d("update adapter exercise results 7 :$activeExercise ${activeExercise.results}")
            adapter.setContent(activeExercise)
        }
        if (actionType == RESULTS_ADD) {
            TextView1.visibility = View.VISIBLE
            TextView1.text = date
            EditText2.visibility = View.VISIBLE
            ResultsRecyclerView.visibility = View.GONE

        }

    }

    private val addButtonListener = View.OnClickListener {
        var resultDate = TextView1.text.toString()
        var resultString = SpannableStringBuilder(EditText2.text.trim().toString()).toString()

        activeExercise.addResult(resultDate, resultString)
        dataViewModel.updateExercise(activeExercise)
        Timber.d("after adding result exercise 3 :$activeExercise ${activeExercise.results}")
        finish() //?
    }
}