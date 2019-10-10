package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import timber.log.Timber

class BlockListAdapter(context: Context, var deletableItems:Boolean = false) :
    RecyclerView.Adapter<EditableItemViewHolder>() {

    //this adapter doesnt take the listener as a parameter, instead it is set from the invoking
    //activity with the setListener method, where do we want the listeners?

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var blocks = emptyList<Block>()
    private var listenerRecyclerView: ClickListenerRecyclerView? = null

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditableItemViewHolder {
        val itemView = inflater.inflate(R.layout.item_holder_exercise, parent, false)
        return EditableItemViewHolder(itemView,
            listenerRecyclerView, deletableItems)
    }

    override fun onBindViewHolder(holder: EditableItemViewHolder, position: Int) {
        val current = blocks[position]
        val textString = current.name
        holder.itemInfoView.text = textString
    }

    internal fun setBlocks(blocks: List<Block>?) {

        if (blocks!=null) {
            this.blocks = blocks
            Timber.d("blocks updated in blocklist adapter 2 : $blocks")
            notifyDataSetChanged()
        }
    }

    fun setListener (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }

}