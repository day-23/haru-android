package com.example.haru.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.databinding.FragmentCalendarBinding
import com.example.haru.databinding.ListItemMonthBinding
import com.example.haru.viewmodel.CalendarViewModel
import java.util.*

//월간 달력 어뎁터
class AdapterMonth(lifecycleOwner: LifecycleOwner):
    RecyclerView.Adapter<AdapterMonth.MonthView>() {
    private val lifecycle = lifecycleOwner
    private var calendar = Calendar.getInstance()
    private val todoAdapter = AdapterDay()

    lateinit var calendarviewModel: CalendarViewModel

    inner class MonthView(itemView: View) : RecyclerView.ViewHolder(itemView){
        val month_text = itemView.findViewById<TextView>(R.id.item_month_text)
        val month_recyclerview = itemView.findViewById<RecyclerView>(R.id.calendar_recyclerview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_month,
            parent,
            false
        )

        calendarviewModel = CalendarViewModel()

        return MonthView(view)
    }

    override fun onBindViewHolder(holder: MonthView, position: Int) {
        Log.d("position",position.toString())

        calendar.time = Date()
        calendar.add(Calendar.MONTH, position)
        holder.month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        holder.month_recyclerview.adapter = todoAdapter
        holder.month_recyclerview.layoutManager =
            GridLayoutManager(holder.itemView.context, 7)

        calendarviewModel.init_viewModel(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH))
        calendarviewModel.liveDataList.observe(lifecycle) {
            val dataList = it
            todoAdapter.updateData(
                dataList,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)
            )
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}