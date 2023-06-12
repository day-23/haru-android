package com.example.haru.view.sns

import BaseActivity
import android.os.Bundle
import android.provider.MediaStore.Video.Media
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.haru.data.model.Post
import com.example.haru.databinding.FragmentDetailPostBinding
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel

class DetailFragment(media : com.example.haru.data.model.Media, post : Post) : Fragment() {
    lateinit var binding : FragmentDetailPostBinding
    val media = media
    val post = post

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

        return binding.root
    }
}