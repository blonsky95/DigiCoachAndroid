package com.tatoe.mydigicoach.ui.day

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
import kotlinx.android.synthetic.main.fragment_day_view.view.*

class DayFragment(val day: Day?, var date: String) : Fragment() {

    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        fragmentView = inflater.inflate(R.layout.fragment_day_view, container, false)
        fragmentView.weekDay.text = Day.dayIDtoDashSeparator(date)

        var recyclerView = fragmentView.dayContentRecyclerView as RecyclerView

        if (day!=null) {
            if (day.blocks.isEmpty()) {

                fragmentView.ifEmptyDaytext.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE

            } else {

                fragmentView.ifEmptyDaytext.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                var dayContentAdapter = DayContentAdapter(context!!, date)
                recyclerView.adapter = dayContentAdapter
                recyclerView.layoutManager = LinearLayoutManager(context!!)
                dayContentAdapter.setContent(day)
            }
        }

        return fragmentView
    }

}