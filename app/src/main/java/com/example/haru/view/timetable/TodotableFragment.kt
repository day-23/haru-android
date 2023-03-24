package com.example.haru.view.timetable

import android.graphics.Color
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
import kotlin.text.Typography.times

class TodotableFragment : Fragment()  {
    private lateinit var binding : FragmentTodotableBinding
    private lateinit var timetableviewModel: TimetableViewModel
    private lateinit var todoreviewModel: TodoTableRecyclerViewmodel
    private lateinit var sun_todotableAdapter: TodotableAdapter
    lateinit var todorecyclerView: RecyclerView
    var todoList: ArrayList<String> = ArrayList()
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

        todoreviewModel.init_value()

        todoreviewModel.TodoContentsList.observe(viewLifecycleOwner) { contents ->
        val sunday = contents.get(0)
        Log.d("daylist", "sunday: $sunday")
        sun_todotableAdapter = TodotableAdapter(requireContext(), sunday ?: todoList)
        todorecyclerView = binding.sunTodosRecycler
        todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        todorecyclerView.adapter = sun_todotableAdapter
        }


        timetableviewModel.Selected.observe(viewLifecycleOwner) { times ->
            val year = times.year.slice(0..times.year.length - 2)
            val month = times.month.slice(0..times.month.length - 2)
            val day = times.day.slice(0..times.day.length - 2)

            binding.sunLayout.setBackgroundResource(R.color.white)
            binding.monLayout.setBackgroundResource(R.color.white)
            binding.tueLayout.setBackgroundResource(R.color.white)
            binding.wedLayout.setBackgroundResource(R.color.white)
            binding.thuLayout.setBackgroundResource(R.color.white)
            binding.friLayout.setBackgroundResource(R.color.white)
            binding.satLayout.setBackgroundResource(R.color.white)

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

        timetableviewModel.Colors.observe(viewLifecycleOwner) { colors ->
            binding.todoSunDate.setTextColor(Color.parseColor(colors.get(0)))
            binding.todoMonDate.setTextColor(Color.parseColor(colors.get(1)))
            binding.todoTueDate.setTextColor(Color.parseColor(colors.get(2)))
            binding.todoWedDate.setTextColor(Color.parseColor(colors.get(3)))
            binding.todoThuDate.setTextColor(Color.parseColor(colors.get(4)))
            binding.todoFriDate.setTextColor(Color.parseColor(colors.get(5)))
            binding.todoSatDate.setTextColor(Color.parseColor(colors.get(6)))
        }

        timetableviewModel.Dates.observe(viewLifecycleOwner) { Dates ->
            todoreviewModel.getTodo(Dates)
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