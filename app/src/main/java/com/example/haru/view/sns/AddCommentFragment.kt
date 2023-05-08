package com.example.haru.view.sns

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.haru.data.model.Profile
import com.example.haru.databinding.FragmentAddCommentBinding
import com.example.haru.view.adapter.PicturesPagerAdapter

class AddCommentFragment(postitem : ArrayList<Profile>) : Fragment() {
    lateinit var binding : FragmentAddCommentBinding
    val postitem = postitem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "SnsMypageFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "SnsFragment - onCreateView() called")
        binding = FragmentAddCommentBinding.inflate(inflater, container, false)

        val viewpager = binding.commentImage
        val viewPagerAdapter = PicturesPagerAdapter(requireContext(), postitem)
        viewpager.adapter = viewPagerAdapter

        return binding.root
    }
}