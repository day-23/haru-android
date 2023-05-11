package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.view.calendar.CalendarDetailDialog
import com.example.haru.view.calendar.CalendarFragment
import com.example.haru.view.calendar.TouchEventDecoration
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.view.checklist.CalendarAddFragment
import com.example.haru.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


//월간 달력 어뎁터
class AdapterMonth(val activity: Activity,
                   val fragment: FragmentActivity,
                   val lifecycleOwner: LifecycleOwner,
                   val thisViewPager: ViewPager2,
                   val parentFragmentManager: FragmentManager,
                   val parentFragment: CalendarFragment):
    RecyclerView.Adapter<AdapterMonth.MonthView>() {

    private var categories: List<Category?> = emptyList()

    fun setCategories(new_categories:List<Category?>){
        categories = new_categories
    }

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

    @SuppressLint("ClickableViewAccessibility")
    fun init_date(holder: MonthView, year:Int, month:Int, viewModel:CalendarViewModel): Int{
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

        val touchEventRecyclerView = holder.itemView.findViewById<RecyclerView>(R.id.touch_event_recyclerview)

        var dateArrayList = ArrayList<Date>()

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
                dateArrayList.add(calendar.time)

                if(calendar.time.year == Date().year &&
                    calendar.time.month == Date().month &&
                    calendar.time.date == Date().date){
                    dateTextViews[i*7 + k].background = ContextCompat.getDrawable(parentFragment.requireContext(),R.drawable.calendar_in_today_image)
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

        var touchList = ArrayList<Boolean>()

        if(maxi == 4){
            for(i in 0..34){
                touchList.add(false)
            }
        } else {
            for(i in 0..41){
                touchList.add(false)
            }
        }

        var adapter: AdapterCalendarTouch

        if(maxi == 4){
            touchEventRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 7)
            touchEventRecyclerView.addItemDecoration(TouchEventDecoration(5))

            adapter = AdapterCalendarTouch(35, touchList)
            touchEventRecyclerView.adapter = adapter
        } else {
            touchEventRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 7)
            touchEventRecyclerView.addItemDecoration(TouchEventDecoration(6))

            adapter = AdapterCalendarTouch(42, touchList)
            touchEventRecyclerView.adapter = adapter
        }

        var longPress = false
        var startTime = System.currentTimeMillis()

        var startColumn = -1
        var startRow = -1

        touchEventRecyclerView.setOnTouchListener{ v, event ->
            val column = (event.x/(parentConstraintLayout.width/7)).toInt()
            val row = (event.y/(parentConstraintLayout.height/(maxi+1))).toInt()

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startTime = System.currentTimeMillis()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if(!longPress && System.currentTimeMillis() - startTime > 1000){
                        thisViewPager.setUserInputEnabled(false)
                        longPress = true
                        startColumn = column
                        startRow = row
                    }

                    if(longPress && startColumn != -1 && startRow != -1){
                        Log.d("long press", "%d %d to %d %d로 움직임".format(startColumn,startRow, column, row))

                        var startPosition = startRow * 7 + startColumn
                        var endPosition = row * 7 + column

                        if(startPosition > endPosition){
                            val tmp = startPosition
                            startPosition = endPosition
                            endPosition = tmp
                        }

                        for(i in startPosition..endPosition){
                            if(!adapter.touchList[i]) {
                                adapter.itemChange(i, true)
                            }
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if(longPress) {
                        thisViewPager.setUserInputEnabled(true)
                        longPress = false

                        if(startColumn != -1 && startRow != -1){
                            var startPosition = startRow * 7 + startColumn
                            var endPosition = row * 7 + column

                            if(startPosition > endPosition){
                                val tmp = startPosition
                                startPosition = endPosition
                                endPosition = tmp
                            }

                            for(i in startPosition..endPosition){
                                if(adapter.touchList[i]) {
                                    adapter.itemChange(i, false)
                                }
                            }

                            val scheduleInput = CalendarAddFragment(
                                activity,
                                categories,
                                this,
                                dateArrayList[startPosition],
                                dateArrayList[endPosition])

                            scheduleInput.show(parentFragmentManager, scheduleInput.tag)
                        }

                        startColumn = -1
                        startRow = -1
                    } else {
                        val detailDialog = CalendarDetailDialog(
                            holder.itemView.context,
                            lifecycleOwner,
                            dateArrayList[row*7 + column],
                            this,
                            fragment
                        )

                        detailDialog.show(parentConstraintLayout.height)
                    }

                    false
                }
                else -> false
            }
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
        drawable.setColorFilter(color,PorterDuff.Mode.SRC_ATOP)

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

        calendarviewModel.liveTodoCalendarList.observe(lifecycleOwner) { livetodo ->
            calendarviewModel.liveScheduleCalendarList.observe(lifecycleOwner) { liveschedule ->
                var cloneLiveTodo = livetodo as ArrayList
                var cloneLiveSchedule = liveschedule as ArrayList

                var spanList = ArrayList<Int>()

                var cntlist = ArrayList<Int>()
                var positionplus = 0

                var saveLineList = ArrayList<Int>()
                var saveCntList = ArrayList<Int>()
                var saveScheduleList = ArrayList<Schedule>()

                loop@for (position2 in 0 until 210) {
                    val newpostion = positionplus + position2

                    if(newpostion >= 210) break

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

                            continue
                        }
                    }

                    var returnvalue = 1

                    if(calendarMainData.scheduleApply) {
                        for (content in cloneLiveSchedule) {
                            var interval = 0

                            if(content.cnt != null){
                                interval = content.cnt!!
                            } else {
                                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                                val repeatStart = format.parse(content.schedule.repeatStart)
                                val calendar2 = Calendar.getInstance()
                                Log.d("repeatStart", repeatStart.toString())
                                calendar2.time = repeatStart
                                calendar2.add(Calendar.MILLISECOND, content.timeInterval!!)
                                Log.d("calendar2",calendar2.time.toString())


                                repeatStart.hours = 0
                                repeatStart.minutes = 0
                                repeatStart.seconds = 0

                                calendar2.time.hours = 0
                                calendar2.time.minutes = 0
                                calendar2.time.seconds = 0

                                val dateminus = (calendar2.time.time - repeatStart.time) / (60 * 60 * 24 * 1000)
                                interval = dateminus.toInt()
                                Log.d("interval",interval.toString())
                            }

                            if(content.schedule.category != null) {
                                if (content.schedule.category.isSelected) {
                                    if (content.position == contentPosition) {
                                        cloneLiveSchedule.remove(content)

                                        var color = Color.parseColor(content.schedule.category!!.color)

                                        if ((contentPosition + interval - 1) / 7 != contentPosition / 7) {
                                            val overflowvalue =
                                                contentPosition + interval - (contentPosition + interval) / 7 * 7
                                            saveCntList.add(overflowvalue)
                                            saveScheduleList.add(content.schedule)
                                            saveLineList.add(contentLine)

                                            positionplus += interval - overflowvalue - 1
                                            returnvalue = interval - overflowvalue
                                            cntlist.add(returnvalue)

                                            if (size == 5) {
                                                addViewFunction(
                                                    holder,
                                                    content.schedule.content,
                                                    (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                    (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                    returnvalue,
                                                    color,
                                                    content.schedule.completed
                                                )
                                            } else {
                                                addViewFunction(
                                                    holder,
                                                    content.schedule.content,
                                                    (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                    (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                    returnvalue,
                                                    color,
                                                    content.schedule.completed
                                                )
                                            }

                                            continue@loop
                                        }

                                        positionplus += interval - 1
                                        returnvalue = interval
                                        cntlist.add(returnvalue)

                                        if (size == 5) {
                                            addViewFunction(
                                                holder,
                                                content.schedule.content,
                                                (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                returnvalue,
                                                color,
                                                content.schedule.completed
                                            )
                                        } else {
                                            addViewFunction(
                                                holder,
                                                content.schedule.content,
                                                (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                returnvalue,
                                                color,
                                                content.schedule.completed
                                            )
                                        }

                                        continue@loop
                                    }
                                }
                            } else {
                                if(calendarMainData.unclassifiedCategory) {
                                    if (content.position == contentPosition) {
                                        cloneLiveSchedule.remove(content)

                                        var color = Color.rgb(0x1D, 0xAF, 0xFF)

                                        if ((contentPosition + interval - 1) / 7 != contentPosition / 7) {
                                            val overflowvalue =
                                                contentPosition + interval - (contentPosition + interval) / 7 * 7
                                            saveCntList.add(overflowvalue)
                                            saveScheduleList.add(content.schedule)
                                            saveLineList.add(contentLine)

                                            positionplus += interval - overflowvalue - 1
                                            returnvalue = interval - overflowvalue
                                            cntlist.add(returnvalue)

                                            if (size == 5) {
                                                addViewFunction(
                                                    holder,
                                                    content.schedule.content,
                                                    (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                    (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                    returnvalue,
                                                    color,
                                                    content.schedule.completed
                                                )
                                            } else {
                                                addViewFunction(
                                                    holder,
                                                    content.schedule.content,
                                                    (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                    (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                    returnvalue,
                                                    color,
                                                    content.schedule.completed
                                                )
                                            }

                                            continue@loop
                                        }

                                        positionplus += interval - 1
                                        returnvalue = interval
                                        cntlist.add(returnvalue)

                                        if (size == 5) {
                                            addViewFunction(
                                                holder,
                                                content.schedule.content,
                                                (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                returnvalue,
                                                color,
                                                content.schedule.completed
                                            )
                                        } else {
                                            addViewFunction(
                                                holder,
                                                content.schedule.content,
                                                (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                                returnvalue,
                                                color,
                                                content.schedule.completed
                                            )
                                        }

                                        continue@loop
                                    }
                                }
                            }
                        }
                    }

                    if (calendarMainData.todoApply) {
                        if(cloneLiveTodo[contentPosition].todos.size > 0) {
                            if((cloneLiveTodo[contentPosition].todos[0].completed &&
                                    calendarMainData.todoComplete)||
                                (!cloneLiveTodo[contentPosition].todos[0].completed &&
                                        calendarMainData.todoInComplete)) {
                                if (size == 5) {
                                    addViewFunction(
                                        holder,
                                        cloneLiveTodo[contentPosition].todos[0].content,
                                        (contentPosition % 7) / 6f,
                                        (5 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        1,
                                        Color.rgb(0xED, 0xED, 0xED),
                                        cloneLiveTodo[contentPosition].todos[0].completed
                                    )
                                } else {
                                    addViewFunction(
                                        holder,
                                        cloneLiveTodo[contentPosition].todos[0].content,
                                        (contentPosition % 7) / 6f,
                                        (6 * (contentPosition / 7) + (contentLine + 1)) / 29f,
                                        1,
                                        Color.rgb(0xED, 0xED, 0xED),
                                        cloneLiveTodo[contentPosition].todos[0].completed
                                    )
                                }

                                cloneLiveTodo[contentPosition].todos.removeAt(0)
                            }
                        }
                    }
                }

                var allCntList = ArrayList<Int>()

                if(size == 4){
                    for(i in 0..34){
                        allCntList.add(0)
                    }
                } else if(size == 5){
                    for(i in 0..41){
                        allCntList.add(0)
                    }
                }

                if(calendarMainData.todoApply) {
                    for (i in 0 until cloneLiveTodo.size) {
                        for(j in 0 until cloneLiveTodo[i].todos.size) {
                            if ((cloneLiveTodo[i].todos[j].completed &&
                                        calendarMainData.todoComplete) ||
                                (!cloneLiveTodo[i].todos[j].completed &&
                                        calendarMainData.todoInComplete)
                            ) {
                                allCntList[i] += 1
                            }
                        }
                    }
                }

                if(calendarMainData.scheduleApply) {
                    for (k in 0 until cloneLiveSchedule.size) {
                        if(cloneLiveSchedule[k].schedule.category != null) {
                            if (cloneLiveSchedule[k].schedule.category!!.isSelected) {
                                var interval = 0

                                if(cloneLiveSchedule[k].cnt != null){
                                    interval = cloneLiveSchedule[k].cnt!!
                                } else {
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                                    val repeatStart = format.parse(cloneLiveSchedule[k].schedule.repeatStart)
                                    val calendar2 = Calendar.getInstance()
                                    calendar2.time = repeatStart
                                    calendar2.add(Calendar.MILLISECOND, cloneLiveSchedule[k].timeInterval!!)

                                    repeatStart.hours = 0
                                    repeatStart.minutes = 0
                                    repeatStart.seconds = 0

                                    calendar2.time.hours = 0
                                    calendar2.time.minutes = 0
                                    calendar2.time.seconds = 0

                                    val dateminus = (calendar2.time.time - repeatStart.time) / (60 * 60 * 24 * 1000)
                                    interval = dateminus.toInt()
                                }

                                for (j in cloneLiveSchedule[k].position until cloneLiveSchedule[k].position + interval) {
                                    if(allCntList.size <= j) break

                                    allCntList[j]++
                                }
                            }
                        } else {
                            if(calendarMainData.unclassifiedCategory){
                                var interval = 0

                                if(cloneLiveSchedule[k].cnt != null){
                                    interval = cloneLiveSchedule[k].cnt!!
                                } else {
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                                    val repeatStart = format.parse(cloneLiveSchedule[k].schedule.repeatStart)
                                    val calendar2 = Calendar.getInstance()
                                    calendar2.time = repeatStart
                                    calendar2.add(Calendar.MILLISECOND, cloneLiveSchedule[k].timeInterval!!)

                                    repeatStart.hours = 0
                                    repeatStart.minutes = 0
                                    repeatStart.seconds = 0

                                    calendar2.time.hours = 0
                                    calendar2.time.minutes = 0
                                    calendar2.time.seconds = 0

                                    val dateminus = (calendar2.time.time - repeatStart.time) / (60 * 60 * 24 * 1000)
                                    interval = dateminus.toInt()
                                }

                                for (j in cloneLiveSchedule[k].position until cloneLiveSchedule[k].position + interval) {
                                    if(allCntList.size <= j) break

                                    allCntList[j]++
                                }
                            }
                        }
                    }
                }

                for(i in 0 until allCntList.size) {
                    if (allCntList[i] > 0) {
                        if (size == 5) {
                            addViewFunction(
                                holder,
                                "+" + allCntList[i].toString(),
                                (i % 7) / 6f,
                                (5 * (i / 7) + 4) / 29f,
                                1,
                                Color.rgb(0xED, 0xED, 0xED),
                                false
                            )
                        } else if (size == 4) {
                            addViewFunction(
                                holder,
                                "+" + allCntList[i].toString(),
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