package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.viewmodels.DayViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewModelFactory
import kotlinx.android.synthetic.main.activity_week_viewer.*
import timber.log.Timber
import java.util.*


class WeekViewer : AppCompatActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    private lateinit var dayViewModel: DayViewModel
    private var weekDaysViewHashMap = hashMapOf<Int,TextView>()

    private var activeDay: Day? = null
    private var activeDayId: String? = null
    private var activePosition = 0
    private var allDays: List<Day> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_viewer)

        setSupportActionBar(findViewById(R.id.my_toolbar))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        backBtn.setOnClickListener {
            super.onBackPressed()
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        initListeners()
        weekDaysViewHashMap[Day.MONDAY]=monday_btn
        weekDaysViewHashMap[Day.TUESDAY]=tuesday_btn
        weekDaysViewHashMap[Day.WEDNESDAY]=wednesday_btn
        weekDaysViewHashMap[Day.THURSDAY]=thursday_btn
        weekDaysViewHashMap[Day.FRIDAY]=friday_btn
        weekDaysViewHashMap[Day.SATURDAY]=saturday_btn
        weekDaysViewHashMap[Day.SUNDAY]=sunday_btn

        weekDaysViewHashMap[Day.MONDAY]?.setOnClickListener(MyOnClickListenerWithIntParameter(Day.MONDAY))
        weekDaysViewHashMap[Day.TUESDAY]?.setOnClickListener(MyOnClickListenerWithIntParameter(Day.TUESDAY))
        weekDaysViewHashMap[Day.WEDNESDAY]?.setOnClickListener(MyOnClickListenerWithIntParameter(Day.WEDNESDAY))
        weekDaysViewHashMap[Day.THURSDAY]?.setOnClickListener(MyOnClickListenerWithIntParameter(Day.THURSDAY))
        weekDaysViewHashMap[Day.FRIDAY]?.setOnClickListener(MyOnClickListenerWithIntParameter(Day.FRIDAY))
        weekDaysViewHashMap[Day.SATURDAY]?.setOnClickListener(MyOnClickListenerWithIntParameter(Day.SATURDAY))
        weekDaysViewHashMap[Day.SUNDAY]?.setOnClickListener(MyOnClickListenerWithIntParameter(Day.SUNDAY))


        mPager = findViewById(R.id.pager)

        dayViewModel = ViewModelProviders.of(this, MyDayViewModelFactory(application))
            .get(DayViewModel::class.java)

        addTrainingViewContainer.setOnClickListener(updateDayTrainingListener)

        initObservers()

        dayViewModel.changeActiveDay(intent.getStringExtra(MonthViewerFragment.DAY_ID_KEY))

    }

    private fun initObservers() {
        dayViewModel.activeDayIdStr.observe(this, androidx.lifecycle.Observer { dayId ->
            activeDayId = dayId
            var nonCurrentCalendar = getDayIDCalendar(dayId)
            activePosition = changeDayPositionInPager(nonCurrentCalendar)
            changeSelectedDay(activePosition)
            mPager.currentItem = activePosition
        })

        dayViewModel.allDays.observe(this, androidx.lifecycle.Observer { days ->
            days?.let {
                allDays = days
            }
            //for now this will do, the observer is being triggered after the Adapter creation so it searches in an empty allDays List
            //this way Adapter is created when observer is triggered
            pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
            mPager.adapter = pagerAdapter
            if (DataHolder.pagerPosition >= 0) {
                mPager.setCurrentItem(DataHolder.pagerPosition, false)
                DataHolder.pagerPosition = -1
            } else {
                mPager.setCurrentItem(Day.getDayOfWeek0to6(getDayIDCalendar(activeDayId)), false)
            }
        })
    }

    fun updateBlankResult(exercise: Exercise){
        dayViewModel.updateExercise(exercise)
    }

    private fun getDayIDCalendar(dayId: String?): Calendar {
        var nonCurrentCalendar = Calendar.getInstance()
        nonCurrentCalendar.time = Day.dayIDToDate(dayId!!)
        return nonCurrentCalendar
    }

    private fun changeDayPositionInPager(nonCurrentCalendar: Calendar): Int {
        //to get the right slider pager position (between 0 and 6) have to check if leap year,
        // which will give us a value in range 1-7 and then subtract 1 so it's 0-6
        return Day.getDayOfWeek0to6(nonCurrentCalendar)
    }

    private fun changeSelectedDay(activeDayOfWeek: Int) {
        for (entry in weekDaysViewHashMap) {
            if (entry.key==activeDayOfWeek) {
                entry.value.setBackgroundColor(resources.getColor(R.color.palette3))
                entry.value.setTextColor(resources.getColor(R.color.white))
            } else {
                entry.value.setBackgroundColor(resources.getColor(R.color.palette8))
                entry.value.setTextColor(resources.getColor(R.color.palette9))
            }
        }
    }

    override fun onDestroy() {
        DataHolder.pagerPosition = -1
        super.onDestroy()
    }

    private val updateDayTrainingListener = View.OnClickListener {

        val nonCurrentCalendar = getDayIDCalendar(activeDayId)
        activeDayId=Day.calendarAndPositionToDayId(nonCurrentCalendar,activePosition)

        activeDay = getDayByDayId(activeDayId)
        DataHolder.activeDayHolder = activeDay

        val intent = Intent(this, DayCreator::class.java)
        intent.putExtra(DayCreator.DAY_ID, activeDayId)
        DataHolder.pagerPosition = mPager.currentItem
        startActivity(intent)
    }

    private inner class MyOnClickListenerWithIntParameter(var dayOfWeekInt: Int) :
        View.OnClickListener {

        override fun onClick(v: View?) {
            val activeDayIdCalendar = Calendar.getInstance()
            activeDayIdCalendar.time=Day.dayIDToDate(activeDayId!!)
            val activeDayIdDayWeek=Day.getDayOfWeek0to6(activeDayIdCalendar)

            activeDayIdCalendar.timeInMillis+=((dayOfWeekInt-activeDayIdDayWeek)*Day.MS_IN_DAY)

            dayViewModel.changeActiveDay(Day.dateToDayID(activeDayIdCalendar.time))
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val DAYS_WEEK = 7
        var alreadyInitialised = -1

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            //this says which position is currently active, use it to know which activedayid
            super.setPrimaryItem(container, position, `object`)
            if (alreadyInitialised!=position) {  //this function is called 2 or 3 times per swipe so avoid reupdating the dayid variable unnecesarily and creating conflicts
                //this should be done with live data, but setprimary item is quite slow and has an initial trigger at position 0
                if (position!=activePosition){
                    activePosition=position
                    changeSelectedDay(activePosition)
                }

                alreadyInitialised = position
                Timber.d("ptg displaying day $activeDayId position: $position")
            }
        }

        override fun getCount(): Int = DAYS_WEEK

        override fun getItem(position: Int): Fragment {
            //this is also called to load the adjacent fragments - so shouldnt be used to know which fragment dayId is currently active
            var loadingDayId = positionToDayId(position)
            var loadDay = getDayByDayId(loadingDayId)
            Timber.d("ptg generating day $loadingDayId position: $position")

            return DayFragment.newInstance(loadDay, loadingDayId)
        }

        private fun positionToDayId(position: Int): String {
            var selectedDateCalendar = Calendar.getInstance()
            selectedDateCalendar.time = Day.dayIDToDate(activeDayId!!)
            var dayOfWeek = Day.getDayOfWeek0to6(selectedDateCalendar)

            selectedDateCalendar.timeInMillis =
                selectedDateCalendar.timeInMillis + ((position - dayOfWeek) * Day.MS_IN_DAY)

            return Day.dateToDayID(selectedDateCalendar.time)

        }

    }

    private fun getDayByDayId(activeDayId: String?): Day? {
        for (day in allDays) {
            if (day.dayId == activeDayId) {
                return day
            }
        }
        return null
    }

}
