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
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_view_of_week.*
import timber.log.Timber
import java.util.*


class CalendarView : AppCompatActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var dataViewModel: DataViewModel
    private var activeDay:Day?=null
    private lateinit var activeDayId:String

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
        setContentView(R.layout.activity_view_of_week)
        title = "Week View"
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

        Timber.d("activeDay of week is $dayOfWeek")
        mPager.currentItem = dayOfWeek
        AddTrainingBtn.setOnClickListener(updateDayListener)

    }


    private val updateDayListener = View.OnClickListener {
        Timber.d("position clicked: ${mPager.currentItem}")
        Timber.d("Calendar View --> Day Creator")

        DataHolder.oldDayHolder=activeDay

        val intent = Intent(this, DayCreator::class.java)
//        intent.putExtra(DayCreator.DAY_ACTION, DayCreator.DAY_NEW)
        intent.putExtra(DayCreator.DAY_ID,activeDayId)
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

            activeDayId = getDayId(dayOfWeek-position)
            val dataArray = arrayListOf(tempDayOfWeek, tempDayOfMonth, tempMonthOfYear)
            activeDay = dataViewModel.getDayById(activeDayId)
//            Timber.d("currentDay: $activeDay")
            return DayFragment(activeDay, dataArray)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == dayCreatorAcitivtyRequestCode && resultCode == DayCreator.DAY_UPDATE_RESULT_CODE) {

            val updatedDay = DataHolder.updatedDayHolder
                        Timber.d("on activity result day: $updatedDay")

            if (DataHolder.oldDayHolder==null) {
                dataViewModel.insertDay(updatedDay)
            } else {
                dataViewModel.updateDay(updatedDay)
            }

//            val actionNotification = Snackbar.make(recyclerView, "Exercise added", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        }
        else {
        }
    }


}
