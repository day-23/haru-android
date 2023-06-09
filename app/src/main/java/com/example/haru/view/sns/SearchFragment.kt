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
import com.example.haru.data.model.Flag
import com.example.haru.data.model.SuccessFail
import com.example.haru.databinding.FragmentSearchBinding
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

            val todoListView: RecyclerView = binding.searchRecyclerTwo
            val todoAdapter = SearchTodoAdapter(requireContext())

            scheduleListView.adapter = scheduleAdapter
            scheduleListView.layoutManager = ChecklistFragment.WrapContentLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            todoListView.adapter = todoAdapter
            todoListView.layoutManager = ChecklistFragment.WrapContentLinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            todoAdapter.todoClick = object : SearchTodoAdapter.TodoClick {
                override fun onClick(view: View, id: String) {
                    if (viewModel.searchList.value?.second?.find {
                            it.id == id
                        }?.type == 2) {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
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