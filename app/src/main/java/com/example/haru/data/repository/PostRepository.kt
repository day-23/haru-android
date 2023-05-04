package com.example.haru.data.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.haru.data.api.PostService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import java.io.File
import java.util.UUID

class PostRepository() {
    private val postService = RetrofitClient.postService

    //게시글 추가
    suspend fun addPost(post: AddPost, callback: (postInfo: SendPost) -> Unit) = withContext(Dispatchers.IO) {
        // Prepare the request data
        val content = RequestBody.create(MediaType.parse("text/plain"), "이미지 업로드 성공! post.content 여기 수정해야됨")

        val hashTags = post.hashtags.mapIndexed { index, hashtag ->
            "hashTags[$index]" to RequestBody.create(MediaType.parse("text/plain"), hashtag)
        }.toMap()

        val images = post.images.map { imagePart ->
            MultipartBody.Part.createFormData("images", imagePart.headers()?.get("Content-Disposition")?.substringAfter("filename=\"")?.substringBefore("\""), imagePart.body())
        }

        // Call the addPost function from the PostService
        val call = postService.addPost("lmj", images, content, hashTags)
        val response = call.execute()

        val postInfo = SendPost()
        if (response.isSuccessful) {
            Log.d("TAG", "Success to post")
            // Deserialize the response body here to get the result (AddPostResponse)
            // and then update postInfo
        } else {
            Log.d("TAG", "Fail to post")
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