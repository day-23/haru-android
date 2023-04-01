package com.example.haru.view.timetable

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.marginTop
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
import org.w3c.dom.Text


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
        reviewModel.init_value()
        timetableAdapter = TimetableAdapter(requireContext(), reviewModel.TimeList.value ?: timeList)
        recyclerView1 = binding.timetableRecyclerview
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        recyclerView1.adapter = timetableAdapter
        timetableviewModel.init_value()

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

                if(schedule[0].size > 0)
                    binding.sunTable.removeAllViews()
                    Drawtimes(binding.sunTable, schedule[0])

                if(schedule[1].size > 0)
                    binding.monTable.removeAllViews()
                    Drawtimes(binding.monTable, schedule[1])

                if(schedule[2].size > 0)
                    binding.tueTable.removeAllViews()
                    Drawtimes(binding.tueTable, schedule[2])

                if(schedule[3].size > 0)
                    binding.wedTable.removeAllViews()
                    Drawtimes(binding.wedTable, schedule[3])

                if(schedule[4].size > 0)
                    binding.thuTable.removeAllViews()
                    Drawtimes(binding.thuTable, schedule[4])

                if(schedule[5].size > 0)
                    binding.friTable.removeAllViews()
                    Drawtimes(binding.friTable, schedule[5])

                if(schedule[6].size > 0)
                    binding.satTable.removeAllViews()
                    Drawtimes(binding.satTable, schedule[6])
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

    fun scroll(){
        val child = recyclerView1.getChildAt(8)
        val originalPos = IntArray(2)
        if (child != null) {
            child.getLocationInWindow(originalPos)
        }
        binding.timetableScroll.smoothScrollBy(0, originalPos[1], 1000)
        binding.invalidateAll()
    }

    fun Drawtimes(table: ViewGroup, times: ArrayList<Schedule>){
        var past_start = 0
        var past_end = 2359
        val UnionList = ArrayList<ArrayList<Schedule>>()
        var overlapList = ArrayList<Schedule>()

        for(times in times){
            val start = times.repeatStart?.slice(IntRange(11,12)) + times.repeatStart?.slice(IntRange(14,15))
            val end = times.repeatEnd?.slice(IntRange(11,12)) + times.repeatEnd?.slice(IntRange(14,15))

            if(start.toInt() in past_start .. past_end){
                overlapList.add(times)
            }
            else{
                val arraylist = ArrayList<Schedule>()
                for(i in overlapList){
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
        for(union in UnionList){
            val layout = LinearLayout(requireContext())
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layout.layoutParams = layoutParams

            for(time in union){
                val union_start_hour = time.repeatStart?.slice(IntRange(11,12))
                val union_start_min = time.repeatStart?.slice(IntRange(14,15))
                val union_end_hour = time.repeatEnd?.slice(IntRange(11,12))
                val union_end_min = time.repeatEnd?.slice(IntRange(14,15))
                val start_hour = union_start_hour!!.toInt()
                val start_min = union_start_min!!.toInt()
                val end_hour = union_end_hour!!.toInt()
                val end_min = union_end_min!!.toInt()

                var hour = end_hour - start_hour
                var min = end_min - start_min
                if(min < 0) {
                    hour -= 1
                    min += 60
                }
                val displayMetrics = resources.displayMetrics
                val itemparams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round( (hour * 120 + min * 2) * displayMetrics.density),1f)
                val margin = start_hour * 120 + start_min * 2
                Log.d("Schedules" , "times : $hour and $min $margin ${time.content}")
                itemparams.topMargin = Math.round( margin * displayMetrics.density)
                itemparams.rightMargin = 1
                val Schedule_View = TextView(requireContext())
                Schedule_View.layoutParams = itemparams
                Schedule_View.setText(time.content)
                Schedule_View.setOnClickListener {
                    Toast.makeText(requireContext(), "${time.content}", Toast.LENGTH_SHORT).show()
                }
                Schedule_View.setBackgroundResource(R.drawable.timetable_schedule)
                layout.addView(Schedule_View)
            }
            table.addView(layout)
        }
    }
}