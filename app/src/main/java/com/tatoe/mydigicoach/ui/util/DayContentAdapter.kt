package com.tatoe.mydigicoach.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.exercise.ExerciseResults
import kotlinx.android.synthetic.main.item_day_result.view.*
import timber.log.Timber


class DayContentAdapter(var context: Context, var date:String) : RecyclerView.Adapter<CollapsibleItemViewHolderDay>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blocks = emptyList<Block>()
    private var exerciseTextSize:Float=14.toFloat()

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollapsibleItemViewHolderDay {
        val itemView =
            inflater.inflate(R.layout.item_holder_block, parent, false)
        return CollapsibleItemViewHolderDay(itemView)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: CollapsibleItemViewHolderDay, position: Int) {

        val bindingBlock = blocks[position]
        holder.itemTitle.text = bindingBlock.name
        val exercises = bindingBlock.components
        if (exercises.isNotEmpty()) {
            for (exercise in exercises) {
                val inflater2 = LayoutInflater.from(context)
                val exerciseView = inflater2.inflate(R.layout.item_day_result,null)
                exerciseView.exercise_name.text=exercise.name
                exerciseView.exercise_name.setOnClickListener{
                    viewExerciseInCreator(exercise)
                }
                exerciseView.result_button.setOnClickListener {
                    goToExerciseResults(exercise)
                }
                holder.collapsibleLayout.addView(exerciseView)
            }
        } else {
            var exerciseText = TextView(context)
            exerciseText.text = "No exercises in ths block"
            exerciseText.setPadding(5,3,5,3)
            exerciseText.setTextSize(TypedValue.COMPLEX_UNIT_SP,exerciseTextSize)

            holder.collapsibleLayout.addView(exerciseText)
        }

        holder.itemTitle.setOnClickListener {
            holder.collapsibleLayout.visibility = if (!holder.expanded) View.VISIBLE else View.GONE
            holder.expanded = !holder.expanded
        }
    }

    private fun viewExerciseInCreator(exercise: Exercise) {
        DataHolder.activeExerciseHolder = exercise
        val intent = Intent(context, ExerciseCreator::class.java)
        intent.putExtra(ExerciseCreator.EXERCISE_ACTION, ExerciseCreator.EXERCISE_VIEW)
        startActivity(context,intent, null)
    }

    private fun goToExerciseResults(exercise: Exercise) {
        Timber.d("go to exercise results 1 : $exercise")

//        DataHolder.activeExerciseHolder = exercise
        val intent = Intent(context, ExerciseResults::class.java)
        intent.putExtra(ExerciseResults.RESULTS_ACTION, ExerciseResults.RESULTS_ADD)
        intent.putExtra(ExerciseResults.RESULTS_DATE, date)
        intent.putExtra(ExerciseResults.RESULTS_EXE_ID, exercise.exerciseId)


        startActivity(context,intent, null)
    }

    internal fun setContent(day: Day?) {
        if (day != null) {
//            Timber.d("darude ${day.dayId}")

            this.blocks = day.blocks
            notifyDataSetChanged()
        }
    }
}