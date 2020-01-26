package com.tatoe.mydigicoach.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.day.CustomAdapterFragment
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.results.ResultsCreator
import kotlinx.android.synthetic.main.item_day_result.view.*
import timber.log.Timber


class DayContentAdapter(var context: Context, var date: String, var itemType: Int) :
    RecyclerView.Adapter<CollapsibleItemViewHolderDay>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blocks = emptyList<Block>()
    private var exercises = emptyList<Exercise>()

    private var exerciseTextSize: Float = 14.toFloat()

    override fun getItemCount(): Int {
        var size = 0
        when (itemType) {
            CustomAdapterFragment.BLOCK_TYPE_ADAPTER -> size = blocks.size
            CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> size = exercises.size
        }
        return size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollapsibleItemViewHolderDay {

        var itemView = View(context)
        when (itemType) {
            CustomAdapterFragment.BLOCK_TYPE_ADAPTER -> itemView =
                inflater.inflate(R.layout.item_holder_block, parent, false)
            CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> itemView =
                inflater.inflate(R.layout.item_day_result, parent, false)
        }

        return CollapsibleItemViewHolderDay(itemView)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: CollapsibleItemViewHolderDay, position: Int) {

        when (itemType) {
            CustomAdapterFragment.BLOCK_TYPE_ADAPTER -> {
                populateBlocks(holder, position)
            }
            CustomAdapterFragment.EXERCISE_TYPE_ADAPTER -> {
                populateExercises(holder, position)
            }
        }


    }

    private fun populateBlocks(holder: CollapsibleItemViewHolderDay, position: Int) {
        val bindingBlock = blocks[position]
        holder.itemTitle!!.text = bindingBlock.name
        val exercises = bindingBlock.components
        if (exercises.isNotEmpty()) {
            for (exercise in exercises) {
                val inflater2 = LayoutInflater.from(context)
                val exerciseView = inflater2.inflate(R.layout.item_day_result, null)
//                createExerciseTabLayout(CollapsibleItemViewHolderDay(exerciseView),exercise)
                exerciseView.exercise_name.text = exercise.name
                exerciseView.exercise_name.setOnClickListener {
                    viewExerciseInCreator(exercise)
                }
                var xxx = getExerciseResultButtonStateColour(exercise)
                Timber.d("COLOUR EXERCISE BLOCK: $xxx")
                exerciseView.result_button.setBackgroundColor(
                    getExerciseResultButtonStateColour(
                        exercise
                    )
                )

                exerciseView.result_button.setOnClickListener {
                    goToExerciseResults(exercise)
                }
                holder.collapsibleLayout!!.addView(exerciseView)
            }
        } else {
            val exerciseText = TextView(context)
            exerciseText.text = "No exercises in this block"
            exerciseText.setPadding(5, 3, 5, 3)
            exerciseText.setTextSize(TypedValue.COMPLEX_UNIT_SP, exerciseTextSize)

            holder.collapsibleLayout!!.addView(exerciseText)
        }

        holder.itemTitle.setOnClickListener {
            holder.collapsibleLayout!!.visibility =
                if (!holder.expanded!!) View.VISIBLE else View.GONE
            holder.expanded = !holder.expanded!!
        }
    }

    private fun populateExercises(holder: CollapsibleItemViewHolderDay, position: Int = -1) {
        val bindingExercise = exercises[position]
        createExerciseTabLayout(holder,bindingExercise)
    }

    private fun createExerciseTabLayout(holder:CollapsibleItemViewHolderDay, exercise: Exercise) {
        holder.exerciseTextView!!.text = exercise.name
        holder.exerciseTextView.setOnClickListener {
            viewExerciseInCreator(exercise)
        }

        holder.exerciseResultButton!!.setBackgroundColor(
            getExerciseResultButtonStateColour(
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
        Timber.d("go to exercise results 1 : $exercise")

        DataHolder.activeExerciseHolder = exercise
        val intent = Intent(context, ResultsCreator::class.java)

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
        //todo here save the day instance - and use it to get exercises
        if (day != null) {
            this.blocks = day.blocks
            this.exercises = day.exercises
            notifyDataSetChanged()
        }
    }

    //checks if that result already has an entry and returns a different colour to apply to button
    private fun getExerciseResultButtonStateColour(exercise: Exercise): Int {
        var colourInt = R.color.lightBlue
        Timber.d("LIGHT BLUE: $colourInt")

        if (exercise.exerciseResults.containsResult(date)) {
            colourInt = R.color.darkBlue
            Timber.d("DARK BLUE: $colourInt")
        }

        return ContextCompat.getColor(context, colourInt)
    }
}