package com.example.haru.view.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.provider.CalendarContract.Colors
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.viewmodel.CalendarViewModel
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max


//월간 달력 어뎁터
class AdapterMonth(lifecycleOwner: LifecycleOwner, view: View):
    RecyclerView.Adapter<AdapterMonth.MonthView>() {
    private val lifecycle = lifecycleOwner

    private var todo_schedule = true

    inner class MonthView(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_month,
            parent,
            false
        )

        return MonthView(view)
    }

    fun init_date(holder: MonthView,year:Int, month:Int, viewModel:CalendarViewModel): Int{
        val dateTextViewList = listOf(
            R.id.date_text_one,
            R.id.date_text_two,
            R.id.date_text_three,
            R.id.date_text_four,
            R.id.date_text_five,
            R.id.date_text_six,
            R.id.date_text_seven,
            R.id.date_text_eight,
            R.id.date_text_nine,
            R.id.date_text_ten,
            R.id.date_text_eleven,
            R.id.date_text_twelve,
            R.id.date_text_thirteen,
            R.id.date_text_fourteen,
            R.id.date_text_fifteen,
            R.id.date_text_sixteen,
            R.id.date_text_seventeen,
            R.id.date_text_eighteen,
            R.id.date_text_nineteen,
            R.id.date_text_twenty,
            R.id.date_text_twentyone,
            R.id.date_text_twentytwo,
            R.id.date_text_twentythree,
            R.id.date_text_twentyfour,
            R.id.date_text_twentyfive,
            R.id.date_text_twentysix,
            R.id.date_text_twentyseven,
            R.id.date_text_twentyeight,
            R.id.date_text_twentynine,
            R.id.date_text_thirty,
            R.id.date_text_thirtyone,
            R.id.date_text_thirtytwo,
            R.id.date_text_thirtythree,
            R.id.date_text_thirtyfour,
            R.id.date_text_thirtyfive,
            R.id.date_text_thirtysix,
            R.id.date_text_thirtyseven,
            R.id.date_text_thirtyeight,
            R.id.date_text_thirtynine,
            R.id.date_text_fourty,
            R.id.date_text_fourtyone,
            R.id.date_text_fourtytwo,
        )
        val dateTextViews = ArrayList<TextView>()

        for(i in 0..41){
            dateTextViews.add(holder.itemView.findViewById<TextView>(dateTextViewList[i]))
        }

        var startDate = ""
        var endDate = ""
        var maxi = 5
        var breakOption = false

        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

        val dateLayoutFive = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_five)
        val dateLayoutSix = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_six)

        val parentConstraintLayout = holder.itemView.findViewById<ConstraintLayout>(R.id.parent_contraint_layout)

        val eraseableView = holder.itemView.findViewById<View>(R.id.eraseable_view)

        val dateLayoutEnd = holder.itemView.findViewById<View>(R.id.date_layout_end)

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)

                if(i == 5 && k == 0 && calendar.get(Calendar.MONTH) != month){
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                    endDate = format.format(calendar.time)+"T00:00:00+09:00"
                    maxi = 4
                    breakOption = true
                }

                if(breakOption){
                    dateTextViews[i*7 + k].text = ""
                    continue
                }

                if(i == 0 && k == 0){
                    startDate = format.format(calendar.time)+"T00:00:00+09:00"
                }

                if(i == 5 && k == 6){
                    endDate = format.format(calendar.time)+"T00:00:00+09:00"
                }

                dateTextViews[i*7 + k].text = calendar.time.date.toString()

                if(calendar.time.year == Date().year &&
                    calendar.time.month == Date().month &&
                    calendar.time.date == Date().date){
                    dateTextViews[i*7 + k].background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.calendar_in_today_image)
                } else {
                    dateTextViews[i*7 + k].setBackgroundColor(Color.WHITE)
                }

                when(k % 7) {
                    0 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0xFD,0xBB,0xCD))
                        } else {
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0xF7,0x1E,0x58))
                        }
                    }
                    6 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0xBB,0xE7,0xFF))
                        } else {
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0x1D,0xAF,0xFF))
                        }
                    }
                    else -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0xBA,0xBA,0xBA))
                        } else {
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0x70,0x70,0x70))
                        }
                    }
                }
            }

            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        if(breakOption){
            eraseableView.setBackgroundColor(Color.WHITE)

            dateLayoutFive.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = dateLayoutEnd.id
            }
        } else {
            eraseableView.setBackgroundColor(Color.GRAY)

            dateLayoutFive.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = dateLayoutSix.id
            }
        }

        while (parentConstraintLayout.get(0).id != R.id.date_layout_start) {
            parentConstraintLayout.removeViewAt(0)
        }

        viewModel.init_viewModel(startDate,endDate, maxi)

        return maxi
    }

    fun addViewFunction(holder: MonthView,
                        textViewText: String,
                        x:Float,
                        y:Float,
                        width:Int,
                        color:Int,
                        completed:Boolean){
        Log.d("completed옵션", completed.toString())
        val parentConstraintLayout = holder.itemView.findViewById<ConstraintLayout>(R.id.parent_contraint_layout)

        val testView = TextView(holder.itemView.context)
        val layoutParams = ConstraintLayout.LayoutParams(
            parentConstraintLayout.width/7*width,
            parentConstraintLayout.height/30
        )

        layoutParams.leftToLeft = parentConstraintLayout.id
        layoutParams.rightToRight = parentConstraintLayout.id
        layoutParams.topToTop = parentConstraintLayout.id
        layoutParams.bottomToBottom = parentConstraintLayout.id
        layoutParams.horizontalBias = x
        layoutParams.verticalBias = y

        testView.setBackgroundResource(R.drawable.calendar_border)
        val drawable = testView.background as GradientDrawable
        drawable.setColor(color)

        testView.gravity = Gravity.CENTER
        testView.setText(textViewText)
        testView.setTextSize(Dimension.SP,12.0f)
        testView.setTextColor(Color.BLACK)

        if(completed){
            testView.alpha = 0.5f
            testView.setPaintFlags(testView.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        }

        testView.layoutParams = layoutParams

        parentConstraintLayout.addView(testView, 0)
    }

    fun setView(holder:MonthView, position: Int){
        val calendar = Calendar.getInstance()

        var calendarviewModel = CalendarViewModel()

//        val calendar_recyclerview = holder.itemView.findViewById<RecyclerView>(R.id.calendar_recyclerview)

        calendar.time = Date()
        calendar.add(Calendar.MONTH, position - Int.MAX_VALUE / 2)

//        val todoAdapter = AdapterDay()
//        calendar_recyclerview.adapter = todoAdapter
//        calendar_recyclerview.setHasFixedSize(true)
//        calendar_recyclerview.setRecycledViewPool(recyclerviewPool)

        var size = init_date(holder, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendarviewModel)

        calendarviewModel.liveTodoCalendarList.observe(lifecycle) { livetodo ->
            calendarviewModel.liveScheduleCalendarList.observe(lifecycle) { liveschedule ->
                var cloneLiveSchedule = liveschedule as ArrayList
                var scheduleCntList = ArrayList<Int>()

                if(size == 4){
                    for(i in 0..34){
                        scheduleCntList.add(0)
                    }
                } else if(size == 5){
                    for(i in 0..41){
                        scheduleCntList.add(0)
                    }
                }

                for(schedule in cloneLiveSchedule){
                    for(i in schedule.position until schedule.position+schedule.cnt){
                        scheduleCntList[i]++
                    }
                }

                Log.d("scheduleCntList",scheduleCntList.toString())

//                Log.d("cloneScheudle", cloneLiveSchedule.toString())

                if (todo_schedule) {
                    for(i in 0 until livetodo.size){
                        for(j in 0 until livetodo[i].todos.size) {
                            if(size == 5){
                                if (j >= 3){
                                    addViewFunction(
                                        holder,
                                        "+"+(livetodo[i].todos.size - 3).toString(),
                                        (i % 7) / 6f,
                                        (5 * (i / 7) + (j + 1)) / 29f,
                                        1,
                                        Color.rgb(0xED, 0xED, 0xED),
                                        false
                                    )

                                    break
                                }
                            } else if(size == 4){
                                if(j >= 4){
                                    addViewFunction(
                                        holder,
                                        "+"+(livetodo[i].todos.size - 4).toString(),
                                        (i % 7) / 6f,
                                        (5 * (i / 7) + (j + 1)) / 29f,
                                        1,
                                        Color.rgb(0xED, 0xED, 0xED),
                                        false
                                    )

                                    break
                                }
                            }

                            if(size == 5) {
                                addViewFunction(
                                    holder,
                                    livetodo[i].todos[j].content,
                                    (i % 7) / 6f,
                                    (5 * (i / 7) + (j + 1)) / 29f,
                                    1,
                                    Color.rgb(0xED, 0xED, 0xED),
                                    livetodo[i].todos[j].completed
                                )
                            } else {
                                addViewFunction(
                                    holder,
                                    livetodo[i].todos[j].content,
                                    (i % 7) / 6f,
                                    (6 * (i / 7) + (j + 1)) / 29f,
                                    1,
                                    Color.rgb(0xED, 0xED, 0xED),
                                    livetodo[i].todos[j].completed
                                )
                            }
                        }
                    }
                }
                else {
                    var spanList = ArrayList<Int>()

                    var cntlist = ArrayList<Int>()
                    var positionplus = 0

                    var saveLineList = ArrayList<Int>()
                    var saveCntList = ArrayList<Int>()
                    var saveScheduleList = ArrayList<Schedule>()

                    loop@for (position2 in 0 until 210) {
                        val newpostion = positionplus + position2

                        if(size == 4){
                            if(newpostion/7 in arrayOf(0,6,12,18,24,30)) continue
                        } else if(size == 5){
                            if(newpostion/7 in arrayOf(0,5,10,15,20,25,30)) continue
                        }

                        var contentPosition = 0
                        var contentLine = 0

                        if(size == 5) {
                            val cyclevalue = newpostion / 35 * 7
                            contentPosition = cyclevalue + newpostion % 7
                            contentLine = (newpostion / 7) % 5 - 1

                            if(contentLine >= 3) continue
                        } else {
                            val cyclevalue = newpostion / 42 * 7
                            contentPosition = cyclevalue + newpostion % 7
                            contentLine = (newpostion / 7) % 6 - 1

                            if(contentLine >= 4) continue
                        }

                        if (newpostion % 7 == 0 && saveLineList.contains(contentLine)) {
                            val index = saveLineList.indexOf(contentLine)

                            if (saveCntList[index] > 7) {
                                saveCntList[index] -= 7
                                positionplus += 6
                                spanList.add(7)

                                var color = Color.rgb(0xBB ,0xE7, 0xFF)

                                if (saveScheduleList[index].category != null &&
                                    saveScheduleList[index].category!!.color != null){
                                    color = Color.parseColor(saveScheduleList[index].category!!.color)
                                }

                                if(size == 5) {
                                    addViewFunction(
                                        holder,
                                        "",
                                        0f,
                                        (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        7,
                                        color,
                                        saveScheduleList[index].completed
                                    )
                                } else {
                                    addViewFunction(
                                        holder,
                                        "",
                                        0f,
                                        (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        7,
                                        color,
                                        saveScheduleList[index].completed
                                    )
                                }

                                for(i in contentPosition until contentPosition+7){
                                    scheduleCntList[i]--
                                }

                                continue
                            } else {
                                val returncnt = saveCntList[index]

                                var color = Color.rgb(0xBB ,0xE7, 0xFF)

                                if (saveScheduleList[index].category != null &&
                                    saveScheduleList[index].category!!.color != null){
                                    color = Color.parseColor(saveScheduleList[index].category!!.color)
                                }

                                val completed = saveScheduleList[index].completed

                                saveCntList.removeAt(index)
                                saveScheduleList.removeAt(index)
                                saveLineList.removeAt(index)

                                positionplus += returncnt - 1
                                spanList.add(returncnt)

                                if(size == 5) {
                                    addViewFunction(
                                        holder,
                                        "",
                                        0f,
                                        (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        returncnt,
                                        color,
                                        completed
                                    )
                                } else {
                                    addViewFunction(
                                        holder,
                                        "",
                                        0f,
                                        (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        returncnt,
                                        color,
                                        completed
                                    )
                                }

                                for(i in contentPosition until contentPosition+returncnt){
                                    scheduleCntList[i]--
                                }

                                continue
                            }
                        }

                        var returnvalue = 1

                        for (content in cloneLiveSchedule) {
                            if (content.position == contentPosition) {
//                                Log.d("확인용", "newPosition:"+newpostion.toString())
//                                Log.d("확인용", "contentPosition: "+contentPosition.toString())
//                                Log.d("확인용", "contentLine: "+contentLine.toString())
//                                Log.d("확인용", "content:"+content.schedule.content)

                                cloneLiveSchedule.remove(content)

                                var color = Color.rgb(0xBB ,0xE7, 0xFF)

                                if (content.schedule.category != null &&
                                    content.schedule.category!!.color != null){
                                    color = Color.parseColor(content.schedule.category!!.color)
                                }

                                if ((contentPosition + content.cnt - 1) / 7 != contentPosition / 7) {
                                    val overflowvalue =
                                        contentPosition + content.cnt - (contentPosition + content.cnt) / 7 * 7
                                    saveCntList.add(overflowvalue)
                                    saveScheduleList.add(content.schedule)
                                    saveLineList.add(contentLine)

                                    positionplus += content.cnt - overflowvalue - 1
                                    returnvalue = content.cnt - overflowvalue
                                    cntlist.add(returnvalue)

                                    if(size == 5) {
                                        addViewFunction(
                                            holder,
                                            content.schedule.content,
                                            (contentPosition % 7) / (7-returnvalue).toFloat(),
                                            (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                            returnvalue,
                                            color,
                                            content.schedule.completed
                                        )
                                    } else {
                                        addViewFunction(
                                            holder,
                                            content.schedule.content,
                                            (contentPosition % 7) / (7-returnvalue).toFloat(),
                                            (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                            returnvalue,
                                            color,
                                            content.schedule.completed
                                        )
                                    }

                                    for(i in contentPosition until contentPosition+returnvalue){
                                        scheduleCntList[i]--
                                    }

                                    continue@loop
                                }

                                positionplus += content.cnt - 1
                                returnvalue = content.cnt
                                cntlist.add(returnvalue)

                                if(size == 5) {
                                    addViewFunction(
                                        holder,
                                        content.schedule.content,
                                        (contentPosition % 7) / (7-returnvalue).toFloat(),
                                        (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        returnvalue,
                                        color,
                                        content.schedule.completed
                                    )
                                } else {
                                    addViewFunction(
                                        holder,
                                        content.schedule.content,
                                        (contentPosition % 7) / (7-returnvalue).toFloat(),
                                        (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        returnvalue,
                                        color,
                                        content.schedule.completed
                                    )
                                }

                                for(i in contentPosition until contentPosition+returnvalue){
                                    scheduleCntList[i]--
                                }

                                continue@loop
                            }
                        }
                    }

                    for(i in 0 until scheduleCntList.size){
                        if(scheduleCntList[i] > 0){
                            if(size==5) {
                                addViewFunction(
                                    holder,
                                    "+" + scheduleCntList[i].toString(),
                                    (i % 7) / 6f,
                                    (5 * (i / 7) + 4) / 29f,
                                    1,
                                    Color.rgb(0xED, 0xED, 0xED),
                                    false
                                )
                            } else if(size == 4){
                                addViewFunction(
                                    holder,
                                    "+" + scheduleCntList[i].toString(),
                                    (i % 7) / 6f,
                                    (6 * (i / 7) + 5) / 29f,
                                    1,
                                    Color.rgb(0xED, 0xED, 0xED),
                                    false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: MonthView, position: Int) {
        setView(holder, position)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}