package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ViewCompat.canScrollVertically
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentFriendsListBinding
import com.example.haru.databinding.PopupFriendDeleteConfirmBinding
import com.example.haru.databinding.PopupSnsPostCancelBinding
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.FriendsListAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.sdk.talk.model.Friend

interface OnFriendClicked {

    fun onProfileClick(item: FriendInfo)
    fun onDeleteClick(item: FriendInfo)
    fun onAcceptClick(item: FriendInfo)
    fun onRejectClick(item: FriendInfo)
    fun onCancelClick(item: FriendInfo)
    fun onRequestClick(item: FriendInfo)
    fun onPopupClick(position: Int)
}

class FriendsListFragment(val targetId: String) : Fragment(), OnFriendClicked {
    lateinit var binding: FragmentFriendsListBinding
    private lateinit var mypageViewModel: MyPageViewModel
    private lateinit var friendAdapter: FriendsListAdapter
    var isFriendList = true // 친구목록 보기중인지 false == 친구신청 보여주기
    var lastCreatedAt = ""
    var deleteTarget = FriendInfo()

    override fun onProfileClick(item: FriendInfo) {
        val newFrag = MyPageFragment(item.id!!)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        transaction.addToBackStack("friendlist")
        transaction.commit()
    }

    override fun onDeleteClick(item: FriendInfo) {
        deleteTarget = item
        val fragment = PopupDeleteFriend(item, this)
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.friend_list_anchor, fragment)
        transaction.commit()
    }

    override fun onAcceptClick(item: FriendInfo) {
        mypageViewModel.requestAccpet(Friendbody(item.id!!))
        item.friendStatus = 2
        friendAdapter.patchInfo(item)
    }

    override fun onRejectClick(item: FriendInfo) { //친구요청 거절
        mypageViewModel.requestUnFriend(item.id!!, UnFollowbody(User.id))
        mypageViewModel.FriendRequest.observe(viewLifecycleOwner) { result ->
            friendAdapter.deleteFriend(item)
            Toast.makeText(requireContext(), "거절 성공", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCancelClick(item: FriendInfo) { //친구신청 취소
        mypageViewModel.requestUnFriend(com.example.haru.utils.User.id, UnFollowbody(item.id!!))
    }

    override fun onRequestClick(item: FriendInfo) { //친구신청
        mypageViewModel.requestFriend(Followbody(item.id!!))
    }

    override fun onPopupClick(position: Int) {
//        val fragmentManager = childFragmentManager
//        val fragment = fragmentManager.findFragmentById(R.id.friend_list_anchor)
//        if (fragment != null) {
//            MainActivity.hideNavi(false)
//            val transaction = fragmentManager.beginTransaction()
//            transaction.remove(fragment)
//            transaction.commit()
//        }
        if (position == 0) {
            mypageViewModel.requestDelFriend(DelFriendBody(deleteTarget.id!!))
            mypageViewModel.FriendRequest.observe(viewLifecycleOwner) { result ->
                friendAdapter.deleteFriend(deleteTarget)
                Toast.makeText(requireContext(), "삭제 성공", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "FriendsListFragment - onCreate() called")
        mypageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.friendsListTitle.id)

        MainActivity.hideNavi(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.friendsListTitle.id)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        if (User.id != targetId) {
            binding.requestListContainer.visibility = View.GONE
        }
        mypageViewModel.getFirstFriendsList(targetId)
        friendAdapter = FriendsListAdapter(requireContext(), arrayListOf(), targetId, this)
        binding.friendsListRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.friendsListRecycler.adapter = friendAdapter

        mypageViewModel.getFirstFriendsList(targetId)
        mypageViewModel.getFirstFriendsRequestList(targetId)

        mypageViewModel.FirstFriends.observe(viewLifecycleOwner) { friends ->
            Log.d("Friends", "${friends.pagination} :: ${friends.data}")
            val friendCount = friends.pagination.totalItems
            binding.friendslistFriendsCount.text = "친구 목록 $friendCount"
            if (isFriendList) {
                if (friends.data.size > 0) {
                    lastCreatedAt = getLastCreated(friends.data)
                }
                friendAdapter.addFirstList(friends.data)
            }
        }

        mypageViewModel.Friends.observe(viewLifecycleOwner) { friends ->
            if (friends.data.size > 0) {
                friendAdapter.addList(friends.data)
                lastCreatedAt = getLastCreated(friends.data)
            }
        }

        mypageViewModel.SearchedFriends.observe(viewLifecycleOwner) { friends ->
            if (friends.data.size > 0) {
                friendAdapter.addFirstList(friends.data)
            } else friendAdapter.addFirstList(arrayListOf())
        }

        mypageViewModel.SearchedRequests.observe(viewLifecycleOwner) { friends ->
            if (friends.data.size > 0) {
                friendAdapter.addFirstList(friends.data)
            } else friendAdapter.addFirstList(arrayListOf())
        }

        mypageViewModel.FirstRequests.observe(viewLifecycleOwner) { friends ->
            binding.friendslistFriendsRequestCount.text = "친구 신청 ${friends.pagination.totalItems}"
            val list = friends.data
            val requests = arrayListOf<FriendInfo>()
            for (request in list) {
                val temp = request
                temp.friendStatus = 3
                requests.add(temp)
            }
            if (!isFriendList) {
                if (friends.data.size > 0) {
                    lastCreatedAt = getLastCreated(requests)
                }
                friendAdapter.addFirstList(requests)
            }
        }

        mypageViewModel.Requests.observe(viewLifecycleOwner) { friends ->
            val list = friends.data
            val requests = arrayListOf<FriendInfo>()
            for (request in list) {
                val temp = request
                temp.friendStatus = 3
                requests.add(temp)
            }
            if (friends.data.size > 0) {
                friendAdapter.addList(requests)
                lastCreatedAt = getLastCreated(requests)
            }
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.friendsListRecycler.canScrollVertically(1)) {
                    if (isFriendList) {// 친구 목록 보기중인 상태라면
                        mypageViewModel.getFriendsList(targetId, lastCreatedAt)
                    } else {//요청 목록 보기중인 상태라면
                        mypageViewModel.getFriendsRequestList(targetId, lastCreatedAt)
                    }
                    Toast.makeText(context, "새 페이지 불러오는 중....", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.friendsListRecycler.addOnScrollListener(scrollListener)

        //친구요청 클릭
        binding.friendslistFriendsRequestCount.setOnClickListener {
            if (isFriendList) {
                isFriendList = false
                mypageViewModel.getFirstFriendsRequestList(targetId)
                //TODO:알맞게 뷰의 색등이 변하도록
                binding.friendslistFriendsRequestCount.setTextColor(Color.parseColor("#1DAFFF"))
                binding.requestListUnderline.setImageResource(R.drawable.todo_table_selected)
                binding.friendslistFriendsCount.setTextColor(Color.parseColor("#acacac"))
                binding.friendsCountUnderline.setImageResource(R.color.white)
            }
        }

        //친구목록 클릭
        binding.friendslistFriendsCount.setOnClickListener {
            if (!isFriendList) {
                isFriendList = true
                mypageViewModel.getFirstFriendsList(targetId)

                binding.friendslistFriendsRequestCount.setTextColor(Color.parseColor("#acacac"))
                binding.requestListUnderline.setImageResource(R.color.white)
                binding.friendslistFriendsCount.setTextColor(Color.parseColor("#1DAFFF"))
                binding.friendsCountUnderline.setImageResource(R.drawable.todo_table_selected)
            }
        }

        binding.friendsListBack.setOnClickListener {
            val fragmentManager = parentFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            }
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher { // 소프트웨어 키보드의 스페이스바 감지
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null)
                    return

                val str = s.toString()
                if (str == "") {
                    mypageViewModel.getFirstFriendsList(targetId)
                    mypageViewModel.getFirstFriendsRequestList(targetId)
                    return
                }

                if (str[str.length - 1] == '\n') {
                    val result = str.replace("\n", "")
                    binding.editTextSearch.setText(result)
                    if (isFriendList)
                        mypageViewModel.searchOnFriends(targetId, result)
                    else
                        mypageViewModel.searchOnRequests(targetId, result)
                }
            }

        })

        return binding.root
    }

    fun getLastCreated(items: ArrayList<FriendInfo>): String {
        val lastIndex = items.size - 1
        val lastCreated = items[lastIndex].createdAt
        return lastCreated ?: ""
    }
}

class PopupDeleteFriend(val targetItem: FriendInfo, val listener: OnFriendClicked) :
    BottomSheetDialogFragment() {
    lateinit var binding: PopupFriendDeleteConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopupFriendDeleteConfirmBinding.inflate(inflater)

        if (targetItem.profileImageUrl == null || targetItem.profileImageUrl == "")
            binding.ivProfileImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)
        else
            Glide
                .with(requireContext())
                .load(targetItem.profileImageUrl)
                .into(binding.ivProfileImage)

        binding.tvUserId.text = targetItem.name

        binding.btnDeleteUser.setOnClickListener {
            listener.onPopupClick(0)
            dismiss()
        }

        binding.btnDeleteCancel.setOnClickListener {
//            listener.onPopupClick(1)
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