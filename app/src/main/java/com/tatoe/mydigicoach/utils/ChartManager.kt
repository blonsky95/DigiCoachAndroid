package com.tatoe.mydigicoach.utils

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.tatoe.mydigicoach.PlottableBundle
import com.tatoe.mydigicoach.R
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class ChartManager(var context:Context, lineChartView: LineChart, plottableBundle: PlottableBundle) {

    var sPlottableBundle = plottableBundle
    var sChartView = lineChartView
    var values = arrayListOf<Entry>()

    init {
        setupChart()
    }

    private fun setupChart() {
        configureChartFeatures()

        setLineDataSet(sPlottableBundle)
//        sChartView.data=createLineDataSet()
//        configureXAxis()
//        configureYAxis()

    }

    fun setLineDataSet(plottableBundle: PlottableBundle) {

//        sChartView.clear()
//        sChartView.data?.clearValues()
        sChartView.invalidate()
//        Timber.d("CHART MANAGER DATASET 1: ${sChartView.data.dataSets.toString()}")

        sPlottableBundle = plottableBundle
        sChartView.data = createLineDataSet()
        configureXAxis()
        configureYAxis()
//        Timber.d("CHART MANAGER DATASET 2: ${sChartView.data.dataSets.toString()}")

//        Timber.d("CHART MANAGER PLOTTABLE BUNDLE: ${sPlottableBundle.sValuesy.toString()}")
//        sChartView.refreshDrawableState()
    }

    private fun createLineDataSet(): LineData {
        values= arrayListOf()

        var xAxisDates = sPlottableBundle.sValuesX
        var yAxisDouble = sPlottableBundle.sValuesy

        for (i in yAxisDouble.size - 1 downTo 0) {
            //date/times are set to 12am, so adding 12 hours here so training is at middday approximately
            //however, date to time doesnt take in account the GMT so little problemo here
            //however, worst case, you are gmt-12 and it puts you at 12am of the right day, or you are gmt+11 and get 11pm right day
            Timber.d("X AXIS what im adding: ${TimeUnit.HOURS.toMillis(12)}")

            val modifiedResultTimeToMidday=xAxisDates[i].time.toFloat() + TimeUnit.HOURS.toMillis(12)
            Timber.d("X AXIS results value millis: ${modifiedResultTimeToMidday}")

            values.add(Entry(modifiedResultTimeToMidday, yAxisDouble[i]))
        }


        var lineDataSet = LineDataSet(values, sPlottableBundle.sName)
        lineDataSet.setDrawIcons(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.setCircleColor(context.resources.getColor(R.color.darkGreen))
        lineDataSet.color = context.resources.getColor(R.color.darkGreen)
        lineDataSet.lineWidth = 3f
        lineDataSet.circleRadius = 3f
//        lineDataSet.setDrawCircleHole(true)
//        lineDataSet.valueTextSize=9f
//        lineDataSet.label=""
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = context.resources.getColor(R.color.lightGreen)

        var dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(lineDataSet)

        return LineData(dataSets)
    }

    private fun configureChartFeatures() {
        sChartView.setBackgroundColor(Color.WHITE)
        sChartView.description.isEnabled = false
//        chart.setVisibleXRangeMaximum(5f)
//        chart.moveViewToX()
        sChartView.setTouchEnabled(true)
        sChartView.setDrawGridBackground(false)
        sChartView.isDragEnabled = true
        sChartView.setScaleEnabled(true)
        sChartView.setPinchZoom(true)
    }

    private fun configureYAxis() {
        var yAxis = sChartView.axisLeft
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.textSize = 10f
        yAxis.granularity = 0.1f
        yAxis.isGranularityEnabled = true
    }

    private fun configureXAxis() {
        var xAxis = sChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.textSize = 10f
//        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = TimeUnit.DAYS.toMillis(1).toFloat()
        xAxis.isGranularityEnabled=true
//        xAxis.setLabelCount(7, true)
        if (values.isNotEmpty()) {
            xAxis.axisMinimum = values[0].x - TimeUnit.MINUTES.toMillis(5)
            xAxis.axisMaximum = values[values.size - 1].x + TimeUnit.MINUTES.toMillis(5)
        }


        Timber.d("X AXIS granularity value: ${TimeUnit.DAYS.toMillis(1).toFloat()}")
        Timber.d("X AXIS Locale ddefault: ${Locale.getDefault()}")

        xAxis.valueFormatter = object : ValueFormatter() {
            var mFormat = java.text.SimpleDateFormat("dd-MM", java.util.Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
//                Timber.d("DATE FORMAT from $value to ${mFormat.format(value)}")
                Timber.d("X AXIS formatter millis: ${value.toLong()}")
                return mFormat.format(value)
            }
        }
    }

}