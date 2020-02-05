package com.tatoe.mydigicoach.utils

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
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ChartManager (lineChartView:LineChart, plottableBundle: PlottableBundle) {

    var sPlottableBundle = plottableBundle
    var sChartView= lineChartView
    var values = arrayListOf<Entry>()

    init {
        setupChart()
    }

    private fun setupChart() {

        configureChartFeatures()
        sChartView.data=createLineDataSet()

        configureXAxis()
        configureYAxis()

    }

    private fun createLineDataSet() :LineData{

        var xAxisDates = sPlottableBundle.sValuesX
        var yAxisDouble = sPlottableBundle.sValuesy

        for (i in xAxisDates.size - 1 downTo 0) {
            values.add(Entry(xAxisDates[i].time.toFloat(), yAxisDouble[i].toFloat()))
        }


        var lineDataSet = LineDataSet(values, sPlottableBundle.sName)
        lineDataSet.setDrawIcons(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.setCircleColor(Color.BLUE)
        lineDataSet.color = Color.BLUE
        lineDataSet.lineWidth = 3f
        lineDataSet.circleRadius = 3f
//        lineDataSet.setDrawCircleHole(true)
//        lineDataSet.valueTextSize=9f
//        lineDataSet.label=""
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = Color.BLUE

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
        sChartView.setPinchZoom(true)    }

    private fun configureYAxis() {
        var yAxis = sChartView.axisLeft
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.textSize = 10f
        yAxis.granularity = 1f
        yAxis.isGranularityEnabled = false    }

    private fun configureXAxis() {
        var xAxis = sChartView.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.textSize = 10f
//        xAxis.setCenterAxisLabels(true)
//        xAxis.granularity = TimeUnit.DAYS.toMillis(1).toFloat()
//        xAxis.isGranularityEnabled=true
        xAxis.setLabelCount(7, true)
        xAxis.axisMinimum = values[0].x - TimeUnit.MINUTES.toMillis(5)
        xAxis.axisMaximum = values[values.size - 1].x + TimeUnit.MINUTES.toMillis(5)


        xAxis.valueFormatter = object : ValueFormatter() {
            var mFormat = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                Timber.d("DATE FORMAT from $value to ${mFormat.format(value)}")
                return mFormat.format(value)
            }
        }    }

}