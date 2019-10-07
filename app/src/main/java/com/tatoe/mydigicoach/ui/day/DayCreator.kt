package com.tatoe.mydigicoach.ui.day

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.BlockListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_day_creator.*
import timber.log.Timber

class DayCreator : AppCompatActivity() {

    companion object {
        var DAY_ID = "day_id"
        var DAY_UPDATE_RESULT_CODE = 2

    }

    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewV2: RecyclerView
    private lateinit var adapterBlocks: BlockListAdapter
    private lateinit var adapterDeletableBlocks: BlockListAdapter

    private var currentDayBlocks: ArrayList<Block> = arrayListOf()
    private var currentDayExercises: ArrayList<Exercise> = arrayListOf()

    lateinit var activeDay: Day
    lateinit var activeDayId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_creator)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        // add init block in classes that require variables to be initialised
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        DataHolder.activeDayHolder?.let { it ->
            activeDay = it
            currentDayBlocks = activeDay.blocks
            currentDayExercises=activeDay.exercises
            Timber.d("data holder: active day: $activeDay")
        }

        activeDayId = intent.getStringExtra(DAY_ID)

//        DayId.text=Day.dayIDtoDashSeparator(activeDayId)
        title = Day.dayIDtoDashSeparator(activeDayId)


        recyclerView = BlocksDisplay as RecyclerView
        recyclerViewV2 = CurrentDayDisplay as RecyclerView

        AddDayBtn.setOnClickListener(updateDayListener)

        adapterBlocks = BlockListAdapter(this)
        adapterBlocks.setListener(blockSelectorListener)
        recyclerView.adapter = adapterBlocks
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapterDeletableBlocks = BlockListAdapter(this, true)
        adapterDeletableBlocks.setListener(itemDeletableListener)
        recyclerViewV2.adapter = adapterDeletableBlocks
        recyclerViewV2.layoutManager = LinearLayoutManager(this)
        updateAdaptersDisplay()

        dataViewModel.allBlocks.observe(this, Observer { exercises ->
            exercises?.let {
                Timber.d("PTG all blocks observer triggered: $exercises")
                if (it.isNotEmpty()) {
                    adapterBlocks.setBlocks(it)

                    IfBlocksEmptyText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    IfBlocksEmptyText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

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
            currentDayBlocks.add(currentDayBlocks.size, clickedBlock!!)
            updateAdaptersDisplay()
            Timber.d("block creator exercise list after addition - $currentDayBlocks")
        }
    }

    val itemDeletableListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            val clickedBlock = currentDayBlocks[position]
            var removedSuccess = currentDayBlocks.remove(clickedBlock)
            Timber.d("removal success $removedSuccess")

            updateAdaptersDisplay()
            Timber.d("block creator exercise list after removal - $currentDayBlocks")
        }
    }

    private val updateDayListener = View.OnClickListener {

        if (DataHolder.activeDayHolder!=null) {
            //update
            activeDay.blocks=currentDayBlocks
            dataViewModel.updateDay(activeDay)
        }
        else {
            dataViewModel.insertDay(Day(activeDayId,currentDayBlocks, arrayListOf()))
        }
        backToViewer()
//        activeDay = Day(activeDayId, currentDayBlocks, arrayListOf())
//        DataHolder.updatedDayHolder = activeDay
//        val replyIntent = Intent()
//        setResult(DAY_UPDATE_RESULT_CODE, replyIntent)

//        finish()
    }

    private fun updateAdaptersDisplay() {
        if (currentDayBlocks.isEmpty()) {
            recyclerViewV2.visibility = View.GONE
            IfDayEmptyText.visibility = View.VISIBLE
        } else {
            recyclerViewV2.visibility = View.VISIBLE
            IfDayEmptyText.visibility = View.GONE
            adapterDeletableBlocks.setBlocks(currentDayBlocks)
        }
    }

    private fun backToViewer() {
        val intent = Intent(this, DayViewer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
