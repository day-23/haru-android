package com.example.haru.view.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentChecklistTodayBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.viewmodel.CheckListViewModel
import java.util.*

class ChecklistTodayFragment(checkListVewModel: CheckListViewModel) : Fragment() {
    private lateinit var binding : FragmentChecklistTodayBinding
    private var checkListViewModel : CheckListViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(checkListVewModel: CheckListViewModel): ChecklistTodayFragment {
            return ChecklistTodayFragment(checkListVewModel)
        }
    }

    init{
        this.checkListViewModel = checkListVewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChecklistTodayBinding.inflate(inflater)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        checkListViewModel.clearToday()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToday()

        binding.tvTodayDate.text = FormatDate.simpleTodayToStr(Date())

        binding.ivTodayBackIcon.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initToday(){
        val todayRecyclerView : RecyclerView = binding.todayRecyclerView
        val todoAdapter = TodoAdapter(requireContext())

        todoAdapter.todoClick = object : TodoAdapter.TodoClick {
            override fun onClick(view: View, id: String) {
                if (checkListViewModel.todayTodo.value!!.find { it.id == id }!!.type == 2){
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, ChecklistItemFragment(checkListViewModel, id))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        todoAdapter.flagClick = object : TodoAdapter.FlagClick {
            override fun onClick(view: View, id: String) {
                val flag = if (checkListViewModel.todayTodo.value!!.find { it.id == id }!!.flag) Flag(false)
                else Flag(true)
                checkListViewModel.updateFlag(
                    flag,
                    id
                )
            }
        }

        todoAdapter.completeClick = object : TodoAdapter.CompleteClick {
            override fun onClick(view: View, id: String) {
                val todo = checkListViewModel.todayTodo.value!!.find{ it.id == id}!!
                val completed =
                    if (todo.completed) Completed(false)
                    else Completed(true)

                if (todo.completed || todo.repeatOption == null)
                    checkListViewModel.updateNotRepeatTodo(completed, id)
                else {
                    when(todo.repeatOption){
                        "매일" -> {
                            val nextEndDate = FormatDate.nextEndDate(todo.endDate, todo.repeatEnd)

                            if (nextEndDate != null){  // nextEndDate가 null이 아니라면 다음 반복이 있다는 의미
                                val nextEndDateStr = FormatDate.dateToStr(nextEndDate)
                                checkListViewModel.updateRepeatTodo(id, EndDate(nextEndDateStr))
                            } else // null이라면 반복 종료
                                checkListViewModel.updateNotRepeatTodo(completed, id)
                        }
                        "매주" -> {

                        }
                        "2주마다" -> {}
                        "매월" -> {}
                        "매년" -> {}
                    }
                }
            }
        }

        todoAdapter.subTodoCompleteClick = object : TodoAdapter.SubTodoCompleteClick {
            override fun onClick(view: View, subTodoPosition: Int) {
                val subTodo = checkListViewModel.todayTodo.value!!.find { it.id == todoAdapter.subTodoClickId }!!.subTodos[subTodoPosition]
                val completed =
                    if (subTodo.completed) Completed(
                        false
                    )
                    else Completed(true)

                checkListViewModel.updateSubTodo(
                    completed,
                    todoAdapter.subTodoClickId!!,
                    subTodo.id,
                    subTodoPosition
                )
            }
        }

        todoAdapter.toggleClick = object : TodoAdapter.ToggleClick {
            override fun onClick(view: View, id: String) {
                val folded = if (view.isSelected) Folded(true) else Folded(false)

                checkListViewModel.updateFolded(folded, id)
            }
        }

        todayRecyclerView.adapter = todoAdapter
        todayRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        checkListViewModel.todayTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val todayTodo = it.filterIsInstance<Todo>()
            todoAdapter.setDataList(todayTodo)
        })
    }
}