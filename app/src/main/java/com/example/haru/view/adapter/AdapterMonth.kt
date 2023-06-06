package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
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
import com.example.haru.utils.FormatDate
import com.example.haru.view.calendar.CalendarDetailDialog
import com.example.haru.view.calendar.CalendarFragment
import com.example.haru.view.calendar.TouchEventDecoration
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.view.checklist.CalendarAddFragment
import com.example.haru.viewmodel.CalendarViewModel
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.time.Month
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
    fun init_date(holder: MonthView, year:Int, month:Int, viewModel:CalendarViewModel,
                  callback:(start : Date) -> Unit): Int{
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

        val dateLayoutViewList = listOf(
            R.id.today_date_layout_one,
            R.id.today_date_layout_two,
            R.id.today_date_layout_three,
            R.id.today_date_layout_four,
            R.id.today_date_layout_five,
            R.id.today_date_layout_six,
            R.id.today_date_layout_seven,
            R.id.today_date_layout_eight,
            R.id.today_date_layout_nine,
            R.id.today_date_layout_ten,
            R.id.today_date_layout_eleven,
            R.id.today_date_layout_twelve,
            R.id.today_date_layout_thirteen,
            R.id.today_date_layout_fourteen,
            R.id.today_date_layout_fifteen,
            R.id.today_date_layout_sixteen,
            R.id.today_date_layout_seventeen,
            R.id.today_date_layout_eightteen,
            R.id.today_date_layout_nineteen,
            R.id.today_date_layout_twenty,
            R.id.today_date_layout_twentyone,
            R.id.today_date_layout_twentytwo,
            R.id.today_date_layout_twentythree,
            R.id.today_date_layout_twentyfour,
            R.id.today_date_layout_twentyfive,
            R.id.today_date_layout_twentysix,
            R.id.today_date_layout_twentyseven,
            R.id.today_date_layout_twentyeight,
            R.id.today_date_layout_twentynine,
            R.id.today_date_layout_thirty,
            R.id.today_date_layout_thirtyone,
            R.id.today_date_layout_thirtytwo,
            R.id.today_date_layout_thirtythree,
            R.id.today_date_layout_thirtyfour,
            R.id.today_date_layout_thirtyfive,
            R.id.today_date_layout_thirtysix,
            R.id.today_date_layout_thirtyseven,
            R.id.today_date_layout_thirtyeight,
            R.id.today_date_layout_thirtynine,
            R.id.today_date_layout_fourty,
            R.id.today_date_layout_fourtyone,
            R.id.today_date_layout_fourtytwo
        )

        val dateTextViews = ArrayList<TextView>()
        val dateLayoutViews = ArrayList<FrameLayout>()

        for(i in 0..41){
            dateTextViews.add(holder.itemView.findViewById<TextView>(dateTextViewList[i]))
            dateLayoutViews.add(holder.itemView.findViewById<FrameLayout>(dateLayoutViewList[i]))
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

                if(i >= 4 && k == 0 && calendar.get(Calendar.MONTH) != month){
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                    endDate = format.format(calendar.time)+"T23:59:59+09:00"
                    maxi = 4
                    breakOption = true
                }

                if(breakOption){
                    dateTextViews[i*7 + k].text = ""
                    continue
                }

                if(i == 0 && k == 0){
                    callback(calendar.time)
                    startDate = format.format(calendar.time)+"T00:00:00+09:00"
                }

                if(i == 5 && k == 6){
                    endDate = format.format(calendar.time)+"T23:59:59+09:00"
                }

                dateTextViews[i*7 + k].text = calendar.time.date.toString()
                dateArrayList.add(calendar.time)

                if(calendar.time.month != month){
                    dateTextViews[i*7 + k].setTextColor(Color.parseColor("#DBDBDB"))
                } else {
                    dateTextViews[i*7 + k].setTextColor(Color.parseColor("#646464"))
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
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0xDB,0xDB,0xDB))
                        } else {
                            dateTextViews[i*7 + k].setTextColor(Color.rgb(0x64,0x64,0x64))
                        }
                    }
                }

                if(calendar.time.year == Date().year &&
                    calendar.time.month == Date().month &&
                    calendar.time.date == Date().date
                ){
                    dateTextViews[i*7 + k].setTypeface(Typeface.SERIF, Typeface.BOLD)
                    dateTextViews[i*7 + k].setTextColor(Color.parseColor("#1DAFFF"))
                    dateLayoutViews[i*7 + k].background = ContextCompat.getDrawable(parentFragment.requireContext(),R.drawable.calendar_in_today_image)
                } else {
                    dateTextViews[i*7 + k].setTypeface(Typeface.SERIF, Typeface.NORMAL)
                    dateLayoutViews[i*7 + k].background = ContextCompat.getDrawable(parentFragment.requireContext(),R.color.white)
                }
            }

            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        if(maxi == 4){
            eraseableView.setBackgroundResource(R.color.white)

            dateLayoutFive.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = dateLayoutEnd.id
            }
        } else {
            eraseableView.setBackgroundColor(Color.parseColor("#DBDBDB"))

            dateLayoutFive.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = dateLayoutSix.id
            }
        }

        while (parentConstraintLayout.get(0).id != R.id.date_layout_start) {
            parentConstraintLayout.removeViewAt(0)
        }

        val touchList = ArrayList<Boolean>()

        if(maxi == 4){
            for(i in 0..34){
                touchList.add(false)
            }
        } else {
            for(i in 0..41){
                touchList.add(false)
            }
        }

        val adapter: AdapterCalendarTouch

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
                        var startPosition = startRow * 7 + startColumn
                        var endPosition = row * 7 + column

                        if(startPosition > endPosition){
                            val tmp = startPosition
                            startPosition = endPosition
                            endPosition = tmp
                        }

                        adapter.itemChange(startPosition, endPosition, true)

