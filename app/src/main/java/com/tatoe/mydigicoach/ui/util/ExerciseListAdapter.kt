package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise

class ExerciseListAdapter(context: Context, var deletableItems:Boolean = false) :
    RecyclerView.Adapter<EditableItemViewHolder>() {

    // use the diffutil  https://medium.com/@iammert/using-diffutil-in-android-recyclerview-bdca8e4fbb00

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises = emptyList<Exercise>() // Cached copy of words
    private var listenerRecyclerView: ClickListenerRecyclerView? = null


    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditableItemViewHolder {
        val itemView = inflater.inflate(R.layout.item_holder_exercise, parent, false)
        return EditableItemViewHolder(itemView, listenerRecyclerView, deletableItems) //if deletable Items, then will assign the listener to the delete button (plus make it visible)
    }

    override fun onBindViewHolder(holder: EditableItemViewHolder, position: Int) {
        val current = exercises[position]
        val textString = current.name
        holder.itemInfoView.text = textString
    }

    internal fun setExercises(exercises: List<Exercise>) {

        this.exercises = exercises
        notifyDataSetChanged()
    }

    fun setListener (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }

}