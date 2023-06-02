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

    private val _Posts = MutableLiveData<ArrayList<Post>>()//요청한 게시글 첫번째 페이지
    val Posts : LiveData<ArrayList<Post>>
        get() = _Posts

    private val _newPost = MutableLiveData<ArrayList<Post>>()//이후 페이지
    val newPost : LiveData<ArrayList<Post>>
        get() = _newPost

    private val _Page = MutableLiveData<Int>()
    val Page : LiveData<Int>
        get() = _Page

    private val _Comments = MutableLiveData<ArrayList<Comments>>()
    val Comments : LiveData<ArrayList<Comments>>
        get() = _Comments

    private val _HotTags = MutableLiveData<ArrayList<Tag>>()
    val HotTags : LiveData<ArrayList<Tag>>
        get() = _HotTags

    private val _HotFirstPosts = MutableLiveData<ArrayList<Post>>()
    val HotFirstPosts : LiveData<ArrayList<Post>>
        get() = _HotFirstPosts

    private val _HotPosts = MutableLiveData<ArrayList<Post>>()
    val HotPosts : LiveData<ArrayList<Post>>
        get() = _HotPosts

    private val _TotalComments = MutableLiveData<Int>()
    val TotalComments: LiveData<Int>
        get() = _TotalComments

    private val _FirstComments = MutableLiveData<ArrayList<Comments>>()
    val FirstComments : LiveData<ArrayList<Comments>>
        get() = _FirstComments

    private val _DeleteResult = MutableLiveData<Boolean>()
    val DeleteResult : LiveData<Boolean>
        get() = _DeleteResult

    private val _ChangeResult = MutableLiveData<Boolean>()
    val ChangeResult : LiveData<Boolean>
        get() = _ChangeResult

    fun getPosts(lastCreatedAt: String){
        var newPost: ArrayList<Post> = arrayListOf()
        viewModelScope.launch {
            PostRepository.getPost(lastCreatedAt) {
                if (it.size > 0){
                    newPost = it
                }
            }
        _newPost.value = newPost
        }
    }
    fun getFirstPosts(){
        var newPost: ArrayList<Post> = arrayListOf()
        viewModelScope.launch{
            PostRepository.getFirstPost() {
                if(it.size > 0){ //get success
                    newPost = it
                }
            }
            _Posts.value = newPost // 첫번째 페이지일 경우
        }
    }
    //태그선택 둘러보기
    fun getFirstHotPosts(tagId:String){
        var firstPost = arrayListOf<Post>()
        viewModelScope.launch {
            PostRepository.getFirstHotPosts(tagId){
                if(it.isNotEmpty())
                    firstPost = ArrayList(it)
            }
            _HotFirstPosts.value = firstPost
        }
    }

    fun getHotPosts(tagId: String, lastCreatedAt: String){
        var post = arrayListOf<Post>()
        viewModelScope.launch {
            PostRepository.getHotPosts(tagId, lastCreatedAt){
                if(it.isNotEmpty())
                    post = it
            }
            _HotPosts.value = post
        }
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

    fun getComments(postId: String, imageId: String, lastCreatedAt:String) {
        var comments = ArrayList<Comments>()
        var total = 0
        viewModelScope.launch {
            PostRepository.getComment(postId, imageId, lastCreatedAt){
                if(it.size > 0){
                    comments = it
                }
            }
            _Comments.value = comments
        }
    }

    fun getFirstComments(postId: String, imageId: String) {
        var comments = ArrayList<Comments>()
        var total = 0
        viewModelScope.launch {
            PostRepository.getFirstComment(postId, imageId){
                if(it.data.size > 0){
                    comments = it.data
                    total = it.pagination.totalItems!!
                }
            }
            _FirstComments.value = comments
            _TotalComments.value = total
        }
    }

    fun getHotTags(){
        var tags = arrayListOf<Tag>()

        viewModelScope.launch {
            PostRepository.getHotTags(){
                tags = it
            }
            _HotTags.value = tags
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
            Log.d("20191668", "값변경 완료 : $result")
            _ChangeResult.value = result
        }
    }
}