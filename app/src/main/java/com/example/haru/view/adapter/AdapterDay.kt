package com.example.haru.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.CalendarContent
import com.example.haru.data.model.CalendarDate
import com.example.haru.data.model.ContentMark
import com.example.haru.databinding.ListItemDayBinding

class AdapterDay : RecyclerView.Adapter<AdapterDay.DayView>() {
    private var date = emptyList<CalendarDate>()
    private var content = emptyList<ContentMark>()

    inner class DayView(private val binding: ListItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(date: CalendarDate, content: ContentMark){
                binding.calendarDate = date
                binding.calendarContent = content
                //binding.dayContentListview.adapter = AdapterContent(item, true)
            }
        }

    fun updateData(newdayList:List<CalendarDate>, newcontentList:List<ContentMark>) {
        date = newdayList
        content = newcontentList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val binding = ListItemDayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DayView(binding)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.bind(date[position], content[position])
    }

    override fun getItemCount(): Int {
        return date.size * 5
    }
}