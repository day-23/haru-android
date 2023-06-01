package com.example.haru.view.sns

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentLookAroundBinding
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
    private lateinit var postAdapter: MediaTagAdapter
    private lateinit var binding : FragmentLookAroundBinding

    override fun onTagClicked(tag: Tag, holder: MediaTagAdapter.MediaTagViewHolder) {
        TODO("Not yet implemented")
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
        lookAroundTags = binding.lookAroundTags
        getTags()
        lookAroundPosts.layoutManager = LinearLayoutManager(context)
        lookAroundTags.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        return binding.root
    }

    fun getTags(){
        tagAdapter = MediaTagAdapter(requireContext(), arrayListOf(), this)
        snsViewModel.getHotTags()
        snsViewModel.HotTags.observe(viewLifecycleOwner){tags ->
            tagAdapter.newPage(tags)
            lookAroundTags.adapter = tagAdapter
        }
    }

    fun getPosts(){
        
    }
}