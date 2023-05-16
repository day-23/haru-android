package com.example.haru.view.sns

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.contains
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.CommentBody
import com.example.haru.data.model.Comments
import com.example.haru.data.model.Post
import com.example.haru.data.model.User
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

    //사진위 댓글 값
    var onWrite = false
    var ImageWidth = 0
    var ImageHeight = 0
    var AddContent = ""
    var AddX = 35
    var AddY = 45

    override fun OnImageClick(position: Int) {
        if(!onWrite) {
            onWrite = true
            writeComment(position)
            val color = Color.argb(170, 0, 0, 0) // 204 represents 80% transparency black (255 * 0.8 = 204)
            writeContainer.setBackgroundColor(color)
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
        commentContainer.post{
            ImageWidth = commentContainer.width
            ImageHeight = commentContainer.height
        }
        writeContainer = binding.moveFrame
        val viewpager = binding.commentImage
        val viewPagerAdapter = AddCommentPagerAdapter(requireContext(), postitem.images, this)

        var imageIndex = 0

        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                writeContainer.removeAllViews()
                onWrite = false
                imageIndex = position
                binding.addcommentIndex.text = "${position + 1} / ${postitem.images.size}"
                if (commentContainer.childCount != 0) {
                    commentContainer.removeAllViews()
                }
                for(comment in postIndex[position].comments) {
                    Log.d("20191668","내용:  ${comment.content}")
                    commentContainer.post {
                        bindComment(comment)
                    }
                }
            }
        })

        binding.writeCommentCancel.setOnClickListener {
            if(onWrite) {
                if (writeContainer.contains(writeBox)) {
                    writeContainer.removeView(writeBox)
                    onWrite = false
                    val color = Color.argb(0, 0, 0, 0) // 204 represents 80% transparency black (255 * 0.8 = 204)
                    writeContainer.setBackgroundColor(color)
                }
            }
        }

        binding.writeCommentApply.setOnClickListener {
            if(AddContent != ""){
                snsViewModel.writeComment(CommentBody(AddContent,AddX,AddY), postitem.id,postIndex[imageIndex].id)
                val addedComment = Comments("",User("","","","",false,0,0,0),AddContent,AddX,AddY, "","")
                postIndex[imageIndex].comments.add(addedComment)
                bindComment(addedComment)
                onWrite = false
                writeContainer.removeAllViews()
                val color = Color.argb(0, 0, 0, 0) // 204 represents 80% transparency black (255 * 0.8 = 204)
                writeContainer.setBackgroundColor(color)
            }else{
                Toast.makeText(requireContext(), "댓글 내용을 입력해주세오", Toast.LENGTH_SHORT).show()
            }
        }

        viewpager.adapter = viewPagerAdapter
        return binding.root
    }

    fun bindComment(comment : Comments){
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_comment_on_picture, null)
        val viewHeight = commentContainer.height
        Log.d("20191668" , "$viewHeight")
        // TextView를 찾아서 텍스트를 변경
        val textView = view.findViewById<TextView>(R.id.comment_on_picture_text)
        textView.text = comment.content
        Log.d("20191668", "${comment.content}")

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        view.setOnClickListener{
            Toast.makeText(requireContext(),"${comment.content}", Toast.LENGTH_SHORT).show()
        }
        params.leftMargin = commentContainer.width * comment.x / 100
        params.topMargin =  commentContainer.height * comment.y / 100
        Log.d("20191668", "params : ${params.topMargin} ${textView.height} $viewHeight")
        if(params.topMargin + textView.height > viewHeight){
            params.topMargin -= (params.leftMargin + textView.height) - viewHeight
        }
        view.layoutParams = params
        commentContainer.addView(view)
    }

    fun writeComment(position : Int){

        val dragTouchListener = object : View.OnTouchListener {
            private var offsetX = 0f
            private var offsetY = 0f
            private var isDragging = false
            private var initialX = 0f
            private var initialY = 0f
            private var x_start = 0f
            private var x_end = 0f
            private var y_start = 0f
            private var y_end = 0f

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
                        initialX = parentView.x
                        initialY = parentView.y
                        isDragging = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        binding.writeCommentDelete.visibility = View.VISIBLE
                        if (!isDragging && (Math.abs(event.rawX - initialX) > ViewConfiguration.get(parentView.context).scaledTouchSlop ||
                                    Math.abs(event.rawY - initialY) > ViewConfiguration.get(parentView.context).scaledTouchSlop)) {
                            isDragging = true
                        }
                        if (isDragging) {
                            x_start = event.rawX + offsetX
                            x_end = x_start + parentView.width
                            y_start = event.rawY + offsetY
                            y_end = y_start + parentView.height
                            if(x_start > 0 && x_end < writeContainer.width)
                                parentView.x = x_start
                            if(y_start > 0 && y_end < writeContainer.height)
                                parentView.y = event.rawY + offsetY
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isDragging) {
                            if(y_end > ImageHeight){
                                parentView.x = initialX
                                parentView.y = initialY
                            }
                            else{
                                AddX = (parentView.x / ImageWidth * 100).toInt()
                                AddY = (parentView.y / ImageHeight * 100).toInt()
                                Log.d("20191668", "Percentage: $AddX $AddY")
                            }
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
        editView.addTextChangedListener {
            AddContent = editView.text.toString()
            val layoutParams = editView.layoutParams

            if(AddContent == ""){
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }else {
                val paint = Paint()
                paint.textSize = editView.textSize
                paint.typeface = editView.typeface
                val textWidth = paint.measureText(AddContent).toInt()
                val textPaint = TextPaint(editView.paint)
                val staticLayout = StaticLayout.Builder
                    .obtain(AddContent, 0, AddContent.length, textPaint, textWidth)
                    .setLineSpacing(editView.lineSpacingExtra, editView.lineSpacingMultiplier)
                    .build()

                layoutParams.width = textWidth + 20
                layoutParams.height = staticLayout.height
            }
            editView.layoutParams = layoutParams
        }

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