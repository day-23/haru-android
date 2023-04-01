package com.example.haru.view.timetable

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoTable_data
import com.example.haru.databinding.FragmentTodotableBinding
import com.example.haru.viewmodel.TimetableViewModel
import com.example.haru.viewmodel.TodoTableRecyclerViewmodel

class TodotableFragment : Fragment() {
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

    var todoList: ArrayList<Todo> = ArrayList()
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
        timetableviewModel.init_value()
        todoreviewModel.init_value()

        todoreviewModel.TodoDataList.observe(viewLifecycleOwner) { contents ->
            val date = timetableviewModel.getDates()
            sun_todotableAdapter = TodotableAdapter(requireContext(), contents[0] ?: todoList,date[0] ,Todo_draglistener())
            sun_todorecyclerView.adapter = sun_todotableAdapter
            mon_todotableAdapter = TodotableAdapter(requireContext(), contents[1] ?: todoList,date[1],Todo_draglistener())
            mon_todorecyclerView.adapter = mon_todotableAdapter
            tue_todotableAdapter = TodotableAdapter(requireContext(), contents[2] ?: todoList,date[2],Todo_draglistener())
            tue_todorecyclerView.adapter = tue_todotableAdapter
            wed_todotableAdapter = TodotableAdapter(requireContext(), contents[3] ?: todoList,date[3],Todo_draglistener())
            wed_todorecyclerView.adapter = wed_todotableAdapter
            thu_todotableAdapter = TodotableAdapter(requireContext(), contents[4] ?: todoList,date[4],Todo_draglistener())
            thu_todorecyclerView.adapter = thu_todotableAdapter
            fri_todotableAdapter = TodotableAdapter(requireContext(), contents[5] ?: todoList,date[5],Todo_draglistener())
            fri_todorecyclerView.adapter = fri_todotableAdapter
            sat_todotableAdapter = TodotableAdapter(requireContext(), contents[6] ?: todoList,date[6],Todo_draglistener())
            sat_todorecyclerView.adapter = sat_todotableAdapter
        }

        val dragListener = Todo_draglistener()
        //리사이클러뷰 요일별 바인딩
        sun_todorecyclerView = binding.sunTodosRecycler
        sun_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        sun_todorecyclerView.setOnDragListener(dragListener)

        mon_todorecyclerView = binding.monTodosRecycler
        mon_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mon_todorecyclerView.setOnDragListener(dragListener)

        tue_todorecyclerView = binding.tueTodosRecycler
        tue_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tue_todorecyclerView.setOnDragListener(dragListener)

        wed_todorecyclerView = binding.wedTodosRecycler
        wed_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        wed_todorecyclerView.setOnDragListener(dragListener)

        thu_todorecyclerView = binding.thuTodosRecycler
        thu_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        thu_todorecyclerView.setOnDragListener(dragListener)

        fri_todorecyclerView = binding.friTodosRecycler
        fri_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fri_todorecyclerView.setOnDragListener(dragListener)

        sat_todorecyclerView = binding.satTodosRecycler
        sat_todorecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        sat_todorecyclerView.setOnDragListener(dragListener)

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

            binding.sunday.setTextColor(Color.parseColor(colors.get(0)))
            binding.monday.setTextColor(Color.parseColor(colors.get(1)))
            binding.tueday.setTextColor(Color.parseColor(colors.get(2)))
            binding.wedday.setTextColor(Color.parseColor(colors.get(3)))
            binding.thuday.setTextColor(Color.parseColor(colors.get(4)))
            binding.friday.setTextColor(Color.parseColor(colors.get(5)))
            binding.satday.setTextColor(Color.parseColor(colors.get(6)))
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