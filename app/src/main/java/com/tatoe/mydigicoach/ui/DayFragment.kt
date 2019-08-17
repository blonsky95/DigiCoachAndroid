package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.fragment_day_view.view.*

class DayFragment( var dayId: String, var dataArray:ArrayList<String>) : Fragment() {
        //todo use a map instead of an array so there is keys
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_day_view, container, false)
        var date = "${dataArray[0]} ${dataArray[1]} of ${dataArray[2]}"
        view.weekDay.text=date
        return view
    }

}