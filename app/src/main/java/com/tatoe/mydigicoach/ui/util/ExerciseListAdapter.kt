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
        //todo add the onclicklistener to this holder using interface or inheriting class or something
    }

    internal fun setExercises(exercises: List<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(singleItemView: View) : RecyclerView.ViewHolder(singleItemView) {


        val exerciseItemView: TextView = singleItemView.findViewById(R.id.textView)

    }

    open inner class MyClickListener (var exercise: Exercise):View.OnClickListener,View.OnLongClickListener{

        override fun onClick(v: View?) {
            val intent = Intent(context, ExerciseLab::class.java)
            intent.putExtra("exercise_name", exercise.name)
            intent.putExtra("exercise_desc", exercise.description)
            Timber.d("View exercise: ${exercise.name}")
            context.startActivity(intent)        }

        override fun onLongClick(v: View?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}