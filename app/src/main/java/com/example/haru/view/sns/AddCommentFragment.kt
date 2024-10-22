package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.media.Image
import android.os.Bundle
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
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
import com.example.haru.databinding.PopupAddCommentHideBinding
import com.example.haru.databinding.PopupSnsCommentCancelBinding
import com.example.haru.databinding.PopupSnsCommentDeleteAgainBinding
import com.example.haru.utils.GetPastDate
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.AddCommentPagerAdapter
import com.example.haru.view.adapter.ImageClickListener
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.sdk.talk.model.Friend

class AddCommentFragment(
    var isTemplate: String? = "",
    val content: String,
    postId: String,
    postImages: ArrayList<Profile>,
    val likeCnt: Int,
    val commentCnt: Int,
    writerInfo: User
) : Fragment(), ImageClickListener {
    lateinit var binding: FragmentAddCommentBinding
    lateinit var commentContainer: FrameLayout
    lateinit var infoContainer: FrameLayout
    lateinit var writeContainer: FrameLayout
    lateinit var filterFrame: LinearLayout
    lateinit var writeBox: View
    val postId = postId
    val postImages = postImages
    private lateinit var snsViewModel: SnsViewModel
    private lateinit var profileViewModel: MyPageViewModel
    private lateinit var backPressed: OnBackPressedCallback
    var imageIndex = 0
    var CommentIsVisible = true
    val writerInfo = writerInfo
    var onEdit = false
    var myInfo = User()
    var isMyPost = false
    var isCommented = false

    //수정 사진 리스트
    val idList = arrayListOf<String>()
    val xList = arrayListOf<Int>()
    val yList = arrayListOf<Int>()

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
        if (!onEdit && writerInfo.id == com.example.haru.utils.User.id) {
            if (showWriter) {
                infoContainer.visibility = View.VISIBLE
                showWriter = false //다시누르면 사라지도록
            } else {
                infoContainer.visibility = View.GONE
                showWriter = true //다시누르면 보이도록
            }
        }
    }

    override fun OnHideClick(comment: Comments, content: View, writer: View, position: Int) {
        if (position == 0) {//숨기기
            snsViewModel.patchComment(
                comment.user!!.id,
                comment.id,
                PatchCommentBody(comment.x!!, comment.y!!, false)
            )
            commentContainer.removeView(content)
            infoContainer.removeView(writer)
            postImages[imageIndex].comments.remove(comment)

            isCommented = false
            setUi()
        }
    }

    override fun OnPopupClick(position: Int) {

        if (position == 0) {//저장안함
            if (onWrite) {
                if (writeContainer.contains(writeBox)) {
                    writeContainer.removeView(writeBox)
                    onWrite = false
                    writeEnd()
                }
            } else if (onEdit) {
                editCancel()
                editEnd()
            }
        } else if (position == 1) {//저장하기
            if (onWrite) {
                if (AddContent != "") {
                    snsViewModel.writeComment(
                        CommentBody(AddContent, AddX, AddY),
                        postId,
                        postImages[imageIndex].id
                    )
                } else {
                    Toast.makeText(requireContext(), "댓글 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            } else if (onEdit) {
                if (idList.size > 0) {
                    val changes = ChangedComments(idList, xList, yList)
                    snsViewModel.patchComments(postImages[imageIndex].id, changes)
                }
                editEnd()
            }
        }
    }

    fun writeEnd() {
        binding.addCommentLayout.setBackgroundColor(Color.parseColor("#FDFDFD"))
        binding.commentVisibility.visibility = View.VISIBLE
        binding.writeCommentBack.visibility = View.VISIBLE
        binding.writeCommentCancel.visibility = View.GONE
        binding.writeCommentApply.visibility = View.GONE
        writeContainer.isClickable = false
        binding.addCommentButtonsLayout.visibility = View.VISIBLE
        binding.writeCommentTitle.setTextColor(Color.parseColor("#191919"))
        binding.writeCommentTitle.text = "코멘트"

        if (com.example.haru.utils.User.id == writerInfo.id) {
            binding.addCommentInfo.visibility = View.VISIBLE
        }
    }

    fun writeStart() {
        binding.addCommentLayout.setBackgroundColor(Color.parseColor("#191919"))
        binding.writeCommentBack.visibility = View.GONE
        binding.commentVisibility.visibility = View.GONE
        binding.writeCommentApply.visibility = View.VISIBLE
        binding.writeCommentCancel.visibility = View.VISIBLE
        writeContainer.isClickable = true
        binding.addCommentButtonsLayout.visibility = View.GONE
        binding.writeCommentTitle.setTextColor(Color.parseColor("#FDFDFD"))
        binding.writeCommentTitle.text = "코멘트 작성"
        binding.addCommentInfo.visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "SnsMypageFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)

    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.addCommentLayout.id)
        MainActivity.hideNavi(true)
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
        profileViewModel.getUserInfo(com.example.haru.utils.User.id)
        profileViewModel.UserInfo.observe(viewLifecycleOwner) { user ->
            if(com.example.haru.utils.User.id == user.id) {
                isMyPost = com.example.haru.utils.User.id == user.id
                myInfo = user
            }
        }
        binding = FragmentAddCommentBinding.inflate(inflater, container, false)

        if (com.example.haru.utils.User.id == writerInfo.id) {
            binding.addCommentInfo.visibility = View.VISIBLE
        } else {
            binding.addCommentInfo.visibility = View.GONE
        }

        backPressed = object : OnBackPressedCallback(true) { //뒤로가기 처리
            override fun handleOnBackPressed() {
                if (onEdit || onWrite) {
                    val fragment = PopupComment(this@AddCommentFragment)
                    fragment.show(parentFragmentManager, fragment.tag)
                } else {
                    val backManager = parentFragmentManager
                    backManager.popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressed)

        if (isTemplate != "" && isTemplate != null) {
            binding.addCommentTemplateText.text = content
            binding.addCommentTemplateText.setTextColor(Color.parseColor(isTemplate))
        }
        binding.addCommentLikeCount.text = likeCnt.toString()
        binding.addCommentCommentCount.text = commentCnt.toString()
        commentContainer = binding.commentFrame
        infoContainer = binding.writerFrame
        filterFrame = binding.filterFrame

        binding.commentImage.post {
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels //기기의 width값
            binding.commentImage.layoutParams.height = screenWidth
            ImageWidth = screenWidth
            ImageHeight = screenWidth
        }

        writeContainer = binding.moveFrame
        val viewpager = binding.commentImage
        val viewPagerAdapter = AddCommentPagerAdapter(requireContext(), postImages, this)

        binding.addCommentWriteText.setOnClickListener {
            if (!onWrite) {
                onWrite = true
                writeComment()
                writeStart()
            }
        }
        binding.addCommentWriteButton.setOnClickListener {
            if (!onWrite) {
                onWrite = true
                writeComment()
                writeStart()
            }
        }

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
            backManager.popBackStack()
        }

        binding.showTotalComment.setOnClickListener {
            val friend = FriendInfo(writerInfo.id,writerInfo.name,writerInfo.profileImage,writerInfo.profileImage,0,0,0,"")
            val post = Post(postId, friend, content, isTemplate, postImages, arrayListOf(), false, false, likeCnt, commentCnt,"","")
            val newFrag = CommentsFragment(post, com.example.haru.utils.User.id)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                writeContainer.removeAllViews()
                onWrite = false
                isCommented = false
                imageIndex = position
                binding.lastPicture.visibility = View.VISIBLE
                binding.nextPicture.visibility = View.VISIBLE

                if (position == 0) {
                    binding.lastPicture.visibility = View.GONE
                }
                if (position == postImages.size - 1) {
                    binding.nextPicture.visibility = View.GONE
                }

                binding.addcommentIndex.text = "${position + 1} / ${postImages.size}"
                if (commentContainer.childCount != 0) {
                    commentContainer.removeAllViews()
                    infoContainer.removeAllViews()
                    infoContainer.visibility = View.GONE
                    showWriter = true
                }
                for (comment in postImages[position].comments) {
                    comment.user?.let {
                        if (it.id == com.example.haru.utils.User.id)
                            isCommented = true
                    }
                    commentContainer.post {
                        bindComment(comment)
                    }
                }
                setUi()
            }
        })

        binding.writeCommentCancel.setOnClickListener {
            Log.e("20191627", "writeCommentCancel")
            if (onWrite || onEdit) {
                val fragment = PopupComment(this)
                fragment.show(parentFragmentManager, fragment.tag)
            } else {
                this.OnPopupClick(0)
            }
        }

        var addedComment = Comments("", User(), "", 0, 0, false, "", "")

        binding.writeCommentApply.setOnClickListener {
            if (AddContent != "") {
                Log.d("20191668", "position $AddX, $AddY")

                if (isTemplate != null && isTemplate != "") {
                    snsViewModel.writeComment(
                        CommentBody(AddContent, AddX, AddY),
                        postId,
                    )
                } else {
                    snsViewModel.writeComment(
                        CommentBody(AddContent, AddX, AddY),
                        postId,
                        postImages[imageIndex].id
                    )
                }
            } else {
                Toast.makeText(requireContext(), "댓글 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        snsViewModel.AddComment.observe(viewLifecycleOwner) { comment ->
                if(comment.id != "") {
                    profileViewModel.getUserInfo(com.example.haru.utils.User.id)
                    addedComment = comment
                }
        }

        profileViewModel.UserInfo.observe(viewLifecycleOwner) { user ->
            addedComment.user = user
            if (addedComment.id != "" && onWrite) {
                postImages[imageIndex].comments.add(addedComment)
                bindComment(addedComment)
                isCommented = true
            } else if (onWrite) {
                Toast.makeText(requireContext(), "댓글 작성에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
            setUi()
            onWrite = false
            writeContainer.removeAllViews()
            writeEnd()
            binding.writeCommentTitle.text = "코멘트 남기기"
        }

        binding.editCommentApply.setOnClickListener {
            if (idList.size > 0) {
                val changes = ChangedComments(idList, xList, yList)
                snsViewModel.patchComments(postImages[imageIndex].id, changes)
            }
            editEnd()
        }

        binding.addCommentResetText.setOnClickListener {
            editCancel()
            editEnd()
        }

        binding.addCommentResetIcon.setOnClickListener {
            editCancel()
            editEnd()
        }

        binding.commentVisibility.setOnClickListener {
            if (CommentIsVisible) {
                infoContainer.visibility = View.GONE
                showWriter = true //다시누르면 보이도록
                CommentIsVisible = false
                commentContainer.visibility = View.INVISIBLE
                binding.commentVisibility.setBackgroundResource(R.drawable.unvisibility_icon)
                binding.commentVisibility.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.highlight
                    )
                )
            } else {
                CommentIsVisible = true
                commentContainer.visibility = View.VISIBLE
                binding.commentVisibility.setBackgroundResource(R.drawable.visibility_icon)
                binding.commentVisibility.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.date_text
                    )
                )
            }
        }

        binding.addCommentEditComments.setOnClickListener {
            if (onEdit) {//편집 종료
                editCancel()
                editEnd()
                onEdit = false
            } else {//편집 시작
                infoContainer.visibility = View.GONE
                showWriter = true //다시누르면 보이도록
                editStart()
                onEdit = true
            }
        }

        binding.addCommentEditText.setOnClickListener {
            if (onEdit) {//편집 종료
                editCancel()
                editEnd()
                onEdit = false
            } else {//편집 시작
                infoContainer.visibility = View.GONE
                showWriter = true //다시누르면 보이도록
                editStart()
                onEdit = true
            }
        }

        viewpager.adapter = viewPagerAdapter
        return binding.root
    }

    fun setUi() {
        if (com.example.haru.utils.User.id == writerInfo.id) {
            binding.addCommentEditComments.visibility = View.VISIBLE
            binding.addCommentEditText.visibility = View.VISIBLE
        } else if (isCommented) {
            binding.addCommentWriteButton.visibility = View.GONE
            binding.addCommentWriteText.visibility = View.GONE
            binding.addCommentMyCommentIcon.visibility = View.VISIBLE
            binding.addCommentMyCommentText.visibility = View.VISIBLE
        } else {
            binding.addCommentWriteButton.visibility = View.VISIBLE
            binding.addCommentWriteText.visibility = View.VISIBLE
            binding.addCommentMyCommentIcon.visibility = View.GONE
            binding.addCommentMyCommentText.visibility = View.GONE
        }
    }

    fun editStart() {
        onEdit = true

        binding.addCommentInfo.visibility = View.GONE
        binding.addCommentLayout.setBackgroundColor(Color.parseColor("#191919"))
        val color = Color.argb(100, 25, 25, 25)
        binding.editFilterFrame.setBackgroundColor(color)
        binding.addCommentEditComments.setImageResource(R.drawable.edit_comment_blue_finger)
        binding.writeCommentBack.setBackgroundResource(R.drawable.back_arrow_white)
        binding.writeCommentTitle.setTextColor(Color.parseColor("#FDFDFD"))
        binding.addCommentEditText.setText("편집중")
        binding.addCommentEditText.setTextColor(Color.parseColor("#1DAFFF"))
        binding.addCommentResetIcon.visibility = View.VISIBLE
        binding.addCommentResetText.visibility = View.VISIBLE
        binding.lastPicture.visibility = View.GONE
        binding.nextPicture.visibility = View.GONE
        binding.addcommentIndex.visibility = View.GONE
        binding.commentVisibility.visibility = View.GONE
        binding.showTotalComment.visibility = View.GONE
        binding.editCommentApply.visibility = View.VISIBLE
        binding.writeCommentBack.visibility = View.GONE
        binding.writeCommentCancel.visibility = View.VISIBLE
        binding.writeCommentTitle.text = "코멘트 편집"

        //댓글들이 드래그가 가능하도록
        if (commentContainer.childCount > 0) {
            for (i in 0..commentContainer.childCount - 1) {
                val view = commentContainer.getChildAt(i)
                view.isClickable = true
            }
        }
    }

    fun editCancel() {
        if (commentContainer.childCount != 0) {
            commentContainer.removeAllViews()
            infoContainer.removeAllViews()
        }
        for (comment in postImages[imageIndex].comments) {
            commentContainer.post {
                bindComment(comment)
            }
        }
    }

    fun editEnd() {
        onEdit = false

        binding.addCommentInfo.visibility = View.VISIBLE
        binding.addCommentLayout.setBackgroundColor(Color.parseColor("#FDFDFD"))
        val color = Color.argb(0, 0, 0, 0)
        binding.editFilterFrame.setBackgroundColor(color)
        binding.addCommentEditComments.setImageResource(R.drawable.edit_comment_gray_finger)
        binding.writeCommentBack.setBackgroundResource(R.drawable.back_arrow)
        binding.writeCommentTitle.setTextColor(Color.parseColor("#191919"))
        binding.addCommentEditText.setText("편집하기")
        binding.addCommentEditText.setTextColor(Color.parseColor("#646464"))
        binding.addCommentResetIcon.visibility = View.GONE
        binding.addCommentResetText.visibility = View.GONE
        binding.lastPicture.visibility = View.VISIBLE
        binding.nextPicture.visibility = View.VISIBLE
        binding.addcommentIndex.visibility = View.VISIBLE
        binding.commentVisibility.visibility = View.VISIBLE
        binding.showTotalComment.visibility = View.VISIBLE
        binding.writeCommentBack.visibility = View.VISIBLE
        binding.writeCommentCancel.visibility = View.GONE
        binding.editCommentApply.visibility = View.GONE
        binding.writeCommentTitle.text = "코멘트"

        //댓글들이 드래그가 불가하도록
        if (commentContainer.childCount > 0) {
            for (i in 0..commentContainer.childCount - 1) {
                val view = commentContainer.getChildAt(i)
                view.isClickable = false
            }
        }
    }

    //편집한 댓글 정보
    fun editList(commentId: String, x: Int, y: Int) {
        if (idList.contains(commentId)) {
            val index = idList.indexOf(commentId)
            xList[index] = x
            yList[index] = y
        } else {
            idList.add(commentId)
            xList.add(x)
            yList.add(y)
        }
    }

    fun bindComment(comment: Comments) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_comment_on_picture, null)
        val viewHeight = commentContainer.height
        // TextView를 찾아서 텍스트를 변경
        val textView = view.findViewById<TextView>(R.id.comment_on_picture_text)
        textView.text = comment.content
        view.bringToFront()

        //작성자 정보를 위한 뷰
        val Name = comment.user!!.name
        var profileImage = ""
        if (!comment.user!!.profileImage.isNullOrEmpty())
            profileImage = comment.user!!.profileImage
        val Id = comment.user!!.id

        val writerView = inflater.inflate(R.layout.item_comment_on_picture_writer, null)
        val writerName = writerView.findViewById<TextView>(R.id.comment_on_picture_name)
        val writerProfile = writerView.findViewById<ImageView>(R.id.comment_on_picture_profile)

        val commentDragListener = object : View.OnTouchListener {
            private var offsetX = 0f
            private var offsetY = 0f
            private var isDragging = false
            private var initialX = 0f
            private var initialY = 0f
            private var x_start = 0f
            private var x_end = 0f
            private var y_start = 0f
            private var y_end = 0f
            var onHide = false

            override fun onTouch(view: View, event: MotionEvent): Boolean {

                binding.writeCommentHide.visibility = View.GONE
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        offsetX = view.x - event.rawX
                        offsetY = view.y - event.rawY
                        initialX = view.x
                        initialY = view.y
                        isDragging = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //사진 밖으로 댓글을 드래그 하지 않았는지 검사
                        if (!isDragging && (Math.abs(event.rawX - initialX) > ViewConfiguration.get(
                                view.context
                            ).scaledTouchSlop ||
                                    Math.abs(event.rawY - initialY) > ViewConfiguration.get(view.context).scaledTouchSlop)
                        ) {
                            isDragging = true
                        }
                        if (isDragging) {
                            x_start = event.rawX + offsetX
                            x_end = x_start + view.width
                            y_start = event.rawY + offsetY
                            y_end = y_start + view.height


                            if (x_start > 0 && x_end < writeContainer.width)
                                view.x = x_start

                            if (y_start > 0 && y_end < writeContainer.height) {
                                if (y_end > commentContainer.height) {
                                    view.setBackgroundResource(R.color.light_gray)
                                } else {
                                    view.setBackgroundResource(R.drawable.comment_ballon)
                                }
                                view.y = event.rawY + offsetY
                            }

                            binding.writeCommentHide.visibility = View.VISIBLE
                            val deletex = binding.writeCommentHide.x
                            val deletexEnd = deletex + binding.writeCommentHide.width
                            val deletey = binding.writeCommentHide.y
                            val deleteyEnd = deletey + binding.writeCommentHide.height

                            //가리기 칸에 드래그 되었는지
                            if (x_start > deletexEnd || x_end < deletex) {
                                binding.writeCommentHide.setImageResource(R.drawable.comment_hide_off)
                                onHide = false
                            } else if (y_start > deleteyEnd || y_end < deletey) {
                                binding.writeCommentHide.setImageResource(R.drawable.comment_hide_off)
                                onHide = false
                            } else {
                                binding.writeCommentHide.setImageResource(R.drawable.comment_hide_on)
                                onHide = true
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isDragging) {
                            if (y_end > ImageHeight) {
                                view.x = initialX
                                view.y = initialY
                            } else {
                                AddX = (view.x / ImageWidth * 100).toInt()
                                AddY = (view.y / ImageHeight * 100).toInt()
                                editList(comment.id, AddX, AddY)
                                writerView.x = view.x + view.width / 2
                                writerView.y = view.y - view.height - 30
                                if (writerView.width + writerView.x > ImageWidth) {
                                    if (view.width > writerView.width) //댓글 시작지점이랑 x값 맞추기
                                        writerView.x = view.x
                                    else { //댓글 끝지점이랑 정보 끝지점 맞추기
                                        writerView.x = view.x - (writerView.width - view.width)
                                    }
                                }
                            }
                            binding.writeCommentHide.visibility = View.GONE
                            binding.writeCommentHide.setImageResource(R.drawable.comment_hide_off)
                            view.setBackgroundResource(R.drawable.comment_ballon)

                            if (onHide) {
                                commentContainer.removeView(view)
                                infoContainer.removeView(writerView)
                                snsViewModel.patchComment(
                                    comment.user!!.id,
                                    comment.id,
                                    PatchCommentBody(comment.x!!, comment.y!!, false)
                                )
                            }
                        }
                        isDragging = false // Reset the dragging flag
                    }
                }
                return isDragging
            }
        }

        if(writerInfo.id == com.example.haru.utils.User.id) {
            view.setOnTouchListener(commentDragListener)
            view.isClickable = false
        }

        if (com.example.haru.utils.User.id == comment.user!!.id) {
            view.setOnClickListener {
                val fragment = PopupDelMyComment(comment, view, writerView, this, snsViewModel)
                fragment.show(parentFragmentManager, fragment.tag)
            }
        }


        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val density = resources.displayMetrics.density

        val writerParam = FrameLayout.LayoutParams(
            (120 * density + 0.5f).toInt(),
            (33 * density + 0.5f).toInt()
        )

        params.leftMargin = ImageWidth * comment.x!! / 100
        params.topMargin = ImageHeight * comment.y!! / 100
        if (params.topMargin + textView.height > viewHeight) { // 사진에서 벗어나는 댓글에 대한 보정
            params.topMargin -= (params.leftMargin + textView.height) - viewHeight
        }
        view.layoutParams = params
        commentContainer.addView(view)

        view.post {//작성자 정보창 param
            writerParam.leftMargin = params.leftMargin + view.width / 2
            writerParam.topMargin = params.topMargin - (view.height + 30)

            writerView.layoutParams = writerParam

            infoContainer.addView(writerView)
        }

        writerView.post {
            if (writerParam.leftMargin + (120 * density + 0.5f).toInt() >= ImageWidth) { // 사진에서 벗어나는 정보에 대한 보정
                if (view.width > writerView.width) //댓글 시작지점이랑 x값 맞추기
                    writerParam.leftMargin = params.leftMargin
                else { //댓글 끝지점이랑 정보 끝지점 맞추기
                    writerView.x = view.x - (writerView.width - view.width)
                }
            }

            Log.d("20191668", "${writerView.width}, ${writerView.y}")
            writerName.text = Name// 유저명

            if (profileImage.isNullOrEmpty()) {
                writerProfile.setImageResource(R.drawable.default_profile)
            } else {
                Glide.with(this)//프로필 사진
                    .load(profileImage)
                    .into(writerProfile)
            }

            writerView.setOnClickListener {// 댓글 숨기기 팝업
                val fragment = PopupHide(comment, view, writerView, this)
                fragment.show(parentFragmentManager, fragment.tag)
            }

        }
    }

    fun writeComment() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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
                } catch (e: java.lang.Exception) {

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
                        if (!isDragging && (Math.abs(event.rawX - initialX) > ViewConfiguration.get(
                                parentView.context
                            ).scaledTouchSlop ||
                                    Math.abs(event.rawY - initialY) > ViewConfiguration.get(
                                parentView.context
                            ).scaledTouchSlop)
                        ) {
                            isDragging = true
                        }
                        if (isDragging) {
                            x_start = event.rawX + offsetX
                            x_end = x_start + parentView.width
                            y_start = event.rawY + offsetY
                            y_end = y_start + parentView.height


                            if (x_start > 0 && x_end < writeContainer.width)
                                parentView.x = x_start

                            if (y_start > 0 && y_end < writeContainer.height) {
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
                            //삭제 칸에 드래그 되었는지
                            if (x_start > deletexEnd || x_end < deletex) {
                                binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_default)
                                onDelete = false
                            } else if (y_start > deleteyEnd || y_end < deletey) {
                                binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_default)
                                onDelete = false
                            } else {
                                binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_ondrag)
                                onDelete = true
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isDragging) {
                            if (y_end > ImageHeight) {
                                parentView.x = initialX
                                parentView.y = initialY
                            } else {
                                AddX = (parentView.x / ImageWidth * 100).toInt()
                                AddY = (parentView.y / ImageHeight * 100).toInt()
                            }
                            binding.writeCommentDelete.visibility = View.GONE
                            binding.writeCommentDelete.setImageResource(R.drawable.cancel_write_default)
                            parentView.setBackgroundResource(R.drawable.comment_ballon)

                            if (onDelete) {
                                writeContainer.removeView(writeBox)
                                onWrite = false
                                val color = Color.argb(
                                    0,
                                    0,
                                    0,
                                    0
                                ) // 204 represents 80% transparency black (255 * 0.8 = 204)
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

            if (AddContent == "") {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
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
                parentView.y = (commentContainer.height * 0.25).toFloat()
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
        view.y = (commentContainer.height * 0.25).toFloat()

        writeBox = view
        writeContainer.addView(view)

        editView.requestFocus() // EditText에 포커스를 설정합니다.
        imm.showSoftInput(editView, InputMethodManager.SHOW_IMPLICIT) // 키보드를 활성화합니다.
    }
}

class PopupComment(val listener: ImageClickListener) : BottomSheetDialogFragment() {
    lateinit var popupbinding: PopupSnsCommentCancelBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupbinding = PopupSnsCommentCancelBinding.inflate(inflater, container, false)

        popupbinding.snsAddCommentUnsave.setOnClickListener {
            listener.OnPopupClick(0)
            dismiss()
        }

        popupbinding.snsAddCommentSave.setOnClickListener {
            listener.OnPopupClick(1)
            dismiss()
        }

        popupbinding.snsAddCommentCancel.setOnClickListener {
            listener.OnPopupClick(2)
            dismiss()
        }

        return popupbinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 35 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}

class PopupDelMyComment( val comment: Comments,
                         val content: View,
                         val writer: View,
                         val listener: ImageClickListener,
                         val viewModel : SnsViewModel) : BottomSheetDialogFragment() {
    lateinit var popupbinding: PopupSnsCommentDeleteAgainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupbinding = PopupSnsCommentDeleteAgainBinding.inflate(inflater, container, false)
        popupbinding.snsCommentRealDelete.setOnClickListener {
            viewModel.deleteComment(comment.user!!.id, comment.id)
        }

        viewModel.DeleteResult.observe(viewLifecycleOwner){result ->
            if(result){
                listener.OnHideClick(comment, content, writer, 0)
                dismiss()
            }
            else{
                Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        popupbinding.snsDeleteCancel.setOnClickListener {
            dismiss()
        }

        return popupbinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 35 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}

class PopupHide (
    val comment: Comments,
    val content: View,
    val writer: View,
    val listener: ImageClickListener
) : BottomSheetDialogFragment() {
    lateinit var binding: PopupAddCommentHideBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopupAddCommentHideBinding.inflate(inflater, container, false)

        if (comment.user!!.profileImage != null) {
            Glide
                .with(this)
                .load(comment.user!!.profileImage)
                .into(binding.hideTargetProfile)
        } else
            binding.hideTargetProfile.setImageResource(R.drawable.default_profile)

        binding.hideTargetName.text = comment.user!!.name
        binding.hideTargetContent.text = comment.content
        binding.hideTargetTime.text =
            GetPastDate.getPastDate(comment.createdAt!!)

        binding.hideComment.setOnClickListener {
            listener.OnHideClick(comment, content, writer, 0)
            dismiss()
        }
        return binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 35 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }

}