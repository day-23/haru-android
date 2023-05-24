package com.example.haru.view.sns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.PatchCommentBody
import com.example.haru.databinding.FragmentAddPostBinding
import com.example.haru.databinding.FragmentCommentsBinding
import com.example.haru.view.adapter.CommentsAdapter
import com.example.haru.viewmodel.SnsViewModel
import org.w3c.dom.Comment

interface onCommentClick{

    fun onDeleteClick(writerId: String, commentId: String, position: Int)

    fun onPublicClick(writerId: String, commentId: String, body: PatchCommentBody)
}

class CommentsFragment(postId:String) : Fragment(), onCommentClick{
    lateinit var binding : FragmentCommentsBinding
    lateinit var commentsRecyclerView: RecyclerView
    lateinit var snsViewModel: SnsViewModel
    private lateinit var adapter:  CommentsAdapter
    val postId = postId

    override fun onDeleteClick(writerId: String, commentId: String, position: Int) {
        snsViewModel.deleteComment(writerId, commentId)

        snsViewModel.DeleteResult.observe(viewLifecycleOwner){result ->
            if(result){
                adapter.deleteItem(position)
            }
            else{
                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPublicClick(writerId: String, commentId: String, body: PatchCommentBody) {
        snsViewModel.patchComment(writerId, commentId, body)

        snsViewModel.ChangeResult.observe(viewLifecycleOwner){result ->
            if(result){
               Toast.makeText(requireContext(),"변경 성공", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"변경 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "CommentsFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        commentsRecyclerView = binding.commentRecycler
        snsViewModel.getComments(postId)

        snsViewModel.Comments.observe(viewLifecycleOwner){comments ->
            adapter = CommentsAdapter(requireContext(), comments, this)
            commentsRecyclerView.adapter = adapter
            commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.commentCount.text = adapter.itemCount.toString()
        }

        binding.commentBackbutton.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragment = SnsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragments_frame, fragment)
                .commit()
        }

        return binding.root
    }
}