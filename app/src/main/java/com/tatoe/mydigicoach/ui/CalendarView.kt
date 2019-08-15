package com.tatoe.mydigicoach.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_view_of_week.*
import java.util.*



//todo check the calendar provider some time
class CalendarView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_of_week)
        title = "Week View"

        prepareUI()

        //todo NEXT https://developer.android.com/training/animation/screen-slide look at code

    }

    private fun prepareUI() {
        val calendar = Calendar.getInstance()
        val dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val monthLongName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        val string = "$dayLongName ${calendar.get(Calendar.DAY_OF_MONTH)} of $monthLongName"
        DateDisplay.text=string

        if (TrainingToday.text.isEmpty()) {
            TrainingToday.text="Add a block or exercise to view them"
        }
    }
}
