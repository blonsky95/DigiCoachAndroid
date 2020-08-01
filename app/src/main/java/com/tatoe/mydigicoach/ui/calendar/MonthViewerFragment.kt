package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.DayPackage
import com.tatoe.mydigicoach.network.FirebaseListenerService
import com.tatoe.mydigicoach.ui.fragments.PackageReceivedFragment
import com.tatoe.mydigicoach.viewmodels.*
import kotlinx.android.synthetic.main.fragment_month_viewer.*
import kotlin.collections.ArrayList

class MonthViewerFragment : Fragment() {

    companion object {
        const val DAY_ID_KEY = "day_id"
    }

    private lateinit var dayViewModel: DayViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var calendar: MaterialCalendarView

    var daysWithTraining = arrayListOf<Day>()
    var calendarDaysWithTraining = arrayListOf<CalendarDay>()
    var calendarDaysWithTrainingCompleted = arrayListOf<CalendarDay>()

    var allExercises = listOf<Exercise>()
    var allFriends = listOf<Friend>()


    var calendarDatesToShare = arrayListOf<String>()

    private lateinit var receivedDays: ArrayList<DayPackage>

    private lateinit var mService: FirebaseListenerService

    var mBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dayViewModel = ViewModelProviders.of(this, MyDayViewModelFactory(activity!!.application))
            .get(DayViewModel::class.java)
        mainViewModel =
            ViewModelProviders.of(activity!!, MyMainViewModelFactory(activity!!.application))
                .get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_month_viewer, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = calendarView

        setUpNormalCalendar()

        initObservers()

        share_button.setOnClickListener {
            setUpSelectorUI(true)
        }

        social_button.setOnClickListener {
            mainViewModel.displayPackageReceiverFragmentType.postValue(PackageReceivedFragment.TRANSFER_PACKAGE_DAY)

            //update a value in view model which makes mainactivity display the received packages framgnet
        }
    }

    private fun setUpSelectorUI(toSelectorUI: Boolean) {
        if (toSelectorUI) {
            share_button.visibility = View.GONE

            //reformat calendar
            setUpMultipleSelectionCalendar()

            //make text at the bottom with instructions
            textView4.visibility = View.VISIBLE

            //add button for dialog
            share_btn.visibility = View.VISIBLE
            share_btn.setOnClickListener {
                var string: String = ""
                if (calendarDatesToShare.isNotEmpty()) {
                    var daysToSend = calendarDatesToDays(calendarDatesToShare)
                    if (daysToSend.isNotEmpty()) {
                        mainViewModel.daysToSend.postValue(daysToSend)
                        setUpNormalCalendar()
                        setUpSelectorUI(false)
                    } else {
                        Toast.makeText(activity, "No days selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    string = "No days selected"
                    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
                }

            }
            //add button to cancel and make it back to normal
            cancel_btn.visibility = View.VISIBLE
            cancel_btn.setOnClickListener {
                setUpNormalCalendar()
                setUpSelectorUI(false)
            }
        } else {
            textView4.visibility = View.GONE
            share_btn.visibility = View.GONE
            cancel_btn.visibility = View.GONE
            share_button.visibility = View.VISIBLE
        }
    }

    private fun calendarDatesToDays(calendarDatesToShare: java.util.ArrayList<String>): List<Day> {

        val daysToSend = mutableListOf<Day>()
        for (dayId in calendarDatesToShare) {
            for (day in daysWithTraining) {
                if (day.dayId == dayId) {
                    daysToSend.add(day)
                }
            }
        }
        //if user selects days without training in them, even if day has been initialised, they are
        // not added to list because they are not in the "daysWithTraining" variable
        return daysToSend.toList()
    }

    //reset selection in calendar, when pressing back button from week viewer to month viewer it restores
    //the calendar state with the month day selected (its decorator is displayed)
    override fun onResume() {
        initObservers()
        calendar.clearSelection()
        super.onResume()
    }

    private fun initObservers() {

        mainViewModel.receivedDaysPackages.observe(this, Observer { exePackages ->
            updateSocialButtonNumber(exePackages.size)
        })

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

        dayViewModel.allExercises.observe(this, Observer { exes ->
            allExercises = exes
        })

        dayViewModel.allFriends.observe(this, Observer { friends ->
            allFriends = friends
        })
    }

    private fun updateSocialButtonNumber(number: Int) {
        if (number == 0) {
            textOne.visibility = View.GONE
            return
        }
        if (number > 9) {
            textOne.visibility = View.VISIBLE
            textOne.text = "9+"
        } else {
            textOne.visibility = View.VISIBLE
            textOne.text = number.toString()
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
//        calendar.selectionColor = nu
        if (daysWithTraining.isNotEmpty()) {
            updateDaysWithTrainingDecorator()
            updateDaysWithTrainingWithResultsDecorator()
        }
        updateCurrentDayDecorator()
        calendar.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                var dayId = Day.intDatetoDayId(date.day, date.month, date.year)
                var intent = Intent(activity!!, WeekViewer::class.java)
                intent.putExtra(DAY_ID_KEY, dayId)
                startActivity(intent)
            }
        }
    }


    private fun updateCurrentDayDecorator() {
        calendar.addDecorators(
            CurrentDayDecorator(
                CalendarDay.today(),
                activity!!.getDrawable(R.drawable.rounded_border_background_blue)!!
            )
        )
    }

    private fun updateDaysWithTrainingDecorator() {
        calendar.addDecorators(
            DatesWithDecorator(
                calendarDaysWithTraining,
                activity!!.getDrawable(R.drawable.circle_background_palette3_20dp)!!
            )
        )
    }

    private fun updateDaysWithTrainingWithResultsDecorator() {
        calendar.addDecorators(
            DatesWithDecorator(
                calendarDaysWithTrainingCompleted,
                activity!!.getDrawable(R.drawable.circle_background_palette6_20dp)!!
            )
        )
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
            view.addSpan(ForegroundColorSpan(Color.WHITE))
        }

    }

    class CurrentDayDecorator(currentDay: CalendarDay, var drawable: Drawable) : DayViewDecorator {

        var myDay = currentDay

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == myDay
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(drawable)
            view.addSpan(ForegroundColorSpan(Color.WHITE))
        }
    }


}