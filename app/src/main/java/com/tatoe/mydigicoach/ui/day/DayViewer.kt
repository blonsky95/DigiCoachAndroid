package com.tatoe.mydigicoach.ui.day

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_day_viewer.*
import timber.log.Timber
import java.util.*


class DayViewer : AppCompatActivity() {



    private lateinit var mPager: ViewPager
    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    private lateinit var dataViewModel: DataViewModel
    private var activeDay: Day? = null
    private lateinit var activeDayId: String
    private var allDays: List<Day> = listOf()

    private val dayCreatorAcitivtyRequestCode = 1

    val calendar: Calendar = Calendar.getInstance()
    //    val dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
//    val monthLongName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    var dayOfWeek = filterDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))

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

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mPager = findViewById(R.id.pager)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        Timber.d("activeDay of week is $dayOfWeek")
        AddTrainingBtn.setOnClickListener(updateDayListener)

        dataViewModel.allDays.observe(this, androidx.lifecycle.Observer { days ->
            days?.let {
                allDays = days
            }
            //for now this will do, the observer is being triggered after the Adapter creation so it searches in an empty allDays List
            //this way Adapter is created when observer is triggered
            pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
            mPager.adapter = pagerAdapter
            if (DataHolder.pagerPosition>=0) {
                mPager.currentItem = DataHolder.pagerPosition
                DataHolder.pagerPosition=-1
            } else {
                mPager.currentItem = dayOfWeek
            }


            Timber.d("list of days has been updated to: $allDays")
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        DataHolder.pagerPosition=-1
        super.onDestroy()
    }

    private val updateDayListener = View.OnClickListener {
        Timber.d("position clicked: ${mPager.currentItem}")
        Timber.d("Calendar View --> Day Creator")

        DataHolder.oldDayHolder = activeDay
        Timber.d("data holder calendarview : active day: ${DataHolder.oldDayHolder}")

        val intent = Intent(this, DayCreator::class.java)
//        intent.putExtra(DayCreator.DAY_ACTION, DayCreator.DAY_NEW)
        intent.putExtra(DayCreator.DAY_ID, activeDayId)
        DataHolder.pagerPosition=mPager.currentItem
        startActivityForResult(intent, dayCreatorAcitivtyRequestCode)
    }

    override fun onBackPressed() {
        if (mPager.currentItem != dayOfWeek) {
            mPager.currentItem = dayOfWeek
        } else {
            super.onBackPressed()
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

        var tempDayOfWeek = ""
        var tempDayOfMonth = ""
        var tempMonthOfYear = ""
        var primaryItemSet = -1

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            //this says which position is currently active, use it to know which activedayid
            super.setPrimaryItem(container, position, `object`)
            if (position != primaryItemSet) {  //this function is called 2 or 3 times per swipe so avoid reupdating the dayid variable unnecesarily and creating conflicts
                activeDayId = toDayIdFormat(dayOfWeek - position)
                activeDay = getDayById(activeDayId)
                primaryItemSet = position
            }
        }

        override fun getCount(): Int = 7

        override fun getItem(position: Int): Fragment {
            //this is also called to load the adjacent fragments - so shouldnt be used to know which fragment dayId is currently active
            var loadingDayId = toDayIdFormat(dayOfWeek - position)
            val dataArray = arrayListOf(tempDayOfWeek, tempDayOfMonth, tempMonthOfYear)
            var loadDay = getDayById(loadingDayId)
            Timber.d("get item created day instance: $loadDay at position $position")
            return DayFragment(loadDay, dataArray)
        }

        private fun toDayIdFormat(dayDiff: Int): String {

            var fakeCalendar = Calendar.getInstance()
            fakeCalendar.timeInMillis =
                System.currentTimeMillis() - ((24 * 60 * 60 * 1000) * dayDiff)
            tempDayOfMonth = fakeCalendar.get(Calendar.DAY_OF_MONTH).toString()
            tempMonthOfYear =
                fakeCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            tempDayOfWeek = fakeCalendar.getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault()
            )
            return Day.intDatetoDayId(
                fakeCalendar.get(Calendar.DAY_OF_MONTH),
                fakeCalendar.get(Calendar.MONTH),
                fakeCalendar.get(Calendar.YEAR)
            )
        }
    }

    private fun getDayById(activeDayId: String): Day? {
        for (day in allDays) {
            if (day.dayId == activeDayId) {
                return day
            }
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        mPager.currentItem=DataHolder.pagerPosition
        Timber.d("pager position holder: ${DataHolder.pagerPosition}")
        if (requestCode == dayCreatorAcitivtyRequestCode && resultCode == DayCreator.DAY_UPDATE_RESULT_CODE) {

            val updatedDay = DataHolder.updatedDayHolder
            Timber.d("on activity result day: $updatedDay")

            if (DataHolder.oldDayHolder == null) {
                dataViewModel.insertDay(updatedDay)
            } else {
                dataViewModel.updateDay(updatedDay)
            }


        } else {
        }
    }

}
