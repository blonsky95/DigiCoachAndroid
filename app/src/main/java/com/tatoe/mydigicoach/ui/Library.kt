package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.ImportExportUtils
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.FileListAdapter
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import timber.log.Timber
import java.io.File

class Library : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileListAdapter
    private lateinit var dataViewModel: DataViewModel

    private lateinit var fileActionHandler: ClickListenerRecyclerView

    private var filesArray = listOf<File>()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_library)
        title = "Library"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = recyclerview as RecyclerView

        initAdapterListeners()

        adapter = FileListAdapter(this) //will have to change this to a FileListAdapter and change the set content parameters
        adapter.setOnClickInterface(fileActionHandler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadFileArray()
    }

    private fun moveToInternalFolder() {
        //so this is probably going to open the Document Provider (use a startactivityforresult or similar)
        // by Android so user selects file, when selected it returns it's File address, if it's a Digicoach
        //file it will be moved to the internal Digicoach folder.
        var selectedFile:File
        //provide value with Doc provider intent

//        ImportExportUtils.moveToPrivateFolder(selectedFile)
    }

    private fun loadFileArray() {
        //so this is going to scan through the Digicoach whatever folder and load the files
        //need to check library/methods that can do this
        filesArray=ImportExportUtils.getFilesList()
        adapter.loadFiles(filesArray)

    }

    private fun initAdapterListeners() {
        fileActionHandler = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int, actionId: Int) {
                super.onClick(view, position, actionId)

                when (actionId) {
                    R.id.import_btn -> mGetFile(position)
                    R.id.export_btn -> mSendFile(position)
                    R.id.delete_btn -> mDeleteFile(position)
                }

            }
        }
    }

    private fun mDeleteFile(position: Int) {
        if (filesArray[position].delete()) {
            Timber.d("Succesfully deleted")
        } else {
            Timber.d("Couldn't delete")
        }
    }

    private fun mSendFile(position: Int) {
        //open intent to share a file
    }

    private fun mGetFile(position: Int) {
        var exercises = ImportExportUtils.importExercises(filesArray[position])
        for (exercise in exercises) {
            dataViewModel.insertExercise(exercise)
        }
        Toast.makeText(this,"Exercises have been imported",Toast.LENGTH_SHORT).show()
        //or has it failed to insert?
    }
}