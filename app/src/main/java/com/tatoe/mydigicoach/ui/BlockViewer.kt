package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.util.BlockListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_block_viewer.*
import kotlinx.android.synthetic.main.activity_block_viewer.ifEmptyText
import timber.log.Timber

class BlockViewer : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_viewer)
        title = "Block Viewer"

        recyclerView = blockRecyclerView as RecyclerView

        val myListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)

                Toast.makeText(this@BlockViewer, "$position was clicked", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this@BlockViewer, ExerciseCreator::class.java)
//                intent.putExtra(ExerciseCreator.EXERCISE_ACTION, ExerciseCreator.EXERCISE_UPDATE)
//                updateUpdatingExercise(position)
//
//                startActivityForResult(intent, exerciseLabAcitivtyRequestCode)

            }
        }
        // todo ptg - make a block creator and a button to create block
        adapter = BlockListAdapter(this, myListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        dataViewModel.allBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                Timber.d("PTG all blocks observer triggered: $blocks")

                if (it.isEmpty()) {
                    ifEmptyText.visibility= View.VISIBLE
                    recyclerView.visibility= View.GONE
                } else {
                    ifEmptyText.visibility= View.GONE
                    recyclerView.visibility= View.VISIBLE
                    adapter.setBlocks(it)
                }
            }
        })

        createBlockBtn.setOnClickListener {
            var intent = Intent(this, BlockCreator::class.java)
            startActivity(intent)
        }
    }
}
