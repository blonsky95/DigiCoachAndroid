package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_block_lab.*

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
