package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Comments
import com.example.haru.data.model.PatchCommentBody
import com.example.haru.data.model.Post
import com.example.haru.databinding.FragmentAddPostBinding
import com.example.haru.databinding.FragmentCommentsBinding
import com.example.haru.databinding.PopupSnsCommentCancelBinding
import com.example.haru.databinding.PopupSnsCommentDeleteBinding
import com.example.haru.utils.User
import com.example.haru.view.adapter.CommentsAdapter
import com.example.haru.viewmodel.SnsViewModel
import org.w3c.dom.Comment

interface onCommentClick{

    fun onDeleteClick(writerId: String, commentId: String, item: Comments)

    fun onPublicClick(writerId: String, commentId: String, body: PatchCommentBody)

    fun onPopupSelect(position: Int)
}

class CommentsFragment(postitem: Post, val userId: String) : Fragment(), onCommentClick{
    lateinit var binding : FragmentCommentsBinding
    lateinit var commentsRecyclerView: RecyclerView
    lateinit var snsViewModel: SnsViewModel
    private lateinit var adapter:  CommentsAdapter
    val post = postitem
    lateinit var comment: Comments
    var newComment = true

    var writerId = ""
    var commentId = ""


    override fun onDeleteClick(writerId: String, commentId: String, item: Comments) {
        this.writerId = writerId
        this.commentId = commentId
        comment = item
        addPopup()
    }

    override fun onPublicClick(writerId: String, commentId: String, body: PatchCommentBody) {
        snsViewModel.patchComment(writerId, commentId, body)
    }

    override fun onPopupSelect(position: Int) {
       deletePopup()
        if(position == 0){
            snsViewModel.deleteComment(writerId, commentId)
        }else if(position == 1){
            //TODO:신고창 만들기
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "CommentsFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.commentMenu.id, 1.1f)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.commentMenu.id)
        (activity as BaseActivity).adjustTopMargin(binding.commentMenu.id, 1.1f)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        if(User.id != userId ){
            binding.totalCommentEdit.visibility = View.GONE
            binding.totalCommentEdit.visibility = View.GONE
        }

        commentsRecyclerView = binding.commentRecycler
        adapter = CommentsAdapter(requireContext(), arrayListOf(), this)
        commentsRecyclerView.adapter = adapter
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        snsViewModel.getFirstComments(post.id, post.images[0].id)
        var index = 0
        binding.commentCommentsIndex.text = "1/${post.images.size}"

        if(post.images.size == 1){
            binding.commentsNextPicture.visibility = View.GONE
            binding.commentsLastPicture.visibility = View.GONE
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!commentsRecyclerView.canScrollVertically(1) && newComment) {
                    val lastday = adapter.getLastComment()
                    snsViewModel.getComments(post.id, post.images[index].id, lastday)
                    Toast.makeText(context, "새 코멘트 불러오는 중....", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val refresher = binding.refreshComment
        refresher.setOnRefreshListener {
            refresher.isRefreshing = true
            snsViewModel.getFirstComments(post.id, post.images[index].id)
            refresher.isRefreshing = false
        }

        binding.commentsLastPicture.setOnClickListener {
            index -= 1
            snsViewModel.getFirstComments(post.id, post.images[index].id)
            binding.commentCommentsIndex.text = "${index + 1}/${post.images.size}"

            if(index == 0){
                binding.commentsLastPicture.visibility = View.GONE
            }else{
                binding.commentsLastPicture.visibility = View.VISIBLE
            }

            if(index != post.images.size - 1){
                binding.commentsNextPicture.visibility = View.VISIBLE
            }
        }

        binding.commentsNextPicture.setOnClickListener {
            index += 1
            snsViewModel.getFirstComments(post.id, post.images[index].id)
            binding.commentCommentsIndex.text = "${index + 1}/${post.images.size}"

            if(index == post.images.size - 1){
                binding.commentsNextPicture.visibility = View.GONE
            }else{
                binding.commentsNextPicture.visibility = View.VISIBLE
            }

            if(index != 0){
                binding.commentsLastPicture.visibility = View.VISIBLE
            }
        }
        commentsRecyclerView.addOnScrollListener(scrollListener)

        snsViewModel.Comments.observe(viewLifecycleOwner){comments ->
            if(comments.size > 0) {
                adapter.newComment(comments)
                binding.commentCount.text = adapter.itemCount.toString()
            }else{
                newComment = false
            }
        }

        snsViewModel.FirstComments.observe(viewLifecycleOwner){comments ->
            if(comments.size > 0){
                adapter.firstComment(comments)
                binding.commentCount.text = adapter.itemCount.toString()
            }else{
                adapter.firstComment(arrayListOf())
                binding.commentCount.text = "0"
                newComment = false
            }
        }

        binding.commentBackbutton.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        snsViewModel.ChangeResult.observe(viewLifecycleOwner){result ->
            if(result){
                Toast.makeText(requireContext(),"변경 성공", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"변경 실패", Toast.LENGTH_SHORT).show()
            }
        }

        snsViewModel.DeleteResult.observe(viewLifecycleOwner){result ->
            if(result){
                adapter.deleteItem(comment)
                binding.commentCount.text = adapter.itemCount.toString()
            }
            else{
                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    fun addPopup(){
        val fragment = PopupDeleteComment(this)
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.add_comment_anchor, fragment)
        transaction.commit()
    }

    fun deletePopup(){
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.add_comment_anchor)

        if(fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }
    }
}

class PopupDeleteComment(listener: onCommentClick): Fragment(){
    lateinit var popupbinding : PopupSnsCommentDeleteBinding
    val listener = listener

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(popupbinding.popupTotalCommentsContainer.id, 2f)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(popupbinding.popupTotalCommentsContainer.id, 2f)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        popupbinding = PopupSnsCommentDeleteBinding.inflate(inflater, container, false)

        popupbinding.commentsDelete.setOnClickListener {
            listener.onPopupSelect(0)
        }

        popupbinding.commentsReport.setOnClickListener {
            listener.onPopupSelect(1)
        }

        popupbinding.commentsCancel.setOnClickListener {
            listener.onPopupSelect(2)
        }

        popupbinding.popupTotalCommentsContainer.setOnClickListener {

        }

        return popupbinding.root
    }
}