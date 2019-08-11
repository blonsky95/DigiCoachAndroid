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
import com.google.android.material.snackbar.Snackbar
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
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

    private val blockCreatorAcitivtyRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_viewer)
        title = "Block Viewer"

        recyclerView = blockRecyclerView as RecyclerView

        val myListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)

                Toast.makeText(this@BlockViewer, "$position was clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@BlockViewer, BlockCreator::class.java)
                intent.putExtra(BlockCreator.BLOCK_ACTION, BlockCreator.BLOCK_UPDATE)
                updateUpdatingBlock(position)

                startActivityForResult(intent, blockCreatorAcitivtyRequestCode)

            }
        }
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
//            var intent = Intent(this, BlockCreator::class.java)
//            startActivity(intent)

            Timber.d("Block Viewer --> Block creator")

            val intent = Intent(this, BlockCreator::class.java)
            intent.putExtra(BlockCreator.BLOCK_ACTION, BlockCreator.BLOCK_NEW)
            startActivityForResult(intent, blockCreatorAcitivtyRequestCode)
        }
    }

    private fun updateUpdatingBlock(position: Int) {
        var clickedBlock = dataViewModel.allBlocks.value?.get(position)

        if (clickedBlock != null) {
            DataHolder.activeBlockHolder = clickedBlock
        } else {
            Timber.d("upsy error")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == blockCreatorAcitivtyRequestCode && resultCode == BlockCreator.BLOCK_NEW_RESULT_CODE) {

            val newBlock = DataHolder.newBlockHolder
            dataViewModel.insertBlock(newBlock)

//            val actionNotification = Snackbar.make(recyclerView, "Block added", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        }
        if (requestCode == blockCreatorAcitivtyRequestCode && resultCode == BlockCreator.BLOCK_UPDATE_RESULT_CODE) {

            val updatedBlock = DataHolder.activeBlockHolder
            Timber.d("PTG block trying to be updated: ${updatedBlock.name} ${updatedBlock.components}")
            dataViewModel.updateBlock(updatedBlock)

//            val actionNotification = Snackbar.make(recyclerView, "Block updated", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        }
        if (requestCode == blockCreatorAcitivtyRequestCode && resultCode == BlockCreator.BLOCK_DELETE_RESULT_CODE) {

            val deletedBlock = DataHolder.activeBlockHolder
            Timber.d("PTG block trying to be deleted: ${deletedBlock.name} ${deletedBlock.components}")
            dataViewModel.deleteBlock(deletedBlock)
//            val actionNotification = Snackbar.make(recyclerView, "Block deleted", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        }
        if (requestCode == blockCreatorAcitivtyRequestCode && resultCode == BlockCreator.BLOCK_FAIL_RESULT_CODE) {
            //accounts for user pressing back
//            val actionNotification = Snackbar.make(recyclerView, "Failure is an option", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        } else {
        }
    }
}
