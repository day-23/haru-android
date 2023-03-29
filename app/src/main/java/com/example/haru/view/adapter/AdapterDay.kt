package com.example.haru.view.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.CalendarDate
import com.example.haru.data.model.ScheduleCalendarData
import com.example.haru.data.model.TodoCalendarData
import com.example.haru.databinding.ListItemDayBinding


class AdapterDay : RecyclerView.Adapter<AdapterDay.DayView>() {
    private var date = emptyList<CalendarDate>()
    private var todocontent = emptyList<TodoCalendarData>()
    private var schedulecontent = emptyList<ScheduleCalendarData>()

    private var todo_schedule = true

    inner class DayView(private val binding: ListItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(content: ScheduleCalendarData){
                val layoutParams = binding.itemDayText.layoutParams as FrameLayout.LayoutParams

                layoutParams.gravity = Gravity.LEFT

                binding.textFrameLayout.setBackgroundResource(R.drawable.calendar_border)

                val drawable = binding.textFrameLayout.background as GradientDrawable

                if(content.schedule.category != null && content.schedule.category!!.color != null) {
                    drawable.setColor(Color.parseColor(content.schedule.category!!.color))
                } else {
                    drawable.setColor(Color.rgb(0xBB,0xE7,0xFF))
                }

                binding.itemDayText.layoutParams = layoutParams

                binding.calendarContent = content.schedule.content
                binding.dateOrContent = false
                binding.black = Color.BLACK
            }

            fun bind(content: TodoCalendarData){
                val layoutParams = binding.itemDayText.layoutParams as FrameLayout.LayoutParams

                layoutParams.gravity = Gravity.LEFT

                binding.textFrameLayout.setBackgroundResource(R.drawable.calendar_border)
                binding.itemDayText.layoutParams = layoutParams

                binding.calendarContent = content.todo.content
                binding.dateOrContent = false
                binding.black = Color.BLACK
            }

            fun bind(date: CalendarDate){
                binding.borderView.setBackgroundColor(Color.GRAY)
                binding.calendarContent = date.date.date.toString()
                binding.calendarDate = date
                binding.dateOrContent = true
            }

            fun bind(background: Boolean){
                if(background){
                    binding.textFrameLayout.setBackgroundResource(R.drawable.calendar_border)
                }

                binding.calendarContent = ""
                binding.dateOrContent = false
                binding.black = Color.BLACK
            }
        }

    fun updateData(newdayList:List<CalendarDate>,
                   newtodocontent:List<TodoCalendarData>,
                   newschedulecontent:List<ScheduleCalendarData>,
                   newtodo_schedule:Boolean) {
        date = newdayList
        todocontent = newtodocontent
        schedulecontent = newschedulecontent
        todo_schedule = newtodo_schedule
        notifyDataSetChanged()
    }

    var todoDupList = ArrayList<TodoCalendarData>()
    var scheduleDupList = ArrayList<ScheduleCalendarData>()
    var cyclevalue = 0
    var positionplus = 0

    var saveLineList = ArrayList<Int>()
    var saveCntList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val binding = ListItemDayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DayView(binding)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        if(todo_schedule) {
            if (position == 0) {
                todoDupList = ArrayList()
                scheduleDupList = ArrayList()
                cyclevalue = 0
                positionplus = 0
                saveLineList = ArrayList()
                saveCntList = ArrayList()
            }

            val newpostion = positionplus + position

            if (newpostion / 7 in arrayOf(0, 5, 10, 15, 20, 25, 30)) {
                val dateposition = arrayOf(0, 5, 10, 15, 20, 25, 30).indexOf(newpostion / 7) * 7
                cyclevalue = dateposition
                holder.bind(date[dateposition + newpostion % 35])
            } else {
                val contentPosition = cyclevalue + newpostion % 7
                val contentLine = (newpostion / 7) % 5

                if (newpostion % 7 == 0 && saveLineList.contains(contentLine)) {
                    val index = saveLineList.indexOf(contentLine)

                    if (saveCntList[index] > 7) {
                        saveCntList[index] -= 7
                        positionplus += 6
                        holder.bind(true)
                        return
                    } else {
                        val returncnt = saveCntList[index]

                        saveCntList.removeAt(index)
                        saveLineList.removeAt(index)

                        positionplus += returncnt - 1
                        holder.bind(true)
                        return
                    }
                }

                for (con in todocontent) {
                    if (!todoDupList.contains(con) && con.position == contentPosition) {
                        todoDupList.add(con)

                        if ((contentPosition + con.cnt - 1) / 7 != contentPosition / 7) {
                            val overflowvalue =
                                contentPosition + con.cnt - (contentPosition + con.cnt) / 7 * 7
                            saveCntList.add(overflowvalue)
                            saveLineList.add(contentLine)

                            positionplus += con.cnt - overflowvalue - 1
                            holder.bind(con)
                            break
                        }


                        positionplus += con.cnt - 1
                        holder.bind(con)
                        return
                    }
                }

                holder.bind(false)
            }
        } else {
            if (position == 0) {
                todoDupList = ArrayList()
                scheduleDupList = ArrayList()
                cyclevalue = 0
                positionplus = 0
                saveLineList = ArrayList()
                saveCntList = ArrayList()
            }

            val newpostion = positionplus + position

            if (newpostion / 7 in arrayOf(0, 5, 10, 15, 20, 25, 30)) {
                val dateposition = arrayOf(0, 5, 10, 15, 20, 25, 30).indexOf(newpostion / 7) * 7
                cyclevalue = dateposition
                holder.bind(date[dateposition + newpostion % 35])
            } else {
                val contentPosition = cyclevalue + newpostion % 7
                val contentLine = (newpostion / 7) % 5

                if (newpostion % 7 == 0 && saveLineList.contains(contentLine)) {
                    val index = saveLineList.indexOf(contentLine)

                    if (saveCntList[index] > 7) {
                        saveCntList[index] -= 7
                        positionplus += 6
                        holder.bind(true)
                        return
                    } else {
                        val returncnt = saveCntList[index]

                        saveCntList.removeAt(index)
                        saveLineList.removeAt(index)

                        positionplus += returncnt - 1
                        holder.bind(true)
                        return
                    }
                }

                for (con in schedulecontent) {
                    if (!scheduleDupList.contains(con) && con.position == contentPosition) {
                        scheduleDupList.add(con)

                        if ((contentPosition + con.cnt - 1) / 7 != contentPosition / 7) {
                            val overflowvalue =
                                contentPosition + con.cnt - (contentPosition + con.cnt) / 7 * 7
                            saveCntList.add(overflowvalue)
                            saveLineList.add(contentLine)

                            positionplus += con.cnt - overflowvalue - 1
                            holder.bind(con)
                            break
                        }


                        positionplus += con.cnt - 1
                        holder.bind(con)
                        return
                    }
                }

                holder.bind(false)
            }
        }
    }

    override fun getItemCount(): Int {
        var size = date.size * 5

        if(todo_schedule) {
            for (con in todocontent) {
                if (con.cnt > 1) {
                    size -= con.cnt - 1
                }
            }
        } else {
            for (con in schedulecontent) {
                if (con.cnt > 1) {
                    size -= con.cnt - 1
                }
            }
        }

        return size
    }
}