package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.BlockV2ListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import kotlinx.android.synthetic.main.activity_exercise_viewer.ifEmptyText
import kotlinx.android.synthetic.main.activity_exercise_viewer.recyclerview
import timber.log.Timber

class Library : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockV2ListAdapter
    private lateinit var dataViewModel: DataViewModel

    private lateinit var blockActionHandler: ClickListenerRecyclerView

    //    private var userBlockList: List<Block> = listOf()
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
                if (blockNeedsInserting) {
                    insertBlockReExercises()
                }
            }
        })

        dataViewModel.allAppBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                Timber.d("app blocks has been updated")
                if (loadDefaultBlockList) {
                    activeBlockList = appBlockList
                    displayAdapter(activeBlockList, "you broke the system")
                    loadDefaultBlockList = false
                }
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
                if (blockToBeUpdated.components[i].name == existingExercise.name && blockToBeUpdated.components[i].description == existingExercise.description) {
                    Timber.d("HAS BEEN SWAPPED ${blockToBeUpdated.components[i]} for $existingExercise")
                    blockToBeUpdated.components[i] = existingExercise
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

        Utils.getInfoDialogView(
            this,
            "Import Block",
            "Are you sure you want to import this block?",
            object :
                DialogPositiveNegativeHandler {

                override fun onPositiveButton(editTextText: String) {
                    super.onPositiveButton(editTextText)
                    val blockToImport = activeBlockList[position]
                    blockToBeUpdated = activeBlockList[position]

                    //import exercises of the block
                    var i = 1
                    for (exercise in blockToImport.components) {
                        Timber.d("about to insert $exercise")
                        dataViewModel.insertExercise(exercise)
                        //this is some dirty shit
                        if (i == blockToImport.components.size) {
                            blockNeedsInserting = true
                        } else {
                            i++
                        }

                    }
                    Toast.makeText(
                        this@Library,
                        "${blockToImport.components.size} new exercises have been added",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })


    }

    private fun deleteBlock(position: Int) {
        val blockToDelete = activeBlockList[position]

        if (blockToDelete.type != Block.APP_PREMADE) {
            if (blockToDelete.type == Block.EXPORT) {
                dataViewModel.deleteBlock(blockToDelete)

            } else {
                askExerciseDeletion(blockToDelete, position)
            }

        } else {
            Toast.makeText(this, "Pre made blocks cant be deleted", Toast.LENGTH_SHORT).show()
        }

    }

    private fun askExerciseDeletion(blockToDelete: Block, position: Int) {
        Utils.getInfoDialogView(
            this,
            title.toString(),
            "Do you want to delete the exercises in this block too?",
            object :
                DialogPositiveNegativeHandler {

                override fun onPositiveButton(editTextText: String) {
                    super.onPositiveButton(editTextText)
                    dataViewModel.deleteBlock(blockToDelete, true)
                    (activeBlockList as MutableList).removeAt(position)
                    displayAdapter(activeBlockList)
                }

                override fun onNegativeButton() {
                    super.onNegativeButton()
                    dataViewModel.deleteBlock(blockToDelete, false)
                    (activeBlockList as MutableList).removeAt(position)
                    displayAdapter(activeBlockList)
                }
            })
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

//        R.id.user_blocks -> {
//            displayAdapter(userBlockList, "You haven't created any blocks")
//            activeBlockList = userBlockList
//            true
//        }
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