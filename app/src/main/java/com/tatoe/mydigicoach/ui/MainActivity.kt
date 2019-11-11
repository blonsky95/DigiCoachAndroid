package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.block.BlockViewer
import com.tatoe.mydigicoach.ui.day.DayViewer
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewer
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Home"
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))

        calendarBtn.setOnClickListener {
            var intent = Intent(this, DayViewer::class.java)
            startActivity(intent)
        }

        blocksBtn.setOnClickListener {
            var intent = Intent(this, BlockViewer::class.java)
            startActivity(intent)
        }

        exercisesBtn.setOnClickListener {
            var intent = Intent(this, ExerciseViewer::class.java)
            startActivity(intent)
        }

        libraryBtn.setOnClickListener {
            var intent = Intent(this, Library::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
