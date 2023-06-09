package com.example.haru.view.sns

import BaseActivity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentSearchBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.SearchScheduleAdapter
import com.example.haru.view.adapter.SearchTodoAdapter
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.view.checklist.ChecklistFragment
import com.example.haru.view.checklist.ChecklistItemFragment
import com.example.haru.viewmodel.CheckListViewModel

class SearchFragment(val viewModel: Any) : Fragment() {
    lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "SearchFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "SearchFragment - onCreateView() called")

        binding = FragmentSearchBinding.inflate(inflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).adjustTopMargin(binding.searchHeader.id)

        Log.e("20191627", "123")

        // checklist와 캘린더에서 접근한 검색 화면일 경우
        if (viewModel is CheckListViewModel) {
            val scheduleListView: RecyclerView = binding.searchRecyclerOne
            val scheduleAdapter = SearchScheduleAdapter(requireContext())

            scheduleListView.adapter = scheduleAdapter
            scheduleListView.layoutManager = ChecklistFragment.WrapContentLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            // -------------------------------------Todo 검색 결과를 맡은 RecyclerView---------------------------------------//
            val todoListView: RecyclerView = binding.searchRecyclerTwo
            val todoAdapter = SearchTodoAdapter(requireContext())

            todoAdapter.todoClick = object : SearchTodoAdapter.TodoClick {
                override fun onClick(view: View, id: String) {
                    if (viewModel.searchList.value?.second?.find {
                            it.id == id
                        }?.type == 2) {

                        requireActivity().supportFragmentManager.beginTransaction()
                            .add(
                                R.id.fragments_frame,
                                ChecklistItemFragment(viewModel, id)
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }

            todoAdapter.flagClick = object : SearchTodoAdapter.FlagClick {
                override fun onClick(
                    view: View,
                    id: String,
                    callback: (flag: Flag, successData: SuccessFail?) -> Unit
                ) {
                    val flag =
                        if (viewModel.searchList.value?.second?.find { it.id == id }!!.flag) Flag(
                            false
                        )
                        else Flag(true)
                    viewModel.updateFlag(
                        flag,
                        id
                    ) { flag, successData ->
                        callback(flag, successData)
                    }
                }
            }

            todoAdapter.completeClick = object : SearchTodoAdapter.CompleteClick {
                override fun onClick(
                    view: View,
                    id: String,
                    callback: (completed: Completed, successData: SuccessFail?) -> Unit
                ) {
                    val todo = viewModel.searchList.value?.second?.find { it.id == id }!!
                    val completed =
                        if (todo.completed) Completed(
                            false
                        )
                        else Completed(true)

                    if (todo.completed || todo.repeatOption == null) // 완료된 Todo이거나 repeatOption이 null
                        viewModel.completeNotRepeatTodo(
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
                        Log.d("20191627", nextEndDate.toString())
                        if (nextEndDate != null) {
                            val nextEndDateStr = FormatDate.dateToStr(nextEndDate)
                            viewModel.completeRepeatFrontTodo(
                                id,
                                FrontEndDate(nextEndDateStr!!)
                            ) {
                                callback(Completed(true), it)
                            }
                        } else
                            viewModel.completeNotRepeatTodo(
                                completed,
                                id
                            ) { completed, successData ->
                                callback(completed, successData)
                            }
                    }

                }
            }

            todoAdapter.subTodoCompleteClick = object : SearchTodoAdapter.SubTodoCompleteClick {
                override fun onClick(view: View, subTodoPosition: Int) {
                    val subTodo =
                        viewModel.searchList.value?.second?.find { it.id == todoAdapter.subTodoClickId }!!.subTodos[subTodoPosition]
                    val completed =
                        if (subTodo.completed) Completed(
                            false
                        )
                        else Completed(true)

                    viewModel.completeSubTodo(
                        completed,
                        todoAdapter.subTodoClickId!!,
                        subTodo.id,
                        subTodoPosition
                    )
                }
            }

            todoAdapter.toggleClick = object : SearchTodoAdapter.ToggleClick {
                override fun onClick(view: View, id: String) {
                    val folded = if (view.isSelected) Folded(true) else Folded(false)
                    viewModel.updateFolded(folded, id)
                }
            }

            todoListView.adapter = todoAdapter
            todoListView.layoutManager = ChecklistFragment.WrapContentLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            // -------------------------------------------------------------------------------------------------------//
            viewModel.searchList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                binding.searchRecyclerOne.visibility =
                    if (it.first == null) View.GONE else View.VISIBLE
                binding.searchRecyclerTwo.visibility =
                    if (it.second == null) View.GONE else View.VISIBLE
                binding.emptyLayout.visibility =
                    if (it.first == null && it.second == null) View.VISIBLE else View.GONE

                if (it.first == null && it.second == null) {
                    scheduleAdapter.content = null
                    todoAdapter.content = null
                }

                scheduleAdapter.setDataList(it.first)
                todoAdapter.setDataList(it.second)
            })

            binding.etSearchContent.setOnKeyListener { view, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                    val content = binding.etSearchContent.text.toString().trim()
                    if (content != "") {
                        viewModel.getScheduleTodoSearch(content = content)
                        scheduleAdapter.content = content
                        todoAdapter.content = content
                    }
                    binding.etSearchContent.setText("")
                    binding.etSearchContent.clearFocus()
                    val imm: InputMethodManager =   // 자동으로 키보드 내리기
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearchContent.windowToken, 0)
                    return@setOnKeyListener true
                }

                return@setOnKeyListener false
            }


        } else {
            TODO()
        }


        // 검색어 입력시에만 bold처리
        binding.etSearchContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()
                if (str == "") {
                    binding.etSearchContent.setTypeface(null, Typeface.NORMAL)
                    return
                }

                binding.etSearchContent.setTypeface(null, Typeface.BOLD)
            }

        })

        binding.ivSearchCancel.setOnClickListener(ClickListener())


    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.searchHeader.id)
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivSearchCancel.id -> {
                    if (viewModel is CheckListViewModel)
                        viewModel.clearSearch()
                    requireActivity().supportFragmentManager.popBackStack()
                }

            }
        }
    }
}