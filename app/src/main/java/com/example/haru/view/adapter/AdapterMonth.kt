package com.example.haru.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.viewmodel.CalendarViewModel
import java.util.*


//월간 달력 어뎁터
class AdapterMonth(lifecycleOwner: LifecycleOwner):
    RecyclerView.Adapter<AdapterMonth.MonthView>() {
    private val lifecycle = lifecycleOwner
    private var calendar = Calendar.getInstance()

    lateinit var calendarviewModel: CalendarViewModel

    inner class MonthView(itemView: View) : RecyclerView.ViewHolder(itemView){}

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
        Log.d("position", position.toString())

        val item_month_text = holder.itemView.findViewById<TextView>(R.id.item_month_text)
        val calendar_recyclerview = holder.itemView.findViewById<RecyclerView>(R.id.calendar_recyclerview)

        val todoAdapter = AdapterDay()

        calendar.time = Date()
        calendar.add(Calendar.MONTH, position - Int.MAX_VALUE / 2)
        item_month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        calendar_recyclerview.adapter = todoAdapter

        calendar_recyclerview.layoutManager = object : GridLayoutManager(holder.itemView.context, 7){
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        calendarviewModel.init_viewModel(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH))

        calendarviewModel.liveDataList.observe(lifecycle) {
            todoAdapter.updateData(it)
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}