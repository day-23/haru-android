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
import org.w3c.dom.Text
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TimetableFragment : Fragment() {

    val timetableData: ArrayList<timetable_data> = ArrayList()
    val days = arrayListOf<Int>()
    var calendar = Calendar.getInstance()
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

        mon_btn = rootView.findViewById<TextView>(R.id.mon_btn)
        tue_btn = rootView.findViewById<TextView>(R.id.tue_btn)
        wed_btn = rootView.findViewById<TextView>(R.id.wed_btn)
        thu_btn = rootView.findViewById<TextView>(R.id.thu_btn)
        fri_btn = rootView.findViewById<TextView>(R.id.fri_btn)
        sat_btn = rootView.findViewById<TextView>(R.id.sat_btn)
        sun_btn = rootView.findViewById<TextView>(R.id.sun_btn)

        daylist(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))

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

        timetableData.clear()
        for (i: Int in 1..23) {
            if (i < 12) {
                timetableData.add(timetable_data("${i}시\n오전"))
            } else {
                if (i == 12) {
                    timetableData.add(timetable_data("12시\n오후"))
                } else {
                    timetableData.add(timetable_data("${i - 12}시\n오후"))
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

        calendar.set(year, month-1, day)
        lastofmonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day)
        val day_of_week = SimpleDateFormat("E").format(Date(year - 1900, month, day))

        when(day_of_week){
            "Mon"-> day -= 1
            "Tue"-> day -= 2
            "Wed"-> day -= 3
            "Thu"-> day -= 4
            "Fri"-> day -= 5
            "Sat"-> day -= 6
            "Sun"-> day
        }

        for (i: Int in 1 .. 7){
            if(day < 1){
                days.add(lastofmonth - abs(day))
            }
            else if(day > lastofmonth){
                days.add(day - lastofmonth)
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
                val month = datePicker.month
                var day = datePicker.dayOfMonth
                val selectedYear = SimpleDateFormat("yyyy년").format(Date(year - 1900, month, day))
                val selectedMonth = SimpleDateFormat("M월").format(Date(year - 1900, month, day))
                timetableMonthTextView.text = selectedMonth
                timetableYearTextView.text = selectedYear
                Toast.makeText(context,"${selectedYear}년 ${selectedMonth}월 ${day}", Toast.LENGTH_SHORT).show()
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
            .setNegativeButton("cancel") {dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

}