package com.example.haru.view.calendar

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.CalendarItem
import com.example.haru.databinding.ListItemDayBinding
import com.example.haru.viewmodel.CalendarViewModel
import java.util.*

class AdapterDay : RecyclerView.Adapter<AdapterDay.DayView>() {
    val ROW = 6
    private var data = emptyList<CalendarItem>()
    private var tempYear: Int = 0
    private var tempMonth: Int = 0

    inner class DayView(val binding: ListItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(item: CalendarItem){
                binding.calendarItem = item
            }
        }

    fun updateData(newdayList:List<CalendarItem>, year:Int, month:Int) {
        data = newdayList
        tempYear = year
        tempMonth = month
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val binding = ListItemDayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DayView(binding)
    }

    override fun onBindViewHolder(holder: AdapterDay.DayView, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }
}