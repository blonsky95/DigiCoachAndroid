package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import kotlinx.android.synthetic.main.activity_view_of_week.*
import timber.log.Timber
import java.util.*


//todo check the calendar provider some time
class CalendarView : AppCompatActivity() {

    private lateinit var mPager: ViewPager

    val calendar: Calendar = Calendar.getInstance()
    val dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
    val monthLongName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    var dayOfWeek = filterDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))


    //day of week goes 1 to 7 (Sunday is 1 and Saturday is 7) - this normalises it to 1 monday, 7 sunday
    private fun filterDayOfWeek(dayWeek: Int): Int {
        return if (dayWeek == 1) {
            7
        } else {
            dayWeek - 1
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_of_week)
        title = "Week View"

//        prepareUI()

        mPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

        Timber.d("day of week is $dayOfWeek")
        mPager.currentItem = dayOfWeek
        AddTrainingBtn.setOnClickListener(updateDay)

    }


    private val updateDay = View.OnClickListener {
        Timber.d("position clicked: ${mPager.currentItem}")
    }

    private fun prepareUI() {
//        val string = "Today is $dayLongName ${calendar.get(Calendar.DAY_OF_MONTH)} of $monthLongName"
//        DateDisplay.text = string

    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        var tempDayOfWeek = ""
        var tempDayOfMonth = ""
        var tempMonthOfYear = ""

        override fun getCount(): Int = 7

        override fun getItem(position: Int): Fragment {
            //todo send the blocks and exercises to DayFragment
            //todo load data view model and get the Day instance through there -- using dayId#
            //todo instead of day id it will send a Day instance - look what happens when you try to find a id and there is nothing

            val dayId = getDayId(dayOfWeek-(position + 1))
            var dataArray = arrayListOf(tempDayOfWeek, tempDayOfMonth, tempMonthOfYear)
            return DayFragment(dayId, dataArray)
        }

        private fun getDayId(dayDiff: Int): String {

            var fakeCalendar = Calendar.getInstance()
            fakeCalendar.timeInMillis = System.currentTimeMillis() - ((24 * 60 * 60 * 1000) * dayDiff)
            tempDayOfMonth = fakeCalendar.get(Calendar.DAY_OF_MONTH).toString()
            tempMonthOfYear = fakeCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            tempDayOfWeek = fakeCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            return Day.toDayId(
                fakeCalendar.get(Calendar.DAY_OF_MONTH),
                fakeCalendar.get(Calendar.MONTH),
                fakeCalendar.get(Calendar.YEAR)
            )
        }
    }


}
