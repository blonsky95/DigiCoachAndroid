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

    private var allExercisePairs= arrayListOf<Pair<String,Exercise>>()
    private var allCategories = arrayListOf<String>()

    private var db = FirebaseFirestore.getInstance()

    //todo get UI sorted
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

        initObservers()

    }

    private fun initObservers() {
        libraryViewModel.categoriesList.observe(this, Observer {
            allCategories=it
        })
        libraryViewModel.exercisesPairsList.observe(this, Observer {
            allExercisePairs=it
        })
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