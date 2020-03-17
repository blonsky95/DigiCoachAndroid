package com.tatoe.mydigicoach.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import timber.log.Timber
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView as ClickListenerRecyclerView
import androidx.appcompat.app.AlertDialog
import com.tatoe.mydigicoach.*
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.dialog_window_export.view.*


class ExerciseViewer : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter

    private lateinit var goToCreatorListener: ClickListenerRecyclerView
    private lateinit var itemSelectorListener: ClickListenerRecyclerView
    private var selectedIndexes = arrayListOf<Int>()

    private lateinit var allExercises: List<Exercise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_viewer)
        title = "Exercise Viewer"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = recyclerview as RecyclerView

        exportBtn.visibility = View.GONE
        initAdapterListeners()

        adapter = ExerciseListAdapter(this)
        adapter.setOnClickInterface(goToCreatorListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                Timber.d("I WANNA SEE THIS: $exercises")

                if (it.isEmpty()) {
                    ifEmptyText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    ifEmptyText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.setExercises(it)
                    allExercises = it
                }
            }
        })

        addExerciseBtn.setOnClickListener {
            Timber.d("Exercise Viewer --> Exercise creator")

            val intent = Intent(this, ExerciseCreator::class.java)
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_NEW)
            startActivity(intent)

        }

        getButton.setOnClickListener {
            //get stuff from firestore
            var exercises = mutableListOf<Exercise>()
            exercises.add(Exercise(LinkedHashMap(mutableMapOf("Name" to "150s", "Description" to "Try 2x4x150 with 3' and 6' or 3x3x150 with 3' and 6'"))))
            exercises.add(Exercise(LinkedHashMap(mutableMapOf("Name" to "Pyramid", "Description" to "200 - 250 - 300 - 350 - 300 - 250 - 200 R=4-6'"))))

            //get exercises from firestore
            //create a androidviewmodel for exerciseviewer, which will need the repository, the allexercises LiveData and the firebase firestore (probs factory too)
            //add in this model a function that you give email and it returns the list of exercises in firestore if not empty
            // and then it replaces your exercises by these ones - needs a dialog
            // + progress thing (https://stackoverflow.com/questions/48239657/how-to-handle-android-livedataviewmodel-with-progressbar-on-screen-rotate)
            //add in this model a function that allows you to update your online exercises + creates document + completion toast

            //if this works - think of exercises/blocks/days how to get references to exercises ( forget blocks)
            dataViewModel.getExercisesFromFirestore(exercises)
            //todo
        }

        postButton.setOnClickListener {
            //post stuff to firestore
        }
    }

    private fun initAdapterListeners() {
        goToCreatorListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)

                val intent = Intent(this@ExerciseViewer, ExerciseCreator::class.java)
                intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
                updateUpdatingExercise(position)

                startActivity(intent)

            }
        }

        itemSelectorListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)
                Timber.d("$position was clicked, selected before: $selectedIndexes")

                if (!selectedIndexes.contains(position)) {
                    view.alpha = 0.5f
                    selectedIndexes.add(position)
                } else {

                    val iterator = selectedIndexes.iterator()
                    while (iterator.hasNext()) {
                        val y = iterator.next()
                        if (y == position) {
                            view.alpha = 1.0f
                            iterator.remove()
                            break
                        }
                    }

                }
                Timber.d("$position was clicked, selected after: $selectedIndexes")

//                Timber.d("current selection: $selectedIndexes")

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.viewer_toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home -> {
            onBackPressed()
            true
        }

        R.id.action_export -> {
            //show dialog with instructions to select
//            checkPermissions()
//            managePermissions.checkPermissions()
            showImportDialog()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun showImportDialog() {

        Utils.getDialogViewWithEditText(
            this,
            title.toString(),
            "Click the exercises you desire to select",
            object :
                DialogPositiveNegativeHandler {
                override fun onPositiveButton(editTextText: String) {
                    super.onPositiveButton(editTextText)
                    if (editTextText.isEmpty()) {
                        Toast.makeText(
                            this@ExerciseViewer,
                            "Block name must not be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        makeListSelectable(editTextText)
                    }
                }
            })
//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_window_export, null)
//        mDialogView.text_info.text = "Click the exercises you desire to select"
//
//        val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setTitle(title)
//        mBuilder.setPositiveButton("OK") { _, _ ->
//            val exportFileName = mDialogView.export_name_edittext.text.trim().toString()
//            if (exportFileName.isEmpty()) {
//                Toast.makeText(this, "Block name must not be empty", Toast.LENGTH_SHORT).show()
//            } else {
//                makeListSelectable(exportFileName)
//            }
//        }
//        mBuilder.show()
    }

    private fun makeListSelectable(exportBlockName: String) {
        selectedIndexes.clear()
        addExerciseBtn.visibility = View.GONE
        title = "Select Exercises"


        updateAdapterListener(itemSelectorListener)
        exportBtn.visibility = View.VISIBLE
        exportBtn.setOnClickListener {
            Timber.d("Final selection: $selectedIndexes")
            exportBtn.visibility = View.GONE
            dataViewModel.insertBlock(
                ImportExportUtils.makeExportBlock(
                    allExercises,
                    selectedIndexes,
                    exportBlockName
                )
            )
            addExerciseBtn.visibility = View.VISIBLE
            title = "Exercise Viewer"
            updateAdapterListener(goToCreatorListener)
        }

    }

    private fun updateAdapterListener(newListener: ClickListenerRecyclerView) {
        adapter = ExerciseListAdapter(this)
        adapter.setOnClickInterface(newListener)
        recyclerView.adapter = adapter
        adapter.setExercises(allExercises)
    }

    private fun updateUpdatingExercise(position: Int) {

        var clickedExercise = dataViewModel.allExercises.value?.get(position)

        if (clickedExercise != null) {
            DataHolder.activeExerciseHolder = clickedExercise
        } else {
            Timber.d("upsy error")
        }

    }


}
