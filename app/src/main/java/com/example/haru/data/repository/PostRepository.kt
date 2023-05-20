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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import java.io.File
import java.util.UUID

class PostRepository() {
    private val postService = RetrofitClient.postService

    //게시글 추가
    suspend fun addPost(post: AddPost, callback: (postInfo: SendPost) -> Unit) = withContext(Dispatchers.IO) {
        // Prepare the request data
        val content = RequestBody.create("text/plain".toMediaTypeOrNull(), post.content)

        val hashTags = post.hashtags.mapIndexed { index, hashtag ->
            "hashTags[$index]" to RequestBody.create("text/plain".toMediaTypeOrNull(), hashtag)
        }.toMap()

        val images = post.images.map { imagePart ->
            MultipartBody.Part.createFormData("images", imagePart.headers?.get("Content-Disposition")?.substringAfter("filename=\"")?.substringBefore("\""),
                imagePart.body
            )
        }

        // Call the addPost function from the PostService
        val call = postService.addPost("jts", images, content, hashTags)
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
    suspend fun getMyFeed(page:String, targetId:String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getMyFeed(
            "jts",
            targetId,
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

    //전체 댓글
    suspend fun getComment(postId: String, callback: (comments: ArrayList<Comments>) -> Unit) = withContext(
        Dispatchers.IO){
            Log.d("TAG", "post id recieve-------------- $postId")
            val response = postService.getComments(
                "jts",
                postId
            ).execute()

            val comments: ArrayList<Comments>
            val data: CommentsResponse

            if(response.isSuccessful) {
                Log.d("TAG","Success to get comments")
                data = response.body()!!
                comments = data.data
            }else{
                Log.d("TAG", "Fail to get comments")
                comments = arrayListOf()
            }
            callback(comments)
        }

    //전체 댓글
    suspend fun writeComment(comment: CommentBody, postId: String, imageId:String, callback: (comments: Comments) -> Unit) = withContext(
        Dispatchers.IO){
        Log.d("TAG", "post id recieve-------------- $postId")
        val response = postService.writeComments(
            "jts",
            postId,
            imageId,
            comment
        ).execute()

        val comments: Comments
        val data: WriteCommentResponse

        if(response.isSuccessful) {
            Log.d("TAG","Success to write comments")
            data = response.body()!!
            comments = data.data
        }else{
            Log.d("TAG", "Fail to write comments")
            comments = Comments("",User("","","","",false,0,0,0),"",-1,-1,"","")
        }
        callback(comments)
    }

}