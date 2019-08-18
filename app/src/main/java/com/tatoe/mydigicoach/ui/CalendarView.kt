package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import kotlinx.android.synthetic.main.activity_view_of_week.*
import timber.log.Timber
import java.util.*


class CalendarView : AppCompatActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var dataViewModel: DataViewModel

    private val dayCreatorAcitivtyRequestCode = 1

    val calendar: Calendar = Calendar.getInstance()
//    val dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
//    val monthLongName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    var dayOfWeek = filterDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))

    //day of week goes 0 to 6 (Calendar.DAY_OF_WEEK returns Sunday as 1 and Saturday as 7) - this normalises it to 0 monday, 6 sunday
    private fun filterDayOfWeek(dayWeek: Int): Int {
        return if (dayWeek == 1) {
            6
        } else {
            dayWeek - 2
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_of_week)
        title = "Week View"
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

        Timber.d("day of week is $dayOfWeek")
        mPager.currentItem = dayOfWeek
        AddTrainingBtn.setOnClickListener(updateDay)

    }


    private val updateDay = View.OnClickListener {
        Timber.d("position clicked: ${mPager.currentItem}")
        Timber.d("Calendar View --> Day Creator")

        val intent = Intent(this, DayCreator::class.java)
        intent.putExtra(DayCreator.DAY_ACTION, DayCreator.DAY_NEW)
        startActivityForResult(intent, dayCreatorAcitivtyRequestCode)
    }

    override fun onBackPressed() {
        if (mPager.currentItem != dayOfWeek) {
            mPager.currentItem=dayOfWeek
        } else {
            super.onBackPressed()
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        var tempDayOfWeek = ""
        var tempDayOfMonth = ""
        var tempMonthOfYear = ""

        override fun getCount(): Int = 7

        override fun getItem(position: Int): Fragment {

            val dayId = getDayId(dayOfWeek-position)
            val dataArray = arrayListOf(tempDayOfWeek, tempDayOfMonth, tempMonthOfYear)
            val currentDay = dataViewModel.getDayById(dayId)
            //todo hereee 111
            return DayFragment(currentDay, dataArray)
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
