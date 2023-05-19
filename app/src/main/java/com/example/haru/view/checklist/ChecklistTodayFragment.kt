package com.example.haru.view.checklist

import android.content.Context
import android.os.Bundle
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
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.viewmodel.CheckListViewModel
import java.util.*

class ChecklistTodayFragment(checkListVewModel: CheckListViewModel) : Fragment() {
    private lateinit var binding: FragmentChecklistTodayBinding
    private var checkListViewModel: CheckListViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(checkListVewModel: CheckListViewModel): ChecklistTodayFragment {
            return ChecklistTodayFragment(checkListVewModel)
        }
    }

    init {
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

        binding.ivTodayBackIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnAddTodoToday.setOnClickListener {
            val text = binding.etSimpleAddTodoToday.text.toString()

            if (text.replace(" ", "") == "") {
                val todoInput = ChecklistInputFragment(checkListViewModel, today = true)
                todoInput.show(parentFragmentManager, todoInput.tag)
            } else {
                val todo = TodoRequest(
                    content = text,
                    memo = "",
                    todayTodo = true,
                    flag = false,
                    isAllDay = false,
                    tags = emptyList(),
                    subTodos = emptyList(),
                    alarms = emptyList()
                )
                checkListViewModel.addTodo(todo) {
                    binding.etSimpleAddTodoToday.setText("")
                    binding.etSimpleAddTodoToday.clearFocus()
                    val imm: InputMethodManager =   // 자동으로 키보드 내리기
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSimpleAddTodoToday.windowToken, 0)
                }
            }
        }
    }

    private fun initToday() {
        val todayRecyclerView: RecyclerView = binding.todayRecyclerView
        val todoAdapter = TodoAdapter(requireContext())

        todoAdapter.sectionToggleClick = object : TodoAdapter.SectionToggleClick {
            override fun onClick(view: View, str: String) {
                checkListViewModel.setVisibility(str, 1)
            }
        }

        todoAdapter.todoClick = object : TodoAdapter.TodoClick {
            override fun onClick(view: View, id: String) {
                if (checkListViewModel.todayTodo.value!!.find { it.id == id }!!.type == 2) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragments_frame,
                            ChecklistItemFragment(checkListViewModel, id)
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        todoAdapter.flagClick = object : TodoAdapter.FlagClick {
            override fun onClick(view: View, id: String, callback: (flag: Flag) -> Unit) {
                val flag =
                    if (checkListViewModel.todayTodo.value!!.find { it.id == id }!!.flag) Flag(false)
                    else Flag(true)
                checkListViewModel.updateFlag(
                    flag,
                    id
                ) {
                    callback(it)
                }
            }
        }

        todoAdapter.completeClick = object : TodoAdapter.CompleteClick {
            override fun onClick(view: View, id: String, callback: (completed: Completed) -> Unit) {
                val todo = checkListViewModel.todayTodo.value!!.find { it.id == id }!!
                val completed =
                    if (todo.completed) Completed(false)
                    else Completed(true)

                if (todo.completed || todo.repeatOption == null)
                    checkListViewModel.completeNotRepeatTodo(completed, id) {
                        callback(it)
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
                        checkListViewModel.completeRepeatFrontTodo(
                            id,
                            FrontEndDate(nextEndDateStr!!)
                        ) { callback(Completed(true)) }
                    } else
                        checkListViewModel.completeNotRepeatTodo(completed, id) {
                            callback(it)
                        }
                }
            }
        }

        todoAdapter.subTodoCompleteClick = object : TodoAdapter.SubTodoCompleteClick {
            override fun onClick(view: View, subTodoPosition: Int) {
                val subTodo =
                    checkListViewModel.todayTodo.value!!.find { it.id == todoAdapter.subTodoClickId }!!.subTodos[subTodoPosition]
                val completed =
                    if (subTodo.completed) Completed(
                        false
                    )
                    else Completed(true)

                checkListViewModel.completeSubTodo(
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

        todayRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    binding.simpleAddFabLayoutToday.visibility = View.VISIBLE
                else binding.simpleAddFabLayoutToday.visibility = View.GONE
            }
        })

        checkListViewModel.todayTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val todayTodo = it.filterIsInstance<Todo>()
            todoAdapter.setDataList(todayTodo)
        })
    }
}