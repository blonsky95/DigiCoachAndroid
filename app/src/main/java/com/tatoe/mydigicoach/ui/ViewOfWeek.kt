package com.tatoe.mydigicoach.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_view_of_week.*
import java.util.*


//todo add on click listeners and add activity to add things?
//todo create day entity
//todo swipe right and left to slide through days
class ViewOfWeek : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_of_week)
        title = "Week View"

        prepareUI()

    }

    private fun prepareUI() {
        val calendar = Calendar.getInstance()
        val dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val monthLongName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        val string = "$dayLongName ${calendar.get(Calendar.DAY_OF_MONTH)} of $monthLongName"
        DayToday.text=string

        if (TrainingToday.text.isEmpty()) {
            TrainingToday.text="Add a block or exercise to view them"
        }
    }
}
