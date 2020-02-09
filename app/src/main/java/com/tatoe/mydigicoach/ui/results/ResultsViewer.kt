package com.tatoe.mydigicoach.ui.results

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ResultListAdapter
import com.github.mikephil.charting.charts.LineChart
import com.tatoe.mydigicoach.PlottableBundle
import com.tatoe.mydigicoach.utils.ChartManager
import kotlinx.android.synthetic.main.activity_results_viewer.*
import timber.log.Timber


class ResultsViewer : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel

    private var activeExercise: Exercise? = null
    lateinit var adapter: ResultListAdapter
    private var sResults = arrayListOf<LinkedHashMap<String, String>>()
    private lateinit var sChart: LineChart
    private var chartManager: ChartManager?=null
    private var plottableBundles = arrayListOf<PlottableBundle>()
    private lateinit var sSpinner: Spinner

    var exerciseId = -1

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
                intent.putExtra(ResultsCreator.RESULT_INDEX, position)

                startActivity(intent)

            }
        }

        adapter = ResultListAdapter(this)
        adapter.setOnClickInterface(myListener)
        ResultsRecyclerView.adapter = adapter
        ResultsRecyclerView.layoutManager = LinearLayoutManager(this)

        activeExercise = DataHolder.activeExerciseHolder
        sResults = activeExercise!!.exerciseResults.resultsArrayList
        plottableBundles = activeExercise!!.exerciseResults.getPlottableArrays()
        sChart = chart1
        sSpinner = spinner
        configureSpinner()

//        displayPlottableParameter(getPlottableBundleFromName(sSpinner.selectedItem.toString()))

        if (sResults.isEmpty()) {
            ifEmptyResultsText.visibility = View.VISIBLE
            ResultsRecyclerView.visibility = View.GONE
            sSpinner.visibility=View.GONE

        } else {
//            displayPlottableParameter()
            ifEmptyResultsText.visibility = View.GONE
            ResultsRecyclerView.visibility = View.VISIBLE

            adapter.setContent(sResults)
        }

//        initObserver()
    }

        //todo - set initial value to spinner
    //todo - see if onitemselected listener is triggered at the start, in which case remove the call to displaydata in onCreate

    private fun getPlottableBundleFromName(pBundleName: String): PlottableBundle? {
        for (pBundle in plottableBundles) {
            if (pBundle.sName == pBundleName)
                return pBundle
        }
        return null
    }

    private fun configureSpinner() {
        var arrayList = activeExercise!!.exerciseResults.getPlottableNames()

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sSpinner.adapter = arrayAdapter
        sSpinner.onItemSelectedListener = spinnerListener
        sSpinner.setSelection(0)
    }


    private fun displayPlottableParameter(plottableBundle: PlottableBundle?) {
        if (plottableBundle != null) {
            if (chartManager!=null) {
                chartManager!!.setLineDataSet(plottableBundle)
            } else {
                chartManager=ChartManager(chart1,plottableBundle)
            }
        }

         else {
            //display toast
        }


    }

    private var spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Timber.d("SPINNER - ITEM SELECTED: ${sSpinner.selectedItem}")
            displayPlottableParameter(getPlottableBundleFromName(sSpinner.selectedItem.toString()))
        }

    }

//    private fun initObserver() {
//        dataViewModel.allExercises.observe(this, Observer { exercises ->
//            exercises?.let {
//                allExercises = it
//                for (exercise in allExercises) {
//                    if (exercise.exerciseId == exerciseId) {
//                        activeExercise = exercise
//                        if (activeExercise!!.exerciseResults.resultsArrayList.isEmpty()) {
//                            ifEmptyResultsText.visibility = View.VISIBLE
//                            ResultsRecyclerView.visibility=View.GONE
//
//                        } else {
//                            ifEmptyResultsText.visibility = View.GONE
//                            ResultsRecyclerView.visibility=View.VISIBLE
//                        }
//                        adapter.setContent(activeExercise!!)
//                        Timber.d("active exercise = $activeExercise")
////                        Timber.d("active exercise results 1 ${activeExercise!!.results[0].sResult.toString()}")
////                        Timber.d("active exercise results 2 ${activeExercise!!.results[1].sResult.toString()}")
//
//
//
//                        return@let
//                    }
//                }
//
//            }
//        })
//    }

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

}