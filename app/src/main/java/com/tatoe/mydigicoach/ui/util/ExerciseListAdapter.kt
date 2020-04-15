package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import kotlinx.android.synthetic.main.item_holder_exercise.view.*

class ExerciseListAdapter(var context: Context, var layout:Int=0, var rightImageHasListener:Boolean = false) :
    RecyclerView.Adapter<EditableItemViewHolder>() {

    // use the diffutil  https://medium.com/@iammert/using-diffutil-in-android-recyclerview-bdca8e4fbb00

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises = emptyList<Exercise>() // Cached copy of words
    private var selectedIndexes = arrayListOf<Int>()
    private var listenerRecyclerView: ClickListenerRecyclerView? = null

    var backgroundColor = context.resources.getColor(R.color.white)
    var imageLeftVisibility = View.VISIBLE
    var imageRightVisibility = View.GONE
    var textColor = context.resources.getColor(R.color.textBlue)

    companion object{
        var DEFAULT_LAYOUT=0
    }


    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditableItemViewHolder {
        if (layout==DEFAULT_LAYOUT) {
            layout=R.layout.item_holder_exercise
        }
        val itemView = inflater.inflate(layout, parent, false)
        itemView.linearLayoutExerciseHolder.setBackgroundColor(backgroundColor)
        itemView.imageLeftExerciseHolder.visibility=imageLeftVisibility
        itemView.imageRightExerciseHolder.visibility=imageRightVisibility
        itemView.titleTextExerciseHolder.setTextColor(textColor)

        return EditableItemViewHolder(itemView, listenerRecyclerView, rightImageHasListener) //if deletable Items, then will assign the listener to the delete button (plus make it visible)
    }

    override fun onBindViewHolder(holder: EditableItemViewHolder, position: Int) {
        val current = exercises[position]
        val textString = current.name
        if (selectedIndexes.contains(position)){
            holder.changeCheckedState(true,context)
        } else {
            holder.changeCheckedState(false,context)

        }
        holder.itemInfoView.text = textString
    }

    internal fun setExercises(exercises: List<Exercise>) {

        this.exercises = exercises
        notifyDataSetChanged()
    }

    internal fun setSelectableExercises(exercises: List<Exercise>, selectedIndexes:ArrayList<Int>) {

        this.exercises = exercises
        this.selectedIndexes = selectedIndexes
        notifyDataSetChanged()
    }

    fun setOnClickInterface (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }


}