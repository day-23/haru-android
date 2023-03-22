package com.example.haru.view.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.haru.R
import com.example.haru.databinding.FragmentTimetableBinding
import com.example.haru.databinding.FragmentTodotableBinding
import com.example.haru.viewmodel.TimeTableRecyclerViewModel
import com.example.haru.viewmodel.TimetableViewModel

class TodotableFragment : Fragment()  {
    private lateinit var binding : FragmentTodotableBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): TodotableFragment {
            return TodotableFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TodotableFragment.TAG, "TodotableFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TimetableFragment.TAG, "TimetableFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todotable, container, false)
        val rootView = binding.root

        return rootView
    }
}