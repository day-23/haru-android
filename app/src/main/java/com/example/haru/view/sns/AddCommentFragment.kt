package com.example.haru.view.sns

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.haru.R
import com.example.haru.data.model.Comments
import com.example.haru.data.model.Post
import com.example.haru.data.model.Profile
import com.example.haru.databinding.FragmentAddCommentBinding
import com.example.haru.view.adapter.PicturesPagerAdapter
import com.example.haru.viewmodel.SnsViewModel

class AddCommentFragment(postitem : Post) : Fragment() {
    lateinit var binding : FragmentAddCommentBinding
    val postitem = postitem
    private lateinit var snsViewModel: SnsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "SnsMypageFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "SnsFragment - onCreateView() called")
        binding = FragmentAddCommentBinding.inflate(inflater, container, false)

        val viewpager = binding.commentImage
        val viewPagerAdapter = PicturesPagerAdapter(requireContext(), postitem.images)
        snsViewModel.getComments(postitem.id)
        snsViewModel.Comments.observe(viewLifecycleOwner){ comments ->
            for(comment in comments)
                bindComment(comment)
        }
        viewpager.adapter = viewPagerAdapter
        return binding.root
    }

    fun bindComment(comment : Comments){
        val container = binding.commentFrame
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_comment_on_picture, null)

        // TextView를 찾아서 텍스트를 변경합니다.
        val textView = view.findViewById<TextView>(R.id.comment_on_picture_text)
        textView.text = comment.content

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        view.setOnClickListener{
            Toast.makeText(requireContext(),"${comment.content}", Toast.LENGTH_SHORT).show()
        }

        params.leftMargin = container.width * comment.x / 100
        params.topMargin =  container.height * comment.y / 100
        view.layoutParams = params

        container.addView(view)

    }
}