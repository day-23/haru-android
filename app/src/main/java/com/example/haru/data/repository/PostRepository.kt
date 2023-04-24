package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.api.PostService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository() {
    private val postService = RetrofitClient.postService

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

}