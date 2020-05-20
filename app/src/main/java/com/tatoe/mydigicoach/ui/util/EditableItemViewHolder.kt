package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.item_holder_exercise.view.*

class EditableItemViewHolder(
    var v: View,
    private var listener: ClickListenerRecyclerView?,
    var rightImageHasListener: Boolean = false
) :
    RecyclerView.ViewHolder(v),
    View.OnClickListener, View.OnLongClickListener {

    val itemInfoView: TextView = v.findViewById(R.id.titleTextExerciseHolder)
    private val rightImage: ImageView = v.findViewById(R.id.imageRightExerciseHolder)
    var isChecked = false


    //this class can have 2 usages
    // 1. In a read only mode, where clicking takes you to a exerciseCreator activity where you can delete, update...

    override fun onLongClick(v: View): Boolean {
        listener?.onLongClick(v, adapterPosition)
        return true
    }

    override fun onClick(v: View) {
        if (!rightImageHasListener) {
            listener?.onClick(v, adapterPosition)
        } else {
            listener?.onClick(v, adapterPosition, this)
        }

    }

    fun changeCheckedState(shouldCheck: Boolean, context: Context){
        isChecked = if (shouldCheck) {
            v.imageRightExerciseHolder.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_white_24dp))
            true
        } else {
            v.imageRightExerciseHolder.setImageDrawable(context.resources.getDrawable(R.drawable.ic_circle_grey))
            false
        }
    }

    init {
        if (rightImageHasListener) {
//            secondaryBtn.visibility = View.VISIBLE
            rightImage.setOnClickListener(this)
        } else {
            v.setOnClickListener(this)
            v.setOnLongClickListener(this)
        }
    }

}