package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.Comments
import com.example.haru.data.model.Post
import com.example.haru.data.model.Profile
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

    init{
        _Page.value = 1
    }

    fun getPosts(page: String){
        var newPost: ArrayList<Post> = arrayListOf()
        var allPost = _Posts.value ?: arrayListOf()
        viewModelScope.launch{
            PostRepository.getPost(page) {
                if(it.size > 0){ //get success
                    newPost = it
                    allPost.addAll(it)
                }
            }
            _newPost.value = newPost
            _Posts.value = allPost
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
}