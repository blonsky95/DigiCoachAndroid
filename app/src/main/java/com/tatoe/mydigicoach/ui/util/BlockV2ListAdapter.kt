package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import kotlinx.android.synthetic.main.item_block_sub_exercise.view.*

class BlockV2ListAdapter(var context: Context) :
    RecyclerView.Adapter<BlockV2FileViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blockList = emptyList<Block>() // Cached copy of words
    private var listenerRecyclerView: ClickListenerRecyclerView? = null

    override fun getItemCount(): Int {
        return blockList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockV2FileViewHolder {
        val itemView = inflater.inflate(R.layout.item_holder_block_two, parent, false)
        return BlockV2FileViewHolder(itemView, listenerRecyclerView) //if deletable Items, then will assign the listener to the delete button (plus make it visible)
    }

    override fun onBindViewHolder(holder: BlockV2FileViewHolder, position: Int) {

        val currentBlock = blockList[position]
        holder.fileNameTextView.text=currentBlock.name

        val exercises = currentBlock.components

        //reset collapsible layout views and expanded state
        holder.collapsibleLayout.removeAllViews()
        holder.collapsibleLayout.visibility=View.GONE
        holder.expanded=false

        if (exercises.isNotEmpty()) {
            for (exercise in exercises) {
                val exerciseListInflater = LayoutInflater.from(context)
                val exerciseListView = exerciseListInflater.inflate(R.layout.item_block_sub_exercise, null)
                exerciseListView.exercise_name.text = exercise.name
//                if (exerciseListView.parent!=null){
//                    (exerciseListView.parent as ViewGroup).removeView(exerciseListView)
//                }
                holder.collapsibleLayout.addView(exerciseListView)
            }
        } else {
            val exerciseListInflater = LayoutInflater.from(context)
            val exerciseListView = exerciseListInflater.inflate(R.layout.item_block_sub_exercise, null)
            exerciseListView.exercise_name.text = "No exercises"
            holder.collapsibleLayout.addView(exerciseListView)
        }

        holder.fileNameTextView.setOnClickListener {
            holder.collapsibleLayout.visibility =
                if (!holder.expanded!!) View.VISIBLE else View.GONE
            holder.expanded = !holder.expanded!!
        }

    }

    internal fun loadBlocks(blocks: List<Block>) {
        this.blockList = blocks
        notifyDataSetChanged()
    }

    fun setOnClickInterface (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }

}