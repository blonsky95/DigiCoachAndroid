package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.ExerciseResults
import timber.log.Timber

class ResultListAdapter(var context: Context) : RecyclerView.Adapter<CollapsibleItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var sResults = arrayListOf<HashMap<Int, Pair<String, String>>>()
    private var listenerRecyclerView: ClickListenerRecyclerView? = null


    override fun getItemCount(): Int {
        return sResults.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollapsibleItemViewHolder {
        val itemView =
            inflater.inflate(com.tatoe.mydigicoach.R.layout.item_holder_result, parent, false)
        return CollapsibleItemViewHolder(itemView,listenerRecyclerView,true)
    }

    override fun onBindViewHolder(holder: CollapsibleItemViewHolder, position: Int) {


        if (sResults.isNotEmpty()) {
                holder.resultDate.text=ExerciseResults.getReadableDate(ExerciseResults.stringToDate(sResults[position][0]!!.second)) //0 is date
                if (sResults[position][0]!!.second!=null) {
                    holder.resultResult.text=sResults[position][1]!!.second //1 is note
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

    internal fun setContent(results : ArrayList<HashMap<Int, Pair<String, String>>>) {
        this.sResults = results
        notifyDataSetChanged()
    }

    fun setOnClickInterface (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }
}