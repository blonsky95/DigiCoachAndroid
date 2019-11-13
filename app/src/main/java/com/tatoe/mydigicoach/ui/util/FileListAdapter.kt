package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import timber.log.Timber
import java.io.File

class FileListAdapter(context: Context) :
    RecyclerView.Adapter<FileViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var filesList = emptyList<File>() // Cached copy of words
    private var listenerRecyclerView: ClickListenerRecyclerView? = null

    override fun getItemCount(): Int {
        return filesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = inflater.inflate(R.layout.item_holder_files, parent, false)
        return FileViewHolder(itemView, listenerRecyclerView) //if deletable Items, then will assign the listener to the delete button (plus make it visible)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val current = filesList[position]
        //todo I LEFT IT HERE - check this timber when running - check fileslist size?
        Timber.d("current file being added to adapter: $current")
        holder.fileNameTextView.text=current.name
    }

    internal fun loadFiles(files: List<File>) {
        this.filesList = files
        notifyDataSetChanged()
    }

    fun setOnClickInterface (listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView=listener
    }

}