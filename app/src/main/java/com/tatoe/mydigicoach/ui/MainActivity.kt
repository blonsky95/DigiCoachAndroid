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

    //todo NEXT - add action bar for navigation - All creators have a toolbar back button which does super.onBackPressed with a finish()
    //todo all viewers have a UP button to go to MainActivity - do the same for the others as in exerciseviewer ( for the other viewers)
    //navigation is MAIN - VIEWER (DAY, BLOCK, EXERCISE) - RESPECTIVE CREATORS

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
            var intent = Intent(this, CalendarView::class.java)
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
//        R.id.action_settings -> {
//            // User chose the "Settings" item, show the app settings UI...
//            true
//        }
//
//        R.id.action_favorite -> {
//            // User chose the "Favorite" action, mark the current item
//            // as a favorite...
//            true
//        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
