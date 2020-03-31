package com.tatoe.mydigicoach.ui.day

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Day.Companion.isLeapYear
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.DaySliderAdapter
import com.tatoe.mydigicoach.viewmodels.DayViewerViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewerViewModelFactory
import kotlinx.android.synthetic.main.activity_day_viewer.*
import timber.log.Timber
import java.util.*


class DayViewer : AppCompatActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    private lateinit var dataViewModel: DayViewerViewModel
    private lateinit var selectDaySliderListener: ClickListenerRecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var daySliderPosition = -1

//    private var isLeapYearBoolean:Boolean?=null

    val MS_IN_WEEK: Long = 7 * 24 * 3600 * 1000

    val calendar: Calendar = Calendar.getInstance()
    var currentCalendar: Calendar = Calendar.getInstance()

    private var activeDay: Day? = null
    private var activeDayId: String = Day.dateToDayID(currentCalendar.time)
    private var allDays: List<Day> = listOf()

    private var currentWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
    private var nonCurrentWeekOfYear = currentWeekOfYear

    var dayOfWeek = filterDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
    var nonCurrentTimeInMillis: Long = System.currentTimeMillis()

    //activeDay of week goes 0 to 6 (Calendar.DAY_OF_WEEK returns Sunday as 1 and Saturday as 7) - this normalises it to 0 monday, 6 sunday
    private fun filterDayOfWeek(dayWeek: Int): Int {
        return if (dayWeek == 1) {
            6
        } else {
            dayWeek - 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_viewer)
        title = "Week View"

        daySliderPosition = DaySliderAdapter.DEFAULT_POS


        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initListeners()

        var recyclerViewDaySlider = daySliderRecyclerView as RecyclerView

        var adapter = DaySliderAdapter(this)
        adapter.setOnClickInterface(selectDaySliderListener)

        recyclerViewDaySlider.adapter = adapter
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDaySlider.layoutManager = linearLayoutManager
        recyclerViewDaySlider.layoutManager!!.scrollToPosition(DaySliderAdapter.DEFAULT_POS - 1)


        mPager = findViewById(R.id.pager)

        dataViewModel = ViewModelProviders.of(this, MyDayViewerViewModelFactory(application))
            .get(DayViewerViewModel::class.java)

        AddTrainingBtn.setOnClickListener(updateDayListener)
//        ChangeWeekBtn.setOnClickListener {
//            generateDialog()
//        }

        initObservers()

    }

    private fun initListeners() {
        selectDaySliderListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)
