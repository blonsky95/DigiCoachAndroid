package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.block.BlockViewer
import com.tatoe.mydigicoach.ui.day.DayViewer
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewer
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber

class HomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title = "Home"
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            welcome_text.text=user.email
        } else {
            // No user is signed in
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        calendar_button.setOnClickListener {
            var intent = Intent(this, DayViewer::class.java)
            startActivity(intent)
        }

        block_button.setOnClickListener {
            var intent = Intent(this, BlockViewer::class.java)
            startActivity(intent)
        }

        exercise_button.setOnClickListener {
            var intent = Intent(this, ExerciseViewer::class.java)
            startActivity(intent)
        }

        library_button.setOnClickListener {
            var intent = Intent(this, Library::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_logout -> {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            true
        }


        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
