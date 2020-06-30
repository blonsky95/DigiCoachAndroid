package com.tatoe.mydigicoach.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.EditableItemViewHolder
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import com.tatoe.mydigicoach.viewmodels.DayViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewModelFactory
import kotlinx.android.synthetic.main.activity_day_creator.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class DayCreator : AppCompatActivity() {

    companion object {
        var DAY_ID = "day_id"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter
    private lateinit var selectExercisesListener: ClickListenerRecyclerView
    private var selectedIndexes = arrayListOf<Int>()


    private lateinit var dataViewModel: DayViewModel
    private var currentDayBlocks: ArrayList<Block> = arrayListOf()
    private var currentDayExercises: ArrayList<Exercise> = arrayListOf()

    private var allExercises: List<Exercise> = listOf()
    private var differentOrderExercise: ArrayList<Exercise> = arrayListOf()

    lateinit var activeDay: Day
    lateinit var activeDayId: String

    private var weekDaysViewHashMap = hashMapOf<Int, TextView>()


    //todo fix errors and create the recyclerview for exercises here in the order bla bla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_creator)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        weekDaysViewHashMap[Day.MONDAY]=monday_btn
        weekDaysViewHashMap[Day.TUESDAY]=tuesday_btn
        weekDaysViewHashMap[Day.WEDNESDAY]=wednesday_btn
        weekDaysViewHashMap[Day.THURSDAY]=thursday_btn
        weekDaysViewHashMap[Day.FRIDAY]=friday_btn
        weekDaysViewHashMap[Day.SATURDAY]=saturday_btn
        weekDaysViewHashMap[Day.SUNDAY]=sunday_btn

        dataViewModel = ViewModelProviders.of(this, MyDayViewModelFactory(application))
            .get(DayViewModel::class.java)

        initObservers()

        recyclerView = day_creator_exercises_recycler_view as RecyclerView

        initAdapterListeners()
        adapter = ExerciseListAdapter(this,ExerciseListAdapter.DEFAULT_LAYOUT,true)
        adapter.imageLeftVisibility=View.GONE
        adapter.imageRightVisibility=View.VISIBLE
        adapter.backgroundColor=this.resources.getColor(R.color.lightGrey)

        adapter.setOnClickInterface(selectExercisesListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        DataHolder.activeDayHolder?.let { it ->
            activeDay = it

//            currentDayBlocks = activeDay.blocks
            currentDayExercises = activeDay.exercises
            Timber.d("data holder: active day: $activeDay")
        }
        activeDayId = intent.getStringExtra(DAY_ID)
        changeWeekDayHighlight()

        addToDiaryViewContainer.setOnClickListener(updateDayListener)

    }

    private fun changeWeekDayHighlight() {
        var calendar = Calendar.getInstance()
        calendar.time=Day.dayIDToDate(activeDayId)
        var weekDay = Day.getDayOfWeek0to6(calendar)

        for (entry in weekDaysViewHashMap) {
            if (entry.key==weekDay) {
                entry.value.setBackgroundColor(resources.getColor(R.color.lightGreen))
                entry.value.setTextColor(resources.getColor(R.color.white))
            } else {
                entry.value.setBackgroundColor(resources.getColor(R.color.lightGrey))
                entry.value.setTextColor(resources.getColor(R.color.darkGrey))
            }
        }
    }

    private fun initObservers() {


        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                allExercises = it
                for (exercise in currentDayExercises){
//                    selectedIndexes.add(allExercises.indexOf(exercise))
                    differentOrderExercise.add(exercise)
                    selectedIndexes.add(differentOrderExercise.indexOf(exercise))
                }
                for (exercise in allExercises) {
                    if (!differentOrderExercise.contains(exercise)) {
                        differentOrderExercise.add(exercise)
                    }
                }
                adapter.setSelectableExercises(differentOrderExercise,selectedIndexes)

//                pagerAdapterTop.mExerciseFragment?.updateExerciseAdapterContent(it)
            }
        })
    }

    private fun initAdapterListeners() {
        selectExercisesListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int, holder: EditableItemViewHolder) {
                super.onClick(view, position, holder)

                if (!holder.isChecked && !selectedIndexes.contains(position)) {
                    selectedIndexes.add(position)
                    holder.changeCheckedState(true,this@DayCreator)
//                    view.imageRightExerciseHolder.setImageDrawable(resources.getDrawable(R.drawable.ic_check_white_24dp))
//                    holder.isChecked=true
                } else {
                    selectedIndexes.remove(position)
                    holder.changeCheckedState(false,this@DayCreator)
//                    view.imageRightExerciseHolder.setImageDrawable(resources.getDrawable(R.drawable.ic_circle_grey))
//                    holder.isChecked=false
                }

            }
        }
    }


    private val updateDayListener = View.OnClickListener {

        //todo change this, find a better way of figuring out if updating or inserting

        currentDayExercises=arrayListOf()

        for (exercisePosition in selectedIndexes) {
            currentDayExercises.add(differentOrderExercise[exercisePosition])
        }

        if (DataHolder.activeDayHolder != null) {
            //update
//            activeDay.blocks = currentDayBlocks
            activeDay.exercises = currentDayExercises
            dataViewModel.updateDay(activeDay)
        } else {
            //new day
            dataViewModel.insertDay(Day(activeDayId, currentDayExercises))
        }
        backToViewer()
    }


    private fun backToViewer() {
        val intent = Intent(this, WeekViewer::class.java)
        intent.putExtra(MonthViewer.DAY_ID_KEY, activeDayId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}
