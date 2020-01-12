package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.ResultSet
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber

class ResultListAdapter(var context: Context) : RecyclerView.Adapter<CollapsibleItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var results = arrayListOf<ResultSet>()
    private var listenerRecyclerView: ClickListenerRecyclerView? = null


    override fun getItemCount(): Int {
        return results.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollapsibleItemViewHolder {
        val itemView =
            inflater.inflate(com.tatoe.mydigicoach.R.layout.item_holder_result, parent, false)
        return CollapsibleItemViewHolder(itemView,listenerRecyclerView,true)
    }

    override fun onBindViewHolder(holder: CollapsibleItemViewHolder, position: Int) {


        if (results.isNotEmpty()) {
                holder.resultDate.text=results[position].getReadableDate()
                if (results[position].sResult!=null) {
                    holder.resultResult.text=results[position].sResult
                    holder.resultResult.visibility=View.GONE
                } else {
                    holder.resultResult.text="nothing written here"
                }
            }


        holder.resultDate.setOnClickListener {
            holder.resultResult.visibility = if (!holder.expanded) View.VISIBLE else View.GONE
            holder.expanded = !holder.expanded
        }
    }

    internal fun setContent(exercise : Exercise) {
        Timber.d("adapter exercise results: ${exercise.exerciseResults}")

        this.results = exercise.exerciseResults.resultsArrayList
        notifyDataSetChanged()
    }

    fun setOnClickInterface (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }
}