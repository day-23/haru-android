package com.example.haru.view.sns

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

    override fun onCommentClick(postitem: Post) {
        val newFrag = AddCommentFragment(postitem)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
        if(!isSnsMainInBackStack)
            transaction.addToBackStack("snsmypage")
        transaction.commit()
    }

    override fun onTotalCommentClick(postId: String) {
        val newFrag = CommentsFragment(postId)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "MypageFragment - onCreate() called")
        mypageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "MyPageFragment - onCreateView() called")

        binding = FragmentSnsMypageBinding.inflate(inflater, container, false)
        binding.myRecords.setTextColor(0xFF1DAFFF.toInt())

        //TODO("내 계정으로 접근할때 조건문 추가해야함")
        if(userId == "" || userId == "jts"){
            mypageViewModel.getUserInfo("jts")
            binding.editProfile.text = "프로필 편집"
            binding.profileShare.visibility = View.VISIBLE
        }else{
            binding.editProfile.text = "팔로우"
            binding.profileShare.visibility = View.GONE
            mypageViewModel.getUserInfo(userId)
        }

        mypageViewModel.UserInfo.observe(viewLifecycleOwner){ user ->
            binding.profileName.text = user.name
            binding.profileIntroduction.text = user.introduction
            binding.profilePostCount.text = user.postCount.toString()
            binding.profileFollowCount.text = user.followingCount.toString()
            binding.profileFollowerCount.text = user.followerCount.toString()

            if(user.isFollowing){
                binding.editProfile.text = "팔로우 취소"
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
            }
            else{
                binding.snsButtons.visibility = View.GONE
                click = false
            }
        }

        binding.editProfile.setOnClickListener {
            if(binding.editProfile.text == "팔로우"){
                requestFollow()
                binding.editProfile.text = "팔로우 취소"
            }else if(binding.editProfile.text == "팔로우 취소"){
                requestUnFollow()
                binding.editProfile.text = "팔로우"
            }else{
                moveEditprofile()
            }
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

    fun moveEditprofile(){
        val newFrag = EditProfileFragment.newInstance()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
        if(!isSnsMainInBackStack)
            transaction.addToBackStack("snsmypage")
        transaction.commit()
        true
    }

    fun requestFollow(){
        mypageViewModel.requestFollow(Followbody(userId))
    }

    fun requestUnFollow(){
        mypageViewModel.requestUnFollow(UnFollowbody(userId))
    }
}