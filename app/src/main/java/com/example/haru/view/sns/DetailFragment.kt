package com.example.haru.view.sns

import BaseActivity
import android.graphics.Picture
import android.os.Bundle
import android.provider.MediaStore.Video.Media
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Post
import com.example.haru.databinding.FragmentDetailPostBinding
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.view.adapter.PicturesPagerAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel

class DetailFragment(media : com.example.haru.data.model.Media, post : Post) : Fragment() {
    lateinit var binding : FragmentDetailPostBinding
    val media = media
    val post = post
    lateinit var adapter : PicturesPagerAdapter
    lateinit var pager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "MypageFragment - onCreate() called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.detailMenu.id)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.detailMenu.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "MyPageFragment - onCreateView() called")
        binding = FragmentDetailPostBinding.inflate(inflater, container, false)
        pager = binding.detailPostPicture
        if(media.id != ""){
            bindMedia(media)
            adapter = PicturesPagerAdapter(requireContext(), media.images)
            pager.adapter = adapter
        }else{
            bindPost(post)
            adapter = PicturesPagerAdapter(requireContext(), post.images)
            pager.adapter = adapter
        }

        binding.detailBack.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val pictureIndex = adapter.itemCount
                val text = "${position + 1}/${pictureIndex}"
                binding.detailPictureIndex.text = text
            }
        })
        return binding.root
    }

    fun bindMedia(media: com.example.haru.data.model.Media){
        Glide.with(requireContext())
            .load(media.user.profileImage)
            .into(binding.detailPostProfile)
        binding.detailUserId.text = media.user.name
        binding.detailPostContents.text = media.content
        binding.detailLikedCount.text = media.likedCount.toString()
        binding.detailPostCommentCount.text = media.commentCount.toString()
        if(media.isLiked){
            binding.detailButtonLike.setBackgroundResource(R.drawable.sns_liked)
        }
        if(media.isCommented){
            binding.detailButtonComment.setBackgroundResource(R.drawable.comment_filled)
        }
    }

    fun bindPost(post: Post){
        Glide.with(requireContext())
             .load(post.user.profileImage)
             .into(binding.detailPostProfile)
        binding.detailUserId.text = post.user.name
        binding.detailPostContents.text = post.content
        binding.detailLikedCount.text = post.likedCount.toString()
        binding.detailPostCommentCount.text = post.commentCount.toString()
        if(media.isLiked){
            binding.detailButtonLike.setBackgroundResource(R.drawable.sns_liked)
        }
        if(media.isCommented){
            binding.detailButtonComment.setBackgroundResource(R.drawable.comment_filled)
        }
    }
}