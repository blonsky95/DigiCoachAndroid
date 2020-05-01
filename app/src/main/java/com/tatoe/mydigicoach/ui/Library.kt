package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.viewmodels.DataViewModel
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.BlockV2ListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.viewmodels.LibraryViewModel
import com.tatoe.mydigicoach.viewmodels.MyLibraryViewModelFactory
import kotlinx.android.synthetic.main.activity_exercise_viewer.ifEmptyText
import kotlinx.android.synthetic.main.activity_exercise_viewer.recyclerview
import timber.log.Timber

class Library : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockV2ListAdapter
    private lateinit var libraryViewModel: LibraryViewModel

    private var db = FirebaseFirestore.getInstance()

//    private lateinit var blockActionHandler: ClickListenerRecyclerView
//
//    //    private var userBlockList: List<Block> = listOf()
//    private var appBlockList: List<Block> = listOf()
//    private var importBlockList: List<Block> = listOf()
//    private var exportBlockList: List<Block> = listOf()
//
//    private var activeBlockList: List<Block> = mutableListOf()
//
//    private var loadDefaultBlockList = true //so display user blocks
//    private var blockNeedsInserting = false
//    lateinit var blockToBeUpdated: Block
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

        libraryViewModel = ViewModelProviders.of(
            this,
            MyLibraryViewModelFactory(application, db)
        ).get(
            LibraryViewModel::class.java
        )

        //todo observe the online stored_exercises

        initObservers()

    }

    private fun initObservers() {
//        dataViewModel.allExercises.observe(this, Observer { exercises ->
//            exercises.let {
//                allExercises = it
//                //the following is run when a premade block has been inserted and needs non-IDied
//                //exercises to be swapped for exercises with ID
//                if (blockNeedsInserting) {
//                    insertBlockReExercises()
//                }
//            }
//        })

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



        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}