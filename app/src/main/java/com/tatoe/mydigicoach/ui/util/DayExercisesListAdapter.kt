package com.tatoe.mydigicoach.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.calendar.CustomAdapterFragment
import com.tatoe.mydigicoach.ui.calendar.WeekViewer
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.results.ResultsCreator
import kotlinx.android.synthetic.main.inflate_results_collapsible_textview_layout.view.*


class DayExercisesListAdapter(var context: Context, var dayId: String, var itemType: Int) :
    RecyclerView.Adapter<DayExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
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
        val bindingExercise = exercises[position]
        updateExerciseLayout(holder, bindingExercise)

    }

    private fun updateExerciseLayout(holder: DayExerciseViewHolder, exercise: Exercise) {
        val showDONEButton: Boolean
        if (exercise.exerciseResults.containsResult(dayId)) {
            holder.mainLinearLayout!!.setBackgroundColor(context.resources.getColor(R.color.palette3))
            holder.exerciseTextView!!.setTextColor(Color.WHITE)
            showDONEButton = false
        } else {
            holder.mainLinearLayout!!.setBackgroundColor(context.resources.getColor(R.color.palette3_70))
            holder.exerciseTextView!!.setTextColor(context.resources.getColor(R.color.darkGrey))
            showDONEButton=true
            holder.exerciseDoneButton!!.setOnClickListener(exerciseDoneListener(exercise))
        }

        loadResultsLayout(holder.collapsibleLinearLayout, exercise.exerciseResults)

        holder.exerciseTextView.text = exercise.name
        holder.exerciseTextView.setOnClickListener {
            holder.toggleExpand(exerciseDoneBtnVisibility = showDONEButton)
        }

        holder.exerciseResultButton!!.setOnClickListener {
            goToExerciseResults(exercise)
        }

//        holder.exerciseDoneButton!!.setOnClickListener(exerciseDoneListener(exercise))

        holder.questionBtn!!.setOnClickListener {
            goToExercise(exercise)
        }
    }

    private fun exerciseDoneListener(exercise: Exercise) = View.OnClickListener {
        if (context is WeekViewer) {
            var quickResultMap = ExerciseResults.getQuickResultMap(dayId)
            exercise.exerciseResults.addResult(quickResultMap)
            (context as WeekViewer).updateBlankResult(exercise)
        }
    }

    private fun goToExercise(exercise: Exercise) {

        val intent = Intent(context, ExerciseCreator::class.java)
        intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
        DataHolder.activeExerciseHolder = exercise
        startActivity(context, intent, null)

    }

    private fun loadResultsLayout(
        collapsibleLinearLayout: LinearLayout?,
        exerciseResults: ExerciseResults
    ) {

        collapsibleLinearLayout!!.removeAllViews()
        var resultsMap = exerciseResults.getResultFromDate(dayId)
        if (resultsMap.isEmpty()) {
            collapsibleLinearLayout.addView(
                inflater.inflate(
                    R.layout.inflate_noresults_collapsible_textview_layout,
                    null
                )
            )
        } else {

            for (i in 1 until resultsMap.size) {
                val fieldLayout =
                    inflater.inflate(R.layout.inflate_results_collapsible_textview_layout, null)
                //the reason I use iterator().next() is because when I load resultsmap[i]
                //even though I have a hashmap of size 1, it is still a hashmap of undefined size (and order, thats why i use ints), so to
                //iterate through the entries and get the first one I use that, otherwise could get entries
                val keyString = resultsMap[i]!!.iterator().next().key
                val valueString = resultsMap[i]!!.iterator().next().value

                if (valueString.isEmpty()) {
                    continue
                }

                fieldLayout.fieldKey7.text = keyString
                if (ExerciseResults.isANumericEntry(keyString)) {
                    fieldLayout.fieldValueTextView8.text =
                        ExerciseResults.toReadableFormat(valueString, keyString)
                } else {
                    var stringValue = valueString
                    if (keyString==ExerciseResults.MEDIA_KEY) {
                        stringValue=Utils.getUriFileName(valueString)
                    }
                    fieldLayout.fieldValueTextView8.text = stringValue
                }
                collapsibleLinearLayout.addView(fieldLayout)
            }
        }
    }

    private fun goToExerciseResults(exercise: Exercise) {

        DataHolder.activeExerciseHolder = exercise
        var intent = Intent(context, ResultsCreator::class.java)

        if (exercise.exerciseResults.containsResult(dayId)) {
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
            intent.putExtra(
                ResultsCreator.RESULT_INDEX,
                exercise.exerciseResults.getResultPosition(dayId)
            )
        } else {
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_NEW)
            intent.putExtra(ResultsCreator.RESULTS_DATE, dayId)
        }

        startActivity(context, intent, null)
    }


    internal fun setContent(day: Day?) {
        if (day != null) {
            this.exercises = day.exercises
            this.sDay = day
            notifyDataSetChanged()
        }
    }

}