package com.example.haru.view.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R


class TimetableFragment : Fragment() {

    val timetableData: ArrayList<timetable_data> = ArrayList()
    lateinit var recyclerView1: RecyclerView

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
}