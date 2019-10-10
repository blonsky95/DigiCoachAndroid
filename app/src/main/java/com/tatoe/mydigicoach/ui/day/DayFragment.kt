package com.tatoe.mydigicoach.ui.day

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.util.DayContentAdapter
import kotlinx.android.synthetic.main.fragment_day_view.view.*

class DayFragment : Fragment() {

    private lateinit var fragmentView: View
    private var day: Day? = null
    private lateinit var date: String

    companion object {

        const val BUNDLE_DAY_KEY = "day_object"
        const val BUNDLE_DATE_KEY = "date_object"

        fun newInstance(day: Day?, date: String): DayFragment {
            var dayFragment = DayFragment()

            dayFragment.arguments = Bundle().apply {
                putString(BUNDLE_DAY_KEY, Gson().toJson(day))
                putString(BUNDLE_DATE_KEY, date)
            }

            return dayFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getString(BUNDLE_DAY_KEY)?.let {
            day = Gson().fromJson(it, Day::class.java)
        }
        arguments?.getString(BUNDLE_DATE_KEY)?.let {
            date = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentView = inflater.inflate(R.layout.fragment_day_view, container, false)
        fragmentView.weekDay.text = Day.dayIDtoDashSeparator(date)

        var recyclerView = fragmentView.dayContentRecyclerView as RecyclerView

        if (day == null || day!!.blocks.isEmpty()) {
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


        return fragmentView
    }

}