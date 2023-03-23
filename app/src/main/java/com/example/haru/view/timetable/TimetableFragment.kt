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
import androidx.databinding.DataBindingUtil
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
import com.example.haru.viewmodel.TimetableViewModel
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.Typography.times


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

        reviewModel.times.observe(viewLifecycleOwner) { times ->
            timetableAdapter.setData(times)
            timetableAdapter.notifyDataSetChanged()
        }

        timetableviewModel.Selected.observe(viewLifecycleOwner) { times ->
            binding.invalidateAll()
        }

        timetableviewModel.Days.observe(viewLifecycleOwner) { days ->
            binding.invalidateAll()
        }

        binding.todolistChange.setOnClickListener{
            Log.d("Frag", "changed")
            val newFrag = TodotableFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        return rootView
    }
}