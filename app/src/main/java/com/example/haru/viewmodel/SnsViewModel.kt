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
import com.example.haru.data.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class SnsViewModel: ViewModel() {
    private val PostRepository = PostRepository()
    private val userRepository = UserRepository()

    private val _Posts = MutableLiveData<ArrayList<Post>>()//요청한 게시글 첫번째 페이지
    val Posts : LiveData<ArrayList<Post>>
        get() = _Posts

    private val _FeedIsEmpty = MutableLiveData<Boolean>()//요청한 게시글 첫번째 페이지
    val FeedIsEmpty : LiveData<Boolean>
        get() = _FeedIsEmpty

    private val _newPost = MutableLiveData<ArrayList<Post>>()//이후 페이지
    val newPost : LiveData<ArrayList<Post>>
        get() = _newPost

    private val _Page = MutableLiveData<Int>()
    val Page : LiveData<Int>
        get() = _Page

    private val _Comments = MutableLiveData<ArrayList<Comments>>()
    val Comments : LiveData<ArrayList<Comments>>
        get() = _Comments

    private val _AddComment = MutableLiveData<Comments>()
    val AddComment : LiveData<Comments>
        get() = _AddComment

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

    private val _SearchedUser = MutableLiveData<User>()
    val SearchedUser : LiveData<User>
        get() = _SearchedUser

    //둘러보기 게시글
    fun getPosts(lastCreatedAt: String){
        Log.d("20191668", "$lastCreatedAt")
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

    //친구피드
    fun getFirstFeeds(){
        var newPost: ArrayList<Post> = arrayListOf()
        var feedIsEmpty = true
        viewModelScope.launch{
            PostRepository.getFirstFeeds() {
                if(it.size > 0){ //get success
                    newPost = it
                    feedIsEmpty = false
                }
            }
            _Posts.value = newPost // 첫번째 페이지일 경우
            _FeedIsEmpty.value = feedIsEmpty
        }
    }

    fun getFeeds(lastCreatedAt: String){
        Log.d("20191668", "$lastCreatedAt")
        var newPost: ArrayList<Post> = arrayListOf()
        viewModelScope.launch {
            PostRepository.getFeeds(lastCreatedAt) {
                if (it.size > 0){
                    newPost = it
                }
            }
            _newPost.value = newPost
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

    fun getFirstTemplateComments(postId: String) {
        var comments = ArrayList<Comments>()
        var total = 0
        viewModelScope.launch {
            PostRepository.getFirstTemplateComment(postId){
                if(it.data.size > 0){
                    comments = it.data
                    total = it.pagination.totalItems!!
                }
            }
            _FirstComments.value = comments
            _TotalComments.value = total
        }
    }

    fun getTemplateComments(postId: String, lastCreatedAt: String) {
        var comments = ArrayList<Comments>()
        var total = 0
        viewModelScope.launch {
            PostRepository.getTemplateComment(postId, lastCreatedAt){
                if(it.data.size > 0){
                    comments = it.data
                    total = it.pagination.totalItems!!
                }
            }
            _Comments.value = comments

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
        var result = Comments("",User(),"",0,0,false,"","")
        viewModelScope.launch {
            PostRepository.writeComment(comment, postId, imageId){
                result = it
            }
            if(result.id != ""){
                _AddComment.value = result
            }else{
                Log.d("Comment", "Fail to add Comment")
            }
        }
    }

    //템플릿 댓글달기
    fun writeComment(comment: CommentBody, postId: String){
        var result = Comments("",User(),"",0,0,false,"","")
        viewModelScope.launch {
            PostRepository.writeComment(comment, postId){
                result = it
            }
            if(result.id != ""){
                _AddComment.value = result
            }else{
                Log.d("Comment", "Fail to add Comment")
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
    //게시글 숨기기
    fun hidePost(postId: String){
        var result = false
        viewModelScope.launch {
            PostRepository.hidePost(postId){
                result = it
            }
            _DeleteResult.value = result
        }
    }

    //게시글 신고하기
    fun reportPost(postId: String){
        var result = false
        viewModelScope.launch {
            PostRepository.reportPost(postId){
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

    fun reportComment(writerId: String, commentId:String){
        var result = false
        viewModelScope.launch {
            PostRepository.reportComment(writerId, commentId){
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

    fun patchComments(imageId: String, body: ChangedComments){
        var result = false
        viewModelScope.launch {
            PostRepository.chageComments(imageId, body){
                result = it
            }
            _ChangeResult.value = result
        }
    }

}