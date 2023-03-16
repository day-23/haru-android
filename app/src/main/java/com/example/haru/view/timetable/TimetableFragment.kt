package com.example.haru.view.timetable

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import org.w3c.dom.Text
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TimetableFragment : Fragment() {

    val timetableData: ArrayList<timetable_data> = ArrayList()
    lateinit var recyclerView1: RecyclerView
    lateinit var timetableMonthTextView: TextView
    lateinit var timetableYearTextView: TextView

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
    private fun showDatePickerDialog() {
       val calendar = Calendar.getInstance()
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
                val day = datePicker.dayOfMonth
                val day_of_week = calendar.get(Calendar.DAY_OF_WEEK)
                val selectedYear = SimpleDateFormat("yyyy년").format(Date(year - 1900, month - 1, day))
                val selectedMonth = SimpleDateFormat("M월").format(Date(year - 1900, month - 1, day))
                timetableMonthTextView.text = selectedMonth
                timetableYearTextView.text = selectedYear
                Toast.makeText(requireContext(), "${day_of_week}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("cancel") {dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

}