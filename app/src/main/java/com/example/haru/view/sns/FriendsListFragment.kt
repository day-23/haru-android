package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat.canScrollVertically
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.FriendInfo
import com.example.haru.data.model.UnFollowbody
import com.example.haru.databinding.FragmentFriendsListBinding
import com.example.haru.utils.User
import com.example.haru.view.adapter.FriendsListAdapter
import com.example.haru.viewmodel.MyPageViewModel

interface OnFriendClicked{
    fun onDeleteClick(item : FriendInfo)
}
class FriendsListFragment(val targetId: String) : Fragment(), OnFriendClicked{
    lateinit var binding : FragmentFriendsListBinding
    private lateinit var mypageViewModel: MyPageViewModel
    private lateinit var friendAdapter: FriendsListAdapter
    var isFriendList = true // 친구목록 보기중인지 false == 친구신청 보여주기
    var lastCreatedAt = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(item: FriendInfo) {
        mypageViewModel.requestDelFriend(UnFollowbody(item.id!!))
        mypageViewModel.FriendRequest.observe(viewLifecycleOwner){result ->

            friendAdapter.deleteFriend(item)
            Toast.makeText(requireContext(), "삭제 성공", Toast.LENGTH_SHORT).show()

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
        (activity as BaseActivity).adjustTopMargin(binding.friendListContainer.id)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.friendListContainer.id)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        mypageViewModel.getFirstFriendsList(targetId)
        friendAdapter = FriendsListAdapter(requireContext(), arrayListOf(),this)
        binding.friendsListRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.friendsListRecycler.adapter = friendAdapter

        mypageViewModel.getFirstFriendsList(targetId)

        mypageViewModel.FirstFriends.observe(viewLifecycleOwner){friends ->
            if(isFriendList){
                friendAdapter.addFirstList(friends.data)
            }
            lastCreatedAt = getLastCreated(friends.data)

            val friendCount = friends.pagination.totalItems
            binding.friendslistFriendsCount.text = "친구 목록 $friendCount"
        }

        mypageViewModel.Friends.observe(viewLifecycleOwner){friends ->
            if(friends.data.size > 0) {
                friendAdapter.addList(friends.data)
                lastCreatedAt = getLastCreated(friends.data)
            }
        }

        mypageViewModel.FirstRequests.observe(viewLifecycleOwner){friends ->
            if(friends.data.size > 0) {
                friendAdapter.addFirstList(friends.data)
                lastCreatedAt = getLastCreated(friends.data)
            }
        }

        mypageViewModel.Requests.observe(viewLifecycleOwner){friends ->
            if(friends.data.size > 0) {
                friendAdapter.addList(friends.data)
                lastCreatedAt = getLastCreated(friends.data)
            }
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.friendsListRecycler.canScrollVertically(1)) {
                    if(isFriendList) {// 친구 목록 보기중인 상태라면
                        mypageViewModel.getFriendsList(targetId, lastCreatedAt)
                    }else{//요청 목록 보기중인 상태라면
                        mypageViewModel.getFriendsRequestList(targetId, lastCreatedAt)
                    }
                    Toast.makeText(context, "새 페이지 불러오는 중....", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.friendsListRecycler.addOnScrollListener(scrollListener)

        //친구요청 클릭
        binding.friendslistFriendsRequestCount.setOnClickListener{
            if(isFriendList){
                isFriendList = false
                mypageViewModel.getFirstFriendsRequestList(targetId)
                //TODO:알맞게 뷰의 색등이 변하도록
            }
        }

        //친구목록
        binding.friendslistFriendsCount.setOnClickListener {
            if(!isFriendList){
                isFriendList = true
                mypageViewModel.getFirstFriendsList(targetId)
            }
        }

        return binding.root
    }

    fun getLastCreated(items: ArrayList<FriendInfo>): String{
        val lastIndex = items.size-1
        val lastCreated = items[lastIndex].createdAt
        return lastCreated ?: ""
    }
}