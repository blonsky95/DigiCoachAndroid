package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.BlockLab
import com.tatoe.mydigicoach.ui.ExerciseLab
import timber.log.Timber

class ExerciseListAdapter(context: Context, var listenerRecyclerView: ClickListenerRecyclerView) :
    RecyclerView.Adapter<ExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises = emptyList<Exercise>() // Cached copy of words

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = inflater.inflate(R.layout.recycler_view_item, parent, false)
        return ExerciseViewHolder(itemView,listenerRecyclerView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val current = exercises[position]
        val textString = "${current.exerciseId} ${current.name}"
        holder.exerciseItemView.text = textString
    }

    internal fun setExercises(exercises: List<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }

}