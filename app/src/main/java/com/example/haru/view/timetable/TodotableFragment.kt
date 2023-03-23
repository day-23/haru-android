package com.example.haru.view.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todotable_date
import com.example.haru.data.model.timetable_data
import com.example.haru.databinding.FragmentTimetableBinding
import com.example.haru.databinding.FragmentTodotableBinding
import com.example.haru.viewmodel.TimeTableRecyclerViewModel
import com.example.haru.viewmodel.TimetableViewModel
import com.example.haru.viewmodel.TodoTableRecyclerViewmodel

class TodotableFragment : Fragment()  {
    private lateinit var binding : FragmentTodotableBinding
    private lateinit var timetableviewModel: TimetableViewModel
    private lateinit var todoreviewModel: TodoTableRecyclerViewmodel
    private lateinit var todotableAdapter: TodotableAdapter
    lateinit var todorecyclerView: RecyclerView
    var timeList: ArrayList<Todotable_date> = ArrayList()
    companion object {
        const val TAG: String = "로그"

        fun newInstance(): TodotableFragment {
            return TodotableFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoreviewModel = TodoTableRecyclerViewmodel()
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

        timetableviewModel = TimetableViewModel(requireContext())
        binding.viewModel = timetableviewModel

        todotableAdapter = TodotableAdapter(requireContext(), todoreviewModel.MonthList.value?: timeList)

//        todorecyclerView = binding.todoDaysRecycler
//        todorecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        todorecyclerView.adapter = todotableAdapter

        timetableviewModel.Selected.observe(viewLifecycleOwner) { times ->
            val year = times.year.slice(0..times.year.length - 2)
            val month = times.month.slice(0..times.month.length - 2)
            val day = times.day.slice(0..times.day.length - 2)
            todoreviewModel.updateMonth(year.toInt(), month.toInt()-1, day.toInt())
            when(day){
                timetableviewModel.Days.value?.get(0)-> binding.sunLayout.setBackgroundResource(R.drawable.todo_table_selected)
                timetableviewModel.Days.value?.get(1)-> binding.monLayout.setBackgroundResource(R.drawable.todo_table_selected)
                timetableviewModel.Days.value?.get(2)-> binding.tueLayout.setBackgroundResource(R.drawable.todo_table_selected)
                timetableviewModel.Days.value?.get(3)-> binding.wedLayout.setBackgroundResource(R.drawable.todo_table_selected)
                timetableviewModel.Days.value?.get(4)-> binding.thuLayout.setBackgroundResource(R.drawable.todo_table_selected)
                timetableviewModel.Days.value?.get(5)-> binding.friLayout.setBackgroundResource(R.drawable.todo_table_selected)
                timetableviewModel.Days.value?.get(6)-> binding.satLayout.setBackgroundResource(R.drawable.todo_table_selected)
            }
            binding.invalidateAll()
        }

        todoreviewModel.MonthList.observe(viewLifecycleOwner) {days ->
            Log.d("MonthList2", "${days.size}")
            todotableAdapter.setData(days)
        }

        binding.todolistChange.setOnClickListener{
            Log.d("Frag", "changed")
            val newFrag = TimetableFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        return rootView
    }
}