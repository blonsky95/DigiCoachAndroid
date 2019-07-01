package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1.setOnClickListener {
            var intent = Intent(this, ViewOfWeek::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            var intent = Intent(this, BlockViewer::class.java)
            startActivity(intent)
        }
    }
}
