package com.tatoe.mydigicoach.ui.exercise

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import timber.log.Timber
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView as ClickListenerRecyclerView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tatoe.mydigicoach.ImportExportUtils
import com.tatoe.mydigicoach.MainApplication
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.ManagePermissions
import kotlinx.android.synthetic.main.info_dialog_window.view.*


class ExerciseViewer : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter

    private lateinit var goToCreatorListener: ClickListenerRecyclerView
    private lateinit var itemSelectorListener: ClickListenerRecyclerView
    private var selectedIndexes = arrayListOf<Int>()

    private lateinit var allExercises: List<Exercise>

    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123

    val list = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

//    private var SELECT_ITEMS_UI = "select_items_ui"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_viewer)
        title = "Exercise Viewer"

        managePermissions = ManagePermissions(this,list,PermissionsRequestCode)

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
                //                Timber.d("PTG all exercises observer triggered: $exercises")

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
//                Timber.d("click registered export")

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
//                Timber.d("current selection: $selectedIndexes")

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.viewer_toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_import -> {
            //intent to document provider
            true
        }

        R.id.action_export -> {
            //show dialog with instructions to select
            managePermissions.checkPermissions()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode ->{
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode,permissions,grantResults)

                if(isPermissionsGranted){
                    showImportDialog()
                }else{
                    Timber.d("Permissions denied.")
                }
                return
            }
        }
    }

    private fun showImportDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.info_dialog_window, null)
//        mDialogView.item_title.text= "Description"
        mDialogView.item_description.text = "Click the exercises you desire to select"
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setTitle(title)
        mBuilder.setPositiveButton("OK") { _, _ ->
            makeListSelectable()
        }
//        mBuilder.setNegativeButton("CANCEL") { _, _ ->
//        }
        mBuilder.show()
    }

    private fun makeListSelectable() {

        updateAdapterListener(itemSelectorListener)
        exportBtn.visibility = View.VISIBLE
        exportBtn.setOnClickListener {
            Timber.d("Final selection: $selectedIndexes")
            exportBtn.visibility = View.GONE
            //todo here call the import export utils with the selectedIndexes
            ImportExportUtils.exportExercises(allExercises, selectedIndexes)
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
