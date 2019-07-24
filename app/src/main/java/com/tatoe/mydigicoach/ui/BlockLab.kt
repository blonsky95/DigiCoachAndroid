package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_block_lab.*
import timber.log.Timber

class BlockLab : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_lab)
        title = "Block Lab"

        val recyclerView = recyclerview as RecyclerView
        val adapter = ExerciseListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let { adapter.setExercises(it) }
            Timber.d("all exercises: ${exercises.toString()}")
        })

        //todo display list of exercises - modify the method for retrieving all the exercises
        //todo check recycler view/adapter doc
        //todo check the let syntax thing

        // add the delete as well

        button4.setOnClickListener {
            Timber.d("block lab --> Exercise lab")

            var intent = Intent(this, ExerciseLab::class.java)
            startActivity(intent)
        }
    }
}
