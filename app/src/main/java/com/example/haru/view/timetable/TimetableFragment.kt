package com.example.haru.view.timetable

import BaseActivity
import android.content.ClipData
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.App
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentTimetableBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.TimetableAdapter
import com.example.haru.view.checklist.CalendarAddFragment
import com.example.haru.view.customDialog.CustomCalendarDialog
import com.example.haru.view.sns.SearchFragment
import com.example.haru.viewmodel.CalendarViewModel
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TimeTableRecyclerViewModel
import com.example.haru.viewmodel.TimetableViewModel
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

//드래그 앤 드롭 그림자 없어지게 하는 클래스
class EmptyShadowBuilder(view: View) : View.DragShadowBuilder(view) {
    override fun onDrawShadow(canvas: Canvas?) {}
}

class TimetableFragment : Fragment() {
    private lateinit var binding: FragmentTimetableBinding
    private lateinit var timetableviewModel: TimetableViewModel
    private lateinit var reviewModel: TimeTableRecyclerViewModel
    private lateinit var checkListViewModel: CheckListViewModel
    private lateinit var timetableAdapter: TimetableAdapter
    var timeList: ArrayList<timetable_data> = ArrayList()
    lateinit var timeTableRecyclerView: RecyclerView
    private lateinit var scheduleDrag: TimeTableScheduleDragListener
    val scheduleViewList: ArrayList<View> = ArrayList<View>()
    val scheduleMap = mutableMapOf<View, Schedule>()
    val layoutId: ArrayList<Int> = ArrayList<Int>()
    private lateinit var displayMetrics: DisplayMetrics

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): TimetableFragment {
            return TimetableFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewModel = TimeTableRecyclerViewModel()
        checkListViewModel = CheckListViewModel()
        Log.d(TAG, "TimetableFragment - onCreate() called")
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.timetableHeader.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayMetrics = resources.displayMetrics
        MainActivity.hideNavi(false)
        (activity as BaseActivity).adjustTopMargin(binding.timetableHeader.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "TimetableFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timetable, container, false)
        val rootView = binding.root
        timetableviewModel = TimetableViewModel(requireContext())
        binding.viewModel = timetableviewModel

        reviewModel.init_value()

        timetableAdapter =
            TimetableAdapter(requireContext(), reviewModel.TimeList.value ?: timeList)
        timeTableRecyclerView = binding.timetableRecyclerview
        timeTableRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        timeTableRecyclerView.adapter = timetableAdapter
        timetableviewModel.init_value()


        //타임테이블 frameLayout 프래그먼트 아이디 값
        layoutId.add(binding.sunTable.id)
        layoutId.add(binding.monTable.id)
        layoutId.add(binding.tueTable.id)
        layoutId.add(binding.wedTable.id)
        layoutId.add(binding.thuTable.id)
        layoutId.add(binding.friTable.id)
        layoutId.add(binding.satTable.id)

        scheduleDrag = TimeTableScheduleDragListener(timetableviewModel, layoutId, requireContext(), binding.timetableScroll)

        //각 레이아웃에 드래그 앤 드롭을 위한 리스너 등록
        binding.sunTable.setOnDragListener(scheduleDrag)
        binding.monTable.setOnDragListener(scheduleDrag)
        binding.tueTable.setOnDragListener(scheduleDrag)
        binding.wedTable.setOnDragListener(scheduleDrag)
        binding.thuTable.setOnDragListener(scheduleDrag)
        binding.friTable.setOnDragListener(scheduleDrag)
        binding.satTable.setOnDragListener(scheduleDrag)

        binding.timetableScroll.setOnDragListener { _, event ->
            val boxesLayoutCoords = intArrayOf(0, 0)
            binding.timetableScroll.getLocationInWindow(boxesLayoutCoords)
            when (event.action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
                    checkForScroll(event.y, boxesLayoutCoords[1])
                    true
                }
                else -> true
            }
        }

        //타임테이블 리사이클러뷰 실행
        reviewModel.TimeList.observe(viewLifecycleOwner) { schedule ->
            timetableAdapter.setData(schedule)
            scroll() // 화면에 오후 12시가 중앙에 오도록
            timetableAdapter.notifyDataSetChanged()
        }

        //날짜가 바뀌면 12시를 화면 중앙에 두도록
        timetableviewModel.Selected.observe(viewLifecycleOwner) { schedule ->
            scroll()
        }
        //지난달 다음달을 구분해주는 색 바인딩
        timetableviewModel.Colors.observe(viewLifecycleOwner) { colors ->
            binding.sunBtn.setTextColor(Color.parseColor(colors.get(0)))
            binding.monBtn.setTextColor(Color.parseColor(colors.get(1)))
            binding.tueBtn.setTextColor(Color.parseColor(colors.get(2)))
            binding.wedBtn.setTextColor(Color.parseColor(colors.get(3)))
            binding.thuBtn.setTextColor(Color.parseColor(colors.get(4)))
            binding.friBtn.setTextColor(Color.parseColor(colors.get(5)))
            binding.satBtn.setTextColor(Color.parseColor(colors.get(6)))
        }
        //한주의 숫자 변경시 최신화
        timetableviewModel.Days.observe(viewLifecycleOwner) { days ->
            binding.invalidateAll()
        }

        timetableviewModel.liveCategoryList.observe(viewLifecycleOwner){
            Log.d(TAG, "onCreateView: ${it}")
        }

        //그리드에 그려지는 스케줄을 타임테이블에 바인딩
        timetableviewModel.categoryAndSchedulesCombinedData.observe(viewLifecycleOwner) { (categories, schedules) ->
            scheduleMap.clear()
            binding.sunTable.removeAllViews()
            binding.monTable.removeAllViews()
            binding.tueTable.removeAllViews()
            binding.wedTable.removeAllViews()
            binding.thuTable.removeAllViews()
            binding.friTable.removeAllViews()
            binding.satTable.removeAllViews()
            drawTimesSchedules(binding.sunTable, categories, schedules[0])
            drawTimesSchedules(binding.monTable, categories, schedules[1])
            drawTimesSchedules(binding.tueTable, categories, schedules[2])
            drawTimesSchedules(binding.wedTable, categories, schedules[3])
            drawTimesSchedules(binding.thuTable, categories, schedules[4])
            drawTimesSchedules(binding.friTable, categories, schedules[5])
            drawTimesSchedules(binding.satTable, categories, schedules[6])
        }

        //하루종일 or 2일이상 일정을 바인딩
        timetableviewModel.categoryAndSchedulesAlldayCombinedData.observe(viewLifecycleOwner) { (categories, scheduleCalendarData) ->
            binding.daysTable.removeAllViews()
            drawDaysSchedule(binding.daysTable, categories, scheduleCalendarData, timetableviewModel.getDates())
        }

        //드래그 앤 드랍시 이동한 뷰의 정보
        timetableviewModel.MoveView.observe(viewLifecycleOwner) { view ->
            val movedData = scheduleMap[view]!!
            val start = timetableviewModel.MoveDate.value!!

            timetableviewModel.viewModelScope.launch {
                timetableviewModel.patchMoved(start, movedData){
                    com.example.haru.utils.Alarm.initAlarm(viewLifecycleOwner, requireContext())
                }
            }
        }

        binding.todolistChange.setOnClickListener {
            val newFrag = TodotableFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        //타임테이블 왼쪽 1시부터 24시 시간 표기
        drawLeftTime()



        binding.btnAddSchedule.setOnClickListener {
            val calendarViewModel = CalendarViewModel()

            calendarViewModel.getCategories()

            calendarViewModel.liveCategoryList.observe(viewLifecycleOwner){
                Log.d(TAG, "onCreateView: ${it}")

                val scheduleInput = CalendarAddFragment(
                    null,
                    null,
                    requireContext(),
                    viewLifecycleOwner
                ){
                    timetableviewModel.getSchedule(timetableviewModel.Datelist)
                }

                scheduleInput.show(parentFragmentManager, scheduleInput.tag)
            }
        }


        binding.timetableMonthChooseLayout.setOnClickListener {
            val arrowImv = binding.timetableMonthChooseLayout.getChildAt(2) as ImageView
            arrowImv.rotation = 90f

            val datePicker = CustomCalendarDialog()
            datePicker.calendarClick =
                object : CustomCalendarDialog.CalendarClickListener {
                    override fun onClick(view: View, year: Int, month: Int, day: Int) {
                        timetableviewModel.init_value(year, month, day)
                        arrowImv.rotation = 0f
                    }
                }
            datePicker.cancelListener =
                object : CustomCalendarDialog.CancelListener {
                    override fun onClick() {
                        arrowImv.rotation = 0f
                    }
                }
            datePicker.show(parentFragmentManager, null)
        }


        binding.timetableSearchButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragments_frame, SearchFragment(checkListViewModel))
                .addToBackStack(null)
                .commit()
        }

        return rootView
    }


    fun scroll() {
        val child = timeTableRecyclerView.getChildAt(8)
        val originalPos = IntArray(2)
        if (child != null) {
            child.getLocationInWindow(originalPos)
        }
        binding.timetableScroll.smoothScrollTo(0, originalPos[1], 1000)
        binding.invalidateAll()
    }

    //하루이상의 할일을 동적으로 바인딩
    private fun drawDaysSchedule(table: ViewGroup, categories: List<Category>?, scheduleCalendarDataList: ArrayList<ScheduleCalendarData>, dates: ArrayList<String>) {
        val displayMetrics = resources.displayMetrics
        val deleteSchedule = ArrayList<ScheduleCalendarData>()

        while (scheduleCalendarDataList.size > 0) {
            val layout1 = FrameLayout(requireContext())
            val rowParams1 = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Math.round(18 * displayMetrics.density))
            layout1.layoutParams = rowParams1

            var occupied = 0
            deleteSchedule.clear()

            for (scheduleCalendarData in scheduleCalendarDataList) {
                val schedule = scheduleCalendarData.schedule
                /* 반복 일정이 아닌 경우 */

                var startDate = schedule.repeatStart?.slice(IntRange(0, 3)) + schedule.repeatStart?.slice(IntRange(5, 6)) + schedule.repeatStart?.slice(IntRange(8, 9))
                var endDate = schedule.repeatEnd?.slice(IntRange(0, 3)) + schedule.repeatEnd?.slice(IntRange(5, 6)) + schedule.repeatEnd?.slice(IntRange(8, 9))

                // 반복 일정인 경우 -> startDate, endDate를 다시 구하기
                if(!schedule.repeatValue.isNullOrEmpty()){
                    scheduleCalendarData.position

                    /* 반복 옵션의 현재 week에서 시작 날짜 구하기 */
                    val currentWeekValue = timetableviewModel.Selected.value
                    val year = currentWeekValue?.year?.dropLast(1).toString().padStart(4, '0')
                    val week = currentWeekValue?.month?.dropLast(1).toString().padStart(2, '0')
                    val date = timetableviewModel.Days.value?.get(scheduleCalendarData.position)?.padStart(2, '0')

                    startDate = year+week+date

                    // Reformat newDate to match the format in the originalDateTime
                    val reformattedNewDate = "${year}-${week}-${date}"
                    val newDateTime = "${reformattedNewDate}${schedule.repeatStart?.substring(10)}" // "2024-05-12T21:00:00+09:00"
                    val originalDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")
                    val originalDateTime = OffsetDateTime.parse(newDateTime, originalDateTimeFormatter)

                    // Add seconds
                    val newEndDateTime = schedule.repeatValue.drop(1).toLong().let { originalDateTime.plusSeconds(it/1000) }

                    // Convert new date-time back to string
                    val newDateTimeString = newEndDateTime?.format(originalDateTimeFormatter)
                    endDate = newDateTimeString?.slice(IntRange(0, 3)) + newDateTimeString?.slice(IntRange(5, 6)) + newDateTimeString?.slice(IntRange(8, 9))
                }

                if (startDate.toInt() > occupied) {
                    val period = (dates.indexOf(endDate) - dates.indexOf(startDate)) + 1
                    val position = dates.indexOf(startDate)

                    layout1.addView(makeInnerLinearLayoutInDrawDaySchedules(schedule, categories, position, period))
                    deleteSchedule.add(scheduleCalendarData)
                    occupied = endDate.toInt()
                }
            }

            for (item in deleteSchedule) {
//                Log.d(TAG, "DrawDays: ${item.repeatStart}")
                scheduleCalendarDataList.remove(item)
            }
            table.addView(layout1)
        }
    }

    private fun makeInnerLinearLayoutInDrawDaySchedules(day : Schedule, categories: List<Category>?, position:Int, period : Int): LinearLayout{
        val layout2 = LinearLayout(requireContext())
        val rowParams2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layout2.layoutParams = rowParams2

        val view = TextView(requireContext())
        view.text = day.content
        view.textSize = 12f
        view.gravity = Gravity.CENTER
        view.setPadding(2, 2, 2, 2)
        view.maxLines = 1

        // 카테고리, 글자색 색칠하기
        colorCategoryAndTextColorOnScheduleView(view, day, categories, 2, 4)

        val frontPadding = View(requireContext())
        val backPadding = View(requireContext())
        view.setOnClickListener {
            Toast.makeText(requireContext(), "${day.content}", Toast.LENGTH_SHORT)
                .show()
        }

        val frontParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            position.toFloat()
        )
        val backParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            (7 - (position + period).toFloat())
        )
        val itemParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            period.toFloat()
        )

        view.layoutParams = itemParams
        frontPadding.layoutParams = frontParams
        backPadding.layoutParams = backParams
        layout2.addView(frontPadding)
        layout2.addView(view)
        layout2.addView(backPadding)

        return layout2
    }


    //하나의 날짜내에서 그려지는 시간 일정을 동적으로 바인딩
    private fun drawTimesSchedules(table: ViewGroup, categories: List<Category>?, schedules: ArrayList<Schedule>) {
        var pastStart = 0
        var pastEnd = 2400
        val unionList = ArrayList<ArrayList<Schedule>>()
        val overlapList = ArrayList<Schedule>()

        /* 겹쳐지는 일정들 unionList로 만들기 */
        for (schedule in schedules) {
            val repeatStart = schedule.repeatStart!!
            val repeatEnd = schedule.repeatEnd!!



            val startTime = (repeatStart.slice(IntRange(11, 12)) + repeatStart.slice(IntRange(14, 15))).toInt()
            val endTime = (repeatEnd.slice(IntRange(11, 12)) + repeatEnd.slice(IntRange(14, 15))).toInt()

            Log.d("drawTimesSchedules", "drawTimesSchedules: ${repeatStart} ${repeatEnd} ${schedule.content} ${startTime} ${endTime}")
            if (startTime in pastStart until pastEnd) {
                overlapList.add(schedule)
            } else {
                unionList.add(ArrayList(overlapList))
                overlapList.clear()
                overlapList.add(schedule)
            }

            pastStart = startTime
            pastEnd = endTime
        }
        unionList.add(overlapList)

        for (union in unionList) {
            /* 부모 리니어 레이아웃 */
            val unionLinearLayout = LinearLayout(requireContext())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unionLinearLayout.layoutParams = layoutParams
//            unionLinearLayout.setBackgroundColor(Color.parseColor("#000000"))

            for (schedule in union) {
                val scheduleView = makeScheduleViewInDrawTimesSchedule(schedule, categories)
                scheduleViewList.add(scheduleView)
                scheduleMap.put(scheduleView, schedule)
                unionLinearLayout.addView(scheduleView)
            }

            table.addView(unionLinearLayout)
        }
    }

    /* 시간표에 표시되는 일정 textView를 만드는 함수 */
    private fun makeScheduleViewInDrawTimesSchedule(schedule: Schedule, categories: List<Category>?) : TextView{
        val scheduleView = TextView(requireContext())

        val (scheduleCalculatedValue, margin) = getHeightAndMarginOfLinearLayout(schedule.repeatStart!!, schedule.repeatEnd!!)
        val scheduleViewLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            Math.round((scheduleCalculatedValue) * displayMetrics.density),
            1f
        ).apply {
            topMargin = Math.round(margin * displayMetrics.density)
            //여러개 겹쳐있을 때 짤리는 문제 해결
            bottomMargin = Math.round(margin * displayMetrics.density)
            gravity = (Gravity.TOP)
        }
        scheduleView.layoutParams = scheduleViewLayoutParams
        scheduleView.text = schedule.content
        scheduleView.textSize = 12f
        scheduleView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        scheduleView.gravity = Gravity.CENTER

        val spacingInPxFromXD = 18f  // Adobe XD에서 제공하는 행간
        val spacingInDp = spacingInPxFromXD / resources.displayMetrics.density
        val spacingInPx = spacingInDp * resources.displayMetrics.density
        scheduleView.setLineSpacing(spacingInPx,1f)

        val paddingValue = 4 * displayMetrics.density.toInt()
        scheduleView.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)

        scheduleView.setOnClickListener {
            Toast.makeText(requireContext(), "${schedule.content}", Toast.LENGTH_SHORT)
                .show()
        }

        scheduleView.setOnLongClickListener { view ->
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = EmptyShadowBuilder(view)
            view?.startDragAndDrop(data, shadowBuilder, view, 0)
            false
        }

        // 카테고리, 글자색 색칠하기
        colorCategoryAndTextColorOnScheduleView(scheduleView, schedule, categories, 0, 10)
        return scheduleView
    }


    private fun checkForScroll(pointerY: Float, startOfScrollView: Int) {
        val lowerLimForScroll = (Resources.getSystem().displayMetrics.heightPixels * 0.8).toInt()
        /* if the upper limit is passed, meaning a pixel height, scroll up */
        Log.d("DRAGGED", "$pointerY, $startOfScrollView, $lowerLimForScroll")
        if (pointerY < 100) {
            binding.timetableScroll.smoothScrollBy(0, -20)
            Log.d("DRAGGED", "up")
        }
        /* if the lower limit is passed, meaning a pixel height, scroll down */
        else if (pointerY > lowerLimForScroll) {
            binding.timetableScroll.smoothScrollBy(0, 15)
            Log.d("DRAGGED", "down")
        }
    }



    private fun colorCategoryAndTextColorOnScheduleView(scheduleView: TextView, schedule:Schedule, categories: List<Category>?, padding: Int, cornerRadius: Int) {
        val category = categories?.find { it.id == schedule.category?.id }
        scheduleView.background = makeShapeDrawable(category?.color, padding, cornerRadius)
        if(colorList.indexOf(category?.color) in listOf(
                0,1,2,6,7,8,12,13,14,15,
                18,19,20,24,25,26,27,30,31,32,
                36,37,38
            )){
            scheduleView.setTextColor(Color.parseColor("#FDFDFD"))
        } else{
            scheduleView.setTextColor(Color.parseColor("#191919"))
        }
    }


    //타임테이블 왼쪽 1시부터 24시 시간 표기
    private fun drawLeftTime() {
        val standardTextView = binding.leftTimeTextview
        for (num in 3..24) {
            val newTextView = TextView(context).apply {
                text = num.toString()
                textSize = 12f  // 12sp
                setTextColor(standardTextView.currentTextColor)
                typeface = standardTextView.typeface
                gravity = standardTextView.gravity
                layoutParams = LinearLayout.LayoutParams(
                    standardTextView.layoutParams.width,
                    standardTextView.layoutParams.height
                ).apply {
                    if (standardTextView.layoutParams is ViewGroup.MarginLayoutParams) {


                        val marginPx = (context.resources.displayMetrics.density * 52).toInt()
                        val params =
                            standardTextView.layoutParams as ViewGroup.MarginLayoutParams
                        setMargins(
                            0,
                            marginPx,
                            0,
                            0
                        )
                    }
                }
                setPadding(
                    standardTextView.paddingLeft, standardTextView.paddingTop,
                    standardTextView.paddingRight, standardTextView.paddingBottom
                )
            }
            binding.leftTimeTextviewLayout.addView(newTextView)
        }
    }


    private fun getHeightAndMarginOfLinearLayout(repeatStart : String, repeatEnd : String): Pair<Int, Int>{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

        // Parse the strings to ZonedDateTime instances
        val preRepeatStart = ZonedDateTime.parse(repeatStart, formatter)
        val preRepeatEnd = ZonedDateTime.parse(repeatEnd, formatter)

        // Calculate the difference (offset) between the two dates
        val diff = Duration.between(preRepeatStart, preRepeatEnd)


        // Get only the time part
        val timeOfRepeatStart = LocalTime.parse(repeatStart.substring(11, 16))
        val timeOfRepeatEnd = LocalTime.parse(repeatEnd.substring(11, 16))
        val offsetInMinutes = ChronoUnit.MINUTES.between(timeOfRepeatStart, timeOfRepeatEnd).let { if (it < 0) it + 24 * 60 else it }

        val startHour = preRepeatStart.hour
        val startMin = preRepeatStart.minute

        val scheduleCalculatedValue = (offsetInMinutes / 5 * 6).toInt()
        val margin = (startHour * 72 + startMin / 5 * 6)

        return Pair(scheduleCalculatedValue, margin)
    }


    private fun makeShapeDrawable(color : String?, padding: Int, cornerRadius: Int): LayerDrawable {
        val gd = GradientDrawable()

        if(color == null){
            gd.setColor(Color.parseColor("#AAD7FF")) // Initial color
        }else{
            gd.setColor(Color.parseColor(color))
        }
        gd.cornerRadius = cornerRadius * displayMetrics.density

        // Create another GradientDrawable as background
        val bg = GradientDrawable()
//        bg.setColor(Color.WHITE) // Set the color to white
        bg.cornerRadius = cornerRadius * displayMetrics.density

        // Use a LayerDrawable to put two drawables together
        val ld = LayerDrawable(arrayOf(bg, gd))
        ld.setLayerInset(1, padding, padding, padding, padding) // This is equivalent to padding 1dp to gd

        return ld
    }

    private val colorList = listOf(
        "#2E2E2E", "#656565", "#818181", "#9D9D9D", "#B9B9B9", "#D5D5D5",
        "#FF0959", "#FF509C", "#FF5AB6", "#FE7DCD", "#FFAAE5", "#FFBDFB",
        "#B237BB", "#C93DEB", "#B34CED", "#9D5BE3", "#BB93F8", "#C6B2FF",
        "#4C45FF", "#2E57FF", "#4D8DFF", "#45BDFF", "#6DDDFF", "#65F4FF",
        "#FE7E7E", "#FF572E", "#C22E2E", "#A07553", "#E3942E", "#E8A753",
        "#FF892E", "#FFAB4C", "#FFD166", "#FFDE2E", "#CFE855", "#B9D32E",
        "#105C08", "#39972E", "#3EDB67", "#55E1B6", "#69FFD0", "#05C5C0",
    )
}