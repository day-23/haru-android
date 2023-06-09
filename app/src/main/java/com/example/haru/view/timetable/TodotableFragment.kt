package com.example.haru.view.timetable

import BaseActivity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentTodotableBinding
import com.example.haru.view.adapter.TodotableAdapter
import com.example.haru.view.checklist.ChecklistInputFragment
import com.example.haru.view.customDialog.CustomCalendarDialog
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TimetableViewModel
import com.example.haru.viewmodel.TodoTableRecyclerViewmodel
import kotlin.collections.ArrayList

class TodotableFragment : Fragment() {
    private lateinit var binding : FragmentTodotableBinding
    private lateinit var timetableviewModel: TimetableViewModel
    private lateinit var todoreviewModel: TodoTableRecyclerViewmodel
    private lateinit var checkListViewModel: CheckListViewModel

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
        checkListViewModel = CheckListViewModel()
        Log.d(TodotableFragment.TAG, "TodotableFragment - onCreate() called")
    }


    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.todotableHeader.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.todotableHeader.id)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TimetableFragment.TAG, "TimetableFragment - onCreateView() called")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todotable, container, false)
        val rootView = binding.root

        binding.btnAddTodo.setOnClickListener {
            val todoInput = ChecklistInputFragment(checkListViewModel,null, false, true)
            todoInput.onDismissListener = object : ChecklistInputFragment.OnDismissListener {
                override fun onDismiss() {
                    todoreviewModel.getTodo(timetableviewModel.Dates.value!!)
                }
            }
            todoInput.show(parentFragmentManager, todoInput.tag)
        }

        timetableviewModel = TimetableViewModel(requireContext())
        binding.viewModel = timetableviewModel
        timetableviewModel.init_value()
        todoreviewModel.init_value()

        todoreviewModel.TodoDataList.observe(viewLifecycleOwner) { contents ->
            val date = timetableviewModel.getDates()
            sun_todotableAdapter = TodotableAdapter(requireContext(), contents[0] ,date[0] ,TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel))
            sun_todorecyclerView.adapter = sun_todotableAdapter
            mon_todotableAdapter = TodotableAdapter(requireContext(), contents[1] ,date[1],TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel))
            mon_todorecyclerView.adapter = mon_todotableAdapter
            tue_todotableAdapter = TodotableAdapter(requireContext(), contents[2] ,date[2],TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel))
            tue_todorecyclerView.adapter = tue_todotableAdapter
            wed_todotableAdapter = TodotableAdapter(requireContext(), contents[3] ,date[3],TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel))
            wed_todorecyclerView.adapter = wed_todotableAdapter
            thu_todotableAdapter = TodotableAdapter(requireContext(), contents[4] ,date[4],TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel))
            thu_todorecyclerView.adapter = thu_todotableAdapter
            fri_todotableAdapter = TodotableAdapter(requireContext(), contents[5] ,date[5],TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel))
            fri_todorecyclerView.adapter = fri_todotableAdapter
            sat_todotableAdapter = TodotableAdapter(requireContext(), contents[6] ,date[6],TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel))
            sat_todorecyclerView.adapter = sat_todotableAdapter
        }

        val dragListener = TodoTimeTableTodoDragListener(todoreviewModel, timetableviewModel)
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


        /* 현재 고른 날짜에 따라서 색칠이 되고 있는데 오늘날짜로만 바꾸기 */
        timetableviewModel.Selected.observe(viewLifecycleOwner) { times ->
            val year = times.year.slice(0..times.year.length - 2)
            val month = times.month.slice(0..times.month.length - 2)
            val day = times.day.slice(0..times.day.length - 1)
            Log.d("WHAT", "$day")
            binding.sunLayout.setBackgroundResource(R.color.white)
            binding.monLayout.setBackgroundResource(R.color.white)
            binding.tueLayout.setBackgroundResource(R.color.white)
            binding.wedLayout.setBackgroundResource(R.color.white)
            binding.thuLayout.setBackgroundResource(R.color.white)
            binding.friLayout.setBackgroundResource(R.color.white)
            binding.satLayout.setBackgroundResource(R.color.white)

//            when(day){
//                timetableviewModel.Days.value?.get(0)-> binding.sunLayout.setBackgroundResource(R.drawable.todo_table_selected)
//                timetableviewModel.Days.value?.get(1)-> binding.monLayout.setBackgroundResource(R.drawable.todo_table_selected)
//                timetableviewModel.Days.value?.get(2)-> binding.tueLayout.setBackgroundResource(R.drawable.todo_table_selected)
//                timetableviewModel.Days.value?.get(3)-> binding.wedLayout.setBackgroundResource(R.drawable.todo_table_selected)
//                timetableviewModel.Days.value?.get(4)-> binding.thuLayout.setBackgroundResource(R.drawable.todo_table_selected)
//                timetableviewModel.Days.value?.get(5)-> binding.friLayout.setBackgroundResource(R.drawable.todo_table_selected)
//                timetableviewModel.Days.value?.get(6)-> binding.satLayout.setBackgroundResource(R.drawable.todo_table_selected)
//            }
            binding.invalidateAll()
        }


        timetableviewModel.Colors.observe(viewLifecycleOwner) { colors ->
            val date = timetableviewModel.getDates()
            val today = timetableviewModel.getToday()
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

            binding.todoSunDate.setTypeface(Typeface.DEFAULT)
            binding.todoMonDate.setTypeface(Typeface.DEFAULT)
            binding.todoTueDate.setTypeface(Typeface.DEFAULT)
            binding.todoWedDate.setTypeface(Typeface.DEFAULT)
            binding.todoThuDate.setTypeface(Typeface.DEFAULT)
            binding.todoFriDate.setTypeface(Typeface.DEFAULT)
            binding.todoSatDate.setTypeface(Typeface.DEFAULT)

            binding.todoSunDate.setBackgroundColor((Color.parseColor("#00000000")))
            binding.todoMonDate.setBackgroundColor((Color.parseColor("#00000000")))
            binding.todoTueDate.setBackgroundColor((Color.parseColor("#00000000")))
            binding.todoWedDate.setBackgroundColor((Color.parseColor("#00000000")))
            binding.todoThuDate.setBackgroundColor((Color.parseColor("#00000000")))
            binding.todoFriDate.setBackgroundColor((Color.parseColor("#00000000")))
            binding.todoSatDate.setBackgroundColor((Color.parseColor("#00000000")))

            /* 오늘 날짜에 해당되면 동그라미 */
            when(today){
                date[0] -> {
                    binding.todoSunDate.setTextColor(Color.parseColor("#2CA4FF"))
                    binding.todoSunDate.setTypeface(Typeface.DEFAULT_BOLD)
                    binding.todoSunDate.setBackgroundResource(R.drawable.calendar_today_background_image)
                    binding.sunLayout.setBackgroundResource(R.drawable.todo_table_selected)
                    }
                date[1] -> {
                    binding.todoMonDate.setTextColor(Color.parseColor("#2CA4FF"))
                    binding.todoMonDate.setTypeface(Typeface.DEFAULT_BOLD)
                    binding.todoMonDate.setBackgroundResource(R.drawable.calendar_today_background_image)
                    binding.monLayout.setBackgroundResource(R.drawable.todo_table_selected)
                }
                date[2] -> {
                    binding.todoTueDate.setTextColor(Color.parseColor("#2CA4FF"))
                    binding.todoTueDate.setTypeface(Typeface.DEFAULT_BOLD)
                    binding.todoTueDate.setBackgroundResource(R.drawable.calendar_today_background_image)
                    binding.tueLayout.setBackgroundResource(R.drawable.todo_table_selected)
                }
                date[3] -> {
                    binding.todoWedDate.setTextColor(Color.parseColor("#2CA4FF"))
                    binding.todoWedDate.setTypeface(Typeface.DEFAULT_BOLD)
                    binding.todoWedDate.setBackgroundResource(R.drawable.calendar_today_background_image)
                    binding.wedLayout.setBackgroundResource(R.drawable.todo_table_selected)
                }
                date[4] -> {
                    binding.todoThuDate.setTextColor(Color.parseColor("#2CA4FF"))
                    binding.todoThuDate.setTypeface(Typeface.DEFAULT_BOLD)
                    binding.todoThuDate.setBackgroundResource(R.drawable.calendar_today_background_image)
                    binding.thuLayout.setBackgroundResource(R.drawable.todo_table_selected)
                }
                date[5] -> {
                    binding.todoSatDate.setTextColor(Color.parseColor("#2CA4FF"))
                    binding.todoSatDate.setTypeface(Typeface.DEFAULT_BOLD)
                    binding.todoFriDate.setBackgroundResource(R.drawable.calendar_today_background_image)
                    binding.friLayout.setBackgroundResource(R.drawable.todo_table_selected)
                }
                date[6] -> {
                    binding.todoSatDate.setTextColor(Color.parseColor("#2CA4FF"))
                    binding.todoSatDate.setTypeface(Typeface.DEFAULT_BOLD)
                    binding.todoSatDate.setBackgroundResource(R.drawable.calendar_today_background_image)
                    binding.satLayout.setBackgroundResource(R.drawable.todo_table_selected)
                }
            }
        }

        //투두 쿼리문 전송
        timetableviewModel.Dates.observe(viewLifecycleOwner) { Dates ->
            todoreviewModel.getTodo(Dates)
        }


        /* 투두 리스트에서 나갈 때 타임테이블로 돌아감 */
        binding.todolistChange.setOnClickListener{
            Log.d("Frag", "changed")
            val newFrag = TimetableFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        binding.todotableMonthChooseLayout.setOnClickListener {
            val arrowImv = binding.todotableMonthChooseLayout.getChildAt(2) as ImageView
            arrowImv.rotation = 90f

            val datePicker = CustomCalendarDialog()
            datePicker.calendarClick =
                object : CustomCalendarDialog.CalendarClickListener {
                    override fun onClick(view: View, year: Int, month: Int, day: Int) {
                        timetableviewModel.init_value(year, month, day)
                        arrowImv.rotation = 0f
                    }
                }
            datePicker.cancelListener =
                object : CustomCalendarDialog.CancelListener {
                    override fun onClick() {
                        arrowImv.rotation = 0f
                    }
                }
            datePicker.show(parentFragmentManager, null)
        }


        return rootView
    }
}