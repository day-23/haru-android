package com.example.haru.view.sns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Post
import com.example.haru.data.model.Profile
import com.example.haru.data.model.SnsPost
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.view.adapter.MyFeedAdapter
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel

class MyPageFragment : Fragment(), OnPostClickListener{
    private lateinit var binding: FragmentSnsMypageBinding
    private lateinit var FeedRecyclerView: RecyclerView
    private lateinit var feedAdapter: SnsPostAdapter
    private lateinit var mediaAdapter: MyFeedAdapter
    private lateinit var mypageViewModel: MyPageViewModel
    private var click = false

    override fun onCommentClick(postitem: ArrayList<Profile>) {
        val newFrag = AddCommentFragment(postitem)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
        if(!isSnsMainInBackStack)
            transaction.addToBackStack("snsmypage")
        transaction.commit()
    }

    override fun onTotalCommentClick(postId: String) {
        val newFrag = CommentsFragment.newInstance()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
        if(!isSnsMainInBackStack)
            transaction.addToBackStack("snsmypage")
        transaction.commit()
    }

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : MyPageFragment {
            return MyPageFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SnsMypageFragment - onCreate() called")
        mypageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SnsFragment - onCreateView() called")

        binding = FragmentSnsMypageBinding.inflate(inflater, container, false)
        FeedRecyclerView = binding.feedRecycler
        feedAdapter = SnsPostAdapter(requireContext(), arrayListOf(), this)
        FeedRecyclerView.adapter = feedAdapter
        val layoutManager = LinearLayoutManager(context)
        FeedRecyclerView.layoutManager = layoutManager

        mypageViewModel.Profile.observe(viewLifecycleOwner){profile ->
            if(profile.url != "") {
                Log.d("TAG", "${profile.url}")
                Glide.with(this)
                    .load(profile.url)
                    .into(binding.profileImage)
            }
        }

        mypageViewModel.Page.observe(viewLifecycleOwner){page ->
            val page = page.toString()
            mypageViewModel.getFeed(page)
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
            val newFrag = EditProfileFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmypage")
            if(!isSnsMainInBackStack)
                transaction.addToBackStack("snsmypage")
            transaction.commit()
            true
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
}