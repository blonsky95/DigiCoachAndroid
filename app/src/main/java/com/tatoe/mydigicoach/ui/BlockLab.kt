package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_block_lab.*
import kotlinx.android.synthetic.main.activity_main.*

class BlockLab : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_lab)

        button4.setOnClickListener {
            var intent = Intent(this, ExerciseLab::class.java)
            startActivity(intent)
        }
    }
}
