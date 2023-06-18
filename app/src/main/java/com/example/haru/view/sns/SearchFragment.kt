package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentSearchBinding
import com.example.haru.databinding.PopupFriendDeleteConfirmBinding
import com.example.haru.utils.FormatDate
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.FriendsListAdapter
import com.example.haru.view.adapter.SearchScheduleAdapter
import com.example.haru.view.adapter.SearchTodoAdapter
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.view.calendar.CalendarItemFragment
import com.example.haru.view.checklist.ChecklistFragment
import com.example.haru.view.checklist.ChecklistItemFragment
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.MyPageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class SearchFragment(val viewModel: Any) : Fragment(){
    lateinit var binding: FragmentSearchBinding
    private lateinit var imm: InputMethodManager
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
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).adjustTopMargin(binding.searchHeader.id)

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

            scheduleAdapter.scheduleClick = object : SearchScheduleAdapter.ScheduleClick {
                override fun onClick(view: View, schedule: Schedule, today: Date) {
                    val calendarItemFragment = CalendarItemFragment(schedule, today)
                    calendarItemFragment.searchCallback =
                        object : CalendarItemFragment.SearchCallback {
                            override fun updateCallback(callback: () -> Unit) {
                                viewModel.updateSearchData()
                                callback()
                            }
                        }
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(
                            R.id.fragments_frame,
                            calendarItemFragment
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }

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
//                    val imm: InputMethodManager =   // 자동으로 키보드 내리기
//                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearchContent.windowToken, 0)
                    return@setOnKeyListener true
                }

                return@setOnKeyListener false
            }


        } else if (viewModel is MyPageViewModel) {
            binding.tvEmptyDescription.text = "아이디 또는 닉네임 검색을 통해\n친구를 찾을 수 있어요."

            var friendStatus = -1
            var targetInfo = com.example.haru.data.model.User()
            viewModel.searchUser.observe(viewLifecycleOwner) {
                Log.e("20191627", it.toString())
                if (it == null) {
                    targetInfo = User()
                    friendStatus = -1
                    binding.tvEmptyDescription.text = "아이디 또는 닉네임 검색을 통해\n친구를 찾을 수 있어요."
                    binding.ivEmpty.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.hagi_ruri_back)
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.userSearchLayout.visibility = View.GONE
                } else if (it.id == "") {
                    targetInfo = User()
                    friendStatus = -1
                    binding.userSearchLayout.visibility = View.GONE
                    binding.tvEmptyDescription.text = "검색 아이디 또는 닉네임을 가진\n친구를 찾을 수 없어요."
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.ivEmpty.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.account_delete_image)
                } else {
                    friendStatus = it.friendStatus
                    targetInfo = it
                    binding.userSearchLayout.visibility = View.VISIBLE
                    binding.emptyLayout.visibility = View.GONE
                    if (it.profileImage == "" || it.profileImage == "null" || it.profileImage == null)
                        binding.ivSearchUserProfile.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.profile_base_image
                            )
                    else Glide.with(this)
                        .load(it.profileImage)
                        .into(binding.ivSearchUserProfile)
                    binding.tvSearchUserId.text = it.name
                    setButtons(friendStatus)
                }
            }

            binding.btnSearchUser.setOnClickListener {
                when(friendStatus){
                    0 -> { //친구신청
                        viewModel.requestFriend(Followbody(targetInfo.id))
                    }
                    1 -> { //신청 취소
                        viewModel.requestUnFriend(com.example.haru.utils.User.id, UnFollowbody(targetInfo.id))
                    }
                    2 -> { //내 친구
                        val fragment = PopupDelFriend(targetInfo, viewModel)
                        fragment.show(parentFragmentManager, fragment.tag)
                    }
                    3 -> { //신청 수락
                        viewModel.requestAccpet(Friendbody(targetInfo.id))
                    }
                }
            }

            viewModel.FriendRequest.observe(viewLifecycleOwner){result ->
                if(result){
                    when(friendStatus){
                        0 -> {
                            friendStatus = 1
                        }
                        1 -> {
                            friendStatus = 0
                        }
                        2 -> {
                            friendStatus = 0
                        }
                        3 -> {
                            friendStatus = 2
                        }
                    }
                    setButtons(friendStatus)
                }
            }

            binding.etSearchContent.setOnKeyListener { view, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                    val content = binding.etSearchContent.text.toString().trim()
                    if (content != "") {
                        viewModel.getSearchUserInfo(content) {
                            Log.d("20191627", "정보 가져오기 완료")
                        }
                    }
                    binding.etSearchContent.setText("")
                    binding.etSearchContent.clearFocus()
                    imm.hideSoftInputFromWindow(binding.etSearchContent.windowToken, 0)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
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

    fun setButtons(friendStatus:Int){
        when(friendStatus){
            0 -> {
                binding.btnSearchUser.text = "친구 신청"
                binding.btnSearchUser.setBackgroundResource(R.drawable.gradation_btn_custom)
                binding.btnSearchUser.setTextColor(Color.parseColor("#FDFDFD"))
            }
            1 -> {
                binding.btnSearchUser.text = "신청 취소"
                binding.btnSearchUser.setBackgroundResource(R.drawable.custom_btn_date)
                binding.btnSearchUser.setTextColor(Color.parseColor("#191919"))
            }
            2 -> {
                binding.btnSearchUser.text = "내 친구"
                binding.btnSearchUser.setBackgroundResource(R.drawable.custom_btn_date)
                binding.btnSearchUser.setTextColor(Color.parseColor("#191919"))
            }
            3 -> {
                binding.btnSearchUser.text = "친구 수락"
                binding.btnSearchUser.setBackgroundResource(R.drawable.custom_btn_date)
                binding.btnSearchUser.setTextColor(Color.parseColor("#191919"))
            }
        }
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
                    else if (viewModel is MyPageViewModel)
                        viewModel.clearSearch()
                    requireActivity().supportFragmentManager.popBackStack()
                }

            }
        }
    }
}

class PopupDelFriend(val targetItem: com.example.haru.data.model.User, val viewModel: MyPageViewModel) :
    BottomSheetDialogFragment() {
    lateinit var binding: PopupFriendDeleteConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopupFriendDeleteConfirmBinding.inflate(inflater)

        if (targetItem.profileImage == null || targetItem.profileImage == "")
            binding.ivProfileImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)
        else
            Glide
                .with(requireContext())
                .load(targetItem.profileImage)
                .into(binding.ivProfileImage)

        binding.tvUserId.text = targetItem.name

        binding.btnDeleteUser.setOnClickListener {
            viewModel.requestDelFriend(DelFriendBody(targetItem.id!!))
            dismiss()
        }

        binding.btnDeleteCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 45 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}