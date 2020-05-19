package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.HomeScreen
import com.tatoe.mydigicoach.viewmodels.DayViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewModelFactory
import kotlinx.android.synthetic.main.activity_month_viewer.*
import kotlin.collections.ArrayList

class MonthViewer : AppCompatActivity() {

    companion object {
        const val DAY_ID_KEY = "day_id"
    }

    private lateinit var dayViewModel: DayViewModel

    private lateinit var calendar: MaterialCalendarView

    var daysWithTraining = arrayListOf<Day>()
    var calendarDaysWithTraining = arrayListOf<CalendarDay>()
    var calendarDaysWithTrainingCompleted = arrayListOf<CalendarDay>()

    var calendarDatesToShare = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_viewer)
        title = "Exercise Result"

        dayViewModel = ViewModelProviders.of(this, MyDayViewModelFactory(application))
            .get(DayViewModel::class.java)

        calendar = calendarView

        setUpNormalCalendar()

        initObservers()

        home_button.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }

        share_button.setOnClickListener {

            share_button.visibility = View.GONE

            //reformat calendar
            setUpMultipleSelectionCalendar()

            //make text at the bottom with instructions
            textView4.visibility = View.VISIBLE

            //add button for dialog
            share_btn.visibility = View.VISIBLE
            share_btn.setOnClickListener(sendToUserListener)

            //add button to cancel and make it back to normal
            cancel_btn.visibility = View.VISIBLE
            cancel_btn.setOnClickListener {
                setUpNormalCalendar()
                textView4.visibility = View.GONE
                share_btn.visibility = View.GONE
                cancel_btn.visibility = View.GONE
                share_button.visibility = View.VISIBLE
            }

        }

    }

    private fun setUpMultipleSelectionCalendar() {
        calendar.removeDecorators()
        calendar.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
        updateDaysWithTrainingDecorator()
        calendar.setOnDateChangedListener { _, date, selected ->
            val dayId = Day.intDatetoDayId(date.day, date.month, date.year)
            if (selected) {
                calendarDatesToShare.add(dayId)
            } else {
                calendarDatesToShare.remove(dayId)
            }
            val string =
                "Tap dates you want to share, empty days can't be shared\nTotal: ${calendarDatesToShare.size}"
            textView4.text = string
        }
    }

    private fun setUpNormalCalendar() {
        calendar.removeDecorators()
        calendar.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
        if (daysWithTraining.isNotEmpty()) {
            updateDaysWithTrainingDecorator()
            updateDaysWithTrainingWithResultsDecorator()
        }
        updateCurrentDayDecorator()
        calendar.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                var dayId = Day.intDatetoDayId(date.day, date.month, date.year)
                var intent = Intent(this, WeekViewer::class.java)
                intent.putExtra(DAY_ID_KEY, dayId)
                startActivity(intent)
            }
        }
    }

    private fun updateCurrentDayDecorator() {
        calendar.addDecorators(
            CurrentDayDecorator(
                CalendarDay.today(),
                this.getDrawable(R.drawable.rounded_border_background_blue)!!
            )
        )
    }

    private fun updateDaysWithTrainingDecorator() {
        calendar.addDecorators(
            DatesWithDecorator(
                calendarDaysWithTraining,
                this.getDrawable(R.drawable.rounded_border_background_light)!!
            )
        )
    }

    private fun updateDaysWithTrainingWithResultsDecorator() {
        calendar.addDecorators(
            DatesWithDecorator(
                calendarDaysWithTrainingCompleted,
                this.getDrawable(R.drawable.rounded_border_background)!!
            )
        )
    }


    private fun initObservers() {
        dayViewModel.allDays.observe(this, androidx.lifecycle.Observer { days ->
            days?.let {
                daysWithTraining = getDaysWithTraining(days)
                daysToCalendarDays(daysWithTraining)
                updateDaysWithTrainingDecorator()
                updateDaysWithTrainingWithResultsDecorator()
                //put it here so it doesnt get overlayered by the other decorators
                updateCurrentDayDecorator()

            }
        })
    }

    private val sendToUserListener = View.OnClickListener {
        Utils.getDialogViewWithEditText(this, "Send to User", null, "Username",
            object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(username: String) {
                    dayViewModel.sendDaysToUser(calendarDatesToShare,daysWithTraining, username)
                }

            })
    }

    private fun getDaysWithTraining(days: List<Day>): ArrayList<Day> {
        var dasd = arrayListOf<Day>()
        for (day in days) {
            if (day.exercises.isNotEmpty()) {
                dasd.add(day)
            }
        }
        return dasd
    }

    private fun daysToCalendarDays(daysList: java.util.ArrayList<Day>) {
        calendarDaysWithTraining = arrayListOf()
        calendarDaysWithTrainingCompleted = arrayListOf()
        daysList.forEach { day ->
            if (day.hasExercises()) {
                calendarDaysWithTraining.add(Day.dayToCalendarDay(day))
                if (day.allExercisesHaveResult()) {
                    calendarDaysWithTrainingCompleted.add(Day.dayToCalendarDay(day))
                    return@forEach
                }
            }
        }
    }

    class DatesWithDecorator(var dates: ArrayList<CalendarDay>, var drawable: Drawable) :
        DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(drawable)
        }

    }

    class CurrentDayDecorator(currentDay: CalendarDay, var drawable: Drawable) : DayViewDecorator {

        var myDay = currentDay

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == myDay
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(drawable)
        }
    }

}