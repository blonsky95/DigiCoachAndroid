package com.tatoe.mydigicoach.ui.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import kotlinx.android.synthetic.main.activity_month_viewer.*
import timber.log.Timber

class MonthViewer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_viewer)
        title = "Exercise Result"

        var calendar = calendarView
        var button = goToDayBtn

        button.setOnClickListener {
            Timber.d("OIOI ${calendar.selectedDate}")
            if (calendar.selectedDate!=null) {
                var dayId= Day.intDatetoDayId(calendar.selectedDate!!.day,calendar.selectedDate!!.month,calendar.selectedDate!!.year)
                Timber.d("OIOI 2 $dayId")
            }

        }

    }

}