package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import kotlinx.android.synthetic.main.activity_month_viewer.*
import timber.log.Timber

class MonthViewer : AppCompatActivity() {

    companion object {
        const val DAY_ID_KEY="day_id"
    }

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
                var intent = Intent(this, WeekViewer::class.java)
                intent.putExtra(DAY_ID_KEY, dayId)
                startActivity(intent)
            } else {
                Toast.makeText(this,"Select a date first",Toast.LENGTH_SHORT).show()
            }

        }

    }

}