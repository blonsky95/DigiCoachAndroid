package com.tatoe.mydigicoach.ui.results

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
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
//                updateUpdatingExercise(position)

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

        activeExercise = DataHolder.activeExerciseHolder
        if (activeExercise!!.exerciseResults.resultsArrayList.isEmpty()) {
            ifEmptyResultsText.visibility = View.VISIBLE
            ResultsRecyclerView.visibility = View.GONE

        } else {
            displayPlottableParameters()
            ifEmptyResultsText.visibility = View.GONE
            ResultsRecyclerView.visibility = View.VISIBLE
            adapter.setContent(activeExercise!!)
        }

//        initObserver()
    }

    private fun displayPlottableParameters() {
        var xxx = activeExercise!!.exerciseResults.getPlottableArrays()
        Timber.d("ARRAYS TO PLOT: $xxx")
        //todo user can change what they are looking in graph, so data series will change,
        //create graph class
        // - one block is the basic generation of graph + line generation
        // - simply changes the line that is added to graph
        // - add spinner to layout with plottable values

        var randomGraphData = xxx[0]

        var anyChartView = graph_chart_view
        var cartesian = AnyChart.line()
        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 20.0)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true)
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        cartesian.title("TITLE GOES HERE")

        cartesian.yAxis(0).title("Y units")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        val seriesData: MutableList<DataEntry> = ArrayList()
        for (i in 0..randomGraphData.sValuesX.size)
        seriesData.add(ValueDataEntry(randomGraphData.sValuesX[i].time, randomGraphData.sValuesy[i]))

        val set = Set.instantiate()
        set.data(seriesData)
        val series1Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }")

        val series1: Line = cartesian.line(series1Mapping)
        series1.name("Param 1")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(com.anychart.enums.Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

//        cartesian.legend().enabled(true)
//        cartesian.legend().fontSize(13.0)
//        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

        anyChartView.setChart(cartesian)
    }

    private class CustomDataEntry internal constructor(
        x: String?,
        value: Number?,
        value2: Number?,
        value3: Number?
    ) :
        ValueDataEntry(x, value) {
        init {
            setValue("value2", value2)
            setValue("value3", value3)
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