package com.tatoe.mydigicoach.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.calendar.CustomAdapterFragment
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.results.ResultsCreator


class DayExercisesListAdapter(var context: Context, var date: String, var itemType: Int) :
    RecyclerView.Adapter<DayExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blocks = emptyList<Block>()
    private var exercises = emptyList<Exercise>()
    private var sDay: Day? = null

    private var listenerRecyclerView: ClickListenerRecyclerView? = null

    override fun getItemCount(): Int {
        var size = 0
        when (itemType) {
            CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> size = exercises.size
        }
        return size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayExerciseViewHolder {

        var itemView = View(context)
        when (itemType) {
            CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> itemView =
                inflater.inflate(R.layout.item_holder_day_exercise, parent, false)
        }

        return DayExerciseViewHolder(itemView)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: DayExerciseViewHolder, position: Int) {

        when (itemType) {
            CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> {
                populateExercises(holder, position)
            }
        }
    }

    private fun populateExercises(holder: DayExerciseViewHolder, position: Int = -1) {
        val bindingExercise = exercises[position]
        createExerciseTabLayout(holder, bindingExercise)
    }

    private fun createExerciseTabLayout(holder: DayExerciseViewHolder, exercise: Exercise) {
        holder.exerciseTextView!!.text = exercise.name
        holder.exerciseTextView.setOnClickListener {
            viewExerciseInCreator(exercise)
        }

        holder.exerciseResultButton!!.setBackgroundColor(
            getExerciseResultButtonDrawable(
                exercise
            )
        )



        holder.exerciseResultButton.setOnClickListener {
            goToExerciseResults(exercise)
        }
    }

    private fun viewExerciseInCreator(exercise: Exercise) {
        DataHolder.activeExerciseHolder = exercise
        val intent = Intent(context, ExerciseCreator::class.java)
        intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
        startActivity(context, intent, null)
    }

    private fun goToExerciseResults(exercise: Exercise) {

        DataHolder.activeExerciseHolder = exercise
        var intent = Intent(context, ResultsCreator::class.java)

        if (exercise.exerciseResults.containsResult(date)) {
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
            intent.putExtra(
                ResultsCreator.RESULT_INDEX,
                exercise.exerciseResults.getResultPosition(date)
            )
        } else {
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_NEW)
            intent.putExtra(ResultsCreator.RESULTS_DATE, date)
        }

        startActivity(context, intent, null)
    }



    internal fun setContent(day: Day?) {
        if (day != null) {
            this.blocks = day.blocks
            this.exercises = day.exercises
            this.sDay = day
            notifyDataSetChanged()
        }
    }

    fun setOnClickInterface (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }

    //checks if that result already has an entry and returns a different colour to apply to button
    private fun getExerciseResultButtonStateColour(exercise: Exercise): Int {
        var colourInt = R.color.lightBlue
        if (exercise.exerciseResults.containsResult(date)) {
            colourInt = R.color.darkBlue
        }

        return ContextCompat.getColor(context, colourInt)
    }

    private fun getExerciseResultButtonDrawable(exercise: Exercise): Int {
        var color = R.color.lightGrey
        if (exercise.exerciseResults.containsResult(date)) {
            color = R.color.darkGreen
        }
        return context.resources.getColor(color)

    }
}