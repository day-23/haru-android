package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Comments
import com.example.haru.data.model.PatchCommentBody
import com.example.haru.data.model.Post
import com.example.haru.databinding.FragmentAddPostBinding
import com.example.haru.databinding.FragmentCommentsBinding
import com.example.haru.databinding.PopupSnsCommentCancelBinding
import com.example.haru.databinding.PopupSnsCommentDeleteAgainBinding
import com.example.haru.databinding.PopupSnsCommentDeleteBinding
import com.example.haru.utils.GetPastDate
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.CommentsAdapter
import com.example.haru.viewmodel.SnsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.w3c.dom.Comment

interface onCommentClick {

    fun onDeleteClick(writerId: String, commentId: String, item: Comments)

    fun onPublicClick(writerId: String, commentId: String, body: PatchCommentBody)

    fun onPopupSelect(position: Int)

    fun onRealDeleteClick(position: Int)
}

class CommentsFragment(postitem: Post, val userId: String) : Fragment(), onCommentClick {
    lateinit var binding: FragmentCommentsBinding
    lateinit var commentsRecyclerView: RecyclerView
    lateinit var snsViewModel: SnsViewModel
    private lateinit var adapter: CommentsAdapter
    val post = postitem
    lateinit var comment: Comments
    var newComment = true

    var writerId = ""
    var commentId = ""


    override fun onDeleteClick(writerId: String, commentId: String, item: Comments) {
        this.writerId = writerId
        this.commentId = commentId
        comment = item
        addPopup(item.user?.name, item.user?.profileImage, item?.content, item?.createdAt)
    }

    override fun onPublicClick(writerId: String, commentId: String, body: PatchCommentBody) {
        snsViewModel.patchComment(writerId, commentId, body)
    }

    override fun onPopupSelect(position: Int) {
        deletePopup()
        if (position == 0) {
            val fragment = PopupDeleteCommentAgain(this)
            fragment.show(parentFragmentManager, fragment.tag)
        } else if (position == 1) {
            snsViewModel.reportComment(writerId, commentId)
        }
    }

