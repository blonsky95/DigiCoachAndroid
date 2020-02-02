package com.tatoe.mydigicoach.ui.results

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ResultListAdapter
import kotlinx.android.synthetic.main.activity_results_viewer.*
import timber.log.Timber
import java.sql.Time
import java.util.concurrent.TimeUnit


class ResultsViewer : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel

    private var activeExercise: Exercise? = null
    lateinit var adapter: ResultListAdapter
    private var sResults = arrayListOf<LinkedHashMap<String,String>>()


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

        activeExercise = DataHolder.activeExerciseHolder

        //        if (intent.hasExtra(ResultsCreator.RESULTS_DATE)) {
//             sResults=activeExercise!!.exerciseResults.getResultsPerDate(intent.getStringExtra(ResultsCreator.RESULTS_DATE))
//             enoughInfo = false
//        } else {
           sResults = activeExercise!!.exerciseResults.resultsArrayList
//        }


        val enoughInfo: Boolean = true

        if (sResults.isEmpty()) {
            ifEmptyResultsText.visibility = View.VISIBLE
            ResultsRecyclerView.visibility = View.GONE

        } else {
            displayPlottableParameters(enoughInfo)
            ifEmptyResultsText.visibility = View.GONE
            ResultsRecyclerView.visibility = View.VISIBLE

            adapter.setContent(sResults)
        }

//        initObserver()
    }

    private fun displayPlottableParameters(enoughInfo:Boolean) {

        if (!enoughInfo) {
            return
        }

        var xxx = activeExercise!!.exerciseResults.getPlottableArrays()
        Timber.d("ARRAYS TO PLOT: $xxx")
        //todo user can change what they are looking in graph, so data series will change,
        //create graph class
        // - one block is the basic generation of graph + line generation
        // - simply changes the line that is added to graph
        // - add spinner to layout with plottable values

        var randomGraphData = xxx[0]
        var xAxisDates = randomGraphData.sValuesX
        Timber.d("X AXIS DATES: $xAxisDates")
        var yAxisDouble = randomGraphData.sValuesy

        var values = arrayListOf<Entry>()
        for (i in xAxisDates.size-1 downTo 0) {
            values.add(Entry(xAxisDates[i].time.toFloat(), yAxisDouble[i].toFloat()))
        }

        var chart = chart1

        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
//        chart.setVisibleXRangeMaximum(5f)
//        chart.moveViewToX()
        chart.setTouchEnabled(true)
        chart.setDrawGridBackground(false)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)

        var xAxis = chart.xAxis
        xAxis.position=XAxis.XAxisPosition.TOP
        xAxis.textSize=10f
//        xAxis.setCenterAxisLabels(true)
//        xAxis.granularity = TimeUnit.DAYS.toMillis(1).toFloat()
//        xAxis.isGranularityEnabled=true
        xAxis.setLabelCount(7,true)
        xAxis.axisMinimum=values[0].x-TimeUnit.MINUTES.toMillis(5)
        xAxis.axisMaximum=values[values.size-1].x+TimeUnit.MINUTES.toMillis(5)


        xAxis.valueFormatter = object: ValueFormatter() {
            var mFormat = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
            override fun getFormattedValue(value: Float) :String {
                Timber.d("DATE FORMAT from $value to ${mFormat.format(value)}")
                return mFormat.format(value)
            }
        }

        var yAxis = chart.axisLeft
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.textSize=10f
        yAxis.granularity = 1f
        yAxis.isGranularityEnabled=false

        var lineDataSet = LineDataSet(values, randomGraphData.sName)
        lineDataSet.setDrawIcons(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.setCircleColor(Color.BLUE)
        lineDataSet.color = Color.BLUE
        lineDataSet.lineWidth=3f
        lineDataSet.circleRadius=3f
//        lineDataSet.setDrawCircleHole(true)
//        lineDataSet.valueTextSize=9f
//        lineDataSet.label=""
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = Color.BLUE

        var dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(lineDataSet)

        var lineData = LineData(dataSets)

        chart.data=lineData


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