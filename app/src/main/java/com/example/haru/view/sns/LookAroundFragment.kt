package com.example.haru.view.sns

import BaseActivity
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Post
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentLookAroundBinding
import com.example.haru.utils.User
import com.example.haru.view.adapter.LookAroundAdapter
import com.example.haru.view.adapter.MediaAdapter
import com.example.haru.view.adapter.MediaTagAdapter
import com.example.haru.view.adapter.TagAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel

class LookAroundFragment : Fragment() , OnMediaTagClicked{
    private lateinit var snsViewModel: SnsViewModel
    private lateinit var profileViewModel: MyPageViewModel
    private lateinit var lookAroundPosts: RecyclerView
    private lateinit var lookAroundTags: RecyclerView
    private lateinit var tagAdapter : MediaTagAdapter
    private lateinit var postAdapter: LookAroundAdapter
    private lateinit var binding : FragmentLookAroundBinding
    var selectedTag : MediaTagAdapter.MediaTagViewHolder? = null
    var lastDate = ""
    var isTagSelected = false
    var tagId = ""

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(1)) {
                if(isTagSelected){
                    getHotPosts()
                }else{
                    getPosts()
                }
                Toast.makeText(requireContext(),"loading", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTagClicked(tag: Tag, holder: MediaTagAdapter.MediaTagViewHolder) {
        isTagSelected = true
        holder.tag.setBackgroundResource(R.drawable.tag_btn_clicked)
        holder.tag.setTextColor(Color.parseColor("#fdfdfd"))
        selectedTag?.tag?.setBackgroundResource(R.drawable.tag_btn_custom)
        selectedTag?.tag?.setTextColor(Color.parseColor("#191919"))

        if(holder != selectedTag) {
            getFirstHotPosts(tag.id)
        }
        if(holder == selectedTag){
            getFirstPosts()
            selectedTag = null
            isTagSelected = false
        }else {
            selectedTag = holder
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(SnsFragment.TAG, "LookAroundFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "LookAroundFragment - onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.lookAroundMenu.id)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.lookAroundMenu.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLookAroundBinding.inflate(inflater, container, false)
        lookAroundPosts = binding.lookAroundPosts
        lookAroundPosts.addOnScrollListener(scrollListener)
        lookAroundTags = binding.lookAroundTags
        postAdapter = LookAroundAdapter(requireContext(), arrayListOf())
        lookAroundPosts.adapter = postAdapter
        setGridLayout(lookAroundPosts)
        lookAroundTags.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        getTags()
        getFirstPosts()

        snsViewModel.Posts.observe(viewLifecycleOwner){posts ->
            if(posts.isNotEmpty()) {
                postAdapter.firstPage(posts)
                getLastDate(posts)
                Log.d("20191668", "first : $lastDate")
            }else{
                postAdapter.firstPage(arrayListOf())
            }
        }

        snsViewModel.newPost.observe(viewLifecycleOwner){posts ->
            if(posts.isNotEmpty()) {
                getLastDate(posts)
                postAdapter.newPage(posts)
            }
        }

        snsViewModel.HotPosts.observe(viewLifecycleOwner){posts ->
            if(posts.isNotEmpty()) {
                postAdapter.newPage(posts)
                getLastDate(posts)
            }
        }

        snsViewModel.HotFirstPosts.observe(viewLifecycleOwner){posts ->
            if(posts.isNotEmpty()) {
                postAdapter.firstPage(posts)
                getLastDate(posts)
            }else{
                postAdapter.firstPage(arrayListOf())
            }
        }

        return binding.root
    }

    fun getLastDate(items : ArrayList<Post>){
        val index = items.size - 1
        lastDate = items[index].createdAt
        Log.d("20191668", "change : $lastDate")
    }

    fun getTags(){
        tagAdapter = MediaTagAdapter(requireContext(), arrayListOf(), this)
        snsViewModel.getHotTags()
        snsViewModel.HotTags.observe(viewLifecycleOwner){tags ->
            tagAdapter.newPage(tags)
            lookAroundTags.adapter = tagAdapter
        }
    }

    fun getFirstPosts(){
        snsViewModel.getFirstPosts()
    }

    fun getPosts(){
        snsViewModel.getPosts(lastDate)
    }

    fun getFirstHotPosts(tagId : String){
        snsViewModel.getFirstHotPosts(tagId)
        this.tagId = tagId
    }

    fun getHotPosts(){
        snsViewModel.getHotPosts(tagId, lastDate)
    }
    fun setGridLayout(view : RecyclerView){
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1 // 각 아이템의 너비를 1로 설정
            }
        }
        view.layoutManager = gridLayoutManager

        view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view) // item position
                val column = position % 3 // item column
                if(column == 3) outRect.set(0, 0, 0, 3)
                else outRect.set(0,0,3,3)
            }
        })
    }
}