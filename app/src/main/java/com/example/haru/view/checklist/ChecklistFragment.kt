package com.example.haru.view.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentChecklistBinding
import com.example.haru.view.adapter.TagAdapter
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.viewmodel.CheckListViewModel

class ChecklistFragment : Fragment(), LifecycleObserver {
    private lateinit var binding: FragmentChecklistBinding
    private lateinit var checkListViewModel: CheckListViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistFragment {
            return ChecklistFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistFragment - onCreate() called")

        checkListViewModel = CheckListViewModel()
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
        initTagList()
        initTodoList()

        binding.btnAddTodo.setOnClickListener{
            val todoInput = ChecklistInputFragment(checkListViewModel)
            todoInput.show(parentFragmentManager, todoInput.tag)
        }
    }

    private fun initTagList() {
        val tagRecyclerView: RecyclerView = binding.recyclerTags
        val tagAdapter = TagAdapter(requireContext())
        tagAdapter.tagClick = object :TagAdapter.TagClick{
            override fun onClick(view: View, position: Int) {
                if (position > 2)
                    checkListViewModel.getTodoByTag(position)
                Log.d("20191627", position.toString() + ": 눌렸다")
            }
        }

        tagRecyclerView.adapter = tagAdapter
        tagRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        checkListViewModel.tagDataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val dataList = it.filterIsInstance<Tag>()
            tagAdapter.setDataList(dataList)
        })
    }

    private fun initTodoList(){
        val todoListView: RecyclerView = binding.recyclerTodos
        val todoAdapter = TodoAdapter(requireContext())

        todoListView.adapter = todoAdapter
        todoListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        todoListView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        checkListViewModel.todoDataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val dataList = it.filterIsInstance<Todo>()
            todoAdapter.setDataList(dataList)
        })

    }

}