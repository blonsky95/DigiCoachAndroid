package com.tatoe.mydigicoach.ui.day

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.tatoe.mydigicoach.viewmodels.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.DaySliderAdapter
import com.tatoe.mydigicoach.viewmodels.DayViewerViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewerViewModelFactory
import kotlinx.android.synthetic.main.activity_day_viewer.*
import kotlinx.android.synthetic.main.custom_dialog_window.view.*
import kotlinx.android.synthetic.main.item_holder_day_slider_item.view.*
import timber.log.Timber
import java.util.*


class DayViewer : AppCompatActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    private lateinit var dataViewModel: DayViewerViewModel
    private lateinit var selectDaySliderListener: ClickListenerRecyclerView

    private var activeDay: Day? = null
    private lateinit var activeDayId: String
    private var allDays: List<Day> = listOf()

    val MS_IN_WEEK:Long = 7*24*3600*1000

    val calendar: Calendar = Calendar.getInstance()
    private var currentWeekOfYear=calendar.get(Calendar.WEEK_OF_YEAR)
    private var tempWeekOfYear=currentWeekOfYear

    var dayOfWeek = filterDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
    var fakeTimeInMillis :Long= System.currentTimeMillis()

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

        initListeners()

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var recyclerViewDaySlider = daySliderRecyclerView as RecyclerView

        var adapter = DaySliderAdapter(this)
        adapter.setOnClickInterface(selectDaySliderListener)

        recyclerViewDaySlider.adapter=adapter
        recyclerViewDaySlider.layoutManager=LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        recyclerViewDaySlider.layoutManager!!.scrollToPosition(DaySliderAdapter.DEFAULT_POS-1)


        mPager = findViewById(R.id.pager)

        dataViewModel = ViewModelProviders.of(this,MyDayViewerViewModelFactory(application)).get(DayViewerViewModel::class.java)

        AddTrainingBtn.setOnClickListener(updateDayListener)
        ChangeWeekBtn.setOnClickListener {
            generateDialog()
        }

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
        dataViewModel.activeDay.observe(this, androidx.lifecycle.Observer { dayId ->
            Timber.d("OBSERVER NEW DAYID: $dayId")
            //todo here gets these nuts
//            changeColour(dayId)
//            changePagerPosition(dayId)
        })

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
        })    }

    private fun generateDialog () {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_window, null)
        mDialogView.dialogTextTextView.text= "Current week: $tempWeekOfYear"
        mDialogView.dialogEditText.hint="Enter week"
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Change week")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogEnterBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
            val weekNumber = mDialogView.dialogEditText.text.toString().toInt()
            changeWeekContent(weekNumber)
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    private fun changeWeekContent(weekNumber: Int) {
        tempWeekOfYear=weekNumber
        fakeTimeInMillis=System.currentTimeMillis()+((tempWeekOfYear-currentWeekOfYear)*MS_IN_WEEK)
        mPager.adapter = pagerAdapter
        mPager.currentItem = 0
    }

    override fun onDestroy() {
        DataHolder.pagerPosition=-1
        super.onDestroy()
    }

    private val updateDayListener = View.OnClickListener {
        DataHolder.activeDayHolder = activeDay

        val intent = Intent(this, DayCreator::class.java)
        intent.putExtra(DayCreator.DAY_ID, activeDayId)
        DataHolder.pagerPosition=mPager.currentItem
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
                dataViewModel.changeActiveDay(activeDayId)

                primaryItemSet = position
            }
        }

        override fun getCount(): Int = 7

        override fun getItem(position: Int): Fragment {
            //this is also called to load the adjacent fragments - so shouldnt be used to know which fragment dayId is currently active
            var loadingDayId = toDayIdFormat(dayOfWeek - position)
            var loadDay = getDayById(loadingDayId)
            Timber.d("ptg day ${loadDay?.dayId} $loadingDayId")

            return DayFragment.newInstance(loadDay, loadingDayId)
        }

        // to DDMMYYYY format, dayDiff is the
        private fun toDayIdFormat(dayDiff: Int): String {

            var fakeCalendar = Calendar.getInstance()
//            fakeCalendar.timeInMillis =
//                System.currentTimeMillis() - ((24 * 60 * 60 * 1000) * dayDiff)
            fakeCalendar.timeInMillis =
                fakeTimeInMillis - ((24 * 60 * 60 * 1000) * dayDiff)
            Timber.d("ptg fake calendar week of year: ${fakeCalendar.get(Calendar.WEEK_OF_YEAR)}")

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
                fakeCalendar.get(Calendar.MONTH)+1, //month is always 1 behind despite consistent Locale.getDefault() (?)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.day_viewer_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_info -> {
            Utils.getInfoDialogView(this,"Duplicate exercises","There can only be one result per exercise per date")
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
