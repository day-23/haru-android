package com.example.haru.view.checklist

import BaseActivity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentChecklistTodayBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.view.sns.SnsFragment
import com.example.haru.viewmodel.CheckListViewModel
import java.util.*

class ChecklistTodayFragment() : Fragment() {
    private lateinit var binding: FragmentChecklistTodayBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistTodayFragment {
            return ChecklistTodayFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChecklistTodayBinding.inflate(inflater)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.hideNavi(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        CheckListViewModel.clearToday()
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.checklistTodayLayout.id)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.checklistTodayLayout.id)

        initToday()

        binding.tvTodayDate.text = FormatDate.simpleTodayToStr(Date())

        binding.ivTodayBackIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initToday() {
        val todayRecyclerView: RecyclerView = binding.todayRecyclerView
        val todoAdapter = TodoAdapter(requireContext())

        todoAdapter.sectionToggleClick = object : TodoAdapter.SectionToggleClick {
            override fun onClick(view: View, str: String) {
                CheckListViewModel.setVisibility(str, 1)
            }
        }

        todoAdapter.todoClick = object : TodoAdapter.TodoClick {
            override fun onClick(view: View, id: String) {
                if (CheckListViewModel.todayTodo.value!!.find { it.id == id }!!.type == 2) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragments_frame,
                            ChecklistItemFragment(id)
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        todoAdapter.flagClick = object : TodoAdapter.FlagClick {
            override fun onClick(
                view: View,
                id: String,
                callback: (flag: Flag, successData: SuccessFail?) -> Unit
            ) {
                val flag =
                    if (CheckListViewModel.todayTodo.value!!.find { it.id == id }!!.flag) Flag(false)
                    else Flag(true)
                CheckListViewModel.updateFlag(
                    flag,
                    id
                ) { flag, successData ->
                    callback(flag, successData)
                }
            }
        }

        todoAdapter.completeClick = object : TodoAdapter.CompleteClick {
            override fun onClick(
                view: View,
                id: String,
                callback: (completed: Completed, successData: SuccessFail?) -> Unit
            ) {
                val todo = CheckListViewModel.todayTodo.value!!.find { it.id == id }!!
                val completed =
                    if (todo.completed) Completed(false)
                    else Completed(true)

                if (todo.completed || todo.repeatOption == null)
                    CheckListViewModel.completeNotRepeatTodo(
                        completed,
                        id
                    ) { completed, successData ->
                        callback(completed, successData)
                    }
                else {
                    val nextEndDate = when (todo.repeatOption) {
                        "매일" -> {
                            FormatDate.nextEndDate(todo.endDate, todo.repeatEnd)
                        }
                        "매주", "격주" -> {
                            val repeatOption = if (todo.repeatOption == "매주") 1 else 2
                            FormatDate.nextEndDateEveryWeek(
                                todo.repeatValue!!,
                                repeatOption,
                                todo.endDate,
                                todo.repeatEnd
                            )
                        }
                        "매달" -> {
                            FormatDate.nextEndDateEveryMonth(
                                todo.repeatValue!!,
                                todo.endDate,
                                todo.repeatEnd
                            )
                        }
                        "매년" -> {
                            FormatDate.nextEndDateEveryYear(
                                todo.repeatValue!!,
                                todo.endDate,
                                todo.repeatEnd
                            )
                        }
                        else -> null
                    }
                    if (nextEndDate != null) {
                        val nextEndDateStr = FormatDate.dateToStr(nextEndDate)
                        CheckListViewModel.completeRepeatFrontTodo(
                            id,
                            FrontEndDate(nextEndDateStr!!)
                        ) {
                            callback(Completed(true), it)
                        }
                    } else
                        CheckListViewModel.completeNotRepeatTodo(
                            completed,
                            id
                        ) { completed, successData ->
                            callback(completed, successData)
                        }
                }
            }
        }

        todoAdapter.subTodoCompleteClick = object : TodoAdapter.SubTodoCompleteClick {
            override fun onClick(view: View, subTodoPosition: Int) {
                val subTodo =
                    CheckListViewModel.todayTodo.value!!.find { it.id == todoAdapter.subTodoClickId }!!.subTodos[subTodoPosition]
                val completed =
                    if (subTodo.completed) Completed(
                        false
                    )
                    else Completed(true)

                CheckListViewModel.completeSubTodo(
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

                CheckListViewModel.updateFolded(folded, id)
            }
        }

        todayRecyclerView.adapter = todoAdapter
        todayRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        CheckListViewModel.todayTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val todayTodo = it.filterIsInstance<Todo>()
            todoAdapter.setDataList(todayTodo)
        })
    }
}