    override fun onRealDeleteClick(position: Int) {
        if (position == 0){
            snsViewModel.deleteComment(writerId, commentId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "CommentsFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
        MainActivity.hideNavi(true)

    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.commentMenu.id)
        MainActivity.hideNavi(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.commentMenu.id)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)

        commentsRecyclerView = binding.commentRecycler
        adapter = CommentsAdapter(requireContext(), arrayListOf(), this)
        commentsRecyclerView.adapter = adapter
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (post.isTemplatePost == null){
            snsViewModel.getFirstComments(post.id, post.images[0].id)
        }else{
            snsViewModel.getFirstTemplateComments(post.id)
        }



        var index = 0
        binding.commentCommentsIndex.text = "1/${post.images.size}"
        binding.commentsLastPicture.visibility = View.GONE

        if (post.images.size == 1) {
            binding.commentsNextPicture.visibility = View.GONE
            binding.commentsLastPicture.visibility = View.GONE
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!commentsRecyclerView.canScrollVertically(1) && newComment) {
                    val lastday = adapter.getLastComment()
                    if (!post.isTemplatePost.isNullOrEmpty()){
                        snsViewModel.getComments(post.id, post.images[index].id, lastday)
                    }else{
                        snsViewModel.getTemplateComments(post.id, lastday)
                    }
                }
            }
        }

        val refresher = binding.refreshComment
        refresher.setOnRefreshListener {
            refresher.isRefreshing = true
            if (post.isTemplatePost.isNullOrEmpty()){
                snsViewModel.getFirstComments(post.id, post.images[0].id)
            }else{
                snsViewModel.getFirstTemplateComments(post.id)
            }
            refresher.isRefreshing = false
        }

        binding.commentsLastPicture.setOnClickListener {
            index -= 1
            if (post.isTemplatePost.isNullOrEmpty()){
                snsViewModel.getFirstComments(post.id, post.images[0].id)
            }else{
                snsViewModel.getFirstTemplateComments(post.id)
            }
            binding.commentCommentsIndex.text = "${index + 1}/${post.images.size}"

            if (index == 0) {
                binding.commentsLastPicture.visibility = View.GONE
            } else {
                binding.commentsLastPicture.visibility = View.VISIBLE
            }

            if (index != post.images.size - 1) {
                binding.commentsNextPicture.visibility = View.VISIBLE
            }
        }

        binding.commentsNextPicture.setOnClickListener {
            index += 1
            if (post.isTemplatePost.isNullOrEmpty()){
                snsViewModel.getFirstComments(post.id, post.images[0].id)
            }else{
                snsViewModel.getFirstTemplateComments(post.id)
            }
            binding.commentCommentsIndex.text = "${index + 1}/${post.images.size}"

            if (index == post.images.size - 1) {
                binding.commentsNextPicture.visibility = View.GONE
            } else {
                binding.commentsNextPicture.visibility = View.VISIBLE
            }

            if (index != 0) {
                binding.commentsLastPicture.visibility = View.VISIBLE
            }
        }
        commentsRecyclerView.addOnScrollListener(scrollListener)

        snsViewModel.Comments.observe(viewLifecycleOwner) { comments ->
            if (comments.size > 0) {
                adapter.newComment(comments)
                binding.commentCount.text = adapter.itemCount.toString()
            } else {
                newComment = false
            }
        }

        snsViewModel.FirstComments.observe(viewLifecycleOwner) { comments ->
            if (comments.size > 0) {
                adapter.firstComment(comments)
                binding.commentCount.text = adapter.itemCount.toString()
            } else {
                adapter.firstComment(arrayListOf())
                binding.commentCount.text = "0"
                newComment = false
            }
        }

        binding.commentBackbutton.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        snsViewModel.ChangeResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                Toast.makeText(requireContext(), "변경 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "변경 실패", Toast.LENGTH_SHORT).show()
            }
        }

        snsViewModel.DeleteResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                adapter.deleteItem(comment)
                binding.commentCount.text = adapter.itemCount.toString()
            } else {
                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    fun addPopup(name: String?, profileImage: String?, content: String?, date: String?) {
        val fragment = PopupDeleteComment(this, name, profileImage, content, date)
        fragment.show(parentFragmentManager, fragment.tag)
//        val fragmentManager = childFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//        transaction.add(R.id.add_comment_anchor, fragment)
//        transaction.commit()
    }

    fun deletePopup() {
//        val fragmentManager = childFragmentManager
//        val fragment = fragmentManager.findFragmentById(R.id.add_comment_anchor)
//
//        if(fragment != null) {
//            val transaction = fragmentManager.beginTransaction()
//            transaction.remove(fragment)
//            transaction.commit()
//        }
    }
}

class PopupDeleteComment(
    val listener: onCommentClick,
    val name: String?,
    val profileImage: String?,
    val content: String?,
    val date: String?
) : BottomSheetDialogFragment() {
    lateinit var binding: PopupSnsCommentDeleteBinding

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (profileImage != null)
            Glide.with(this)
                .load(profileImage)
                .into(binding.ivProfileImage)
        else binding.ivProfileImage.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)

        binding.tvUserId.text = name
        if (date != null)
            binding.tvTime.text = GetPastDate.getPastDate(date)
        binding.tvContent.text = content

        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        binding.commentsDelete.setOnClickListener {
            listener.onPopupSelect(0)
            dismiss()
        }

        binding.commentsReport.setOnClickListener {
            listener.onPopupSelect(1)
            dismiss()
        }

//        binding.commentsCancel.setOnClickListener {
//            listener.onPopupSelect(2)
//            dismiss()
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopupSnsCommentDeleteBinding.inflate(inflater, container, false)

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
        return getWindowHeight() * 40 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}

class PopupDeleteCommentAgain(
    val listener: onCommentClick,
) : BottomSheetDialogFragment() {
    lateinit var binding: PopupSnsCommentDeleteAgainBinding

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.snsCommentRealDelete.setOnClickListener {
            listener.onRealDeleteClick(0)
            dismiss()
        }

        binding.snsDeleteCancel.setOnClickListener {
            dismiss()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopupSnsCommentDeleteAgainBinding.inflate(inflater)

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
        return getWindowHeight() * 27 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}