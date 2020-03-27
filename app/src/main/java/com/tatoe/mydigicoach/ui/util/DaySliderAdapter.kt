package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import kotlinx.android.synthetic.main.item_holder_day_slider_item.view.*
import java.util.*

class DaySliderAdapter(var context: Context) : RecyclerView.Adapter<DaySliderAdapter.DaySliderViewHolder>() {

    companion object {
        const val INITIAL_POSITION = 13
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)

//    private var dates = getListDates()
    var dayOfWeek: String = ""
    var numberAndMonth: String = ""

    val rightNowCalendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaySliderViewHolder {
        var itemView = inflater.inflate(R.layout.item_holder_day_slider_item,parent,false)
        return DaySliderViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 28
    }

    override fun onBindViewHolder(holder: DaySliderViewHolder, position: Int) {
        dayOfWeek=getDayOfWeek(position)
        numberAndMonth=getNumberAndMonth(position)
        holder.dayWeek.text=dayOfWeek
        holder.numberAndMonth.text=numberAndMonth
    }

    private fun getNumberAndMonth(position: Int): String {
        val calendar=Calendar.getInstance()
        calendar.timeInMillis=rightNowCalendar.timeInMillis+((position-13)*86400000)
        return Day.numberAndMonthDateFormat.format(calendar.time)
    }

    private fun getDayOfWeek(position: Int): String {
          val calendar=Calendar.getInstance()
          calendar.timeInMillis=rightNowCalendar.timeInMillis+((position-13)*86400000)
        return Day.dayOfWeekDateFormat.format(calendar.time)
    }

//    private fun getListDates(): List<Date> {
//        var currentDate = Calendar.getInstance().time
//
//    }

    inner class DaySliderViewHolder(v:View):RecyclerView.ViewHolder(v){
        val dayWeek=v.week_day as TextView
        val numberAndMonth=v.day_and_month as TextView

    }

}