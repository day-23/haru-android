package com.example.haru.view.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.timetable_data
import com.example.haru.databinding.FragmentTimetableBinding
import com.example.haru.viewmodel.TimeTableRecyclerViewModel
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.Typography.times


class TimetableFragment : Fragment() {
    private lateinit var binding : FragmentTimetableBinding
    private lateinit var reviewModel: TimeTableRecyclerViewModel
    private lateinit var timetableAdapter: TimetableAdapter
    var timeList: ArrayList<timetable_data> = ArrayList()
    var calendar = Calendar.getInstance()
    var days: ArrayList<Int> = ArrayList()
    lateinit var recyclerView1: RecyclerView
    lateinit var timetableMonthTextView: TextView
    lateinit var timetableYearTextView: TextView
    lateinit var mon_btn : TextView
    lateinit var tue_btn : TextView
    lateinit var wed_btn : TextView
    lateinit var thu_btn : TextView
    lateinit var fri_btn : TextView
    lateinit var sat_btn : TextView
    lateinit var sun_btn : TextView

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
        //timetableAdapter = TimetableAdapter(requireContext(), reviewModel.times)
        Log.d(TAG, "TimetableFragment - onCreateView() called")
        binding = FragmentTimetableBinding.inflate(inflater,container,false)
        //var rootView = inflater.inflate(R.layout.fragment_timetable, container, false)
        val rootView = binding.root
        timetableMonthTextView = binding.timetableMonth
        timetableYearTextView = binding.timetableYear
        timetableMonthTextView.text = "${calendar.get(Calendar.MONTH) + 1}월"
        timetableYearTextView.text = "${calendar.get(Calendar.YEAR)}년"

        mon_btn = binding.monBtn
        tue_btn = binding.tueBtn
        wed_btn = binding.wedBtn
        thu_btn = binding.thuBtn
        fri_btn = binding.friBtn
        sat_btn = binding.satBtn
        sun_btn = binding.sunBtn

        daylist(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        mon_btn.text = "${days.get(0)}"
        tue_btn.text = "${days.get(1)}"
        wed_btn.text = "${days.get(2)}"
        thu_btn.text = "${days.get(3)}"
        fri_btn.text = "${days.get(4)}"
        sat_btn.text = "${days.get(5)}"
        sun_btn.text = "${days.get(6)}"

        timetableMonthTextView.setOnClickListener {
            showDatePickerDialog()
        }
        timeList.add(timetable_data("1"))
        //reviewModel = ViewModelProvider(this).get(TimeTableRecyclerViewModel::class.java)
        timetableAdapter = TimetableAdapter(requireContext(), reviewModel.times.value ?: timeList)

        recyclerView1 = binding.timetableRecyclerview
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        recyclerView1.adapter = timetableAdapter

        reviewModel.times.observe(viewLifecycleOwner) { times ->
            timetableAdapter.setData(times)
            timetableAdapter.notifyDataSetChanged()
        }

        return rootView
    }

    fun daylist(year: Int, month: Int, day: Int) {
        days.clear()

        var day = day
        var lastofmonth = 0

        calendar.set(year, month - 1, day)
        lastofmonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day)
        val day_of_week = SimpleDateFormat("E").format(Date(year - 1900, month, day))

        when (day_of_week) {
            "Mon" -> day -= 1
            "Tue" -> day -= 2
            "Wed" -> day -= 3
            "Thu" -> day -= 4
            "Fri" -> day -= 5
            "Sat" -> day -= 6
            "Sun" -> day
        }

        for (i: Int in 1..7) {
            if (day < 1) {
                days.add(lastofmonth - abs(day))
            } else if (day > lastofmonth) {
                days.add(day - lastofmonth)
            } else {
                days.add(day)
            }
            day += 1
        }

    }

        fun showDatePickerDialog() {
            val datePicker = DatePicker(requireContext())
            datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null
            )
            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(datePicker)
                .setPositiveButton("apply") { dialog, _ ->
                    val year = datePicker.year
                    val month = datePicker.month
                    var day = datePicker.dayOfMonth
                    val selectedYear =
                        SimpleDateFormat("yyyy년").format(Date(year - 1900, month, day))
                    val selectedMonth = SimpleDateFormat("M월").format(Date(year - 1900, month, day))
                    timetableMonthTextView.text = selectedMonth
                    timetableYearTextView.text = selectedYear
                    Toast.makeText(
                        context,
                        "${selectedYear} ${selectedMonth} ${day}",
                        Toast.LENGTH_SHORT
                    ).show()
                    daylist(year, month, day)
                    mon_btn.text = "${days.get(0)}"
                    tue_btn.text = "${days.get(1)}"
                    wed_btn.text = "${days.get(2)}"
                    thu_btn.text = "${days.get(3)}"
                    fri_btn.text = "${days.get(4)}"
                    sat_btn.text = "${days.get(5)}"
                    sun_btn.text = "${days.get(6)}"
                    dialog.dismiss()
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }
}