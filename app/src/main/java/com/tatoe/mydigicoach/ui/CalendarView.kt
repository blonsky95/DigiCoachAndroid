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

    val calendar = Calendar.getInstance()
    val dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
    val monthLongName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_of_week)
        title = "Week View"

        prepareUI()

        mPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter


        mPager.currentItem = dayOfWeek-1 //day of week goes 1 to 7, position 0 to 6


        AddTrainingBtn.setOnClickListener(updateDay)

    }

    private val updateDay = View.OnClickListener {
        Timber.d("position clicked: ${mPager.currentItem}")
    }

    private fun prepareUI() {


        val string = "Today is $dayLongName ${calendar.get(Calendar.DAY_OF_MONTH)} of $monthLongName"
        DateDisplay.text=string

    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 7

        override fun getItem(position: Int): Fragment {
            //todo send the blocks and exercises to DayFragment

            val dayId=getDayId(dayOfWeek-position+1)
            Timber.d("please please: $dayId") //TODO TEST THIS THING BUT IT WORKS
            //todo load data view model and get the Day instance through there -- using dayId
           return DayFragment()
        }

        private fun getDayId(dayDiff:Int): String {
            //i need day month and year in int to call the DAY converter
            var fakeCalendar = Calendar.getInstance()
            fakeCalendar.timeInMillis=System.currentTimeMillis() - ((24*60*60*1000)*dayDiff)
            return Day.toDayId(fakeCalendar.get(Calendar.DAY_OF_MONTH),fakeCalendar.get(Calendar.MONTH),fakeCalendar.get(Calendar.YEAR))
        }
    }


}
