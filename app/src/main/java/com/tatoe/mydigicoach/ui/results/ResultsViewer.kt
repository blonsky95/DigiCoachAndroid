package com.tatoe.mydigicoach.ui.results

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.results.ResultsCreator.Companion.RESULTS_EXE_ID
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ResultListAdapter
import kotlinx.android.synthetic.main.activity_results_viewer.*
import timber.log.Timber

class ResultsViewer : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel

    private var activeExercise: Exercise? = null
    lateinit var adapter: ResultListAdapter

    var exerciseId = -1
//    private var intentAction:String? =null
//    private var intentDate:String? = null

    private var allExercises = listOf<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_viewer)
        title = "Exercise Results"

        setSupportActionBar(findViewById(R.id.my_toolbar))

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        val myListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)
                val intent = Intent(this@ResultsViewer, ResultsCreator::class.java)
                intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
                intent.putExtra(ResultsCreator.RESULT_INDEX,position)
                updateUpdatingExercise(position)

                startActivity(intent)

            }
        }

        adapter = ResultListAdapter(this)
        adapter.setOnClickInterface(myListener)
        ResultsRecyclerView.adapter = adapter
        ResultsRecyclerView.layoutManager = LinearLayoutManager(this)


        if (intent.hasExtra(RESULTS_EXE_ID)) {
            exerciseId = intent.getIntExtra(RESULTS_EXE_ID, -1)
            Timber.d("exerciseId received")
        }

        initObserver()

//        if (intent.hasExtra(RESULTS_ACTION)) { //can only reach this with an intent extra
//            intentAction = intent.getStringExtra(RESULTS_ACTION)
//            Timber.d("intent extra action: $intentAction")
//            if (intent.hasExtra(RESULTS_DATE)) {
//                intentDate = intent.getStringExtra(RESULTS_DATE)
//                Timber.d("intent extra date: $intentDate")
//            }
//        }


    }

    private fun initObserver() {
        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                allExercises = it
                for (exercise in allExercises) {
                    if (exercise.exerciseId == exerciseId) {
                        activeExercise = exercise
                        if (activeExercise!!.results.isEmpty()) {
                            ifEmptyResultsText.visibility = View.VISIBLE
                            ResultsRecyclerView.visibility=View.GONE

                        } else {
                            ifEmptyResultsText.visibility = View.GONE
                            ResultsRecyclerView.visibility=View.VISIBLE
                        }
                        adapter.setContent(activeExercise!!)
                        Timber.d("active exercise = $activeExercise")
                        Timber.d("active exercise results ${activeExercise!!.results[0].sResult.toString()}")


                        return@let
                    }
                }

            }
//            updateUI()
        })
    }

    private fun updateUpdatingExercise(position: Int) {

        var clickedExercise = dataViewModel.allExercises.value?.get(position)

        if (clickedExercise != null) {
            DataHolder.activeExerciseHolder = clickedExercise
        } else {
            Timber.d("upsy error")
        }

    }

//    private fun updateUI() {
//        updateButtonUI(intentAction)
//        updateBodyUI(intentAction, intentDate)    }

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

//    private fun updateButtonUI(actionType: String?) {
//
//        if (actionType == RESULTS_VIEW) {
//            right_button.visibility = View.INVISIBLE
//            centre_button.visibility = View.INVISIBLE
//            left_button.visibility = View.INVISIBLE
//            return
//        }
//
//        if (actionType == RESULTS_ADD) {
//            right_button.visibility = View.VISIBLE
//            right_button.text = "ADD"
//            right_button.setOnClickListener(addButtonListener)
//
//            centre_button.visibility = View.INVISIBLE
//            left_button.visibility = View.INVISIBLE
//        }
//
//    }
//
//    private fun updateBodyUI(actionType: String?, date: String?) {
//        if (actionType == RESULTS_VIEW) {
//            TextView1.visibility = View.GONE
//            EditText2.visibility = View.GONE
//
//            if (activeExercise!!.results.isEmpty()) {
//                ifEmptyResultsText.visibility=View.VISIBLE
//                ResultsRecyclerView.visibility=View.GONE
//            } else {
//                ifEmptyResultsText.visibility=View.GONE
//                ResultsRecyclerView.visibility = View.VISIBLE
//                adapter = ResultListAdapter(this)
//                ResultsRecyclerView.adapter = adapter
//                ResultsRecyclerView.layoutManager = LinearLayoutManager(this)
//                Timber.d("update adapter exercise results 7 :$activeExercise ${activeExercise?.results}")
//                if (activeExercise != null) {
//                    adapter.setContent(activeExercise!!)
//                }
//            }
//
//        }
//        if (actionType == RESULTS_ADD) {
//            TextView1.visibility = View.VISIBLE
//            TextView1.text = Day.dayIDtoDashSeparator(date!!)
//            EditText2.visibility = View.VISIBLE
//            ResultsRecyclerView.visibility = View.GONE
//            ifEmptyResultsText.visibility=View.GONE
//
//
//        }
//
//    }

//    private val addButtonListener = View.OnClickListener {
//        var resultDate = TextView1.text.toString()
//        var resultString = SpannableStringBuilder(EditText2.text.trim().toString()).toString()
//
//        activeExercise?.addResult(resultDate, resultString)
//        if (activeExercise != null) {
//            dataViewModel.updateExerciseResult(activeExercise!!)
//        }
//        Timber.d("after adding result exercise 3 :$activeExercise ${activeExercise?.results}")
//        finish() //?
//    }
}