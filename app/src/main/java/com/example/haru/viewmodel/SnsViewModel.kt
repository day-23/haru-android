package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.*
import com.example.haru.data.repository.PostRepository
import com.example.haru.data.repository.ProfileRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class SnsViewModel: ViewModel() {
    private val PostRepository = PostRepository()

    private val _Posts = MutableLiveData<ArrayList<Post>>()//요청한 게시글 전체
    val Posts : LiveData<ArrayList<Post>>
        get() = _Posts

    private val _newPost = MutableLiveData<ArrayList<Post>>()//새로 요청한 페이지
    val newPost : LiveData<ArrayList<Post>>
        get() = _newPost

    private val _Page = MutableLiveData<Int>()
    val Page : LiveData<Int>
        get() = _Page

    private val _Comments = MutableLiveData<ArrayList<Comments>>()
    val Comments : LiveData<ArrayList<Comments>>
        get() = _Comments

    private val _CurrentPost = MutableLiveData<String>()
    val CurrentPost : LiveData<String>
        get() = _CurrentPost

    private val _DeleteResult = MutableLiveData<Boolean>()
    val DeleteResult : LiveData<Boolean>
        get() = _DeleteResult

    private val _ChangeResult = MutableLiveData<Boolean>()
    val ChangeResult : LiveData<Boolean>
        get() = _ChangeResult

    fun init_page(){
        val currentPage = _Page.value
        if(currentPage == 1){
            getPosts("1")
            Log.d("20191668", "new page")
        }else{
            _Page.value = 1
        }
    }

    fun getPosts(page: String){
        var newPost: ArrayList<Post> = arrayListOf()
        var allPost = _Posts.value ?: arrayListOf()
        val page = page
        viewModelScope.launch{
            PostRepository.getPost(page) {
                if(it.size > 0){ //get success
                    newPost = it
                    allPost.addAll(it)
                }
            }
            if(page.toInt() > 1) {
                _newPost.value = newPost // 두번째 페이지일 경우
            }else{
                _Posts.value = newPost // 첫페이지일경우
            }
        }
    }

    fun addPage(){
        var current = _Page.value ?: 1
        _Page.value = current.plus(1)
    }

    fun likeAction(id: String){
        viewModelScope.launch {
            PostRepository.postLike(id) {
                if(it){
                    Log.d("LIKE", "LIKE ACTIONED")
                }
            }
        }
    }

    fun getComments(postId: String) {
        var comments = ArrayList<Comments>()
        viewModelScope.launch {
            PostRepository.getComment(postId){
                if(it.size > 0){
                    comments = it
                }
            }
            _Comments.value = comments
        }
    }

    fun writeComment(comment: CommentBody, postId: String, imageId:String){
        viewModelScope.launch {
            PostRepository.writeComment(comment, postId, imageId){

            }
        }
    }

    fun deletePost(postId: String){
        var result = false
        viewModelScope.launch {
            PostRepository.deletePost(postId){
                result = it
            }
            _DeleteResult.value = result
        }
    }

    fun deleteComment(writerId: String, commentId:String){
        var result = false
        viewModelScope.launch {
            PostRepository.deleteComment(writerId, commentId){
                result = it
            }
            _DeleteResult.value = result
        }
    }

    fun patchComment(writerId: String, commentId: String, body: PatchCommentBody){
        var result = false
        viewModelScope.launch {
            PostRepository.chageComment(writerId, commentId, body){
                result = it
            }
            _ChangeResult.value = result
        }
    }
}