package com.example.haru.view.timetable

import android.content.ClipData
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Schedule
import com.example.haru.data.model.Todo
import com.example.haru.data.model.timetable_data
import com.example.haru.databinding.FragmentTimetableBinding
import com.example.haru.viewmodel.TimeTableRecyclerViewModel
import com.example.haru.viewmodel.TimetableViewModel
import org.w3c.dom.Text

//드래그 앤 드롭 그림자 없어지게 하는 클래스
class EmptyShadowBuilder(view: View) : View.DragShadowBuilder(view) {
    override fun onDrawShadow(canvas: Canvas?) {}
}

class TimetableFragment : Fragment() {
    private lateinit var binding: FragmentTimetableBinding
    private lateinit var timetableviewModel: TimetableViewModel
    private lateinit var reviewModel: TimeTableRecyclerViewModel
    private lateinit var timetableAdapter: TimetableAdapter
    var timeList: ArrayList<timetable_data> = ArrayList()
    lateinit var recyclerView1: RecyclerView
    private lateinit var scheduleDrag: ScheduleDraglistener
    val scheduleViewList: ArrayList<View> = ArrayList<View>()
    val scheduleMap = mutableMapOf<View, Schedule>()
    val layoutId: ArrayList<Int> = ArrayList<Int>()

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): TimetableFragment {
            return TimetableFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewModel = TimeTableRecyclerViewModel()

        Log.d(TAG, "TimetableFragment - onCreate() called")
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
        recyclerView1 = binding.timetableRecyclerview
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        recyclerView1.adapter = timetableAdapter
        timetableviewModel.init_value()

        //타임테이블 프래그먼트 아이디 값
        layoutId.add(binding.sunTable.id)
        layoutId.add(binding.monTable.id)
        layoutId.add(binding.tueTable.id)
        layoutId.add(binding.wedTable.id)
        layoutId.add(binding.thuTable.id)
        layoutId.add(binding.friTable.id)
        layoutId.add(binding.satTable.id)

        scheduleDrag = ScheduleDraglistener(timetableviewModel, layoutId, requireContext())

        //각 레이아웃에 드래그 앤 드롭을 위한 리스너 등록
        binding.sunTable.setOnDragListener(scheduleDrag)
        binding.monTable.setOnDragListener(scheduleDrag)
        binding.tueTable.setOnDragListener(scheduleDrag)
        binding.wedTable.setOnDragListener(scheduleDrag)
        binding.thuTable.setOnDragListener(scheduleDrag)
        binding.friTable.setOnDragListener(scheduleDrag)
        binding.satTable.setOnDragListener(scheduleDrag)

        binding.timetableScroll.setOnDragListener{ _, event ->
            val boxesLayoutCoords = intArrayOf(0, 0)
            binding.timetableScroll.getLocationInWindow(boxesLayoutCoords)
            when(event.action){
                DragEvent.ACTION_DRAG_LOCATION -> {
                    checkForScroll(event.y, boxesLayoutCoords[1])
                    true
                }
                else -> true
            }
        }

        //타임테이블 리사이클러뷰 실행
        reviewModel.TimeList.observe(viewLifecycleOwner) { times ->
            timetableAdapter.setData(times)
            scroll() // 화면에 오후 12시가 중앙에 오도록
            timetableAdapter.notifyDataSetChanged()
        }

        //날짜가 바뀌면 12시를 화면 중앙에 두도록
        timetableviewModel.Selected.observe(viewLifecycleOwner) { times ->
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
        //스케줄을 타임테이블에 바인딩
        timetableviewModel.Schedules.observe(viewLifecycleOwner) { schedule ->
            scheduleMap.clear()

            binding.sunTable.removeAllViews()
            Drawtimes(binding.sunTable, schedule[0])

            binding.monTable.removeAllViews()
            Drawtimes(binding.monTable, schedule[1])

            binding.tueTable.removeAllViews()
            Drawtimes(binding.tueTable, schedule[2])

            binding.wedTable.removeAllViews()
            Drawtimes(binding.wedTable, schedule[3])

            binding.thuTable.removeAllViews()
            Drawtimes(binding.thuTable, schedule[4])

            binding.friTable.removeAllViews()
            Drawtimes(binding.friTable, schedule[5])

            binding.satTable.removeAllViews()
            Drawtimes(binding.satTable, schedule[6])

        }
        //하루종일 or 2일이상 일정을 바인딩
        timetableviewModel.SchedulesAllday.observe(viewLifecycleOwner) { days ->
            binding.daysTable.removeAllViews()
            Log.d("ALLDAYsss", "$days")
            DrawDays(binding.daysTable, days,timetableviewModel.getDates())
        }

        //드래그 앤 드랍시 이동한 뷰의 정보
        timetableviewModel.MoveView.observe(viewLifecycleOwner) { view ->
            val movedData = scheduleMap[view]
            val start = timetableviewModel.MoveDate.value
            val endDate = start!!.slice(IntRange(0, 9))
            val endTime = movedData!!.repeatEnd!!.slice(IntRange(10, 23))
            val end = endDate + endTime
            timetableviewModel.patchMoved(start!!, end, movedData)
        }

        binding.todolistChange.setOnClickListener {
            val newFrag = TodotableFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }
        return rootView
    }

    fun scroll() {
        val child = recyclerView1.getChildAt(8)
        val originalPos = IntArray(2)
        if (child != null) {
            child.getLocationInWindow(originalPos)
        }
        binding.timetableScroll.smoothScrollTo(0, originalPos[1], 1000)
        binding.invalidateAll()
    }

    //하루이상의 할일을 동적으로 바인딩
    fun DrawDays(table: ViewGroup, days: ArrayList<Schedule>, dates: ArrayList<String>){
        val displayMetrics = resources.displayMetrics
        val deleteSchedule = ArrayList<Schedule>()
        while(days.size > 0){
            val layout1 = FrameLayout(requireContext())
            val rowParams1 = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Math.round(18 * displayMetrics.density))

            layout1.layoutParams = rowParams1

            var occupied = 0
            deleteSchedule.clear()

            for(day in days){
                val startDate = day.repeatStart?.slice(IntRange(0,3)) + day.repeatStart?.slice(IntRange(5,6)) + day.repeatStart?.slice(IntRange(8,9))
                val endDate = day.repeatEnd?.slice(IntRange(0,3)) + day.repeatEnd?.slice(IntRange(5,6)) + day.repeatEnd?.slice(IntRange(8,9))
                if(startDate.toInt() > occupied) {
                    val layout2 = LinearLayout(requireContext())
                    val rowParams2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    layout2.layoutParams = rowParams2
                    deleteSchedule.add(day)
                    val period = (dates.indexOf(endDate) - dates.indexOf(startDate)) + 1
                    val position = dates.indexOf(startDate)

                    val view = TextView(requireContext())
                    view.text = day.content
                    view.textSize = (10).toFloat()
                    view.gravity = Gravity.CENTER
                    view.setPadding(2,2,2,2)
                    view.maxLines = 1
                    view.setBackgroundResource(R.drawable.timetable_schedule_allday)
                    val frontPadding = View(requireContext())
                    val backPadding = View(requireContext())
                    view.setOnClickListener {
                        Toast.makeText(requireContext(), "${day.content}", Toast.LENGTH_SHORT).show()
                    }

                    val frontParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, position.toFloat())
                    val backParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, (7 - (position + period).toFloat()))
                    val itemParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, period.toFloat())

                    view.layoutParams = itemParams
                    frontPadding.layoutParams = frontParams
                    backPadding.layoutParams = backParams
                    layout2.addView(frontPadding)
                    layout2.addView(view)
                    layout2.addView(backPadding)
                    occupied = endDate.toInt()
                    layout1.addView(layout2)
                }
            }
            for(item in deleteSchedule){
                days.remove(item)
            }
            table.addView(layout1)
        }

    }
    //하루치 일정을 동적으로 바인딩
    fun Drawtimes(table: ViewGroup, times: ArrayList<Schedule>) {
        var past_start = 0
        var past_end = 2359
        val UnionList = ArrayList<ArrayList<Schedule>>()
        var overlapList = ArrayList<Schedule>()
        Log.d("DRAGGED", "${times}")
        for (times in times) {
            val start = times.repeatStart?.slice(IntRange(11, 12)) + times.repeatStart?.slice(
                IntRange(
                    14,
                    15
                )
            )
            val end =
                times.repeatEnd?.slice(IntRange(11, 12)) + times.repeatEnd?.slice(IntRange(14, 15))

            if (start.toInt() in past_start..past_end-1) {
                overlapList.add(times)
            } else {
                val arraylist = ArrayList<Schedule>()
                for (i in overlapList) {
                    arraylist.add(i)
                }
                overlapList.clear()
                overlapList.add(times)
                UnionList.add(arraylist)
            }

            past_start = start.toInt()
            past_end = end.toInt()
        }
        UnionList.add(overlapList)
        for (union in UnionList) {
            val layout = LinearLayout(requireContext())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layout.layoutParams = layoutParams

            for (time in union) {
                val union_start_hour = time.repeatStart?.slice(IntRange(11, 12))
                val union_start_min = time.repeatStart?.slice(IntRange(14, 15))
                val union_end_hour = time.repeatEnd?.slice(IntRange(11, 12))
                val union_end_min = time.repeatEnd?.slice(IntRange(14, 15))
                val start_hour = union_start_hour!!.toInt()
                val start_min = union_start_min!!.toInt()
                val end_hour = union_end_hour!!.toInt()
                val end_min = union_end_min!!.toInt()

                var hour = end_hour - start_hour
                var min = end_min - start_min
                if (min < 0) {
                    hour -= 1
                    min += 60
                }
                val displayMetrics = resources.displayMetrics
                val itemparams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    Math.round((hour * 120 + min * 2) * displayMetrics.density),
                    1f
                )
                val margin = start_hour * 120 + start_min * 2
                Log.d("Schedules", "times : $hour and $min $margin ${time.content}")
                itemparams.topMargin = Math.round(margin * displayMetrics.density)
                itemparams.rightMargin = 1
                itemparams.gravity = (Gravity.TOP)
                val Schedule_View = TextView(requireContext())
                scheduleMap.put(Schedule_View, time)

                Schedule_View.setOnLongClickListener { view ->
                    val data = ClipData.newPlainText("", "")
                    val shadowBuilder = EmptyShadowBuilder(view)
                    view?.startDragAndDrop(data, shadowBuilder, view, 0)
                    false
                }

                scheduleViewList.add(Schedule_View)
                Schedule_View.layoutParams = itemparams
                Schedule_View.setText(time.content)
                Schedule_View.setLineSpacing((0).toFloat(), (1).toFloat())
                Schedule_View.setPadding(4,4,4,4)
                Schedule_View.setOnClickListener {
                    Toast.makeText(requireContext(), "${time.content}", Toast.LENGTH_SHORT)
                        .show()
                }

                Schedule_View.setBackgroundResource(R.drawable.timetable_schedule)
                layout.addView(Schedule_View)
            }
                table.addView(layout)
            }
        }

        fun checkForScroll(pointerY: Float, startOfScrollView: Int) {
            val lowerLimForScroll = (Resources.getSystem().displayMetrics.heightPixels * 0.8).toInt()
            /* if the upper limit is passed, meaning a pixel height, scroll up */
            Log.d("DRAGGED", "$pointerY $startOfScrollView, $lowerLimForScroll")
            if ( pointerY < 100) {
                binding.timetableScroll.smoothScrollBy(0, -20)
                Log.d("DRAGGED", "up")
            }
            /* if the lower limit is passed, meaning a pixel height, scroll down */
            else if (pointerY > lowerLimForScroll) {
                binding.timetableScroll.smoothScrollBy(0, 15)
                Log.d("DRAGGED", "down")
            }
        }

}