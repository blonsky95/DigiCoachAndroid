package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.util.BlockListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_block_creator.*
import kotlinx.android.synthetic.main.activity_day_creator.*
import timber.log.Timber

class DayCreator: AppCompatActivity() {

    //todo associate this day creation to a date
    //when add is clicked, update dataholder with current Day, or null if empty (send date array anyways?).
    // DayCreator will get it from dataholder regardless, and if null then fuck it. When button clicked load to dataholder
    //and send it back
    //back in daycreator check if that ID is contained in the current data with the dvm function (might have to use observer to trigger it)
    // and if it is then update, else insert


    //days cant be deleted

    //next - present in fragments the infro from the days - design something which leaves open possibility to add more data (results of training)
    // see if I can add exercises

    companion object {
        var DAY_ACTION = "day_action"
        var DAY_NEW = "day_new"
        var DAY_UPDATE = "day_update"

        var DAY_FAIL_RESULT_CODE = 0
        var DAY_NEW_RESULT_CODE = 1
        var DAY_UPDATE_RESULT_CODE = 2
        var DAY_DELETE_RESULT_CODE = 3

    }

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewV2: RecyclerView
    private lateinit var adapterBlocks: BlockListAdapter
    private lateinit var adapterDeletableBlocks: BlockListAdapter
    private lateinit var currentDayComponents: ArrayList<Block>

    lateinit var saveDayButton: Button
    lateinit var dayId:TextView
    lateinit var day:Day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_creator)
        title = "Day Creator"
        // add init block in classes that require variables to be initialised
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        currentDayComponents = arrayListOf()

        recyclerView = BlocksDisplay as RecyclerView
        recyclerViewV2 = CurrentDayDisplay as RecyclerView
//        blockPreviewText = BlockPreviewText as TextView
        dayId = DayId as TextView
        saveDayButton = AddDayBtn as Button

        adapterBlocks = BlockListAdapter(this, blockSelectorListener)
        recyclerView.adapter = adapterBlocks
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterBlocks.setBlocks(dataViewModel.allBlocks.value)

        adapterDeletableBlocks = BlockListAdapter(this,itemDeletableListener,true)
        recyclerViewV2.adapter = adapterDeletableBlocks
        recyclerViewV2.layoutManager = LinearLayoutManager(this)

//        if (intent.hasExtra(BlockCreator.BLOCK_ACTION)) {
//            var action = intent.getStringExtra(BlockCreator.BLOCK_ACTION)
//
//            when (action) {
//                BlockCreator.BLOCK_NEW -> modifyUI(BUTTON_ADD)
//                BlockCreator.BLOCK_UPDATE -> modifyUI(BUTTON_UPDATE)
//            }
//        }

        dataViewModel.allBlocks.observe(this, Observer { exercises ->
            exercises?.let {
                Timber.d("PTG all blocks observer triggered: ${exercises.toString()}")
                if (it.isNotEmpty()) {
                    adapterBlocks.setBlocks(it)
                }
            }
        })

    }

    val blockSelectorListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            Toast.makeText(this@DayCreator, "$position was clicked", Toast.LENGTH_SHORT).show()
            val clickedBlock = dataViewModel.allBlocks.value?.get(position)
            currentDayComponents.add(currentDayComponents.size, clickedBlock!!)
            adapterDeletableBlocks.setBlocks(currentDayComponents)
            Timber.d("block creator exercise list after addition - $currentDayComponents")
        }
    }

    val itemDeletableListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            val clickedBlock = currentDayComponents[position]
            var removedSuccess = currentDayComponents.remove(clickedBlock)
            Timber.d("removal success $removedSuccess")

            adapterDeletableBlocks.setBlocks(currentDayComponents)
            Timber.d("block creator exercise list after removal - $currentDayComponents")
        }
    }

//    private val addButtonListener = View.OnClickListener {
//        day = Day()
////        dataViewModel.insertBlock(block.toBlock())
//        Timber.d("${block.name} ${block.components}")
//
//        DataHolder.newBlockHolder = block
//
//        var replyIntent = Intent()
////
//
//        if (block.name.isEmpty()) {
//            setResult(BlockCreator.BLOCK_FAIL_RESULT_CODE, replyIntent)
//        } else {
//            setResult(BlockCreator.BLOCK_NEW_RESULT_CODE, replyIntent)
//        }
//        finish()
//    }


}
