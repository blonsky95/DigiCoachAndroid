package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day


class DayContentAdapter(context: Context) : RecyclerView.Adapter<DayItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blocks = emptyList<Block>()

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayItemViewHolder {
        val itemView =
            inflater.inflate(com.tatoe.mydigicoach.R.layout.item_holder_block, parent, false)
        return DayItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayItemViewHolder, position: Int) {
        val current = blocks[position]
        holder.blockName.text = current.name
        holder.blockName.setOnClickListener {
            holder.collapsibleLayout.visibility = if (!holder.expanded) View.VISIBLE else View.GONE
            holder.expanded = !holder.expanded
        }
    }

    internal fun setContent(day: Day?) {
        if (day != null) {
            this.blocks = day.blocks
            notifyDataSetChanged()
        }
    }
}