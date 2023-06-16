package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentAddCommentBinding
import com.example.haru.databinding.PopupSnsCommentCancelBinding
import com.example.haru.view.adapter.AddCommentPagerAdapter
import com.example.haru.view.adapter.ImageClickListener
import com.example.haru.viewmodel.SnsViewModel

class AddCommentFragment(postId : String, postImages:ArrayList<Profile>, myInfo: User) : Fragment(), ImageClickListener{
    lateinit var binding : FragmentAddCommentBinding
    lateinit var commentContainer: FrameLayout
    lateinit var writeContainer: FrameLayout
    lateinit var filterFrame: LinearLayout
    lateinit var writeBox: View
    val postId = postId
    val postImages = postImages
    private lateinit var snsViewModel: SnsViewModel
    var imageIndex = 0
    var CommentIsVisible = true
    val myInfo = myInfo
    var onEdit = false
    var ProfileImage = ""

    //사진위 댓글 값
    var onWrite = false
    var showWriter = true
    var ImageWidth = 0
    var ImageHeight = 0
    var AddContent = ""
    var AddX = 35
    var AddY = 45
    var lastX = 0f
    var lastY = 0f
    override fun OnImageClick(position: Int) {
        if(!onWrite) {
            onWrite = true
            writeComment()
            writeStart()
        }
    }
    override fun OnPopupClick(position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.anchor_popup_comment)

        if(fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }

