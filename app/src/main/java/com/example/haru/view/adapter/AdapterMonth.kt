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
import com.example.haru.data.model.ContentMark
import com.example.haru.viewmodel.CalendarViewModel
import java.util.*
import kotlin.collections.ArrayList


//월간 달력 어뎁터
class AdapterMonth(lifecycleOwner: LifecycleOwner):
    RecyclerView.Adapter<AdapterMonth.MonthView>() {
    private val lifecycle = lifecycleOwner
    private var calendar = Calendar.getInstance()

    lateinit var calendarviewModel: CalendarViewModel

    inner class MonthView(itemView: View) : RecyclerView.ViewHolder(itemView)

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

        calendar_recyclerview.layoutManager = GridLayoutManager(holder.itemView.context, 7)

        calendarviewModel.init_viewModel(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH))

        calendarviewModel.liveDateList.observe(lifecycle) { lit ->
            calendarviewModel.liveContentList.observe(lifecycle) {
                val gridlayout = GridLayoutManager(holder.itemView.context, 7)
                var duplist = ArrayList<ContentMark>()

                var dateOrContent = true

                var contentcnt = 0
                var datePosition = 0
                var contentPosition = 0

                var saveLineList = ArrayList<Int>()
                var saveCntList = ArrayList<Int>()

                gridlayout.spanSizeLookup = object : SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        if(dateOrContent){
                            datePosition++

                            if (datePosition % 7 == 0){
                                dateOrContent = false
                            }

                            return 1
                        } else {
                            if(saveLineList.contains(contentcnt)){
                                val index = saveLineList.indexOf(contentcnt)

                                if(saveCntList[index] > 7){
                                    saveCntList[index] -= 7
                                    return 7
                                } else {
                                    val returncnt = saveCntList[index]

                                    saveCntList.removeAt(index)
                                    saveLineList.removeAt(index)

                                    return returncnt
                                }
                            }

                            for(content in it){
                                if(!duplist.contains(content) && content.position == contentPosition){
                                    duplist.add(content)
                                    val lastcontentPosition = contentPosition
                                    contentPosition += content.cnt

                                    if(lastcontentPosition/7 != contentPosition/7){
                                        saveLineList.add(contentcnt)

                                        contentPosition -= contentPosition%7
                                        val returncnt = contentPosition-lastcontentPosition
                                        saveCntList.add(content.cnt-returncnt)

                                        return returncnt
                                    }

                                    return content.cnt
                                }
                            }

                            contentPosition++

                            if(contentPosition % 7 == 0){
                                contentPosition -= 7
                                contentcnt++
                            }

                            if(contentcnt == 4){
                                contentPosition += 7
                                contentcnt = 0
                                dateOrContent = true
                            }

                            return 1
                        }
                    }
                }

                todoAdapter.updateData(lit, it)
            }
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}