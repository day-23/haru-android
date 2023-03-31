package com.example.haru.view.timetable

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Schedule
import com.example.haru.data.model.timetable_data
import com.example.haru.databinding.FragmentTimetableBinding
import com.example.haru.viewmodel.TimeTableRecyclerViewModel
import com.example.haru.viewmodel.TimetableViewModel


class TimetableFragment : Fragment() {
    private lateinit var binding : FragmentTimetableBinding
    private lateinit var timetableviewModel: TimetableViewModel
    private lateinit var reviewModel: TimeTableRecyclerViewModel
    private lateinit var timetableAdapter: TimetableAdapter
    var timeList: ArrayList<timetable_data> = ArrayList()
    lateinit var recyclerView1: RecyclerView

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

        timetableAdapter = TimetableAdapter(requireContext(), reviewModel.times.value ?: timeList)

        recyclerView1 = binding.timetableRecyclerview
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        recyclerView1.adapter = timetableAdapter

        timetableviewModel.init_value()
        val child = recyclerView1.getChildAt(8)
        val originalPos = IntArray(2)
        if (child != null) {
            child.getLocationInWindow(originalPos)
        }
        binding.timetableScroll.smoothScrollBy(0, originalPos[1], 1000)

        //타임테이블 리사이클러뷰 실행
        reviewModel.times.observe(viewLifecycleOwner) { times ->
            timetableAdapter.setData(times)
            timetableAdapter.notifyDataSetChanged()
        }

        //날짜가 바뀌면 12시를 화면 중앙에 두도록
        timetableviewModel.Selected.observe(viewLifecycleOwner) { times ->
            val child = recyclerView1.getChildAt(8)
            val originalPos = IntArray(2)
            if (child != null) {
                child.getLocationInWindow(originalPos)
            }
            binding.timetableScroll.smoothScrollBy(0, originalPos[1], 1000)
            binding.invalidateAll()
        }
        //지난달 다음달을 구분해주는 색 바인딩
        timetableviewModel.Colors.observe(viewLifecycleOwner) { colors->
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
        timetableviewModel.Schedules.observe(viewLifecycleOwner){ schedule ->
            if(schedule.size != 0) {
                Drawtimes(binding.sunTable, schedule[0])
            }
        }

        binding.todolistChange.setOnClickListener{
            val newFrag = TodotableFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        return rootView
    }

    fun Drawtimes(table: ViewGroup, times: ArrayList<Schedule>){
        var past_start = 0
        var past_end = 2355
        val UnionList = ArrayList<ArrayList<Schedule>>()
        val overlapList = ArrayList<Schedule>()

        for(time in times){
            val start = time.repeatStart?.slice(IntRange(11,12)) + time.repeatStart?.slice(IntRange(14,15))
            val end = time.repeatEnd?.slice(IntRange(11,12)) + time.repeatEnd?.slice(IntRange(14,15))

            if(past_start <= start.toInt() || start.toInt() <= past_end){
                overlapList.add(time)
            }
            else{
                UnionList.add(overlapList)
                overlapList.clear()
                overlapList.add(time)
            }
            past_start = start.toInt()
            past_end = end.toInt()
        }

        for(union in UnionList){
            val layout = LinearLayout(requireContext())
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f)
            layout.layoutParams = layoutParams
            for(time in union){
                val union_start_hour = union[0].repeatStart?.slice(IntRange(11,12))
                val union_start_min = union[0].repeatStart?.slice(IntRange(14,15))
                val margin = union_start_hour!!.toInt() * 120 + union_start_min!!.toInt()
                layoutParams.setMargins(0, margin, 0,0)

                val Schedule_View = TextView(requireContext())
                Schedule_View.setText(time.content)
                Schedule_View.setOnClickListener {
                    Toast.makeText(requireContext(), "${time.content}", Toast.LENGTH_SHORT).show()
                }
                Schedule_View.setBackgroundColor(Color.parseColor("#ffffff"))
                layout.addView(Schedule_View)
            }
            table.addView(layout)
        }
    }
}