package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import com.tatoe.mydigicoach.viewmodels.DayViewerViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewerViewModelFactory
import kotlinx.android.synthetic.main.activity_day_creator.*
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import timber.log.Timber
import kotlin.collections.ArrayList

class DayCreator : AppCompatActivity(), CustomAdapterFragment.CustomAdapterEventsListener {

    companion object {
        var DAY_ID = "day_id"
    }

    private lateinit var mPagerTop: ViewPager
    private lateinit var mPagerBottom: ViewPager

//    private lateinit var pagerAdapterTop: ScreenSlidePagerAdapter
//    private lateinit var pagerAdapterBottom: ScreenSlidePagerAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter
    private lateinit var selectExercisesListener: ClickListenerRecyclerView


    private lateinit var dataViewModel: DayViewerViewModel
    private var currentDayBlocks: ArrayList<Block> = arrayListOf()
    private var currentDayExercises: ArrayList<Exercise> = arrayListOf()

    //    private var allUserBlocks: List<Block> = listOf()
    private var allExercises: List<Exercise> = listOf()

    lateinit var activeDay: Day
    lateinit var activeDayId: String

    val NOT_DELETABLE = 0
    val IS_DELETABLE = 2

    //todo fix errors and create the recyclerview for exercises here in the order bla bla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_creator)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        backBtn.setOnClickListener {
            super.onBackPressed()
        }
        dataViewModel = ViewModelProviders.of(this, MyDayViewerViewModelFactory(application))
            .get(DayViewerViewModel::class.java)

        initObservers()


//        mPagerTop = findViewById(R.id.pager_top)
//        pagerAdapterTop = ScreenSlidePagerAdapter(supportFragmentManager, NOT_DELETABLE)
//        mPagerTop.adapter = pagerAdapterTop

//        mPagerBottom = findViewById(R.id.pager_bottom)
//        pagerAdapterBottom = ScreenSlidePagerAdapter(supportFragmentManager, IS_DELETABLE)
//        mPagerBottom.adapter = pagerAdapterBottom

        recyclerView = day_creator_exercises_recycler_view as RecyclerView

//        exportBtn.visibility = View.GONE
//        initAdapterListeners()
        initAdapterListeners()
        adapter = ExerciseListAdapter(this)
        adapter.setOnClickInterface(selectExercisesListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        DataHolder.activeDayHolder?.let { it ->
            activeDay = it
//            currentDayBlocks = activeDay.blocks
            currentDayExercises = activeDay.exercises
            Timber.d("data holder: active day: $activeDay")
        }
//        updateBottomContent()
        activeDayId = intent.getStringExtra(DAY_ID)

//        DayId.text=Day.dayIDtoDashSeparator(activeDayId)
//        title = Day.dayIDtoDashSeparator(activeDayId)
        addToDiaryViewContainer.setOnClickListener(updateDayListener)
        updateAdaptersDisplay()

    }

//    private fun updateBottomContent() {
////        Timber.d("updating bottom content blocks $currentDayBlocks")
////        Timber.d("updating bottom content exes $currentDayExercises")
//
//
////        pagerAdapterBottom.mBlockFragment?.updateBlockAdapterContent(currentDayBlocks)
//        pagerAdapterBottom.mExerciseFragment?.updateExerciseAdapterContent(currentDayExercises)
//    }

    private fun initObservers() {
//        dataViewModel.allUserBlocks.observe(this, Observer { blocks ->
//            blocks?.let {
//                allUserBlocks = it
//                pagerAdapterTop.mBlockFragment?.updateBlockAdapterContent(it)
//            }
//        })

        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                allExercises = it
                adapter.setExercises(allExercises)
//                pagerAdapterTop.mExerciseFragment?.updateExerciseAdapterContent(it)
            }
        })
    }

    private fun initAdapterListeners() {
        selectExercisesListener = object : ClickListenerRecyclerView {
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is CustomAdapterFragment) {
            fragment.setCustomAdapterEventsListenerInterface(this)
        }
    }

    override fun itemSelected(adapterType: Int, position: Int, deletingItem: Boolean) {
        when (adapterType) {
            CustomAdapterFragment.BLOCK_TYPE_ADAPTER -> {
//                if (deletingItem) {
//                    currentDayBlocks.remove(currentDayBlocks[position])
//                } else {
//                    currentDayBlocks.add(
//                        currentDayBlocks.size,
//                        allUserBlocks[position]
//                    )
//                }
            }
            CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> {
                if (deletingItem) {
                    currentDayExercises.remove(currentDayExercises[position])
                } else {
                    currentDayExercises.add(
                        currentDayExercises.size,
                        allExercises[position]
                    )
                }
            }
        }
//        updateBottomContent()

    }

