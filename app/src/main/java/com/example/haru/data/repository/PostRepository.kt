package com.example.haru.data.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.haru.data.api.PostService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostRepository() {
    private val postService = RetrofitClient.postService

    //게시글 추가
    suspend fun addPost(post : AddPost, callback: (postInfo: SendPost) -> Unit) = withContext(Dispatchers.IO){
        val content = MultipartBody.Part.createFormData("content", post.content)
        val hashtags = MultipartBody.Part.createFormData(
            "hashtags",
            TextUtils.join(",", post.hashtags)
        )
        val response = RetrofitClient.postService.addPost(
            "jts",
            post.images,
            content,
            hashtags
        ).execute()

        val result: AddPostResponse
        val postInfo: SendPost
        if(response.isSuccessful) {
            Log.d("TAG", "Success to post")
            result = response.body()!!
            postInfo = result.data!!
        } else{
            Log.d("TAG", "Fail to post")
            postInfo = SendPost()
        }
        callback(postInfo)
    }

    //전체 게시글
    suspend fun getPost(page:String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getPosts(
            "jts",
            page
        ).execute()
        val posts: ArrayList<Post>
        val data: PostResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get posts")
            data = response.body()!!
            posts = data.data!!
        } else{
            Log.d("TAG", "Fail to get posts")
            posts = arrayListOf()
        }
        callback(posts)
    }

    //좋아요
    suspend fun postLike(id:String, callback: (liked: Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.postLike(
            "jts",
            id
        ).execute()
        val data: LikeResponse
        val liked: Boolean
        if (response.isSuccessful) {
            Log.d("LIKED", "Success to like")
            data = response.body()!!
            liked = data.response!!
        } else{
            Log.d("LIKED", "Fail to like")
            liked = false
        }
        callback(liked)
    }

    //내 피드
    suspend fun getMyFeed(page:String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getMyFeed(
            "jts",
            "jts",
            page
        ).execute()
        val posts: ArrayList<Post>
        val data: PostResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get posts")
            data = response.body()!!
            posts = data.data!!
        } else{
            Log.d("TAG", "Fail to get posts")
            posts = arrayListOf()
        }
        callback(posts)
    }

}