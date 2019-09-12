package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import timber.log.Timber


class DayContentAdapter(var context: Context) : RecyclerView.Adapter<DayItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blocks = emptyList<Block>()
    private var exerciseTextSize:Float=14.toFloat()

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayItemViewHolder {
        val itemView =
            inflater.inflate(com.tatoe.mydigicoach.R.layout.item_holder_block, parent, false)
        return DayItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayItemViewHolder, position: Int) {

        val bindingBlock = blocks[position]
        holder.blockName.text = bindingBlock.name
        var exercises = bindingBlock.components
        if (exercises.isNotEmpty()) {
            for (exercise in exercises) {
                var exerciseText = TextView(context)
                exerciseText.text = exercise.name
                exerciseText.setPadding(12,12,5,16)
                exerciseText.setTextSize(TypedValue.COMPLEX_UNIT_SP,exerciseTextSize)
                exerciseText.setOnClickListener {
                    viewExerciseInCreator(exercise)
                }
                holder.collapsibleLayout.addView(exerciseText)
            }
        } else {
            var exerciseText = TextView(context)
            exerciseText.text = "No exercises in ths block"
            exerciseText.setPadding(5,3,5,3)
            exerciseText.setTextSize(TypedValue.COMPLEX_UNIT_SP,exerciseTextSize)

            holder.collapsibleLayout.addView(exerciseText)
        }

        holder.blockName.setOnClickListener {
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

    internal fun setContent(day: Day?) {
        if (day != null) {
            Timber.d("darude ${day.dayId}")

            this.blocks = day.blocks
            notifyDataSetChanged()
        }
    }
}