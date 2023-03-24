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
        val item_month_text = holder.itemView.findViewById<TextView>(R.id.item_month_text)
        val calendar_recyclerview = holder.itemView.findViewById<RecyclerView>(R.id.calendar_recyclerview)

        calendar.time = Date()
        calendar.add(Calendar.MONTH, position - Int.MAX_VALUE / 2)
        item_month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        calendarviewModel.init_viewModel(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH))

        calendarviewModel.liveDateList.observe(lifecycle) { lit ->
            calendarviewModel.liveContentList.observe(lifecycle) {
                var size = lit.size * 5

                for(con in it){
                    if(con.cnt > 1){
                        size -= con.cnt-1
                    }
                }

                var spanList = ArrayList<Int>()

                var duplist = ArrayList<ContentMark>()
                var cntlist = ArrayList<Int>()

                var positionplus = 0

                var saveLineList = ArrayList<Int>()
                var saveCntList = ArrayList<Int>()

                for(position2 in 0 until size) {
                    val newpostion = positionplus + position2
                    if (newpostion / 7 in arrayOf(0, 5, 10, 15, 20, 25)) spanList.add(1)
                    else {
                        val contentPosition = newpostion % 7 + newpostion / (lit.size) * 7
                        val contentLine = (newpostion / 7) % 5

                        if (saveLineList.contains(contentLine)) {
                            val index = saveLineList.indexOf(contentLine)

                            if (saveCntList[index] > 7) {
                                saveCntList[index] -= 7
                                positionplus += 6
                                spanList.add(7)
                                continue
                            } else {
                                val returncnt = saveCntList[index]

                                saveCntList.removeAt(index)
                                saveLineList.removeAt(index)

                                positionplus += returncnt - 1
                                spanList.add(returncnt)
                                continue
                            }
                        }

                        var returnvalue = 1
                        Log.d("newposition", newpostion.toString())
                        Log.d("contentposition", contentPosition.toString())
                        for (content in it) {
                            if (!duplist.contains(content) && content.position == contentPosition) {
                                duplist.add(content)

//                                if ((contentPosition+content.cnt) / 7 != contentPosition / 7) {
//                                    saveLineList.add(contentLine)
//
//                                    val returncnt = contentPosition+content.cnt - (contentPosition+content.cnt)/7*7
//                                    saveCntList.add(content.cnt - returncnt)
//
//                                    positionplus += returncnt - 1
//                                    returnvalue = returncnt
//                                    cntlist.add(returnvalue)
//                                    break
//                                }

                                positionplus += content.cnt - 1
                                returnvalue = content.cnt
                                cntlist.add(returnvalue)
                                break
                            }
                        }

                        spanList.add(returnvalue)
                    }
                }

                val gridlayout = GridLayoutManager(holder.itemView.context, 7)

                gridlayout.spanSizeLookup = object : SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return spanList[position]
                    }
                }

                val todoAdapter = AdapterDay()

                calendar_recyclerview.adapter = todoAdapter
                calendar_recyclerview.layoutManager = gridlayout

                todoAdapter.updateData(lit, it)
            }
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}