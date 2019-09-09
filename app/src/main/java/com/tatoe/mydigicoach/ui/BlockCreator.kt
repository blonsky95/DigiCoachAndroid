package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter

import kotlinx.android.synthetic.main.activity_block_creator.*
import timber.log.Timber

class BlockCreator : AppCompatActivity() {

    //todo set up junit tests to stop having to create exercises and shit

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewV2: RecyclerView
    private lateinit var adapterExercises: ExerciseListAdapter
    private lateinit var adapterDeletableExercises: ExerciseListAdapter

    private lateinit var block: Block
    //    private lateinit var blockPreviewText: TextView
    private lateinit var blockNameText: EditText

    lateinit var saveBlockButton: Button
    lateinit var deleteButton: Button

    //    private var blockString = ""
    private lateinit var currentBlockComponents: ArrayList<Exercise>
    private lateinit var allExercises: List<Exercise>

    lateinit var updatingBlock: Block

    private var BUTTON_ADD = "ADD"
    private var BUTTON_UPDATE = "UPDATE"

    companion object {
        var BLOCK_ACTION = "block_action"
        var BLOCK_NEW = "block_new"
        var BLOCK_UPDATE = "block_update"

        var BLOCK_FAIL_RESULT_CODE = 0
        var BLOCK_NEW_RESULT_CODE = 1
        var BLOCK_UPDATE_RESULT_CODE = 2
        var BLOCK_DELETE_RESULT_CODE = 3

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_creator)
        title = "Block Creator"

        setSupportActionBar(findViewById(R.id.my_toolbar))

        // add init block in classes that require variables to be initialised
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        allExercises = listOf()

        currentBlockComponents = arrayListOf()
        recyclerView = recyclerview as RecyclerView
        recyclerViewV2 = CurrentBlockDisplay as RecyclerView
//        blockPreviewText = BlockPreviewText as TextView
        blockNameText = BlockNameText as EditText
        saveBlockButton = AddBlockBtn as Button
        deleteButton = delete_button as Button

        adapterExercises = ExerciseListAdapter(this, exerciseSelectorListener)
        recyclerView.adapter = adapterExercises
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapterDeletableExercises = ExerciseListAdapter(this, itemDeletableListener, true)
        recyclerViewV2.adapter = adapterDeletableExercises
        recyclerViewV2.layoutManager = LinearLayoutManager(this)

        if (intent.hasExtra(BLOCK_ACTION)) {
            var action = intent.getStringExtra(BLOCK_ACTION)

            when (action) {
                BLOCK_NEW -> modifyUI(BUTTON_ADD)
                BLOCK_UPDATE -> modifyUI(BUTTON_UPDATE)
            }
        }

        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                Timber.d("PTG all exercises observer triggered: ${exercises.toString()}")
                if (it.isNotEmpty()) {
                    allExercises = it
                    adapterExercises.setExercises(allExercises)
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.creator_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_back -> {
            super.onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun modifyUI(buttonText: String) {

        var namePreviewEText = ""

        saveBlockButton.text = buttonText
        if (buttonText == BUTTON_ADD) {
            deleteButton.visibility = View.GONE
            saveBlockButton.setOnClickListener(addButtonListener)
        } else {
            saveBlockButton.setOnClickListener(updateButtonListener)
            deleteButton.setOnClickListener(deleteButtonListener)

            updatingBlock = DataHolder.activeBlockHolder
            currentBlockComponents = updatingBlock.components
            namePreviewEText = updatingBlock.name
        }

        blockNameText.text = SpannableStringBuilder(namePreviewEText)
        adapterDeletableExercises.setExercises(currentBlockComponents)
    }


    val itemDeletableListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            val clickedExercise = currentBlockComponents[position]
            var removedSuccess = currentBlockComponents.remove(clickedExercise)
            Timber.d("removal success $removedSuccess")

            adapterDeletableExercises.setExercises(currentBlockComponents)
            Timber.d("block creator exercise list after removal - $currentBlockComponents")
        }
    }

    val exerciseSelectorListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            Toast.makeText(this@BlockCreator, "$position was clicked", Toast.LENGTH_SHORT).show()
            val clickedExercise = dataViewModel.allExercises.value?.get(position)
            currentBlockComponents.add(currentBlockComponents.size, clickedExercise!!)
            adapterDeletableExercises.setExercises(currentBlockComponents)
            Timber.d("block creator exercise list after addition - $currentBlockComponents")
        }
    }

    private val addButtonListener = View.OnClickListener {
        val blockTitle = if (blockNameText.text.isNotEmpty()) {
            blockNameText.text.toString()
        } else {
            "Unnamed Block"
        }
        block = Block(blockTitle, currentBlockComponents)
        dataViewModel.insertBlock(block)
//        Timber.d("${block.name} ${block.components}")
//
//        DataHolder.newBlockHolder = block
//
//        var replyIntent = Intent()
////
//
//        if (block.name.isEmpty()) {
//            setResult(BLOCK_FAIL_RESULT_CODE, replyIntent)
//        } else {
//            setResult(BLOCK_NEW_RESULT_CODE, replyIntent)
//        }
        backToViewer()
    }

    private val updateButtonListener = View.OnClickListener {

        updatingBlock.name = blockNameText.text.trim().toString()
        updatingBlock.components = currentBlockComponents
        dataViewModel.updateBlock(updatingBlock)

//        DataHolder.activeBlockHolder = updatingBlock
//        var replyIntent = Intent()
//
//        Timber.d("update currentBlock - built: ${updatingBlock.blockId} ${updatingBlock.name} ${updatingBlock.components} ")
//
//
//        if (blockNameText.text.trim().toString().isEmpty()) {
//            setResult(BLOCK_FAIL_RESULT_CODE, replyIntent)
//        } else {
//            setResult(BLOCK_UPDATE_RESULT_CODE, replyIntent)
//        }
        backToViewer()
    }

    private val deleteButtonListener = View.OnClickListener {
        dataViewModel.deleteBlock(updatingBlock)

//        DataHolder.activeBlockHolder = updatingBlock
//        var replyIntent = Intent()
//
//        Timber.d("delete currentBlock - built: ${updatingBlock.blockId} ${updatingBlock.name} ${updatingBlock.components} ")
//
//        setResult(BLOCK_DELETE_RESULT_CODE, replyIntent)

        backToViewer()
    }

    private fun backToViewer() {
        val intent = Intent(this, BlockViewer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}
