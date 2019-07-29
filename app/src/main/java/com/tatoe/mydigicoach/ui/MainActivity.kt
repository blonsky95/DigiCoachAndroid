package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.R
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

        calendarBtn.setOnClickListener {
            var intent = Intent(this, ViewOfWeek::class.java)
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
}
