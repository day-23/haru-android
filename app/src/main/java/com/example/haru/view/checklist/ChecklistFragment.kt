package com.example.haru.view.checklist

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentChecklistBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.adapter.TagAdapter
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.viewmodel.CheckListViewModel
import java.util.*

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

        binding.ivTagEtc.setOnClickListener {
            if (!binding.drawableLayout.isDrawerOpen(Gravity.RIGHT))
                binding.drawableLayout.openDrawer(Gravity.RIGHT)
            else
                binding.drawableLayout.closeDrawer(Gravity.RIGHT)
        }

        binding.btnAddTodo.setOnClickListener {
            val todoInput = ChecklistInputFragment(checkListViewModel)
            todoInput.show(parentFragmentManager, todoInput.tag)
        }

        binding.todayTodoLayout.setOnClickListener {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                set(Calendar.SECOND, 59) // 초를 59초로 설정
            }
            val todayEndDate = TodayEndDate(FormatDate.dateToStr(calendar.time))
            checkListViewModel.getTodayTodo(todayEndDate) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, ChecklistTodayFragment(checkListViewModel))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun initTagList() {
        val tagRecyclerView: RecyclerView = binding.recyclerTags
        val tagAdapter = TagAdapter(requireContext())
        tagAdapter.setTagPosition(checkListViewModel.clickedTag) // 이전 Tag 클릭 값 있으면 해당 값으로
        tagAdapter.tagClick = object : TagAdapter.TagClick {
            override fun onClick(view: View, position: Int) {
                if (position == 0)
                    checkListViewModel.getTodoByFlag()
                else if (position > 0) {
                    checkListViewModel.clear()
                    checkListViewModel.getTodoByTag(position)
                    checkListViewModel.clickedTag = position  // 이전 Tag 클릭 값 기억
                }
            }
        }

        tagRecyclerView.adapter = tagAdapter
        tagRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val animator = tagRecyclerView.itemAnimator     //리사이클러뷰 애니메이터 get
        if (animator is SimpleItemAnimator) {          //아이템 애니메이커 기본 하위클래스
            animator.supportsChangeAnimations =
                false  //애니메이션 값 false (리사이클러뷰가 화면을 다시 갱신 했을때 뷰들의 깜빡임 방지)
        }

        checkListViewModel.tagDataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val dataList = it.filterIsInstance<Tag>()
            tagAdapter.setDataList(dataList)

            for(i in 2 until checkListViewModel.tagDataList.value!!.size){
                val layoutInflater =
                    requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val addView = layoutInflater.inflate(R.layout.tag_example_layout, null)

                addView.findViewById<AppCompatButton>(R.id.btn_tag_etc).text = checkListViewModel.tagDataList.value!![i].content
                binding.tagEtcLayout.tagLayout.addView(addView)
            }
        })
    }

    private fun initTodoList() {
        val todoListView: RecyclerView = binding.recyclerTodos
        val todoAdapter = TodoAdapter(requireContext())
        todoAdapter.todoClick = object : TodoAdapter.TodoClick {
            override fun onClick(view: View, id: String) {
                if (checkListViewModel.todoDataList.value!!.find {
                        it.id == id
                    }!!.type == 2) {
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
            override fun onClick(view: View, id: String) {
                val flag =
                    if (checkListViewModel.todoDataList.value!!.find { it.id == id }!!.flag) Flag(
                        false
                    )
                    else Flag(true)
                checkListViewModel.updateFlag(
                    flag,
                    id
                )
            }
        }

        todoAdapter.completeClick = object : TodoAdapter.CompleteClick {
            override fun onClick(view: View, id: String) {
                val completed =
                    if (checkListViewModel.todoDataList.value!!.find { it.id == id }!!.completed) Completed(
                        false
                    )
                    else Completed(true)

                checkListViewModel.updateNotRepeatTodo(completed, id)
            }
        }

        todoAdapter.subTodoCompleteClick = object : TodoAdapter.SubTodoCompleteClick {
            override fun onClick(view: View, subTodoPosition: Int) {
                val subTodo = checkListViewModel.todoDataList.value!!.find{ it.id == todoAdapter.subTodoClickId }!!.subTodos[subTodoPosition]
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

        todoListView.adapter = todoAdapter
        todoListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        ItemTouchHelper(ChecklistItemTouchHelperCallback(todoAdapter)).attachToRecyclerView(
            todoListView
        )

        checkListViewModel.todoDataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            todoAdapter.setFlagCount(checkListViewModel.flaggedTodos.value?.size)
            todoAdapter.setTagCount(checkListViewModel.taggedTodos.value?.size)
            todoAdapter.setUnTagCount(checkListViewModel.untaggedTodos.value?.size)
            todoAdapter.setCompleteCount(checkListViewModel.completedTodos.value?.size)

            Log.d("20191627", "todoDataList Update")
            val dataList = it.filterIsInstance<Todo>()
            todoAdapter.setDataList(dataList)
        })

        checkListViewModel.addTodoId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val layoutManager = todoListView.layoutManager as LinearLayoutManager
            val todo = checkListViewModel.getTodoList().find { todo -> todo.id == it }
            val position = checkListViewModel.getTodoList().indexOf(todo)
            val height = todoListView.height

            layoutManager.scrollToPositionWithOffset(position, height)
        })

        checkListViewModel.todoByTag.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            todoAdapter.setTodoByTag(checkListViewModel.todoByTagItem)
        })

    }

}