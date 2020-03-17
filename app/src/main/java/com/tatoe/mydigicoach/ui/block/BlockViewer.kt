package com.tatoe.mydigicoach.ui.block

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.viewmodels.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.ui.util.BlockListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_block_viewer.*
import kotlinx.android.synthetic.main.activity_block_viewer.ifEmptyText
import timber.log.Timber

class BlockViewer : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockListAdapter
    private lateinit var allUserBlocks:ArrayList<Block>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_viewer)
        title = "Block Viewer"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = blockRecyclerView as RecyclerView

        val myListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)

                Toast.makeText(this@BlockViewer, "$position was clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@BlockViewer, BlockCreator::class.java)
                intent.putExtra(BlockCreator.BLOCK_ACTION, BlockCreator.BLOCK_UPDATE)
                updateUpdatingBlock(position)

                startActivity(intent)

            }
        }
        adapter = BlockListAdapter(this)
        adapter.setOnClickInterface(myListener)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        dataViewModel.allUserBlocks.observe(this, Observer { blocks ->
            blocks?.let {
                Timber.d("PTG all blocks observer triggered: $blocks")
                allUserBlocks= ArrayList(it)

                if (it.isEmpty()) {
                    ifEmptyText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    ifEmptyText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.setBlocks(it)
                }
            }
        })

        createBlockBtn.setOnClickListener {
            Timber.d("Block Viewer --> Block creator")

            val intent = Intent(this, BlockCreator::class.java)
            intent.putExtra(BlockCreator.BLOCK_ACTION, BlockCreator.BLOCK_NEW)
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

    private fun updateUpdatingBlock(position: Int) {

        if (allUserBlocks.isNotEmpty()) {
            val clickedBlock = allUserBlocks[position]

            DataHolder.activeBlockHolder = clickedBlock
            Timber.d("active block: $clickedBlock ")


        }
    }



}
