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

class AdapterDay : RecyclerView.Adapter<MainViewHolder>() {
    val ROW = 6
    var dateList = emptyList<CalendarItem>()

    fun updateData(newdayList:List<CalendarItem>) {
        dateList = newdayList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder =
        MainViewHolder(ListItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.apply {
            bind(dateList[position])
        }


        /*//각 날짜의 요일과 데이터 텍스트뷰에 표시
        val item_day_text = holder.layout.findViewById<TextView>(R.id.item_day_text)
        item_day_text.text = dayList!![position].date.toString()

        //일요일 월요일 색깔 바꿈
        item_day_text.setTextColor(when(position % 7) {
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })

        Log.d("날짜",dayList[position].toString())
        //그 달이 아닐 경우 투명도 조절
        if(tempMonth != dayList[position].month) {
            item_day_text.alpha = 0.4f
        }
        else{
            item_day_text.alpha = 1f
        }

        holder?.onBind()*/
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }

    /*companion object {
        @JvmStatic
        @BindingAdapter("text")
        fun loadDate(thumbs: TextView, date: Int) {
            thumbs.text = date.toString()
        }
    }*/
}

class MainViewHolder(val binding: ListItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: CalendarItem) {
        binding.calendarItem= movie
    }
}