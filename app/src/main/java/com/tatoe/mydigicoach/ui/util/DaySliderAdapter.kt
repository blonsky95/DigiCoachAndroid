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
import timber.log.Timber
import java.util.*

class DaySliderAdapter(var context: Context) :
    RecyclerView.Adapter<DaySliderAdapter.DaySliderViewHolder>() {

    companion object {
        const val MAX_DAYS = 100
        const val DEFAULT_POS = (MAX_DAYS / 2) - 1
        const val MS_IN_DAY = 86400000

        fun positionToDayId(position: Int): String {
            val myCalendar = getDifferentCalendar(position)

            return Day.intDatetoDayId(
                myCalendar.get(Calendar.DAY_OF_MONTH),
                myCalendar.get(Calendar.MONTH) + 1, //month is always 1 behind despite consistent Locale.getDefault() (?)
                myCalendar.get(Calendar.YEAR)
            )
        }

        private fun getDifferentCalendar(position: Int): Calendar {
            val calendar = Calendar.getInstance()
            //this is like getting current calendar and adding/subtracting millis to set the new calendar
            calendar.timeInMillis =
                calendar.timeInMillis + ((position - DEFAULT_POS) * MS_IN_DAY)
            return calendar
        }
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var listenerRecyclerView: ClickListenerRecyclerView? = null

    //    private var dates = getListDates()
    var dayOfWeek: String = ""
    var numberAndMonth: String = ""

    val rightNowCalendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaySliderViewHolder {
        var itemView = inflater.inflate(R.layout.item_holder_day_slider_item, parent, false)
        return DaySliderViewHolder(itemView, listenerRecyclerView)
    }

    override fun getItemCount(): Int {
        return MAX_DAYS
    }

    override fun onBindViewHolder(holder: DaySliderViewHolder, position: Int) {
        dayOfWeek = getDayOfWeek(position)
        numberAndMonth = getNumberAndMonth(position)
        holder.dayWeek.text = dayOfWeek
        holder.numberAndMonth.text = numberAndMonth
    }

    private fun getNumberAndMonth(position: Int): String {
        val calendar = getDifferentCalendar(position)
//        calendar.timeInMillis =
//            rightNowCalendar.timeInMillis + ((position - DEFAULT_POS) * 86400000)
        return Day.numberAndMonthDateFormat.format(calendar.time)
    }

    private fun getDayOfWeek(position: Int): String {
        val calendar = getDifferentCalendar(position)
//        calendar.timeInMillis =
//            rightNowCalendar.timeInMillis + ((position - DEFAULT_POS) * 86400000)
        return Day.dayOfWeekDateFormat.format(calendar.time)
    }

//    private fun getListDates(): List<Date> {
//        var currentDate = Calendar.getInstance().time
//
//    }

    fun setOnClickInterface(listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView = listener
    }

    inner class DaySliderViewHolder(v: View, var listener: ClickListenerRecyclerView?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        val dayWeek = v.week_day as TextView
        val numberAndMonth = v.day_and_month as TextView

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener?.onClick(v, adapterPosition)
        }

    }

}