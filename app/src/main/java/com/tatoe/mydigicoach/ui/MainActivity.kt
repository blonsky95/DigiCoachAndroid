package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    //todo when basic functionalities are there - FIX FIX CLEAN WARNINGS

    //todo might change viewers toolbars from back button to home icon and button

    //todo NEXT NEXT - write down in paper how to - Exercises need a read - edit mode -- investigate how to turn text view into edit text and vice versa easily dynamically
    // so put a pencil icon at right border which allows editing if possible

    //todo NEXT NEXT NEXT - section in exercises to see which days it is assigned (think of UI and content here as well as in the Calendar View)

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
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
