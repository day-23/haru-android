package com.example.haru.view.sns

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.view.adapter.MyFeedAdapter
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel

class MyPageFragment(userId: String) : Fragment(), OnPostClickListener{
    private lateinit var binding: FragmentSnsMypageBinding
    private lateinit var FeedRecyclerView: RecyclerView
    private lateinit var feedAdapter: SnsPostAdapter
    private lateinit var mediaAdapter: MyFeedAdapter
    private lateinit var mypageViewModel: MyPageViewModel
    private var click = false
    val userId = userId
    var isMyPage = false
    var friendStatus = 0

    override fun onCommentClick(postitem: Post) {
        mypageViewModel.getUserInfo(com.example.haru.utils.User.id)

        mypageViewModel.UserInfo.observe(viewLifecycleOwner){ user ->
            val newFrag = AddCommentFragment(postitem,user)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
            if (!isSnsMainInBackStack)
                transaction.addToBackStack("snsmypage")
            transaction.commit()
        }
    }

    override fun onTotalCommentClick(post: Post) {
        val newFrag = CommentsFragment(post)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
        if(!isSnsMainInBackStack)
            transaction.addToBackStack("snsmypage")
        transaction.commit()
    }

    override fun onProfileClick(userId: String) {
        //Don't need to implement
    }

    override fun onSetupClick(userId: String, postId: String, item: Post) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "MypageFragment - onCreate() called")
        mypageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "MyPageFragment - onCreateView() called")

        binding = FragmentSnsMypageBinding.inflate(inflater, container, false)
        mypageViewModel.init_page()

        if(userId == com.example.haru.utils.User.id){
            isMyPage = true
            binding.editProfile.text = "프로필 편집"
            binding.profileShare.visibility = View.VISIBLE
            binding.myPageMyRecord.visibility = View.GONE
            mypageViewModel.getUserInfo(com.example.haru.utils.User.id)
        }else{
            isMyPage = false
            binding.editProfile.text = "친구 신청"
            binding.profileShare.visibility = View.GONE
            mypageViewModel.getUserInfo(userId)
        }

        mypageViewModel.UserInfo.observe(viewLifecycleOwner){ user ->
            binding.profileName.text = user.name
            binding.profileIntroduction.text = user.introduction
            binding.profilePostCount.text = user.postCount.toString()
            binding.profileFriendsCount.text = user.friendCount.toString()
            friendStatus = user.friendStatus
            if(!isMyPage) { //타인의 페이지라면
                if (user.friendStatus == 0) {
                    binding.editProfile.text = "친구 신청"
                } else if (user.friendStatus == 1) {
                    binding.editProfile.text = "신청 취소"
                } else {
                    binding.editProfile.text = "내 친구"
                }
            }

            if(user.profileImage != "") {
                Log.d("TAG", "${user.profileImage}")
                Glide.with(this)
                    .load(user.profileImage)
                    .into(binding.profileImage)
            }
        }

        FeedRecyclerView = binding.feedRecycler
        feedAdapter = SnsPostAdapter(requireContext(), arrayListOf(), this)
        FeedRecyclerView.adapter = feedAdapter
        val layoutManager = LinearLayoutManager(context)
        FeedRecyclerView.layoutManager = layoutManager


        mypageViewModel.Page.observe(viewLifecycleOwner){page ->
            val page = page.toString()
            mypageViewModel.getFeed(page, userId)
        }

        mypageViewModel.NewFeed.observe(viewLifecycleOwner){feeds ->
            feedAdapter.newPage(feeds)
            if(feeds.size == 0) Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!FeedRecyclerView.canScrollVertically(1)) {
                    mypageViewModel.addPage()
                    Toast.makeText(context, "새 페이지 불러오는 중....", Toast.LENGTH_SHORT).show()
                }
            }
        }
        FeedRecyclerView.addOnScrollListener(scrollListener)

        binding.menuButton.setOnClickListener {
            if(click == false){
                binding.snsButtons.visibility = View.VISIBLE
                click = true
                binding.menuButton.animate().rotation(0f)
            }
            else{
                binding.snsButtons.visibility = View.GONE
                click = false
                binding.menuButton.animate().rotation(-90f)
            }
        }

        binding.editProfile.setOnClickListener {
            if(isMyPage) moveEditprofile(userId) // 내 페이지면 프로필 수정 이동
            else{ //타인 페이지라면 친구 작업
                if (friendStatus == 0) {
                    requestFriend() //TODO:친구 신청
                } else if (friendStatus == 1) {
                    requestUnFriend() //TODO: 친구신청 취소
                } else{
                    requestDelFriend() //TODO:친구끊기
                }
            }
            binding.editProfile.isClickable = false //전송 중 클릭못하도록

            mypageViewModel.FriendRequest.observe(viewLifecycleOwner){result ->
                binding.editProfile.isClickable = true //결과 받고 클릭 가능하도록
                if(result){
                    if(friendStatus == 0) { //신청 성공
                        friendStatus = 1
                        binding.editProfile.text = "신청 대기"
                    }else if(friendStatus == 1){ //신청 취소
                        friendStatus = 0
                        binding.editProfile.text = "친구 신청"
                    }else{
                        friendStatus = 0
                        binding.editProfile.text = "친구 신청"
                    }
                }else{
                    Toast.makeText(requireContext(), "요청에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.profileFriendsLayout.setOnClickListener {
            val newFrag = FriendsListFragment(userId)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
            if(!isSnsMainInBackStack)
                transaction.addToBackStack("snsmypage")
            transaction.commit()
        }

        binding.friendFeed.setOnClickListener {
            val fragmentManager = parentFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack("snsmain", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        return binding.root
    }

    fun isFragmentInBackStack(fragmentManager: FragmentManager, tag: String): Boolean {
        for (i in 0 until fragmentManager.backStackEntryCount) {
            val backStackEntry = fragmentManager.getBackStackEntryAt(i)
            if (backStackEntry.name == tag) {
                return true
            }
        }
        return false
    }

    fun moveEditprofile(userId: String){
        val newFrag = EditProfileFragment(userId)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
        if(!isSnsMainInBackStack)
            transaction.addToBackStack("snsmypage")
        transaction.commit()
        true
    }

    fun requestFriend(){
        mypageViewModel.requestFriend(Followbody(userId))
    }

    fun requestUnFriend(){
        mypageViewModel.requestUnFriend(UnFollowbody(userId))
    }

    fun requestDelFriend(){
        mypageViewModel.requestDelFriend(UnFollowbody(userId))
    }
}