       if(position == 0){
           if(onWrite) {
               if (writeContainer.contains(writeBox)) {
                   writeContainer.removeView(writeBox)
                   onWrite = false
                   writeEnd()
               }
           }
       }else if(position == 1){
           snsViewModel.writeComment(CommentBody(AddContent,AddX,AddY), postId, postImages[imageIndex].id)
           val addedComment = Comments("",myInfo,AddContent,AddX,AddY, true,"","")
           postImages[imageIndex].comments.add(addedComment)
           bindComment(addedComment)
           onWrite = false
           writeContainer.removeAllViews()
           writeEnd()
           binding.writeCommentTitle.text = "코멘트 남기기"
       }
    }

    fun writeEnd(){
        val color = Color.argb(0, 0, 0, 0) // 204 represents 80% transparency black (255 * 0.8 = 204)
        //writeContainer.setBackgroundColor(color)
        filterFrame.setBackgroundColor(color)
        binding.writeCommentBack.visibility = View.VISIBLE
        binding.commentVisibility.visibility = View.VISIBLE
        binding.writeCommentCancel.isGone = true
        binding.writeCommentApply.isGone = true
        binding.commentOnWrite.isGone = true
        writeContainer.isClickable = false
        binding.writeCommentTitle.text = "코멘트 남기기"
    }

    fun writeStart(){
        val color = Color.argb(100, 25, 25, 25)
        //writeContainer.setBackgroundColor(color)
        filterFrame.setBackgroundColor(color)
        binding.writeCommentBack.isGone = true
        binding.commentVisibility.isGone = true
        binding.writeCommentApply.visibility = View.VISIBLE
        binding.writeCommentCancel.visibility = View.VISIBLE
        binding.commentOnWrite.visibility = View.VISIBLE
        writeContainer.isClickable = true
        binding.writeCommentTitle.text = "코멘트 작성"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "SnsMypageFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.addCommentLayout.id)

    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.addCommentLayout.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "SnsFragment - onCreateView() called")
        binding = FragmentAddCommentBinding.inflate(inflater, container, false)
        commentContainer = binding.commentFrame
        filterFrame = binding.filterFrame
        commentContainer.post{
            ImageWidth = commentContainer.width
            ImageHeight = commentContainer.height
        }

        writeContainer = binding.moveFrame
        val viewpager = binding.commentImage
        val viewPagerAdapter = AddCommentPagerAdapter(requireContext(), postImages, this)

        binding.lastPicture.setOnClickListener {
            val currentItem = viewpager.currentItem
            if (currentItem > 0) {
                viewpager.setCurrentItem(currentItem - 1, true)
            }
        }

        binding.nextPicture.setOnClickListener {
            val currentItem = viewpager.currentItem
            if (currentItem < viewpager.adapter?.itemCount ?: 0 - 1) {
                viewpager.setCurrentItem(currentItem + 1, true)
            }
        }

        binding.writeCommentBack.setOnClickListener {
            val backManager = parentFragmentManager
            backManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        binding.tempCheckWriter.setOnClickListener {

            if(showWriter) {
                for (i in 0..commentContainer.size - 1) {
                    val view = commentContainer.get(i)
                    view.visibility = View.VISIBLE
                }
            }
        }

        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                writeContainer.removeAllViews()
                onWrite = false
                imageIndex = position
                binding.lastPicture.visibility = View.VISIBLE
                binding.nextPicture.visibility = View.VISIBLE

                if(position == 0){
                    binding.lastPicture.isGone = true
                }
                if(position == postImages.size - 1){
                    binding.nextPicture.isGone = true
                }

                binding.addcommentIndex.text = "${position + 1} / ${postImages.size}"
                if (commentContainer.childCount != 0) {
                    commentContainer.removeAllViews()
                }
                for(comment in postImages[position].comments) {
                    Log.d("20191668","내용:  ${comment}")
                    commentContainer.post {
                        bindComment(comment)
                    }
                }
            }
        })

        binding.writeCommentCancel.setOnClickListener {
            if(onWrite && AddContent != "") {
                val fragment = PopupComment(this)
                val fragmentManager = childFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.add(R.id.anchor_popup_comment, fragment)
                transaction.commit()
            }else{
                this.OnPopupClick(0)
            }
        }

        binding.writeCommentApply.setOnClickListener {
            if(AddContent != ""){
                snsViewModel.writeComment(CommentBody(AddContent,AddX,AddY), postId,postImages[imageIndex].id)
                val addedComment = Comments("",myInfo,AddContent,AddX,AddY, true,"","")
                postImages[imageIndex].comments.add(addedComment)
                bindComment(addedComment)
                onWrite = false
                writeContainer.removeAllViews()
                writeEnd()
                binding.writeCommentTitle.text = "코멘트 남기기"
            }else{
                Toast.makeText(requireContext(), "댓글 내용을 입력해주세오", Toast.LENGTH_SHORT).show()
            }
        }

        binding.commentVisibility.setOnClickListener {
            if(CommentIsVisible){
                CommentIsVisible = false
                commentContainer.visibility = View.INVISIBLE
                binding.commentVisibility.setBackgroundResource(R.drawable.comment_invisible)
            }
            else{
                CommentIsVisible = true
                commentContainer.visibility = View.VISIBLE
                binding.commentVisibility.setBackgroundResource(R.drawable.comment_white)
            }
        }

        binding.addCommentEditComments.setOnClickListener {
            if(onEdit){//편집 종료
                editEnd()
                onEdit = false
            }else{//편집 시작
                editStart()
                onEdit = true
            }
        }

        viewpager.adapter = viewPagerAdapter
        return binding.root
    }

    fun editStart(){
        val color = Color.argb(100, 25, 25, 25)
        binding.editFilterFrame.setBackgroundColor(color)
        binding.addCommentEditComments.setImageResource(R.drawable.edit_comment_blue_finger)
        binding.addCommentEditText.setText("편집중")
        binding.addCommentEditText.setTextColor(Color.parseColor("#1DAFFF"))
        binding.addCommentResetIcon.visibility = View.VISIBLE
        binding.addCommentResetText.visibility = View.VISIBLE
        binding.lastPicture.visibility = View.GONE
        binding.nextPicture.visibility = View.GONE
        binding.addcommentIndex.visibility = View.GONE
        binding.commentVisibility.visibility = View.GONE
        binding.showTotalComment.visibility = View.GONE


    }

    fun editEnd(){
        val color = Color.argb(0, 0, 0, 0)
        binding.editFilterFrame.setBackgroundColor(color)
        binding.addCommentEditComments.setImageResource(R.drawable.edit_comment_white_finger)
        binding.addCommentEditText.setText("편집하기")
        binding.addCommentEditText.setTextColor(Color.parseColor("#FDFDFD"))
        binding.addCommentResetIcon.visibility = View.GONE
        binding.addCommentResetText.visibility = View.GONE
        binding.lastPicture.visibility = View.VISIBLE
        binding.nextPicture.visibility = View.VISIBLE
        binding.addcommentIndex.visibility = View.VISIBLE
        binding.commentVisibility.visibility = View.VISIBLE
        binding.showTotalComment.visibility = View.VISIBLE
    }

    @SuppressLint("MissingInflatedId")
    fun bindComment(comment : Comments){
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_comment_on_picture, null)
        val viewHeight = commentContainer.height
        // TextView를 찾아서 텍스트를 변경
        val textView = view.findViewById<TextView>(R.id.comment_on_picture_text)
        textView.text = comment.content

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val density = resources.displayMetrics.density


        val writerParam = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            (33 * density + 0.5f).toInt()
        )

        view.setOnClickListener{
            Toast.makeText(requireContext(),"${comment.content}", Toast.LENGTH_SHORT).show()
        }

        params.leftMargin = commentContainer.width * comment.x!! / 100
        params.topMargin =  commentContainer.height * comment.y!! / 100
        if(params.topMargin + textView.height > viewHeight){ // 사진에서 벗어나는 댓글에 대한 보정
            params.topMargin -= (params.leftMargin + textView.height) - viewHeight
        }
        view.layoutParams = params
        commentContainer.addView(view)

        //작성자 정보를 위한 뷰
        val Name = comment.user!!.name
        if(!comment.user.profileImage.isNullOrEmpty())
            ProfileImage = comment.user.profileImage
        val Id = comment.user!!.id

        val writerView = inflater.inflate(R.layout.item_comment_on_picture_writer, null)
        val writerName = writerView.findViewById<TextView>(R.id.comment_on_picture_name)
        val writerProfile = writerView.findViewById<ImageView>(R.id.comment_on_picture_profile)
        val toUserPageBtn = writerView.findViewById<ImageView>(R.id.comment_on_picture_move_page)
        writerView.visibility = View.GONE

        view.post {
            Log.d("20191668", "${view.width}, ${view.height}")
            writerParam.leftMargin = params.leftMargin + view.width / 2
            writerParam.topMargin = params.topMargin - (view.height + 30)
            writerView.layoutParams = writerParam
            commentContainer.addView(writerView)
        }

        writerView.post {
            writerName.text = Name// 유저명

            if(ProfileImage.isNullOrEmpty()){
                writerProfile.setBackgroundResource(R.drawable.haru_logo)
            }else {
                Glide.with(this)//프로필 사진
                    .load(ProfileImage)
                    .into(writerProfile)
            }

            toUserPageBtn.setOnClickListener {// 유저페이지 이동
                val newFrag = MyPageFragment(Id)
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragments_frame, newFrag)
                transaction.addToBackStack("snsaddcomment")
                transaction.commit()

            }
        }


    }

    fun writeComment(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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
            var onDelete = false

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
                        //사진 밖으로 댓글을 드래그 하지 않았는지 검사
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

                            if(y_start > 0 && y_end < writeContainer.height) {
                                if (y_end > commentContainer.height) {
                                    parentView.setBackgroundResource(R.color.light_gray)
                                } else {
                                    parentView.setBackgroundResource(R.drawable.comment_ballon)
                                }
                                parentView.y = event.rawY + offsetY
                            }

                            binding.writeCommentDelete.visibility = View.VISIBLE
                            val deletex = binding.writeCommentDelete.x
                            val deletexEnd = deletex + binding.writeCommentDelete.width
                            val deletey = binding.writeCommentDelete.y
                            val deleteyEnd = deletey + binding.writeCommentDelete.height
                            Log.d("20191668","${parentView.x}, ${parentView.y} , ${binding.writeCommentDelete.x}, ${binding.writeCommentDelete.y}")
                            //삭제 칸에 드래그 되었는지
                            if (x_start > deletexEnd || x_end < deletex) {
                                binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_default)
                                onDelete = false
                            }else if(y_start > deleteyEnd || y_end < deletey){
                                binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_default)
                                onDelete = false
                            }
                            else {
                                binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_ondrag)
                                onDelete = true
                            }
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
                            binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_default)
                            parentView.setBackgroundResource(R.drawable.comment_ballon)

                            if(onDelete){
                                writeContainer.removeView(writeBox)
                                onWrite = false
                                val color = Color.argb(0, 0, 0, 0) // 204 represents 80% transparency black (255 * 0.8 = 204)
                                //writeContainer.setBackgroundColor(color)
                                filterFrame.setBackgroundColor(color)
                                writeEnd()
                                onDelete = false
                            }
                        }
                        isDragging = false // Reset the dragging flag
                    }
                }
                return isDragging
            }
        }

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_write_comment_on_picture, null)
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

        editView.setOnFocusChangeListener { view, hasFocus ->
            val parentView = view.parent as View
            if (hasFocus) {
                lastX = parentView.x
                lastY = parentView.y
                
                parentView.x = (commentContainer.width * 0.35).toFloat()
                parentView.y = (commentContainer.height * 0.45).toFloat()
            } else {
                parentView.x = lastX
                parentView.y = lastY
            }
        }

        editView.isFocusable = true

        editView.setOnClickListener {

        }
        view.setOnTouchListener(dragTouchListener)
        editView.setOnTouchListener(dragTouchListener)
        editView.setTextIsSelectable(false)

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = params

        view.x = (commentContainer.width * 0.35).toFloat()
        view.y = (commentContainer.height * 0.45).toFloat()

        writeBox = view
        writeContainer.addView(view)

        editView.requestFocus() // EditText에 포커스를 설정합니다.
        imm.showSoftInput(editView, InputMethodManager.SHOW_IMPLICIT) // 키보드를 활성화합니다.
    }
}

class PopupComment(listener: ImageClickListener) : Fragment() {

    lateinit var popupbinding : PopupSnsCommentCancelBinding
    val listener = listener
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        popupbinding = PopupSnsCommentCancelBinding.inflate(inflater, container, false)

        popupbinding.snsAddCommentUnsave.setOnClickListener {
            listener.OnPopupClick(0)
        }

        popupbinding.snsAddCommentSave.setOnClickListener {
            listener.OnPopupClick(1)
        }

        popupbinding.snsAddCommentCancel.setOnClickListener {
            listener.OnPopupClick(2)
        }

        popupbinding.popupCommentContainer.setOnClickListener {

        }

        return popupbinding.root
    }

}