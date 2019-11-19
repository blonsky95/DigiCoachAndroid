package com.tatoe.mydigicoach.ui

import android.Manifest
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
import com.tatoe.mydigicoach.ImportExportUtils
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.ui.util.BlockListAdapter
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

    private var userBlockList: List<Block> = listOf()
    private var appBlockList: List<Block> = listOf()
    private var imexportBlockList: List<Block> = listOf()

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

        initObservers()

    }

    private fun initObservers() {
        dataViewModel.allUserBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                userBlockList = it
                displayAdapter(userBlockList,"You haven't created any blocks")
                //use this for first time into app, might have to be placed somewhere else
            }
        })
        dataViewModel.allAppBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                appBlockList = it
            }
        })
        dataViewModel.allImportExportBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                imexportBlockList = it
            }
        })
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

    private fun deleteBlock(position: Int) {
//        if (filesList[position].delete()) {
            Timber.d("delete at position: $position")
////            recyclerView.removeViewAt(position)
////            adapter.notifyItemRemoved(position)
//            filesList.removeAt(position)
//            adapter.notifyDataSetChanged()
//        } else {
//            Timber.d("Couldn't delete")
//        }
        //reload adapter

    }

    private fun sendBlock(position: Int) {
        Timber.d("send at position: $position")

        //open intent to share a file
//        Timber.d("Trying to export ${filesList[position].name}")

    }

    private fun importBlock(position: Int) {
        Timber.d("import at position: $position")

//        var exercises = ImportExportUtils.importExercises(filesList[position])
//        Timber.d("Trying to import: $exercises from ${filesList[position].name}")
//
//        for (exercise in exercises) {
//            dataViewModel.insertExercise(exercise)
//        }
//        Toast.makeText(this, "Exercises have been imported", Toast.LENGTH_SHORT).show()
        //or has it failed to insert?
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
            true
        }
        R.id.app_blocks -> {
            displayAdapter(appBlockList, "You broke the system")
            true
        }
        R.id.import_export_blocks -> {
            displayAdapter(imexportBlockList, "Import or export blocks/exercises to see them here")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}