package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.BlockV2ListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import kotlinx.android.synthetic.main.activity_exercise_viewer.ifEmptyText
import kotlinx.android.synthetic.main.activity_exercise_viewer.recyclerview
import kotlinx.android.synthetic.main.dialog_window_info.view.*
import timber.log.Timber

class Library : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockV2ListAdapter
    private lateinit var dataViewModel: DataViewModel

    private lateinit var blockActionHandler: ClickListenerRecyclerView

    private var userBlockList: List<Block> = listOf()
    private var appBlockList: List<Block> = listOf()
    private var importBlockList: List<Block> = listOf()
    private var exportBlockList: List<Block> = listOf()

    private var activeBlockList: List<Block> = mutableListOf()

    private var loadDefaultBlockList = true //so display user blocks
    private var blockNeedsInserting = false
    lateinit var blockToBeUpdated: Block
    private var allExercises = listOf<Exercise>()
//    companion object {
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        title = "Library"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        recyclerView = recyclerview as RecyclerView

        initAdapterListeners()

        adapter = BlockV2ListAdapter(this)
        adapter.setOnClickInterface(blockActionHandler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

//        activeBlockList = userBlockList

        initObservers()

    }

    private fun initObservers() {
        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises.let {
                allExercises = it
                //the following is run when a premade block has been inserted and needs non-IDied
                //exercises to be swapped for exercises with ID
                if (blockNeedsInserting){
                    insertBlockReExercises()
                }
            }
        })
        dataViewModel.allUserBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                userBlockList = it
                if (loadDefaultBlockList) {
                    activeBlockList = userBlockList
                    displayAdapter(activeBlockList, "You haven't created any blocks")
                    loadDefaultBlockList = false
                }

                //use this for first time into app, might have to be placed somewhere else
            }
        })
        dataViewModel.allAppBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                Timber.d("app blocks has been updated")
                //todo check this block of comment
                //when you delete the last user made block the adapter doesnt load the ifemptytext, but an empty adapter until you reopen Library
                appBlockList = it
                Timber.d("app blocks observer updated to: $appBlockList")

            }
        })
        dataViewModel.allImportBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                importBlockList = it
            }
        })
        dataViewModel.allExportBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                exportBlockList = it
            }
        })
    }

    private fun insertBlockReExercises() {
        for (i in 0 until blockToBeUpdated.components.size) {
            //match it with list of all exercises and swap it round
            for (existingExercise in allExercises) {
                if (blockToBeUpdated.components[i].name==existingExercise.name&&blockToBeUpdated.components[i].description==existingExercise.description){
                    Timber.d("HAS BEEN SWAPPED ${blockToBeUpdated.components[i]} for $existingExercise")
                    blockToBeUpdated.components[i]=existingExercise
                }

            }
        }
        dataViewModel.insertBlock(Block(blockToBeUpdated, Block.USER_GENERATED))
        Toast.makeText(
            this,
            "${blockToBeUpdated.name} has been added to your blocks",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun displayAdapter(blocks: List<Block>, ifEmptyString: String = "") {
        if (blocks.isEmpty()) {
            ifEmptyText.visibility = View.VISIBLE
            ifEmptyText.text = ifEmptyString
            recyclerView.visibility = View.GONE
        } else {
            ifEmptyText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.loadBlocks(blocks)
        }
    }

    private fun initAdapterListeners() {
        blockActionHandler = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int, actionId: Int) {
                super.onClick(view, position, actionId)

                when (actionId) {
                    R.id.import_btn -> importBlock(position)
                    R.id.export_btn -> sendBlock(position)
                    R.id.delete_btn -> deleteBlock(position)
                }

            }
        }
    }

    private fun importBlock(position: Int) {
        var blockToImport = activeBlockList[position]
        blockToBeUpdated = activeBlockList[position]
        if (blockToImport.type != Block.USER_GENERATED || blockToImport.type != Block.EXPORT) {

            //import exercises of the block
            var i =1
            for (exercise in blockToImport.components) {
                Timber.d("about to insert $exercise")
                dataViewModel.insertExercise(exercise)
                //this is some dirty shit
                if (i==blockToImport.components.size) {
                    blockNeedsInserting = true
                } else {
                    i++
                }

            }
            Toast.makeText(
                this,
                "${blockToImport.components.size} new exercises have been added",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(this, "You already have this block", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteBlock(position: Int) {
        val blockToDelete = activeBlockList[position]

        if (blockToDelete.type != Block.APP_PREMADE) {
            askExerciseDeletion(blockToDelete, position)
//            dataViewModel.deleteBlock(blockToDelete)

        } else {
            Toast.makeText(this, "Pre made blocks cant be deleted", Toast.LENGTH_SHORT).show()
        }

    }

    private fun askExerciseDeletion(blockToDelete: Block, position: Int) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_window_info, null)
        mDialogView.item_description.text = "Do you want to delete the exercises in this block too?"
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setTitle(title)
        mBuilder.setPositiveButton("Yes") { _, _ ->
            dataViewModel.deleteBlock(blockToDelete, true)
            (activeBlockList as MutableList).removeAt(position)
            displayAdapter(activeBlockList)
        }
        mBuilder.setNegativeButton("No") { _, _ ->
            dataViewModel.deleteBlock(blockToDelete, false)
            (activeBlockList as MutableList).removeAt(position)
            displayAdapter(activeBlockList)
        }
        mBuilder.show()
    }


    private fun sendBlock(position: Int) {
        Timber.d("send at position: $position")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.library_toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home -> {
            onBackPressed()
            true
        }

        R.id.user_blocks -> {
            displayAdapter(userBlockList, "You haven't created any blocks")
            activeBlockList = userBlockList
            true
        }
        R.id.app_blocks -> {
            displayAdapter(appBlockList, "You broke the system")
            activeBlockList = appBlockList
            true
        }
        R.id.import_blocks -> {
            displayAdapter(importBlockList, "Import blocks/exercises to see them here")
            activeBlockList = importBlockList
            true
        }
        R.id.export_blocks -> {
            displayAdapter(exportBlockList, "Export blocks/exercises to see them here")
            activeBlockList = exportBlockList
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}