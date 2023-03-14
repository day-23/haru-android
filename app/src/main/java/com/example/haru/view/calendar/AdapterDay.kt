package com.example.haru.view.calendar

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import java.util.*

class AdapterDay(var tempMonth: Int,
                 var dayList: MutableList<Date>) : RecyclerView.Adapter<AdapterDay.DayView>() {
    val ROW = 6

    inner class DayView(val layout: View): RecyclerView.ViewHolder(layout)

    fun updateData(newtemp: Int, newdayList:MutableList<Date>) {
        tempMonth = newtemp
        dayList = newdayList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_day, parent, false)
        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        //각 날짜의 요일과 데이터 텍스트뷰에 표시
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
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }
}