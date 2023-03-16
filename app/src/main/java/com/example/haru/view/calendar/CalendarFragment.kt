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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.CalendarItem
import com.example.haru.databinding.FragmentCalendarBinding
import com.example.haru.databinding.ListItemDayBinding
import com.example.haru.viewmodel.CalendarViewModel
import java.util.*

class CalendarFragment : Fragment() {
    private var calendar = Calendar.getInstance()
    private val todoAdapter = AdapterDay()

    private lateinit var binding: FragmentCalendarBinding
    lateinit var calendarviewModel: CalendarViewModel

    var tempYear: Int = 0
    var tempMonth: Int = 0

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarFragment - onCreate() called")

        calendarviewModel = CalendarViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "CalendarFragment - onCreateView() called")

        binding = FragmentCalendarBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item_month_text = view.findViewById<TextView>(R.id.item_month_text)
        calendar.time = Date()
        tempYear = calendar.get(Calendar.YEAR)
        tempMonth = calendar.get(Calendar.MONTH)

        item_month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
        initCalendar()

        binding.leftMonthBtn.setOnClickListener{
            tempMonth -= 1

            if (tempMonth < 0) {
                tempYear -= 1
                tempMonth = 11
            }

            item_month_text.text = "${tempYear}년 ${tempMonth + 1}월"

            updateCalendar()
        }

        binding.rightMonthBtn.setOnClickListener{
            tempMonth += 1

            if (tempMonth > 11) {
                tempYear += 1
                tempMonth = 0
            }

            item_month_text.text = "${tempYear}년 ${tempMonth + 1}월"

            updateCalendar()
        }
    }

    private fun initCalendar(){
        val todoListView: RecyclerView = binding.calendarRecyclerview

        todoListView.adapter = todoAdapter
        todoListView.layoutManager =
            GridLayoutManager(requireContext(), 7)

        calendarviewModel.liveDataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val dataList = it.filterIsInstance<CalendarItem>()
            todoAdapter.updateData(dataList, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
        })
    }

    private fun updateCalendar(){
        calendarviewModel.init_viewModel(tempYear,tempMonth)
        calendarviewModel.liveDataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val dataList = it.filterIsInstance<CalendarItem>()
            todoAdapter.updateData(dataList, tempYear, tempMonth)
        })
    }
}