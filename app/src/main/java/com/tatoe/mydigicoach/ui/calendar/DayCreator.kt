package com.tatoe.mydigicoach.ui.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.DayPackage
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.ui.fragments.PackageReceivedFragment
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.EditableItemViewHolder
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import com.tatoe.mydigicoach.viewmodels.DayViewModel
import com.tatoe.mydigicoach.viewmodels.MyDayViewModelFactory
import kotlinx.android.synthetic.main.activity_day_creator.*
import kotlinx.android.synthetic.main.activity_day_creator.search_view
import kotlinx.android.synthetic.main.item_holder_exercise.view.*
import kotlinx.android.synthetic.main.item_holder_exercise.view.titleTextExerciseHolder
import kotlinx.android.synthetic.main.item_holder_exercise_with_check.view.*
import kotlinx.android.synthetic.main.item_holder_friends.view.friend_username_textview
import kotlinx.android.synthetic.main.item_holder_request_package.view.*
import java.util.*
import kotlin.collections.ArrayList

class DayCreator : AppCompatActivity(), SearchView.OnQueryTextListener {

    companion object {
        var DAY_ID = "day_id"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyCustomCheckedExercisesAdapter
    private lateinit var selectExercisesListener: ClickListenerRecyclerView
//    private var selectedIndexes = arrayListOf<Int>()

    private lateinit var dayViewModel: DayViewModel
    private var currentDayExercises: ArrayList<Exercise> = arrayListOf()

    private var allExercises: List<Exercise> = listOf()
    private var checkedExercisesTemp = arrayListOf<ExerciseWithChecked>()
    private var checkedExercisesPerm = arrayListOf<ExerciseWithChecked>()
//    private var sortedDayExercisesOwnedNotOwned: ArrayList<Exercise> = arrayListOf()

    private var filteredExes = mutableListOf<Exercise>()

    lateinit var activeDay: Day
    lateinit var activeDayId: String

    private var weekDaysViewHashMap = hashMapOf<Int, TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_creator)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        weekDaysViewHashMap[Day.MONDAY] = monday_btn
        weekDaysViewHashMap[Day.TUESDAY] = tuesday_btn
        weekDaysViewHashMap[Day.WEDNESDAY] = wednesday_btn
        weekDaysViewHashMap[Day.THURSDAY] = thursday_btn
        weekDaysViewHashMap[Day.FRIDAY] = friday_btn
        weekDaysViewHashMap[Day.SATURDAY] = saturday_btn
        weekDaysViewHashMap[Day.SUNDAY] = sunday_btn

        dayViewModel = ViewModelProviders.of(this, MyDayViewModelFactory(application))
            .get(DayViewModel::class.java)

        initObservers()

        recyclerView = day_creator_exercises_recycler_view as RecyclerView

        initAdapterListeners()
        adapter = MyCustomCheckedExercisesAdapter(this)
        adapter.setOnClickInterface(selectExercisesListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        search_view.setOnQueryTextListener(this)

        DataHolder.activeDayHolder?.let { it ->
            activeDay = it
            currentDayExercises = activeDay.exercises
        }
        activeDayId = intent.getStringExtra(DAY_ID)
        changeWeekDayHighlight()

        addToDiaryViewContainer.setOnClickListener(updateDayListener)

    }

    private fun changeWeekDayHighlight() {
        var calendar = Calendar.getInstance()
        calendar.time = Day.dayIDToDate(activeDayId)
        var weekDay = Day.getDayOfWeek0to6(calendar)

        for (entry in weekDaysViewHashMap) {
            if (entry.key == weekDay) {
                entry.value.setBackgroundColor(resources.getColor(R.color.lightGreen))
                entry.value.setTextColor(resources.getColor(R.color.white))
            } else {
                entry.value.setBackgroundColor(resources.getColor(R.color.lightGrey))
                entry.value.setTextColor(resources.getColor(R.color.darkGrey))
            }
        }
    }

