package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise

class BlockListAdapter(context: Context, private var listenerRecyclerView: ClickListenerRecyclerView) :
    RecyclerView.Adapter<ItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blocks = emptyList<Block>() // Cached copy of words

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.recycler_view_exercise, parent, false)
        return ItemViewHolder(itemView, listenerRecyclerView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = blocks[position]
        val textString = "${current.blockId} ${current.name}"
        holder.exerciseItemView.text = textString
    }

    internal fun setBlocks(blocks: List<Block>) {
        this.blocks = blocks
        notifyDataSetChanged()
    }

}