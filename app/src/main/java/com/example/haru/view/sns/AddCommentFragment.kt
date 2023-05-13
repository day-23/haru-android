package com.example.haru.view.sns

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.contains
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.Comments
import com.example.haru.data.model.Post
import com.example.haru.databinding.FragmentAddCommentBinding
import com.example.haru.view.adapter.AddCommentPagerAdapter
import com.example.haru.view.adapter.ImageClickListener
import com.example.haru.viewmodel.SnsViewModel

class AddCommentFragment(postitem : Post) : Fragment(), ImageClickListener{
    lateinit var binding : FragmentAddCommentBinding
    lateinit var commentContainer: FrameLayout
    lateinit var writeContainer: FrameLayout
    lateinit var writeBox: View
    val postitem = postitem
    val postIndex = postitem.images
    private lateinit var snsViewModel: SnsViewModel
    var onWrite = false

    override fun OnImageClick(position: Int) {
        if(!onWrite) {
            onWrite = true
            writeComment(position)
        }
    }

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
        writeContainer = binding.moveFrame
        val viewpager = binding.commentImage
        val viewPagerAdapter = AddCommentPagerAdapter(requireContext(), postitem.images, this)

        for(comment in postIndex[0].comments) {
            bindComment(comment)
        }
        var imageIndex = 0

        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                writeContainer.removeAllViews()
                onWrite = false
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

        binding.writeCommentCancel.setOnClickListener {
            if(writeContainer.contains(writeBox)) {
                writeContainer.removeView(writeBox)
                onWrite = false
            }
        }

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

    fun writeComment(position : Int){

        val dragTouchListener = object : View.OnTouchListener {
            private var offsetX = 0f
            private var offsetY = 0f
            private var isDragging = false
            private var initialX = 0f
            private var initialY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                var parentView = view
                try {
                    parentView = view.parent as View
                }catch (e:java.lang.Exception){

                }
                binding.writeCommentDelete.visibility = View.GONE
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        offsetX = parentView.x - event.rawX
                        offsetY = parentView.y - event.rawY
                        initialX = event.rawX
                        initialY = event.rawY
                        isDragging = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        binding.writeCommentDelete.visibility = View.VISIBLE
                        if (!isDragging && (Math.abs(event.rawX - initialX) > ViewConfiguration.get(parentView.context).scaledTouchSlop ||
                                    Math.abs(event.rawY - initialY) > ViewConfiguration.get(parentView.context).scaledTouchSlop)) {
                            isDragging = true
                        }
                        if (isDragging) {
                            val x_start = event.rawX + offsetX
                            val x_end = x_start + parentView.width
                            val y_start = event.rawY + offsetY
                            val y_end = y_start + parentView.height
                            if(x_start > 0 && x_end < writeContainer.width)
                                parentView.x = x_start
                            if(y_start > 0 && y_end < writeContainer.height)
                                parentView.y = event.rawY + offsetY
                            Log.d("Comment" , "${parentView.width}, ${parentView.height}")
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isDragging) {
                            binding.writeCommentDelete.visibility = View.GONE
                        }
                        isDragging = false // Reset the dragging flag
                    }
                }
                return isDragging
            }
        }

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_write_comment_on_picture, null)
        view.isFocusable = false
        // TextView를 찾아서 텍스트를 변경
        val editView = view.findViewById<EditText>(R.id.write_on_picture_)

        view.setOnTouchListener(dragTouchListener)
        editView.setOnTouchListener(dragTouchListener)

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        //프레임레이아웃 할당 대기 -> 0 리턴 방지
        commentContainer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                commentContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                params.leftMargin = commentContainer.width * 35 / 100
                params.topMargin =  commentContainer.height * 45 / 100
                view.layoutParams = params
            }
        })
        writeBox = view
        writeContainer.addView(view)

        editView.requestFocus() // EditText에 포커스를 설정합니다.
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editView, InputMethodManager.SHOW_IMPLICIT) // 키보드를 활성화합니다.
    }
}