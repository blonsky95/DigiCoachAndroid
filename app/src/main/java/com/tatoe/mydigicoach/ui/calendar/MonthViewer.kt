package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.HomeScreen
import com.tatoe.mydigicoach.ui.util.CurrentDayDecorator
import com.tatoe.mydigicoach.ui.util.DatesWithTrainingDecorator
import com.tatoe.mydigicoach.viewmodels.DayViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewModelFactory
import kotlinx.android.synthetic.main.activity_month_viewer.*
import timber.log.Timber
import kotlin.collections.ArrayList

class MonthViewer : AppCompatActivity() {

    companion object {
        const val DAY_ID_KEY = "day_id"
    }
    private lateinit var dataViewModel: DayViewModel

    private lateinit var calendar:MaterialCalendarView
    var calendarDaysWithTraining = arrayListOf<CalendarDay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_viewer)
        title = "Exercise Result"

        dataViewModel = ViewModelProviders.of(this, MyDayViewModelFactory(application))
            .get(DayViewModel::class.java)

        calendar = calendarView

        calendar.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                var dayId = Day.intDatetoDayId(date.day, date.month, date.year)
                Timber.d("OIOI 2 $dayId")
                var intent = Intent(this, WeekViewer::class.java)
                intent.putExtra(DAY_ID_KEY, dayId)
                startActivity(intent)
            }
        }

        initObservers()

        home_button.setOnClickListener {
            startActivity(Intent(this,HomeScreen::class.java))
            finish()
        }

    }

    private fun initObservers() {
        dataViewModel.allDays.observe(this, androidx.lifecycle.Observer { days ->
            days?.let {
                var daysList = getDaysWithTraining(days)
                daysToCalendarDays(daysList)
                calendar.addDecorators(DatesWithTrainingDecorator(this, calendarDaysWithTraining))
                calendar.addDecorators(CurrentDayDecorator(this, CalendarDay.today()))
            }
        })
    }

    private fun getDaysWithTraining(days: List<Day>): ArrayList<Day> {
        var dasd = arrayListOf<Day>()
        for (day in days) {
            if (day.exercises.isNotEmpty()){
                dasd.add(day)
            }
        }
        return dasd
    }

    private fun daysToCalendarDays(daysList: java.util.ArrayList<Day>) {
        calendarDaysWithTraining= arrayListOf()
        daysList.forEach { day ->
            calendarDaysWithTraining.add(Day.dayToCalendarDay(day))
        }
    }
}