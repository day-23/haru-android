package com.example.haru.view.sns

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
import com.example.haru.databinding.FragmentFriendsListBinding
import com.example.haru.view.adapter.FriendsListAdapter
import com.example.haru.viewmodel.MyPageViewModel

class FriendsListFragment(val targetId: String) : Fragment(){
    lateinit var binding : FragmentFriendsListBinding
    private lateinit var mypageViewModel: MyPageViewModel
    private lateinit var friendAdapter: FriendsListAdapter
    var isFriendList = true
    var lastCreatedAt = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "FriendsListFragment - onCreate() called")
        mypageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        mypageViewModel.getFirstFriendsList(targetId)
        friendAdapter = FriendsListAdapter(requireContext())
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
            friendAdapter.addList(friends.data)
            lastCreatedAt = getLastCreated(friends.data)
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.friendsListRecycler.canScrollVertically(1)) {
                    if(isFriendList) {// 친구 목록 보기중인 상태라면
                        mypageViewModel.getFriendsList(targetId, lastCreatedAt)
                        Toast.makeText(context, "새 페이지 불러오는 중....", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.friendsListRecycler.addOnScrollListener(scrollListener)

        return binding.root
    }

    fun getLastCreated(items: ArrayList<FriendInfo>): String{
        val lastIndex = items.size-1
        val lastCreated = items[lastIndex].createdAt
        return lastCreated ?: ""
    }
}