package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.activity_day_creator.*
import timber.log.Timber

class DayCreator : AppCompatActivity() {

    companion object {
        var DAY_ACTION = "day_action"
        var DAY_ID = "day_id"
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

    private var currentDayComponents: ArrayList<Block> = arrayListOf()

    lateinit var activeDay: Day
    lateinit var activeDayId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_creator)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        // add init block in classes that require variables to be initialised
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        DataHolder.oldDayHolder?.let { it ->
            activeDay = it
            currentDayComponents = activeDay.blocks
            Timber.d("data holder: active day: $activeDay")
        }

        activeDayId = intent.getStringExtra(DAY_ID)

//        DayId.text=Day.dayIDtoDayMonth(activeDayId)
        title = Day.dayIDtoDayMonth(activeDayId)


        recyclerView = BlocksDisplay as RecyclerView
        recyclerViewV2 = CurrentDayDisplay as RecyclerView

        AddDayBtn.setOnClickListener(updateDayListener)

        adapterBlocks = BlockListAdapter(this, blockSelectorListener)
        recyclerView.adapter = adapterBlocks
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapterDeletableBlocks = BlockListAdapter(this, itemDeletableListener, true)
        recyclerViewV2.adapter = adapterDeletableBlocks
        recyclerViewV2.layoutManager = LinearLayoutManager(this)
        adapterDeletableBlocks.setBlocks(currentDayComponents)

        dataViewModel.allBlocks.observe(this, Observer { exercises ->
            exercises?.let {
                Timber.d("PTG all blocks observer triggered: ${exercises.toString()}")
                if (it.isNotEmpty()) {
                    adapterBlocks.setBlocks(it)
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.creator_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_back -> {
            super.onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
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

    private val updateDayListener = View.OnClickListener {
        activeDay = Day(activeDayId, currentDayComponents, arrayListOf())
        DataHolder.updatedDayHolder = activeDay

        val replyIntent = Intent()
        setResult(DAY_UPDATE_RESULT_CODE, replyIntent)

        finish()
    }


}
