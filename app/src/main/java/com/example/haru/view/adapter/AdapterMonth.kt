package com.example.haru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.ScheduleCalendarData
import com.example.haru.data.model.TodoCalendarData
import com.example.haru.viewmodel.CalendarViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


//월간 달력 어뎁터
class AdapterMonth(lifecycleOwner: LifecycleOwner, view: View):
    RecyclerView.Adapter<AdapterMonth.MonthView>() {
    private val lifecycle = lifecycleOwner
    private var calendar = Calendar.getInstance()

    private var todo_schedule = false

    lateinit var calendarviewModel: CalendarViewModel

    inner class MonthView(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_month,
            parent,
            false
        )

        return MonthView(view)
    }

    fun setView(holder:MonthView, position: Int){
        var calendarviewModel = CalendarViewModel()

        val calendar_recyclerview =
            holder.itemView.findViewById<RecyclerView>(R.id.calendar_recyclerview)

        calendar_recyclerview.setItemViewCacheSize(210)

        calendar.time = Date()
        calendar.add(Calendar.MONTH, position - Int.MAX_VALUE / 2)

        calendarviewModel.init_viewModel(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH)
        )

        calendarviewModel.liveDateList.observe(lifecycle) { livedate ->
            calendarviewModel.liveTodoCalendarList.observe(lifecycle) { livetodo ->
                calendarviewModel.liveScheduleCalendarList.observe(lifecycle) { liveschedule ->
                    if (todo_schedule) {
                        var size = livedate.size * 5

                        for (con in livetodo) {
                            if (con.cnt > 1) {
                                size -= con.cnt - 1
                            }
                        }

                        var spanList = ArrayList<Int>()

                        var duplist = ArrayList<TodoCalendarData>()
                        var cntlist = ArrayList<Int>()
                        var cyclevalue = 0

                        var positionplus = 0

                        var saveLineList = ArrayList<Int>()
                        var saveCntList = ArrayList<Int>()

                        for (position2 in 0 until size) {
                            val newpostion = positionplus + position2
                            if (newpostion / 7 in arrayOf(0, 5, 10, 15, 20, 25)) {
                                val dateposition =
                                    arrayOf(
                                        0,
                                        5,
                                        10,
                                        15,
                                        20,
                                        25,
                                        30
                                    ).indexOf(newpostion / 7) * 7
                                cyclevalue = dateposition
                                spanList.add(1)
                            } else {
                                val contentPosition = cyclevalue + newpostion % 7
                                val contentLine = (newpostion / 7) % 5

                                if (newpostion % 7 == 0 && saveLineList.contains(contentLine)) {
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

                                for (content in livetodo) {
                                    if (!duplist.contains(content) && content.position == contentPosition) {
                                        duplist.add(content)

                                        if ((contentPosition + content.cnt - 1) / 7 != contentPosition / 7) {
                                            val overflowvalue =
                                                contentPosition + content.cnt - (contentPosition + content.cnt) / 7 * 7
                                            saveCntList.add(overflowvalue)
                                            saveLineList.add(contentLine)

                                            positionplus += content.cnt - overflowvalue - 1
                                            returnvalue = content.cnt - overflowvalue
                                            cntlist.add(returnvalue)
                                            break
                                        }

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

                        gridlayout.spanSizeLookup = object : SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return spanList[position]
                            }
                        }

                        val todoAdapter = AdapterDay()

                        calendar_recyclerview.adapter = todoAdapter
                        calendar_recyclerview.layoutManager = gridlayout

                        todoAdapter.updateData(livedate, livetodo, liveschedule, todo_schedule)
                    } else {
                        var size = livedate.size * 5

                        for (con in liveschedule) {
                            if (con.cnt > 1) {
                                size -= con.cnt - 1
                            }
                        }

                        var spanList = ArrayList<Int>()

                        var duplist = ArrayList<ScheduleCalendarData>()
                        var cntlist = ArrayList<Int>()
                        var cyclevalue = 0

                        var positionplus = 0

                        var saveLineList = ArrayList<Int>()
                        var saveCntList = ArrayList<Int>()

                        for (position2 in 0 until size) {
                            val newpostion = positionplus + position2
                            if (newpostion / 7 in arrayOf(0, 5, 10, 15, 20, 25)) {
                                val dateposition =
                                    arrayOf(
                                        0,
                                        5,
                                        10,
                                        15,
                                        20,
                                        25,
                                        30
                                    ).indexOf(newpostion / 7) * 7
                                cyclevalue = dateposition
                                spanList.add(1)
                            } else {
                                val contentPosition = cyclevalue + newpostion % 7
                                val contentLine = (newpostion / 7) % 5

                                if (newpostion % 7 == 0 && saveLineList.contains(contentLine)) {
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

                                for (content in liveschedule) {
                                    if (!duplist.contains(content) && content.position == contentPosition) {
                                        duplist.add(content)

                                        if ((contentPosition + content.cnt - 1) / 7 != contentPosition / 7) {
                                            val overflowvalue =
                                                contentPosition + content.cnt - (contentPosition + content.cnt) / 7 * 7
                                            saveCntList.add(overflowvalue)
                                            saveLineList.add(contentLine)

                                            positionplus += content.cnt - overflowvalue - 1
                                            returnvalue = content.cnt - overflowvalue
                                            cntlist.add(returnvalue)
                                            break
                                        }

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

                        gridlayout.spanSizeLookup = object : SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return spanList[position]
                            }
                        }

                        val todoAdapter = AdapterDay()

                        calendar_recyclerview.adapter = todoAdapter
                        calendar_recyclerview.layoutManager = gridlayout

                        todoAdapter.updateData(livedate, livetodo, liveschedule, todo_schedule)
                    }
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: MonthView, position: Int) {
        Log.d("bind position", (position-Int.MAX_VALUE/2).toString())
        setView(holder, position)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}