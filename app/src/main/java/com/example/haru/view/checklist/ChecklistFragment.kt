package com.example.haru.view.checklist

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentChecklistBinding
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.TagAdapter
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.viewmodel.TodoRecyclerViewModel
import java.util.*

class ChecklistFragment : Fragment(), LifecycleObserver {
    private lateinit var binding: FragmentChecklistBinding
    private lateinit var recyclerViewModel: TodoRecyclerViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistFragment {
            return ChecklistFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistFragment - onCreate() called")

        recyclerViewModel = TodoRecyclerViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ChecklistFragment - onCreateView() called")

        binding = FragmentChecklistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initTagList()
        initTodoList()

        binding.btnAddTodo.setOnClickListener{
            val todoInput = ChecklistInputFragment()
            todoInput.show(parentFragmentManager, todoInput.tag)
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mActivity = context as Activity
//    }

//    private fun initTagList() {
//        val tagListView: RecyclerView = binding.recyclerTags
//        val tagAdapter = TagAdapter(requireContext())
//
//        tagListView.adapter = tagAdapter
//        tagListView.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//
//        recyclerViewModel.dataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            val dataList = it.filterIsInstance<Tag>()
//            tagAdapter.setDataList(dataList)
//        })
//    }

    private fun initTodoList(){
        val todoListView: RecyclerView = binding.recyclerTodos
        val todoAdapter = TodoAdapter(requireContext())

        todoListView.adapter = todoAdapter
        todoListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerViewModel.dataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val dataList = it.filterIsInstance<Todo>()
            todoAdapter.setDataList(dataList)
        })

    }

}