//                        for(i in startPosition..endPosition){
//                            if(!adapter.touchList[i]) {
//                                adapter.itemChange(i, true)
//                            }
//                        }
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

                            adapter.itemChange(startPosition, endPosition, false)

//                            for(i in startPosition..endPosition){
//                                if(adapter.touchList[i]) {
//                                    adapter.itemChange(i, false)
//                                }
//                            }

                            val scheduleInput = CalendarAddFragment(
                                categories,
                                dateArrayList[startPosition],
                                dateArrayList[endPosition]
                            ){
                                notifyDataSetChanged()
                            }

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
                            fragment,
                            categories
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
                        line:Int,
                        count:Int,
                        width:Int,
                        color:Int,
                        completed:Boolean,
                        size: Int,
                        layoutInterval: Int,
                        maxTextCount: Int,
                        contentPosition: Int,
                        startDate: Date){
        val parentConstraintLayout = holder.itemView.findViewById<ConstraintLayout>(R.id.parent_contraint_layout)

        val dateLayoutOne = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_one)
        val dateLayoutTwo = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_two)
        val dateLayoutThree = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_three)
        val dateLayoutFour = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_four)
        val dateLayoutFive = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_five)
        val dateLayoutSix = holder.itemView.findViewById<LinearLayout>(R.id.date_layout_six)

        val colorList = listOf(
            "#2E2E2E", "#656565", "#818181", "#9D9D9D", "#B9B9B9", "#D5D5D5",
            "#FF0959", "#FF509C", "#FF5AB6", "#FE7DCD", "#FFAAE5", "#FFBDFB",
            "#B237BB", "#C93DEB", "#B34CED", "#9D5BE3", "#BB93F8", "#C6B2FF",
            "#4C45FF", "#2E57FF", "#4D8DFF", "#45BDFF", "#6DDDFF", "#65F4FF",
            "#FE7E7E", "#FF572E", "#C22E2E", "#A07553", "#E3942E", "#E8A753",
            "#FF892E", "#FFAB4C", "#FFD166", "#FFDE2E", "#CFE855", "#B9D32E",
            "#105C08", "#39972E", "#3EDB67", "#55E1B6", "#69FFD0", "#05C5C0",
        )

        val colors = ArrayList<Int>()

        for (i in colorList){
            colors.add(Color.parseColor(i))
        }

        val layoutList = listOf(
            dateLayoutOne,
            dateLayoutTwo,
            dateLayoutThree,
            dateLayoutFour,
            dateLayoutFive,
            dateLayoutSix
        )

        val lastView = holder.itemView.findViewById<View>(R.id.date_layout_end)

        val testView = TextView(holder.itemView.context)

        val layoutParams = ConstraintLayout.LayoutParams(
            parentConstraintLayout.width/7*width-2,
            dateLayoutOne.height/2
        )

        layoutParams.leftToLeft = parentConstraintLayout.id
        layoutParams.rightToRight = parentConstraintLayout.id
        layoutParams.horizontalBias = x

        layoutParams.topToBottom = layoutList[line].id

        if(line == size) {
            layoutParams.bottomToTop = lastView.id
        } else {
            layoutParams.bottomToTop = layoutList[line+1].id
        }

        val vertical = (count*15).toFloat()/(layoutInterval-15).toFloat()

        layoutParams.verticalBias = vertical

        testView.setBackgroundResource(R.drawable.calendar_textview_border)
        val drawable = testView.background as GradientDrawable
        drawable.setColorFilter(color,PorterDuff.Mode.SRC_ATOP)

        testView.includeFontPadding = false
        testView.gravity = Gravity.CENTER
        testView.setText(textViewText)
        testView.setTextSize(Dimension.SP,12.0f)

        val calendar = Calendar.getInstance()
        val thisMonth = Calendar.getInstance()
        thisMonth.time = startDate.clone() as Date
        calendar.time = startDate.clone() as Date
        calendar.add(Calendar.DATE, contentPosition)
        val start = calendar.time.clone() as Date

        calendar.add(Calendar.DATE, width-1)

        while (thisMonth.time.date != 1){
            thisMonth.add(Calendar.DATE, 1)
        }

        if(colors.indexOf(color) in listOf(
                0,1,2,6,7,8,12,13,14,15,
                18,19,20,24,25,26,27,30,31,32,
                36,37,38
        ) || color == Color.parseColor("#F71E58")){
            testView.setTextColor(Color.parseColor("#FDFDFD"))
        } else{
            testView.setTextColor(Color.parseColor("#191919"))
        }

        if(completed){
            testView.alpha = 0.7f
            testView.setPaintFlags(testView.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        } else {
            if (thisMonth.time.month != calendar.time.month && thisMonth.time.month != start.month){
                testView.alpha = 0.7f
            } else {
                testView.alpha = 1f
            }
        }

        testView.layoutParams = layoutParams

        parentConstraintLayout.addView(testView, 0)
    }

    fun pxToDp(px: Float): Int {
        val resources: Resources = Resources.getSystem()
        val metrics: DisplayMetrics = resources.getDisplayMetrics()
        return (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    fun setHoliDays(holder: MonthView, start: Date, holidays: List<HolidayData>, month: Int){
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

        val calendar = Calendar.getInstance()

        val holidaysList = ArrayList<HolidayData>()

        for (holiday in holidays){
            holidaysList.add(holiday.copy())
        }

        calendar.time = start
        calendar.set(Calendar.HOUR_OF_DAY, 2)

        for (i in 0 until  42){
            if(holidaysList.size == 0) break

            for(holiday in holidaysList) {
                if(holiday.position == i){
                    for(z in 0 until holiday.cnt) {
                        if (calendar.get(Calendar.MONTH) != month) {
                            if (calendar.time.year != Date().year ||
                                calendar.time.month != Date().month ||
                                calendar.time.date != Date().date
                            ) {
                                dateTextViews[i+z].setTextColor(Color.rgb(0xFD, 0xBB, 0xCD))
                            }
                        } else {
                            if (calendar.time.year != Date().year ||
                                calendar.time.month != Date().month ||
                                calendar.time.date != Date().date
                            ) {
                                dateTextViews[i+z].setTextColor(Color.rgb(0xF7, 0x1E, 0x58))
                            }
                        }
                    }

                    break
                }
            }

            calendar.add(Calendar.DATE, 1)
        }
    }

    //레이아웃이 완전히 확립이 안됐을 때 실행이 돼
    fun setView(holder:MonthView, position: Int){
        val calendar = Calendar.getInstance()

        var calendarviewModel = CalendarViewModel()

        calendar.time = Date()
        calendar.add(Calendar.MONTH, position - Int.MAX_VALUE / 2)

        var startDate = Date()

        var size = init_date(holder, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendarviewModel){
            startDate = it
        }

        calendarviewModel.liveTodoCalendarList.observe(lifecycleOwner) { livetodo ->
            calendarviewModel.liveScheduleCalendarList.observe(lifecycleOwner) { liveschedule ->
                calendarviewModel.liveHolidaysList.observe(lifecycleOwner) { liveholiday ->
                    setHoliDays(holder, startDate, liveholiday, calendar.get(Calendar.MONTH))

                    val dateLayoutOne =
                        holder.itemView.findViewById<LinearLayout>(R.id.date_layout_one)
                    val dateLayoutTwo =
                        holder.itemView.findViewById<LinearLayout>(R.id.date_layout_two)

                    dateLayoutTwo.post{
                        //레이아웃 사이 거리 변수 /15 할 시 몇 개의 TextView가 들어갈 수 있는지 구할 수 있음
                        val locationInterval = pxToDp(dateLayoutTwo.y - dateLayoutOne.y) - 28
                        val maxTextCount = locationInterval / 15

                        var cloneLiveTodo = livetodo as ArrayList
                        var cloneLiveSchedule = liveschedule as ArrayList
                        val cloneLiveHoliday = liveholiday as ArrayList

                        Log.d("공휴일", cloneLiveHoliday.toString())

                        var spanList = ArrayList<Int>()

                        var cntlist = ArrayList<Int>()
                        var positionplus = 0

                        var saveLineList = ArrayList<Int>()
                        var saveCntList = ArrayList<Int>()
                        var saveScheduleList = ArrayList<Schedule?>()

                        var position2 = -1

                        loop@ while (true) {
                            position2++
                            val newpostion = positionplus + position2

                            if (newpostion >= (maxTextCount + 1) * (size + 1) * 7) break

                            if ((newpostion / 7) % (maxTextCount + 1) == 0) continue

                            val cyclevalue = newpostion / ((maxTextCount + 1) * 7) * 7

                            var contentPosition = cyclevalue + newpostion % 7
                            var contentLine = (newpostion / 7) % (maxTextCount + 1) - 1

                            if (contentLine >= maxTextCount - 1) continue

                            if (newpostion % 7 == 0 && saveLineList.contains(contentLine)) {
                                val index = saveLineList.indexOf(contentLine)

                                if (saveCntList[index] > 7) {
                                    saveCntList[index] -= 7
                                    positionplus += 6
                                    spanList.add(7)

                                    var color = Color.rgb(0xAA, 0xD7, 0xFF)

                                    if(saveScheduleList[index] == null){
                                        color = Color.rgb(0xF7, 0x1E, 0x58)
                                    } else {
                                        if (saveScheduleList[index]!!.category != null &&
                                            saveScheduleList[index]!!.category!!.color != null
                                        ) {
                                            color =
                                                Color.parseColor(saveScheduleList[index]!!.category!!.color)
                                        }
                                    }

                                    addViewFunction(
                                        holder,
                                        "",
                                        0f,
                                        contentPosition / 7,
                                        contentLine,
                                        7,
                                        color,
                                        false,
                                        size,
                                        locationInterval,
                                        maxTextCount,
                                        contentPosition,
                                        startDate
                                    )

                                    continue
                                } else {
                                    val returncnt = saveCntList[index]

                                    var color = Color.rgb(0xAA, 0xD7, 0xFF)

                                    if(saveScheduleList[index] == null){
                                        color = Color.rgb(0xF7, 0x1E, 0x58)
                                    } else {
                                        if (saveScheduleList[index]!!.category != null &&
                                            saveScheduleList[index]!!.category!!.color != null
                                        ) {
                                            color =
                                                Color.parseColor(saveScheduleList[index]!!.category!!.color)
                                        }
                                    }

                                    saveCntList.removeAt(index)
                                    saveScheduleList.removeAt(index)
                                    saveLineList.removeAt(index)

                                    positionplus += returncnt - 1
                                    spanList.add(returncnt)

                                    addViewFunction(
                                        holder,
                                        "",
                                        0f,
                                        contentPosition / 7,
                                        contentLine,
                                        returncnt,
                                        color,
                                        false,
                                        size,
                                        locationInterval,
                                        maxTextCount,
                                        contentPosition,
                                        startDate
                                    )

                                    continue
                                }
                            }

                            var returnvalue = 1

                            if (calendarMainData.holidayCategory && calendarMainData.scheduleApply) {
                                for (content in cloneLiveHoliday) {
                                    val interval = content.cnt

                                    if (content.position == contentPosition) {
                                        cloneLiveHoliday.remove(content)
                                        val color =
                                            Color.parseColor("#F71E58")

                                        if ((contentPosition + interval - 1) / 7 != contentPosition / 7) {
                                            val overflowvalue =
                                                contentPosition + interval - (contentPosition / 7 + 1) * 7

                                            saveCntList.add(overflowvalue)
                                            saveScheduleList.add(null)
                                            saveLineList.add(contentLine)

                                            positionplus += interval - overflowvalue - 1
                                            returnvalue = interval - overflowvalue
                                            cntlist.add(returnvalue)

                                            addViewFunction(
                                                holder,
                                                content.holiday.content,
                                                (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                contentPosition / 7,
                                                contentLine,
                                                returnvalue,
                                                color,
                                                false,
                                                size,
                                                locationInterval,
                                                maxTextCount,
                                                contentPosition,
                                                startDate
                                            )

                                            continue@loop
                                        }

                                        positionplus += interval - 1
                                        returnvalue = interval
                                        cntlist.add(returnvalue)

                                        addViewFunction(
                                            holder,
                                            content.holiday.content,
                                            (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                            contentPosition / 7,
                                            contentLine,
                                            returnvalue,
                                            color,
                                            false,
                                            size,
                                            locationInterval,
                                            maxTextCount,
                                            contentPosition,
                                            startDate
                                        )

                                        continue@loop
                                    }
                                }
                            }

                            if (calendarMainData.scheduleApply) {
                                for (content in cloneLiveSchedule) {
                                    var interval = 0

                                    if (content.cnt != null) {
                                        interval = content.cnt!!
                                    } else {
                                        val format = SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                            Locale.KOREAN
                                        )
                                        val repeatStart = format.parse(content.schedule.repeatStart)
                                        val calendar2 = Calendar.getInstance()
                                        calendar2.time = repeatStart
                                        calendar2.add(Calendar.SECOND, content.timeInterval!!)

                                        repeatStart.hours = 0
                                        repeatStart.minutes = 0
                                        repeatStart.seconds = 0

                                        calendar2.time.hours = 0
                                        calendar2.time.minutes = 0
                                        calendar2.time.seconds = 0

                                        val dateminus =
                                            (calendar2.time.time - repeatStart.time) / (60 * 60 * 24 * 1000)
                                        interval = dateminus.toInt() + 1
                                    }

                                    if (content.schedule.category != null) {
                                        if (content.schedule.category.isSelected) {
                                            if (content.position == contentPosition) {
                                                cloneLiveSchedule.remove(content)

                                                val color =
                                                    Color.parseColor(content.schedule.category.color)

                                                if ((contentPosition + interval - 1) / 7 != contentPosition / 7) {
                                                    val overflowvalue =
                                                        contentPosition + interval - (contentPosition / 7 + 1) * 7
//                                                    contentPosition + interval - (contentPosition + interval) / 7 * 7
                                                    //overflowvalue = 26 + 9 - (26+9)
                                                    saveCntList.add(overflowvalue)
                                                    saveScheduleList.add(content.schedule)
                                                    saveLineList.add(contentLine)

                                                    positionplus += interval - overflowvalue - 1
                                                    returnvalue = interval - overflowvalue
                                                    cntlist.add(returnvalue)

                                                    addViewFunction(
                                                        holder,
                                                        content.schedule.content,
                                                        (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                        contentPosition / 7,
                                                        contentLine,
                                                        returnvalue,
                                                        color,
                                                        false,
                                                        size,
                                                        locationInterval,
                                                        maxTextCount,
                                                        contentPosition,
                                                        startDate
                                                    )

                                                    continue@loop
                                                }

                                                positionplus += interval - 1
                                                returnvalue = interval
                                                cntlist.add(returnvalue)

                                                addViewFunction(
                                                    holder,
                                                    content.schedule.content,
                                                    (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                    contentPosition / 7,
                                                    contentLine,
                                                    returnvalue,
                                                    color,
                                                    false,
                                                    size,
                                                    locationInterval,
                                                    maxTextCount,
                                                    contentPosition,
                                                    startDate
                                                )

                                                continue@loop
                                            }
                                        }
                                    } else {
                                        if (calendarMainData.unclassifiedCategory) {
                                            if (content.position == contentPosition) {
                                                cloneLiveSchedule.remove(content)

                                                var color = Color.rgb(0xAA, 0xD7, 0xFF)

                                                if ((contentPosition + interval - 1) / 7 != contentPosition / 7) {
                                                    val overflowvalue =
                                                        contentPosition + interval - (contentPosition / 7 + 1) * 7
                                                    saveCntList.add(overflowvalue)
                                                    saveScheduleList.add(content.schedule)
                                                    saveLineList.add(contentLine)

                                                    positionplus += interval - overflowvalue - 1
                                                    returnvalue = interval - overflowvalue
                                                    cntlist.add(returnvalue)

                                                    addViewFunction(
                                                        holder,
                                                        content.schedule.content,
                                                        (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                        contentPosition / 7,
                                                        contentLine,
                                                        returnvalue,
                                                        color,
                                                        false,
                                                        size,
                                                        locationInterval,
                                                        maxTextCount,
                                                        contentPosition,
                                                        startDate
                                                    )

                                                    continue@loop
                                                }

                                                positionplus += interval - 1
                                                returnvalue = interval
                                                cntlist.add(returnvalue)

                                                addViewFunction(
                                                    holder,
                                                    content.schedule.content,
                                                    (contentPosition % 7) / (7 - returnvalue).toFloat(),
                                                    contentPosition / 7,
                                                    contentLine,
                                                    returnvalue,
                                                    color,
                                                    false,
                                                    size,
                                                    locationInterval,
                                                    maxTextCount,
                                                    contentPosition,
                                                    startDate
                                                )

                                                continue@loop
                                            }
                                        }
                                    }
                                }
                            }

                            if (calendarMainData.todoApply) {
                                if (cloneLiveTodo[contentPosition].todos.size > 0) {
                                    if ((cloneLiveTodo[contentPosition].todos[0].completed &&
                                                calendarMainData.todoComplete) ||
                                        (!cloneLiveTodo[contentPosition].todos[0].completed &&
                                                calendarMainData.todoInComplete)
                                    ) {
                                        addViewFunction(
                                            holder,
                                            cloneLiveTodo[contentPosition].todos[0].content,
                                            (contentPosition % 7) / 6f,
                                            contentPosition / 7,
                                            contentLine,
                                            1,
                                            Color.rgb(0xED, 0xED, 0xED),
                                            cloneLiveTodo[contentPosition].todos[0].completed,
                                            size,
                                            locationInterval,
                                            maxTextCount,
                                            contentPosition,
                                            startDate
                                        )

                                        cloneLiveTodo[contentPosition].todos.removeAt(0)
                                    }
                                }
                            }
                        }

                        var allCntList = ArrayList<Int>()

                        if (size == 4) {
                            for (i in 0..34) {
                                allCntList.add(0)
                            }
                        } else if (size == 5) {
                            for (i in 0..41) {
                                allCntList.add(0)
                            }
                        }

                        if (calendarMainData.todoApply) {
                            for (i in 0 until cloneLiveTodo.size) {
                                for (j in 0 until cloneLiveTodo[i].todos.size) {
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

                        if (calendarMainData.scheduleApply) {
                            for (k in 0 until cloneLiveSchedule.size) {
                                if (cloneLiveSchedule[k].schedule.category != null) {
                                    if (cloneLiveSchedule[k].schedule.category!!.isSelected) {
                                        var interval = 0

                                        if (cloneLiveSchedule[k].cnt != null) {
                                            interval = cloneLiveSchedule[k].cnt!!
                                        } else {
                                            val format = SimpleDateFormat(
                                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                                Locale.KOREAN
                                            )
                                            val repeatStart =
                                                format.parse(cloneLiveSchedule[k].schedule.repeatStart)
                                            val calendar2 = Calendar.getInstance()
                                            calendar2.time = repeatStart
                                            calendar2.add(
                                                Calendar.SECOND,
                                                cloneLiveSchedule[k].timeInterval!!
                                            )

                                            repeatStart.hours = 0
                                            repeatStart.minutes = 0
                                            repeatStart.seconds = 0

                                            calendar2.time.hours = 0
                                            calendar2.time.minutes = 0
                                            calendar2.time.seconds = 0

                                            val dateminus =
                                                (calendar2.time.time - repeatStart.time) / (60 * 60 * 24 * 1000)
                                            interval = dateminus.toInt() + 1
                                        }

                                        for (j in cloneLiveSchedule[k].position until cloneLiveSchedule[k].position + interval) {
                                            if (allCntList.size <= j) break

                                            allCntList[j]++
                                        }
                                    }
                                } else {
                                    if (calendarMainData.unclassifiedCategory) {
                                        var interval = 0

                                        if (cloneLiveSchedule[k].cnt != null) {
                                            interval = cloneLiveSchedule[k].cnt!!
                                        } else {
                                            val format = SimpleDateFormat(
                                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                                Locale.KOREAN
                                            )
                                            val repeatStart =
                                                format.parse(cloneLiveSchedule[k].schedule.repeatStart)
                                            val calendar2 = Calendar.getInstance()
                                            calendar2.time = repeatStart
                                            calendar2.add(
                                                Calendar.SECOND,
                                                cloneLiveSchedule[k].timeInterval!!
                                            )

                                            repeatStart.hours = 0
                                            repeatStart.minutes = 0
                                            repeatStart.seconds = 0

                                            calendar2.time.hours = 0
                                            calendar2.time.minutes = 0
                                            calendar2.time.seconds = 0

                                            val dateminus =
                                                (calendar2.time.time - repeatStart.time) / (60 * 60 * 24 * 1000)
                                            interval = dateminus.toInt()
                                        }

                                        for (j in cloneLiveSchedule[k].position until cloneLiveSchedule[k].position + interval) {
                                            if (allCntList.size <= j) break

                                            allCntList[j]++
                                        }
                                    }
                                }
                            }
                        }

                        for (i in 0 until allCntList.size) {
                            if (allCntList[i] > 0) {
                                if (size == 5) {
                                    addViewFunction(
                                        holder,
                                        "+" + allCntList[i].toString(),
                                        (i % 7) / 6f,
                                        i / 7,
                                        maxTextCount - 1,
                                        1,
                                        Color.rgb(0xED, 0xED, 0xED),
                                        false,
                                        size,
                                        locationInterval,
                                        maxTextCount,
                                        i,
                                        startDate
                                    )
                                } else if (size == 4) {
                                    addViewFunction(
                                        holder,
                                        "+" + allCntList[i].toString(),
                                        (i % 7) / 6f,
                                        i / 7,
                                        maxTextCount - 1,
                                        1,
                                        Color.rgb(0xED, 0xED, 0xED),
                                        false,
                                        size,
                                        locationInterval,
                                        maxTextCount,
                                        i,
                                        startDate
                                    )
                                }
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