package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer
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
import org.w3c.dom.Text
import timber.log.Timber

class BlockCreator : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter
    private lateinit var block: Block
    private lateinit var blockPreviewText: TextView
    private lateinit var blockNameText: EditText

    private var blockString = ""
    private lateinit var currentBlockComponents: MutableList<Exercise>
    private lateinit var allExercises: List<Exercise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_creator)
        title = "Block Creator"
        //todo add init block in classes that require variables to be initialised
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        allExercises = listOf()

        currentBlockComponents = mutableListOf()
        recyclerView = recyclerview as RecyclerView
        blockPreviewText = BlockPreviewText as TextView
        blockNameText = BlockNameText as EditText

        val exerciseSelectorListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)
                Toast.makeText(this@BlockCreator, "$position was clicked", Toast.LENGTH_SHORT).show()
                val clickedExercise = dataViewModel.allExercises.value?.get(position)
                val string = clickedExercise?.name + "\n"
                blockString += string
                blockPreviewText.text = blockString
                currentBlockComponents.add(currentBlockComponents.size, clickedExercise!!)
                Timber.d("block creator exercise list - $currentBlockComponents")
            }
        }

        adapter = ExerciseListAdapter(this, exerciseSelectorListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                Timber.d("PTG all exercises observer triggered: ${exercises.toString()}")
                if (it.isNotEmpty()) {
                    allExercises = it
                    adapter.setExercises(allExercises)
                }
            }
        })

        AddBlockBtn.setOnClickListener(addBlockAction)

    }

    private val addBlockAction = View.OnClickListener {
        val blockTitle = if (blockNameText.text.isNotEmpty()) {
            blockNameText.text.toString()
        } else {
            "Unnamed Block" //todo perhaps set a date
        }
        block = Block(blockTitle,currentBlockComponents)
        dataViewModel.insertBlock(block) //todo IMPORTANT - foreign key fails when saved - seee whats up asd
        Timber.d("${block.name} ${block.components}")
    }

}
