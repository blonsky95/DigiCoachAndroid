package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.HomeScreen
import kotlinx.android.synthetic.main.activity_month_viewer.*
import timber.log.Timber

class MonthViewer : AppCompatActivity() {

    companion object {
        const val DAY_ID_KEY = "day_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_viewer)
        title = "Exercise Result"

        var calendar = calendarView
//        var button = goToDayBtn

        calendar.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                var dayId = Day.intDatetoDayId(date.day, date.month, date.year)
                Timber.d("OIOI 2 $dayId")
                var intent = Intent(this, WeekViewer::class.java)
                intent.putExtra(DAY_ID_KEY, dayId)
                startActivity(intent)
            }
        }

        home_button.setOnClickListener {
            startActivity(Intent(this,HomeScreen::class.java))
        }

    }


}