//    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, var isDeletable: Int) :
//        FragmentStatePagerAdapter(fm) {
//
//        var mBlockFragment: CustomAdapterFragment? = null
//        var mExerciseFragment: CustomAdapterFragment? = null
//
//        fun loadBlocks(blocks: List<Block>) {
//            mBlockFragment?.updateBlockAdapterContent(blocks)
//            mBlockFragment?.contentUpdated = true
//        }
//
//        fun loadExercises(exercises: List<Exercise>) {
//            mExerciseFragment?.updateExerciseAdapterContent(exercises)
//            mExerciseFragment?.contentUpdated = true
//        }
//
//        //        override fun getCount(): Int = 2
//        override fun getCount(): Int = 1
//
//
//        override fun getItem(position: Int): CustomAdapterFragment {
////            return CustomAdapterFragment.newInstance(position + isDeletable)
//            return CustomAdapterFragment.newInstance(count + isDeletable)
//
//        }
//
//
//        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
//            val mFragment = `object` as CustomAdapterFragment
//            if (mFragment.adapterType == CustomAdapterFragment.BLOCK_TYPE_ADAPTER || mFragment.adapterType == CustomAdapterFragment.BLOCK_DELETE_TYPE_ADAPTER) {
////                mBlockFragment = mFragment
////                if (mBlockFragment?.contentUpdated == false) {
////                    when (mFragment.adapterType) {
////                        CustomAdapterFragment.BLOCK_TYPE_ADAPTER -> loadBlocks(allUserBlocks)
////                        CustomAdapterFragment.BLOCK_DELETE_TYPE_ADAPTER -> loadBlocks(
////                            currentDayBlocks
////                        )
////                    }
////                }
//            } else {
//                mExerciseFragment = mFragment
//                if (mExerciseFragment?.contentUpdated == false) {
//                    when (mFragment.adapterType) {
//                        CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> loadExercises(allExercises)
//                        CustomAdapterFragment.EXERCISE_DELETE_TYPE_ADAPTER -> loadExercises(
//                            currentDayExercises
//                        )
//                    }
//                }
//            }
//            super.setPrimaryItem(container, position, `object`)
//        }
//
//
//    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.creator_toolbar_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//
//        R.id.action_back -> {
//            super.onBackPressed()
//            true
//        }
//        else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }

    private val updateDayListener = View.OnClickListener {

        //todo change this, find a better way of figuring out if updating or inserting

        if (DataHolder.activeDayHolder != null) {
            //update
            activeDay.blocks = currentDayBlocks
            activeDay.exercises = currentDayExercises
            dataViewModel.updateDay(activeDay)
        } else {
            dataViewModel.insertDay(Day(activeDayId, currentDayBlocks, currentDayExercises))
        }
        backToViewer()
    }

    private fun updateAdaptersDisplay() {
//        if (currentDayBlocks.isEmpty()) {
//            recyclerViewV2.visibility = View.GONE
//            IfDayEmptyText.visibility = View.VISIBLE
//        } else {
//            recyclerViewV2.visibility = View.VISIBLE
//            IfDayEmptyText.visibility = View.GONE

//        adapterDeletableBlocks.setBlocks(currentDayBlocks)
//        }
    }

    private fun backToViewer() {
        val intent = Intent(this, WeekViewer::class.java)
        intent.putExtra(MonthViewer.DAY_ID_KEY, activeDayId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}
