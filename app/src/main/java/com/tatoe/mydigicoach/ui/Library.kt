package com.tatoe.mydigicoach.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.ImportExportUtils
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.FileListAdapter
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import kotlinx.android.synthetic.main.activity_exercise_viewer.ifEmptyText
import kotlinx.android.synthetic.main.activity_exercise_viewer.recyclerview
import kotlinx.android.synthetic.main.activity_library.*
import timber.log.Timber
import java.io.File

class Library : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileListAdapter
    private lateinit var dataViewModel: DataViewModel

    private lateinit var fileActionHandler: ClickListenerRecyclerView

    private var filesList = mutableListOf<File>()
    private val READ_REQUEST_CODE: Int = 42


    companion object {
        val listPermissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        const val PermissionsRequestCode = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        title = "Library"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        recyclerView = recyclerview as RecyclerView

        initAdapterListeners()

        adapter =
            FileListAdapter(this) //will have to change this to a FileListAdapter and change the set content parameters
        adapter.setOnClickInterface(fileActionHandler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadFileArray()
    }

    private fun moveToInternalFolder() {
        //so this is probably going to open the Document Provider (use a startactivityforresult or similar)
        // by Android so user selects file, when selected it returns it's File address, if it's a Digicoach
        //file it will be moved to the internal Digicoach folder.
        var selectedFile: File
        //provide value with Doc provider intent

//        ImportExportUtils.moveToPrivateFolder(selectedFile)
    }

    private fun loadFileArray() {
        //if files returned is null --> no permission granted
        //if files empty --> no files found
        // if files not empty --> display files
        if (ImportExportUtils.getFilesList() != null) {
            filesList = ImportExportUtils.getFilesList()!!.toMutableList()
            if (filesList.isEmpty()) {
                showToUser(
                    "No files were found in the app's folder, if they are in " +
                            "the phone use the <b>move file</b> button or export your" +
                            " own exercises in <b>Exercises</b>"
                )
                return
            }
            displayAdapter()
        } else {
            showToUser(
                "Permission to access storage hasn't been granted, go to " +
                        "Settings, Apps and enable the permission for Digicoach or use the button below"
            )
            showPermissionButton()
        }

    }

    private fun displayAdapter() {
        ifEmptyText.visibility=View.GONE
        permissionBtn.visibility=View.GONE
        recyclerView.visibility=View.VISIBLE
        adapter.loadFiles(filesList)
    }

    private fun showToUser(textDisplay: String) {
        ifEmptyText.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ifEmptyText.text = Html.fromHtml(textDisplay, Html.FROM_HTML_MODE_COMPACT)
        } else {
            ifEmptyText.text = Html.fromHtml(textDisplay)
        }
        recyclerView.visibility = View.GONE
    }

    private fun showPermissionButton() {
        permissionBtn.visibility = View.VISIBLE
        permissionBtn.setOnClickListener {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PermissionsRequestCode
        )

    }

//    private fun hasPermissions(context: Context, permissions: List<String>): Boolean =
//        permissions.all {
//            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//        }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED))
                    loadFileArray()
                else {
                    return
                }
                return
            }
        }
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
        if (filesList[position].delete()) {
            Timber.d("Succesfully deleted")
//            recyclerView.removeViewAt(position)
//            adapter.notifyItemRemoved(position)
            filesList.removeAt(position)
            adapter.notifyDataSetChanged()
        } else {
            Timber.d("Couldn't delete")
        }
        //reload adapter

    }

    private fun mSendFile(position: Int) {
        //open intent to share a file
        Timber.d("Trying to export ${filesList[position].name}")

    }

    private fun mGetFile(position: Int) {
        var exercises = ImportExportUtils.importExercises(filesList[position])
        Timber.d("Trying to import: $exercises from ${filesList[position].name}")

        for (exercise in exercises) {
            dataViewModel.insertExercise(exercise)
        }
        Toast.makeText(this, "Exercises have been imported", Toast.LENGTH_SHORT).show()
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

        R.id.action_send -> {
            //will send a file to a given userId - after first release (?)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}