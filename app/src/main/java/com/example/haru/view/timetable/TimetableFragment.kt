package com.example.haru.view.timetable

import android.app.ProgressDialog.show
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TimetableFragment : Fragment() {

    val timetableData: ArrayList<timetable_data> = ArrayList()
    val days = arrayListOf<Int>()
    val calendar = Calendar.getInstance()
    lateinit var recyclerView1: RecyclerView
    lateinit var timetableMonthTextView: TextView
    lateinit var timetableYearTextView: TextView
    lateinit var mon_btn : Button
    lateinit var tue_btn : Button
    lateinit var wed_btn : Button
    lateinit var thu_btn : Button
    lateinit var fri_btn : Button
    lateinit var sat_btn : Button
    lateinit var sun_btn : Button

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): TimetableFragment {
            return TimetableFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "TimetableFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val timetableAdapter = TimetableAdapter(requireContext(), timetableData)
        Log.d(TAG, "TimetableFragment - onCreateView() called")
        var rootView = inflater.inflate(R.layout.fragment_timetable, container, false)

        timetableMonthTextView = rootView.findViewById<TextView>(R.id.timetable_month)
        timetableYearTextView = rootView.findViewById<TextView>(R.id.timetable_year)
        timetableMonthTextView.text = "${calendar.get(Calendar.MONTH) + 1}월"
        timetableYearTextView.text = "${calendar.get(Calendar.YEAR)}년"

        mon_btn = rootView.findViewById<Button>(R.id.mon_btn)
        tue_btn = rootView.findViewById<Button>(R.id.tue_btn)
        wed_btn = rootView.findViewById<Button>(R.id.wed_btn)
        thu_btn = rootView.findViewById<Button>(R.id.thu_btn)
        fri_btn = rootView.findViewById<Button>(R.id.fri_btn)
        sat_btn = rootView.findViewById<Button>(R.id.sat_btn)
        sun_btn = rootView.findViewById<Button>(R.id.sun_btn)

        daylist(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))

        mon_btn.text = "월\n${days.get(0)}"
        tue_btn.text = "화\n${days.get(1)}"
        wed_btn.text = "수\n${days.get(2)}"
        thu_btn.text = "목\n${days.get(3)}"
        fri_btn.text = "금\n${days.get(4)}"
        sat_btn.text = "토\n${days.get(5)}"
        sun_btn.text = "일\n${days.get(6)}"

        timetableMonthTextView.setOnClickListener {
            showDatePickerDialog()
        }

        timetableData.clear()
        for (i: Int in 1..23) {
            if (i < 12) {
                timetableData.add(timetable_data("오전 \n${i}시"))
            } else {
                if (i == 12) {
                    timetableData.add(timetable_data("오후 \n12시"))
                } else {
                    timetableData.add(timetable_data("오후 \n${i - 12}시"))
                }
            }
        }
        timetableAdapter.notifyDataSetChanged()

        recyclerView1 = rootView.findViewById(R.id.timetable_recyclerview!!) as RecyclerView
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        recyclerView1.adapter = timetableAdapter

        val itemTouchHelperCallback = ItemTouchHelperCallback(timetableAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView1)



        return rootView
        //return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    fun daylist(year: Int, month: Int, day: Int){
        days.clear()

        var day = day
        var lastofmonth = 0

        calendar.add(Calendar.MONTH, -1);
        if(month == 1){
            lastofmonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        else{
            lastofmonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        calendar.add(Calendar.MONTH, +1)
        val day_of_week = SimpleDateFormat("E").format(Date(year - 1900, month - 1, day))

        when(day_of_week){
            "Mon"-> day
            "Tue"-> day -= 1
            "Wed"-> day -= 2
            "Thu"-> day -= 3
            "Fri"-> day -= 4
            "Sat"-> day -= 5
            "Sun"-> day -= 6
        }

        for (i: Int in 1 .. 7){
            if(day < 1){
                days.add(lastofmonth - day)
            }
            else{
                days.add(day)
            }
            day += 1
        }

    }
    private fun showDatePickerDialog() {
        val datePicker = DatePicker(requireContext())
        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(datePicker)
            .setPositiveButton("apply") {dialog, _ ->
                val year = datePicker.year
                val month = datePicker.month + 1
                var day = datePicker.dayOfMonth
                val day_of_week = SimpleDateFormat().format(Date(year - 1900, month - 1, day))
                val selectedYear = SimpleDateFormat("yyyy년").format(Date(year - 1900, month - 1, day))
                val selectedMonth = SimpleDateFormat("M월").format(Date(year - 1900, month - 1, day))
                timetableMonthTextView.text = selectedMonth
                timetableYearTextView.text = selectedYear
                daylist(year, month, day)
                mon_btn.text = "월\n${days.get(0)}"
                tue_btn.text = "화\n${days.get(1)}"
                wed_btn.text = "수\n${days.get(2)}"
                thu_btn.text = "목\n${days.get(3)}"
                fri_btn.text = "금\n${days.get(4)}"
                sat_btn.text = "토\n${days.get(5)}"
                sun_btn.text = "일\n${days.get(6)}"
                dialog.dismiss()
            }
            .setNegativeButton("cancel") {dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

}