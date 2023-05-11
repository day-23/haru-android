package com.example.haru.view.sns

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.Comments
import com.example.haru.data.model.Post
import com.example.haru.data.model.Profile
import com.example.haru.databinding.FragmentAddCommentBinding
import com.example.haru.view.adapter.PicturesPagerAdapter
import com.example.haru.viewmodel.SnsViewModel

class AddCommentFragment(postitem : Post) : Fragment() {
    lateinit var binding : FragmentAddCommentBinding
    lateinit var commentContainer: FrameLayout
    val postitem = postitem
    val postIndex = postitem.images
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
        commentContainer = binding.commentFrame
        val viewpager = binding.commentImage
        val viewPagerAdapter = PicturesPagerAdapter(requireContext(), postitem.images)

        for(comment in postIndex[0].comments) {
            Log.d("Comment", "comment added")
            bindComment(comment)
        }
        var imageIndex = 0

        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                imageIndex = position
                binding.addcommentIndex.text = "${imageIndex + 1} / ${postitem.images.size}"
                if (commentContainer.childCount != 0) {
                    commentContainer.removeAllViews()
                }
                for(comment in postIndex[position].comments) {
                    bindComment(comment)
                }
            }
        })

        viewpager.adapter = viewPagerAdapter
        return binding.root
    }

    fun bindComment(comment : Comments){
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_comment_on_picture, null)

        // TextView를 찾아서 텍스트를 변경
        val textView = view.findViewById<TextView>(R.id.comment_on_picture_text)
        textView.text = comment.content

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        view.setOnClickListener{
            Toast.makeText(requireContext(),"${comment.content}", Toast.LENGTH_SHORT).show()
        }

        //프레임레이아웃 할당 대기 -> 0 리턴 방지
        commentContainer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                commentContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                params.leftMargin = commentContainer.width * comment.x / 100
                params.topMargin =  commentContainer.height * comment.y / 100
                view.layoutParams = params
            }
        })
        commentContainer.addView(view)
    }
}