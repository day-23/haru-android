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
    //투두 리사이클러 뷰
    private lateinit var sun_todotableAdapter: TodotableAdapter
    lateinit var sun_todorecyclerView: RecyclerView
    private lateinit var mon_todotableAdapter: TodotableAdapter
    lateinit var mon_todorecyclerView: RecyclerView
    private lateinit var tue_todotableAdapter: TodotableAdapter
    lateinit var tue_todorecyclerView: RecyclerView
    private lateinit var wed_todotableAdapter: TodotableAdapter
    lateinit var wed_todorecyclerView: RecyclerView
    private lateinit var thu_todotableAdapter: TodotableAdapter
    lateinit var thu_todorecyclerView: RecyclerView
    private lateinit var fri_todotableAdapter: TodotableAdapter
    lateinit var fri_todorecyclerView: RecyclerView
    private lateinit var sat_todotableAdapter: TodotableAdapter
    lateinit var sat_todorecyclerView: RecyclerView

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
            val monday = contents.get(1)
            val tueday = contents.get(2)
            val wedday = contents.get(3)
            val thuday = contents.get(4)
            val friday = contents.get(5)
            val satday = contents.get(6)
            sun_todotableAdapter = TodotableAdapter(requireContext(), sunday ?: todoList)
            sun_todorecyclerView.adapter = sun_todotableAdapter
            mon_todotableAdapter = TodotableAdapter(requireContext(), monday ?: todoList)
            mon_todorecyclerView.adapter = mon_todotableAdapter
            tue_todotableAdapter = TodotableAdapter(requireContext(), tueday ?: todoList)
            tue_todorecyclerView.adapter = tue_todotableAdapter
            wed_todotableAdapter = TodotableAdapter(requireContext(), wedday ?: todoList)
            wed_todorecyclerView.adapter = wed_todotableAdapter
            thu_todotableAdapter = TodotableAdapter(requireContext(), thuday ?: todoList)
            thu_todorecyclerView.adapter = thu_todotableAdapter
            fri_todotableAdapter = TodotableAdapter(requireContext(), friday ?: todoList)
            fri_todorecyclerView.adapter = fri_todotableAdapter
            sat_todotableAdapter = TodotableAdapter(requireContext(), satday ?: todoList)
            sat_todorecyclerView.adapter = sat_todotableAdapter
        }
        //리사이클러뷰 요일별 바인딩
        sun_todorecyclerView = binding.sunTodosRecycler
        sun_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mon_todorecyclerView = binding.monTodosRecycler
        mon_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tue_todorecyclerView = binding.tueTodosRecycler
        tue_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        wed_todorecyclerView = binding.wedTodosRecycler
        wed_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        thu_todorecyclerView = binding.thuTodosRecycler
        thu_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fri_todorecyclerView = binding.friTodosRecycler
        fri_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        sat_todorecyclerView = binding.satTodosRecycler
        sat_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

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

        //투두 쿼리문 전송
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