package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import timber.log.Timber


class DayContentAdapter(var context: Context) : RecyclerView.Adapter<DayItemViewHolder>() {

    //todo MYSTERY FUCKING SOLVED - now find out what that grey layout shit is

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

        //1. create as many collapsible layouts as there is blocks
        //2. create as many items in the collapsible layout as exercises with block

        // - create an exercise text view - day_block_exercise_calendar - which will fill with an exercise name
        // - create a collapsible layout with the block name - day_block_calendar - so a Vertical linear layout
        // of a textview and another vert. layout (which will be filled, and which visibility will vary)
        val bindingBlock = blocks[position]

        holder.blockName.text = bindingBlock.name
        Timber.d("darude $position blockname: ${holder.blockName.text}")

        var exercises = bindingBlock.components
        if (exercises.isNotEmpty()) {
            for (exercise in exercises) {
                var exerciseText = TextView(context)
                exerciseText.text = exercise.name
                exerciseText.setPadding(5,3,5,3)
                exerciseText.setTextSize(TypedValue.COMPLEX_UNIT_SP,exerciseTextSize)

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

    internal fun setContent(day: Day?) {
        if (day != null) {
            Timber.d("darude ${day.dayId}")

            this.blocks = day.blocks
            notifyDataSetChanged()
        }
    }
}