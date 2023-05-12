package com.example.haru.view.checklist

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
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

//        binding.tagEtcLayout.btnTagEtcDone.setOnClickListener {
//            binding.drawableLayout.closeDrawer(Gravity.RIGHT)
//        }

        binding.tagEtcLayout.ivTagAdd.setOnClickListener {
            val inputTag =
                checkListViewModel.readyCreateTag(binding.tagEtcLayout.etTagInput.text.toString())
            when (inputTag) {
                null -> Toast.makeText(requireContext(), "태그에 공백이 포함될 수 없습니다..", Toast.LENGTH_SHORT)
                    .show()
                "" -> Toast.makeText(requireContext(), "추가 할 태그가 없습니다.", Toast.LENGTH_SHORT).show()
                else -> {
                    checkListViewModel.createTag(Content(inputTag))
                    binding.tagEtcLayout.etTagInput.setText("")
                    binding.tagEtcLayout.etTagInput.clearFocus()
                    val imm: InputMethodManager =   // 자동으로 키보드 내리기
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.tagEtcLayout.etTagInput.windowToken, 0)
                }
            }
        }

        binding.tagEtcLayout.etTagInput.setOnKeyListener { view, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_SPACE || i == KeyEvent.KEYCODE_ENTER) {
                binding.tagEtcLayout.ivTagAdd.performClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        binding.btnAddTodo.setOnClickListener {
            val text = binding.etSimpleAddTodo.text.toString()

            if (text.replace(" ", "") == "") {
                val todoInput = ChecklistInputFragment(checkListViewModel)
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
                    binding.etSimpleAddTodo.setText("")
                    binding.etSimpleAddTodo.clearFocus()
                    val imm: InputMethodManager =   // 자동으로 키보드 내리기
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSimpleAddTodo.windowToken, 0)
                }
            }
        }

        binding.todayTodoLayout.setOnClickListener {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23) // 시간을 23시로 설정
                set(Calendar.MINUTE, 59) // 분을 59분으로 설정
                set(Calendar.SECOND, 59) // 초를 59초로 설정
            }
            val todayFrontEndDate = FrontEndDate(FormatDate.dateToStr(calendar.time)!!)
            checkListViewModel.getTodayTodo(todayFrontEndDate) {
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
            Log.d("20191627", "tagList : $it")
            val dataList = it.filterIsInstance<Tag>()
            tagAdapter.setDataList(dataList)

            for (i in binding.tagEtcLayout.tagLayout.childCount - 1 downTo 1)
                binding.tagEtcLayout.tagLayout.removeViewAt(i)

            for (i in 2 until checkListViewModel.tagDataList.value!!.size) {
                val layoutInflater =
                    requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val addView = layoutInflater.inflate(R.layout.tag_example_layout, null)

                val tag = checkListViewModel.tagDataList.value!![i]

                addView.findViewById<AppCompatButton>(R.id.btn_tag_etc).apply {
                    text = tag.content

                    // tag.isSelected가 false이면 보여주는 태그가 아니므로 배경과 글자 색상을 그에 맞춘다.
                    val textColor : Int
                    val drawable : Drawable?
                    if (!tag.isSelected) {
                        textColor = ContextCompat.getColor(context, R.color.white)
                        drawable = ContextCompat.getDrawable(context, R.drawable.tag_btn_un_selected)
                    }
                    else {
                        textColor = ContextCompat.getColor(context, R.color.todo_description)
                        drawable = ContextCompat.getDrawable(context, R.drawable.tag_btn_custom)
                    }
                    this.background = drawable
                    this.setTextColor(textColor)

                    setOnClickListener {
                        checkListViewModel.updateTag(
                            checkListViewModel.tagDataList.value!![i].id,
                            TagUpdate(this.text.toString(), !tag.isSelected)
                        )
                    }
                }

                addView.findViewById<ImageView>(R.id.iv_set_tag_etc).setOnClickListener { iv ->
                    val themeWrapper =
                        ContextThemeWrapper(context, R.style.MyPopupMenu) // tag popup menu 스타일 지정
                    val popUp = PopupMenu(
                        themeWrapper,
                        iv,
                        Gravity.END,
                        0,
                        R.style.MyPopupMenu
                    ) // 스타일 한 번 더  명시해줘야함.
                    popUp.menuInflater.inflate(R.menu.tag_popup_menu, popUp.menu)
                    popUp.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.tag_delete -> {
                                Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT)
                                    .show()
                                checkListViewModel.deleteTagList(TagIdList(listOf(checkListViewModel.tagDataList.value!![i].id)))
                            }
                            R.id.tag_update -> {
                                Toast.makeText(requireContext(), "Update", Toast.LENGTH_SHORT)
                                    .show()
//                                checkListViewModel.updateTag(checkListViewModel.tagDataList.value!![i].id, TagUpdate(content = ""))
                            }
                            else -> {}
                        }
                        return@setOnMenuItemClickListener true
                    }
                    popUp.show()
                }

                binding.tagEtcLayout.tagLayout.addView(addView)
            }
        })
    }

    private fun initTodoList() {
        val todoListView: RecyclerView = binding.recyclerTodos
        val todoAdapter = TodoAdapter(requireContext())

        todoAdapter.sectionToggleClick = object : TodoAdapter.SectionToggleClick {
            override fun onClick(view: View, str: String) {
                Log.d("20191627", "section Click")
                checkListViewModel.setVisibility(str, 0)
            }
        }

        todoAdapter.dropListener = object : TodoAdapter.DropListener {
            override fun onDropFragment(list: List<String>) {
                checkListViewModel.updateOrderMainTodo(changeOrderTodo = ChangeOrderTodo(list))
            }
        }

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
                val todo = checkListViewModel.todoDataList.value!!.find { it.id == id }!!
                val completed =
                    if (todo.completed) Completed(
                        false
                    )
                    else Completed(true)

                if (todo.completed || todo.repeatOption == null) // 완료된 Todo이거나 repeatOption이 null
                    checkListViewModel.completeNotRepeatTodo(completed, id)
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
                        checkListViewModel.completeRepeatFrontTodo(
                            id,
                            FrontEndDate(nextEndDateStr!!)
                        )
                    } else
                        checkListViewModel.completeNotRepeatTodo(completed, id)
                }

            }
        }

        todoAdapter.subTodoCompleteClick = object : TodoAdapter.SubTodoCompleteClick {
            override fun onClick(view: View, subTodoPosition: Int) {
                val subTodo =
                    checkListViewModel.todoDataList.value!!.find { it.id == todoAdapter.subTodoClickId }!!.subTodos[subTodoPosition]
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



        todoListView.adapter = todoAdapter
        todoListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        todoListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    binding.simpleAddFabLayout.visibility = View.VISIBLE
                else binding.simpleAddFabLayout.visibility = View.GONE
            }
        })

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