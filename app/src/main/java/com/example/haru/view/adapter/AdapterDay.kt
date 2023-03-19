package com.example.haru.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.CalendarItem
import com.example.haru.databinding.ListItemDayBinding

class AdapterDay : RecyclerView.Adapter<AdapterDay.DayView>() {
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

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}