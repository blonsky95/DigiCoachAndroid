package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise

class ExerciseListAdapter(context: Context, private var listenerRecyclerView: ClickListenerRecyclerView) :
    RecyclerView.Adapter<ItemViewHolder>() {

    // use the diffutil  https://medium.com/@iammert/using-diffutil-in-android-recyclerview-bdca8e4fbb00

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises = emptyList<Exercise>() // Cached copy of words

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.recycler_view_exercise, parent, false)
        return ItemViewHolder(itemView, listenerRecyclerView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = exercises[position]
        val textString = "${current.exerciseId} ${current.name}"
        holder.exerciseItemView.text = textString
    }

    internal fun setExercises(exercises: List<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }

}