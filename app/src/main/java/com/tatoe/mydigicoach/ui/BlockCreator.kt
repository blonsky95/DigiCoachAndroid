package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter

import kotlinx.android.synthetic.main.activity_block_creator.*
import timber.log.Timber

class BlockCreator : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter
    private lateinit var block: Block
    private lateinit var blockPreviewText: TextView
    private var blockString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_creator)
        title = "Block Creator"

        recyclerView = recyclerview as RecyclerView

        val exerciseSelectorListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)
                //todo create an empty exercise array and add clicked exercises so block can be created
                Toast.makeText(this@BlockCreator, "$position was clicked", Toast.LENGTH_SHORT).show()
                val clickedExercise = dataViewModel.allExercises.value?.get(position)
                val string = clickedExercise?.name + "/n"
                blockString+=string
                blockPreviewText.text = blockString
            }
        }

        adapter = ExerciseListAdapter(this, exerciseSelectorListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        Timber.d("${dataViewModel.allExercises.value}")
        //todo FIRST check why this datamodel doesnt have allExercises (do i have to observe? or different instance? but accesing same repository so whaaat
//        adapter.setExercises(dataViewModel.allExercises.value!!) //check for exclamation null safety here


    }

}
