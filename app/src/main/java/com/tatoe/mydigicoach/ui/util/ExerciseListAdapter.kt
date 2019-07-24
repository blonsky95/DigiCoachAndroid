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
import com.tatoe.mydigicoach.ui.ExerciseLab
import timber.log.Timber

class ExerciseListAdapter(var context: Context) : RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises = emptyList<Exercise>() // Cached copy of words

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = inflater.inflate(R.layout.recycler_view_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val current = exercises[position]
        holder.exerciseItemView.text = current.name
        holder.exerciseView.setOnClickListener(MyClickListener(current))
        holder.exerciseView.setOnLongClickListener(MyClickListener(current))
        //todo add the onclicklistener to this holder using interface or inheriting class or something
    }

    internal fun setExercises(exercises: List<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(singleItemView: View) : RecyclerView.ViewHolder(singleItemView) {

        val exerciseItemView: TextView = singleItemView.findViewById(R.id.textView)
        val exerciseView: View = singleItemView

    }

    inner class MyClickListener(var exercise: Exercise) : View.OnClickListener, View.OnLongClickListener {

        override fun onClick(v: View?) {
            val intent = Intent(context, ExerciseLab::class.java)
            intent.putExtra(ExerciseLab.EXERCISE_ACTION, ExerciseLab.EXERCISE_UPDATE)
            intent.putExtra(ExerciseLab.EXERCISE_NAME_KEY, exercise.name)
            intent.putExtra(ExerciseLab.EXERCISE_DESCRIPTION_KEY, exercise.description)
            Timber.d("on click list item - View exercise: ${exercise.name}")
            context.startActivity(intent)
        }

        override fun onLongClick(v: View?): Boolean {
            Timber.d("on long click list item - View exercise: ${exercise.name}")
            return true
        }

    }
}