//                view.setBackgroundColor(resources.getColor(R.color.darkBlue))
                dataViewModel.changeActiveDay(DaySliderAdapter.positionToDayId(position))
            }
        }
    }

    private fun initObservers() {
        dataViewModel.activeDayIdStr.observe(this, androidx.lifecycle.Observer { dayId ->
            Timber.d("OBSERVER NEW DAYID: $dayId")
//            pagerAdapter.setDay(dayId)
//            mPager.changePagerPosition(dayId)
            activeDayId = dayId
            var nonCurrentCalendar = Calendar.getInstance()
            nonCurrentCalendar.time = Day.dayIDToDate(dayId)

            changeWeekContent(nonCurrentCalendar)
            mPager.currentItem = changeDayPositionInPager(nonCurrentCalendar)
        })
//        dataViewModel.activePosition.observe(this, androidx.lifecycle.Observer { position ->
//            Timber.d("OBSERVER NEW POSITION: $position DAY SLIDER POSITION: $daySliderPosition")
//            //does nothing atm - the slider is clickable but doesnt react to anything
//
//        })

        dataViewModel.allDays.observe(this, androidx.lifecycle.Observer { days ->
            days?.let {
                allDays = days
            }
            //for now this will do, the observer is being triggered after the Adapter creation so it searches in an empty allDays List
            //this way Adapter is created when observer is triggered
            pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
            mPager.adapter = pagerAdapter
            if (DataHolder.pagerPosition >= 0) {
                mPager.setCurrentItem(DataHolder.pagerPosition, false)
//                mPager.currentItem = DataHolder.pagerPosition
                DataHolder.pagerPosition = -1
            } else {
//                mPager.currentItem = dayOfWeek
                mPager.setCurrentItem(dayOfWeek, false)
            }
        })
    }

    private fun changeDayPositionInPager(nonCurrentCalendar: Calendar): Int {
        //to get the right slider pager position (between 0 and 6) have to check if leap year,
        // which will give us a value in range 1-7 and then subtract 1 so it's 0-6
        return reformatDayOfWeek(nonCurrentCalendar)
    }

    private fun reformatDayOfWeek(nonCurrentCalendar: Calendar): Int {
        var mDayOfWeek = nonCurrentCalendar.get(Calendar.DAY_OF_WEEK)

        //checks leap year - gergorian calendars don't do leap years and thats default calendar (?)
        if (isLeapYear(nonCurrentCalendar)) {
            if (mDayOfWeek == 1) {
                mDayOfWeek = 7
            } else {
                mDayOfWeek--
            }
        }
        //day of week varies 1-7 but pager positions are 0-6 so subtract 1:
        return mDayOfWeek -1
    }

    private fun changeWeekContent(nonCurrentCalendar: Calendar) {

        nonCurrentWeekOfYear = nonCurrentCalendar.get(Calendar.WEEK_OF_YEAR)
        currentWeekOfYear = currentCalendar.get(Calendar.WEEK_OF_YEAR)

        if (nonCurrentWeekOfYear != currentWeekOfYear) {
            nonCurrentTimeInMillis =
                currentCalendar.timeInMillis + ((nonCurrentWeekOfYear - currentWeekOfYear) * MS_IN_WEEK)
            currentCalendar.time = nonCurrentCalendar.time

            pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
            mPager.adapter = pagerAdapter
//            mPager.adapter!!.notifyDataSetChanged()
        }
//        mPager.currentItem = 0
    }

    override fun onDestroy() {
        DataHolder.pagerPosition = -1
        super.onDestroy()
    }

    private val updateDayListener = View.OnClickListener {
        DataHolder.activeDayHolder = activeDay

        val intent = Intent(this, DayCreator::class.java)
        intent.putExtra(DayCreator.DAY_ID, activeDayId)
        DataHolder.pagerPosition = mPager.currentItem
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (mPager.currentItem != dayOfWeek) {
            mPager.currentItem = dayOfWeek
        } else {
            super.onBackPressed()
        }
    }


    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val DAYS_WEEK = 7
        var primaryItemSet = -1

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            //this says which position is currently active, use it to know which activedayid
            super.setPrimaryItem(container, position, `object`)
            if (position != primaryItemSet) {  //this function is called 2 or 3 times per swipe so avoid reupdating the dayid variable unnecesarily and creating conflicts
//                activeDayId = toDayIdFormat(mDayOfWeek - position)
                activeDay = getDayByDayId(activeDayId)
                primaryItemSet = position
                Timber.d("ptg displaying day $activeDayId position: $position")
            }
        }

        override fun getCount(): Int = DAYS_WEEK

        override fun getItem(position: Int): Fragment {
            //this is also called to load the adjacent fragments - so shouldnt be used to know which fragment dayId is currently active
//            var loadingDayId = toDayIdFormat(dayOfWeek - position)
            var loadingDayId = toDayIdFormat(reformatDayOfWeek(currentCalendar) - position)
            var loadDay = getDayByDayId(loadingDayId)
            Timber.d("ptg generating day $loadingDayId position: $position")

            return DayFragment.newInstance(loadDay, loadingDayId)
        }

        // to DDMMYYYY format, dayDiff is the
        private fun toDayIdFormat(dayOfWeekDiff: Int): String {


            var nonActiveNonCurrentCalendar = Calendar.getInstance()
            nonActiveNonCurrentCalendar.timeInMillis =
                currentCalendar.timeInMillis - ((Day.MS_IN_DAY) * dayOfWeekDiff)
            return Day.dateToDayID(nonActiveNonCurrentCalendar.time)

        }

    }

    private fun getDayByDayId(activeDayId: String): Day? {
        for (day in allDays) {
            if (day.dayId == activeDayId) {
                return day
            }
        }
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.day_viewer_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_info -> {
            Utils.getInfoDialogView(
                this,
                "Duplicate exercises",
                "There can only be one result per exercise per date"
            )
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
