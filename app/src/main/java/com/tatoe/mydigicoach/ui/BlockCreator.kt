package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
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
import com.tatoe.mydigicoach.entity.BlockV2
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter

import kotlinx.android.synthetic.main.activity_block_creator.*
import org.w3c.dom.Text
import timber.log.Timber

class BlockCreator : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter
    private lateinit var blockV2: BlockV2
    private lateinit var blockPreviewText: TextView
    private lateinit var blockNameText: EditText

    lateinit var saveBlockButton: Button
    lateinit var deleteButton: Button

    private var blockString = ""
    private lateinit var currentBlockComponents: ArrayList<Exercise>
    private lateinit var allExercises: List<Exercise>

    lateinit var updatingBlock : BlockV2

    private var BUTTON_ADD = "ADD"
    private var BUTTON_UPDATE = "UPDATE"

    companion object {
        var BLOCK_ACTION = "exercise_action"
        var BLOCK_NEW = "exercise_new"
        var BLOCK_UPDATE = "exercise_update"

        var BLOCK_ID_KEY = "exercise_id"

        var BLOCK_FAIL_RESULT_CODE = 0
        var BLOCK_NEW_RESULT_CODE = 1
        var BLOCK_UPDATE_RESULT_CODE = 2
        var BLOCK_DELETE_RESULT_CODE = 3

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_creator)
        title = "Block Creator"
        //todo add init block in classes that require variables to be initialised
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        allExercises = listOf()

        currentBlockComponents = arrayListOf()
        recyclerView = recyclerview as RecyclerView
        blockPreviewText = BlockPreviewText as TextView
        blockNameText = BlockNameText as EditText
        saveBlockButton = AddBlockBtn as Button
        deleteButton = delete_button as Button

        if (intent.hasExtra(BLOCK_ACTION)) {
            var action = intent.getStringExtra(BLOCK_ACTION)

            when (action) {
                BLOCK_NEW -> modifyUI(BUTTON_ADD)
                BLOCK_UPDATE -> modifyUI(BUTTON_UPDATE)
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

    }

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
            Timber.d("text being displayed: $blockString")
        }
    }

    private fun modifyUI(buttonText: String) {

        saveBlockButton.text = buttonText
        if (buttonText == BUTTON_ADD) {
            deleteButton.visibility = View.GONE
            saveBlockButton.setOnClickListener(addButtonListener)
        } else {
            saveBlockButton.setOnClickListener(updateButtonListener)
//            deleteButton.setOnClickListener(deleteButtonListener)

            updatingBlock = DataHolder.activeBlockHolder
            currentBlockComponents = updatingBlock.components
            for (exercise in currentBlockComponents) {
                exercise.let { blockString += "${exercise.name}\n" }
            }
            //todo update UI
        }

        blockNameText.text = SpannableStringBuilder(updatingBlock.name)
        blockPreviewText.text=SpannableStringBuilder(blockString)
    }



    private val addButtonListener = View.OnClickListener {
        val blockTitle = if (blockNameText.text.isNotEmpty()) {
            blockNameText.text.toString()
        } else {
            "Unnamed Block" //todo perhaps set a date
        }
        blockV2 = BlockV2(blockTitle,currentBlockComponents)
//        dataViewModel.insertBlock(blockV2.toBlock())
        Timber.d("${blockV2.name} ${blockV2.components}")

        DataHolder.newBlockHolder = blockV2

        var replyIntent = Intent()
//

        if (blockV2.name.isEmpty()) {
            setResult(BLOCK_FAIL_RESULT_CODE, replyIntent)
        } else {
            setResult(BLOCK_NEW_RESULT_CODE, replyIntent)
        }
        finish()
    }

    private val updateButtonListener = View.OnClickListener {

        updatingBlock.name = blockNameText.text.trim().toString()
        updatingBlock.components = currentBlockComponents
        DataHolder.activeBlockHolder = updatingBlock
        var replyIntent = Intent()

        Timber.d("update currentBlock - built: ${updatingBlock.blockPrimaryKeyId} ${updatingBlock.name} ${updatingBlock.components} ")


        if (blockNameText.text.trim().toString().isEmpty()) {
            setResult(BLOCK_FAIL_RESULT_CODE, replyIntent)
        } else {
            setResult(BLOCK_UPDATE_RESULT_CODE, replyIntent)
        }
        finish()
    }

    private val deleteButtonListener = View.OnClickListener {

        DataHolder.activeBlockHolder = updatingBlock
        var replyIntent = Intent()

        Timber.d("delete currentBlock - built: ${updatingBlock.blockPrimaryKeyId} ${updatingBlock.name} ${updatingBlock.components} ")

        setResult(BLOCK_DELETE_RESULT_CODE, replyIntent)

        finish()
    }

}
