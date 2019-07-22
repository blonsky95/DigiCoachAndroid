package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_block_viewer.*

class BlockViewer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_viewer)
        title = "Block Viewer"

        button3.setOnClickListener {
            var intent = Intent(this, BlockLab::class.java)
            startActivity(intent)
        }
    }
}