    private fun initObservers() {
        dayViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                allExercises = it
                checkedExercisesPerm=getDefaultArrayOfDayTraining()
                checkedExercisesTemp.addAll(checkedExercisesPerm)
                adapter.setContent(checkedExercisesTemp)
            }
        })
    }

    private fun initAdapterListeners() {
        selectExercisesListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int, holder: MyCheckedExerciseViewHolder) {
                super.onClick(view, position, holder)

                if (!holder.isChecked) {

//                    checkedExercisesTemp.add(0,exe)
//                    checkedExercisesTemp.remove(checkedExercisesTemp[position])
                    checkedExercisesPerm.remove(checkedExercisesTemp[position])
                    var exe=checkedExercisesTemp[position]
                    exe.mIsChecked=true
                    checkedExercisesPerm.add(0,exe)

                    holder.changeCheckedState(true, this@DayCreator)
//                    adapter.notifyDataSetChanged()
                } else {
                    checkedExercisesTemp[position].mIsChecked=false
//                    checkedExercisesTemp.remove(checkedExercisesTemp[position])
//                    checkedExercisesTemp.add(0,checkedExercisesTemp[position])
                    checkedExercisesPerm.remove(checkedExercisesTemp[position])
                    checkedExercisesPerm.add(checkedExercisesTemp[position])
                    holder.changeCheckedState(false, this@DayCreator)
                }

            }
        }
    }

    //todo do some sort of permanent selected indexes so when you update day it doesnt just collected the selected index that are displayed on screen in that moment
    private val updateDayListener = View.OnClickListener {
        currentDayExercises = arrayListOf()
        var i = 0
        while (checkedExercisesPerm[i].mIsChecked){
            currentDayExercises.add(checkedExercisesPerm[i].mExercise)
            i++
        }
//        for (exercisePosition in selectedIndexes) {
//            currentDayExercises.add(sortedDayExercisesOwnedNotOwned[exercisePosition])
//        }

        if (DataHolder.activeDayHolder != null) {
            activeDay.exercises = currentDayExercises
            dayViewModel.selectedExercises=currentDayExercises
            dayViewModel.updateDay(activeDay)
        } else {
            dayViewModel.insertDay(Day(activeDayId, currentDayExercises))
        }
        backToViewer()
    }


    private fun backToViewer() {
        val intent = Intent(this, WeekViewer::class.java)
        intent.putExtra(MonthViewer.DAY_ID_KEY, activeDayId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(filterText: String?): Boolean {
        adapter.setContent(getFilteredExes(filterText))
        return true
    }

    private fun getFilteredExes(filterText: String?): List<ExerciseWithChecked> {
        checkedExercisesTemp.clear()

        if (filterText != null && filterText.isNotEmpty()) {
            val text = filterText.toLowerCase()
            for (exe in allExercises) {
                //filter exes that meet search characters
                if (exe.name.toLowerCase().contains(text)) {
                    if (currentDayExercises.contains(exe)) {
                        checkedExercisesTemp.add(0,ExerciseWithChecked(exe, true))
                    } else {
                        checkedExercisesTemp.add(ExerciseWithChecked(exe,false))
                    }
                }
            }
        } else {
            checkedExercisesTemp=getDefaultArrayOfDayTraining()
        }
        return checkedExercisesTemp

    }

    private fun getDefaultArrayOfDayTraining(): ArrayList<ExerciseWithChecked> {
        val arrayList = arrayListOf<ExerciseWithChecked>()

        for (exercise in allExercises) {
            if (currentDayExercises.contains(exercise)) {
                arrayList.add(0,ExerciseWithChecked(exercise, true))
            } else {
                arrayList.add(ExerciseWithChecked(exercise,false))
            }
        }
        return arrayList
    }

    class ExerciseWithChecked (exercise: Exercise, isContained:Boolean = false){
        var mExercise = exercise
        var mIsChecked=isContained
    }

    inner class MyCustomCheckedExercisesAdapter(context: Context) :
        RecyclerView.Adapter<MyCheckedExerciseViewHolder>() {

        var exercisesWithChecked = listOf<ExerciseWithChecked>()

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var listenerRecyclerView: ClickListenerRecyclerView? = null

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyCheckedExerciseViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_exercise_with_check, parent, false)
            return MyCheckedExerciseViewHolder(itemView, listenerRecyclerView)
        }

        override fun getItemCount(): Int {
            return exercisesWithChecked.size
        }

        override fun onBindViewHolder(holder: MyCheckedExerciseViewHolder, position: Int) {

            val current = exercisesWithChecked[position]
            val textString = current.mExercise.name
            if (current.mIsChecked){
                holder.changeCheckedState(true,this@DayCreator)
            } else {
                holder.changeCheckedState(false,this@DayCreator)
            }
            holder.exerciseTextView.text = textString

        }

        fun setContent(mExercisesWithChecked: List<ExerciseWithChecked>) {
            exercisesWithChecked = mExercisesWithChecked
            notifyDataSetChanged()
        }

        fun setOnClickInterface (listener: ClickListenerRecyclerView) {
            this.listenerRecyclerView=listener
        }

    }

    class MyCheckedExerciseViewHolder(var v: View, var onClickInterface:ClickListenerRecyclerView?) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var exerciseTextView = v.titleTextExerciseHolder
        var rightCheckButton = v.check_button
        var isChecked = false

        init {
                rightCheckButton.setOnClickListener(this)
        }

        override fun onClick(v: View) {
                onClickInterface?.onClick(v, adapterPosition, this)
        }

        fun changeCheckedState(shouldCheck: Boolean, context: Context){
            isChecked = if (shouldCheck) {
               rightCheckButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_white_24dp))
                true
            } else {
                rightCheckButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_circle_grey))
                false
            }
        }
    }
}
