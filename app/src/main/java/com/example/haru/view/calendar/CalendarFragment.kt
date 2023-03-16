package com.example.haru.view.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.databinding.FragmentCalendarBinding
import com.example.haru.viewmodel.CalendarViewModel
import java.util.*

class CalendarFragment : Fragment() {
    private var calendar = Calendar.getInstance()
    private var month_cal = 0

    private lateinit var binding: FragmentCalendarBinding
    lateinit var viewModel: CalendarViewModel
    val adapter = AdapterDay()

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarFragment - onCreate() called")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "CalendarFragment - onCreateView() called")

        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCalendarBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(
            CalendarViewModel::class.java)

        binding.calendarRecyclerview.adapter = adapter

        viewModel.dataList.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreate: $it")
            adapter.updateData(it)
        })
        //viewModel.errorMessage.observe(this, { })
        viewModel.requestDate(2023,3)

        /*super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "하루"

        val left_month_btn = view.findViewById<Button>(R.id.left_month_btn)
        val right_month_btn = view.findViewById<Button>(R.id.right_month_btn)
        val calendar_recyclerview = view.findViewById<RecyclerView>(R.id.calendar_recyclerview)
        val month_text = view.findViewById<TextView>(R.id.item_month_text)

        calendar.set(Calendar.DAY_OF_MONTH, 1)

        month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
        //현재 달력이 몇 월인지 가져오고
        var tempMonth = calendar.get(Calendar.MONTH)

        var dayList: MutableList<Date> = MutableList(6 * 7) { Date() }

        //달력의 아이템마다 값을 입력
        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val dayListManager = GridLayoutManager(view.context, 7)
        val dayListAdapter = AdapterDay(tempMonth, dayList)

        calendar_recyclerview.apply {
            layoutManager = dayListManager
            adapter = dayListAdapter
        }

        left_month_btn.setOnClickListener {
            month_cal -= 1
            tempMonth -= 1

            if(tempMonth < 0) tempMonth = 11

            calendar.add(Calendar.MONTH,month_cal)

            month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

            for(i in 0..5) {
                for(k in 0..6) {
                    calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                    dayList[i * 7 + k] = calendar.time
                }
                calendar.add(Calendar.WEEK_OF_MONTH, 1)
            }

            dayListAdapter.updateData(tempMonth, dayList)

            calendar.time = Date()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }

        right_month_btn.setOnClickListener {
            month_cal += 1
            tempMonth += 1

            if(tempMonth > 11) tempMonth = 0

            calendar.add(Calendar.MONTH,month_cal)

            month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

            for(i in 0..5) {
                for(k in 0..6) {
                    calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                    dayList[i * 7 + k] = calendar.time
                }
                calendar.add(Calendar.WEEK_OF_MONTH, 1)
            }

            dayListAdapter.updateData(tempMonth, dayList)

            calendar.time = Date()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }*/
    }
}