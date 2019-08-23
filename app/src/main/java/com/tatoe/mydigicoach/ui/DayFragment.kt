package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.util.DayContentAdapter
import kotlinx.android.synthetic.main.fragment_day_view.*
import kotlinx.android.synthetic.main.fragment_day_view.view.*

class DayFragment(val day: Day?, var dataArray: ArrayList<String>) : Fragment() {
    //todo use a map instead of an array so there is keys

    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        fragmentView = inflater.inflate(R.layout.fragment_day_view, container, false)
        var date = "${dataArray[0]} ${dataArray[1]} of ${dataArray[2]}"
        fragmentView.weekDay.text = date
        val string = "There are no blocks nor exercises, add them to see them here"

        var recyclerView = fragmentView.dayContentRecyclerView as RecyclerView

        var dayContentAdapter = DayContentAdapter(context!!)
        recyclerView.adapter = dayContentAdapter
        recyclerView.layoutManager = LinearLayoutManager(context!!)

        dayContentAdapter.setContent(day)

//        fragmentView.dayBlocks.text = string
//        if (day != null) {
//            fragmentView.dayBlocks.text = day.blocks.toString()
//        }
        return fragmentView
    }

}