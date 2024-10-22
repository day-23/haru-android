package com.example.haru.data.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.haru.data.api.PostService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.data.retrofit.RetrofitClient.postService
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
    val userId = com.example.haru.utils.User.id

    //게시글 추가
    suspend fun addPost(post: AddPost, callback: (status: Int) -> Unit) = withContext(Dispatchers.IO) {
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
        val call = postService.addPost(userId, images, content, hashTags)
        val response = call.execute()

        if (response.isSuccessful) {
            Log.d("TAG", "Success to post")
            // Deserialize the response body here to get the result (AddPostResponse)
            // and then update postInfo
        } else {
            Log.d("TAG", "Fail to post")
        }
        callback(response.code())
    }

    suspend fun addTemplate(template: Template, callback: (status: Int) -> Unit) = withContext(Dispatchers.IO) {
        val call = postService.addTemplate(userId, template)
        val response = call.execute()

        if (response.isSuccessful) {
            Log.d("TAG", "Success to post")
        } else {
            Log.d("TAG", "Fail to post")
        }
        callback(response.code())
    }

    //게시글 삭제
    suspend fun deletePost(postId: String, callback: (delete: Boolean) -> Unit) = withContext(Dispatchers.IO) {
        val call = postService.deletePost(userId, postId)
        val response = call.execute()
        var delete = false
        val data = response.body()
        if (response.isSuccessful) {
            Log.d("TAG", "Success to delete post")
            delete = true
        } else {
            Log.d("TAG", "Fail to delete post")
        }
        callback(delete)
    }

    //게시글 숨기기
    suspend fun hidePost(postId: String, callback: (hide: Boolean) -> Unit) = withContext(Dispatchers.IO) {
        val call = postService.hidePost(com.example.haru.utils.User.id, postId)
        val response = call.execute()
        var hide = false
        val data = response.body()
        if (response.isSuccessful) {
            Log.d("TAG", "Success to hide post")
            hide = true
        } else {
            Log.d("TAG", "Fail to hide post")
        }
        callback(hide)
    }

    //게시글 신고하기
    suspend fun reportPost(postId: String, callback: (report: Boolean) -> Unit) = withContext(Dispatchers.IO) {
        val call = postService.reportPost(com.example.haru.utils.User.id, postId)
        val response = call.execute()
        var report = false
        val data = response.body()
        if (response.isSuccessful) {
            Log.d("TAG", "Success to report post")
            report = true
        } else {
            Log.d("TAG", "Fail to report post")
        }
        callback(report)
    }
    //친구피드
    suspend fun getFirstFeeds(callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getFirstFeeds(
            userId,
        ).execute()

        val posts: ArrayList<Post>
        val data: PostResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get feeds")
            data = response.body()!!
            posts = data.data!!
        } else{
            Log.d("TAG", "Fail to get feeds")
            posts = arrayListOf()
        }
        callback(posts)
    }

    suspend fun getFeeds(lastCreatedAt:String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getFeeds(
            userId,
            lastCreatedAt
        ).execute()

        val posts: ArrayList<Post>
        val data: PostResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get Feeds")
            data = response.body()!!
            posts = data.data!!
        } else{
            Log.d("TAG", "Fail to get Feeds")
            posts = arrayListOf()
        }
        callback(posts)
    }

    //전체 게시글(둘러보기)
    suspend fun getPost(lastCreatedAt:String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getPosts(
            userId,
            lastCreatedAt
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

    suspend fun getFirstPost(callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getFirstPosts(
            userId,
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

    //둘러보기 태그 선택
    suspend fun getFirstHotPosts(tagId: String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getFirstHotPosts(
            userId,
            tagId
        ).execute()

        var posts: ArrayList<Post> = arrayListOf()
        val data: PostResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get posts")
            data = response.body()!!
            if(!(data.data).isNullOrEmpty())
                posts = data.data
        } else{
            Log.d("TAG", "Fail to get posts")
        }
        callback(posts)
    }

    suspend fun getHotPosts(tagId: String,lastCreatedAt: String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getHotPosts(
            userId,
            tagId,
            lastCreatedAt
        ).execute()

        var posts: ArrayList<Post> = arrayListOf()
        val data: PostResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get posts")
            data = response.body()!!
            if(!(data.data).isNullOrEmpty())
                posts = data.data
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
            userId,
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

    //피드
    suspend fun getMyFeed(targetId:String, lastCreatedAt: String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getMyFeed(
            userId,
            targetId,
            lastCreatedAt
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

    suspend fun getFirstMyFeed(targetId:String, callback: (posts: ArrayList<Post>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getFirstMyFeed(
            userId,
            targetId,
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



    //미디어
    suspend fun getFirstMedia(targetId: String, callback: (medias : MediaResponse) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getFirstMedia(
            userId,
            targetId
        ).execute()

        val medias: MediaResponse

        if(response.isSuccessful){
            Log.d("TAG", "Success to get Medias")
            medias = response.body()!!
        }else{
            Log.d("TAG", "Fail to get Medias")
            medias = MediaResponse(false, arrayListOf(), pagination())
        }
        callback(medias)
    }

    suspend fun getMedia(targetId: String, lastCreatedAt: String, callback: (medias : MediaResponse) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getMedia(
            userId,
            targetId,
            lastCreatedAt
        ).execute()

        val medias: MediaResponse

        if(response.isSuccessful){
            Log.d("TAG", "Success to get Medias")
            medias = response.body()!!
        }else{
            Log.d("TAG", "Fail to get Medias")
            medias = MediaResponse(false, arrayListOf(), pagination())
        }
        callback(medias)
    }

    suspend fun getFirstTagMedia(targetId: String, tagId: String, callback: (medias : MediaResponse) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getFirstTagMedia(
            userId,
            targetId,
            tagId
        ).execute()

        val medias: MediaResponse

        if(response.isSuccessful){
            Log.d("TAG", "Success to get Medias")
            medias = response.body()!!
        }else{
            Log.d("TAG", "Fail to get Medias")
            medias = MediaResponse(false, arrayListOf(), pagination())
        }
        callback(medias)
    }

    suspend fun getTemplates(callback: (templates: ArrayList<Profile>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getTemplates(
            userId
        ).execute()

        val templates: ArrayList<Profile>

        if(response.isSuccessful){
            Log.d("TAG", "Success to get templates")
            templates = response.body()!!.data
        }else{
            Log.d("TAG", "Fail to get templates")
            templates = arrayListOf()
        }
        callback(templates)
    }

    suspend fun getUserTags(targetId: String, callback: (tags : List<Tag>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getUserTags(
            userId,
            targetId
        ).execute()

        val tags: List<Tag>

        if(response.isSuccessful){
            Log.d("TAG", "Success to get Medias")
            tags = response.body()!!.data
        }else{
            Log.d("TAG", "Fail to get Medias")
            tags = listOf()
        }
        callback(tags)
    }

    suspend fun getHotTags(callback: (tags : ArrayList<Tag>) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.getHotTags(
            userId
        ).execute()

        val tags: ArrayList<Tag>

        if(response.isSuccessful){
            Log.d("TAG", "Success to get HotTags")
            tags = ArrayList(response.body()!!.data)
        }else{
            Log.d("TAG", "Fail to get HotTags")
            tags = arrayListOf()
        }
        callback(tags)
    }

    //전체 댓글
    suspend fun getComment(postId: String, imageId: String, lastCreatedAt: String, callback: (comments: ArrayList<Comments>) -> Unit) = withContext(
        Dispatchers.IO){
            Log.d("TAG", "post id recieve-------------- $postId")
            val response = postService.getComments(
                userId,
                postId,
                imageId,
                lastCreatedAt
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

    suspend fun getFirstComment(postId: String, imageId: String, callback: (comments: CommentsResponse) -> Unit) = withContext(
        Dispatchers.IO){
        Log.d("TAG", "post id recieve-------------- $postId")
        val response = postService.getFirstComments(
            userId,
            postId,
            imageId,
        ).execute()

        val comments: CommentsResponse

        if(response.isSuccessful) {
            Log.d("TAG","Success to get comments")
            comments = response.body()!!
        }else{
            Log.d("TAG", "Fail to get comments")
            comments = CommentsResponse(false, arrayListOf(), pagination(0,0,0,0))
        }
        callback(comments)
    }

    suspend fun getFirstTemplateComment(postId: String, callback: (comments: CommentsResponse) -> Unit) = withContext(
        Dispatchers.IO){
        Log.d("TAG", "post id recieve-------------- $postId")
        val response = postService.getFirstTemplateComments(
            userId,
            postId,
        ).execute()

        val comments: CommentsResponse

        if(response.isSuccessful) {
            Log.d("TAG","Success to get comments")
            comments = response.body()!!
        }else{
            Log.d("TAG", "Fail to get comments")
            comments = CommentsResponse(false, arrayListOf(), pagination(0,0,0,0))
        }
        callback(comments)
    }

    suspend fun getTemplateComment(postId: String, lastCreatedAt: String, callback: (comments: CommentsResponse) -> Unit) = withContext(
        Dispatchers.IO){
        Log.d("TAG", "post id recieve-------------- $postId")
        val response = postService.getTemplateComments(
            userId,
            postId,
            lastCreatedAt
        ).execute()

        val comments: CommentsResponse

        if(response.isSuccessful) {
            Log.d("TAG","Success to get comments")
            comments = response.body()!!
        }else{
            Log.d("TAG", "Fail to get comments")
            comments = CommentsResponse(false, arrayListOf(), pagination(0,0,0,0))
        }
        callback(comments)
    }

    suspend fun writeComment(comment: CommentBody, postId: String, imageId:String, callback: (comments: Comments) -> Unit) = withContext(
        Dispatchers.IO){
        Log.d("TAG", "post id recieve-------------- $postId")
        val response = postService.writeComments(
            userId,
            postId,
            imageId,
            comment
        ).execute()

        val comments: Comments
        val data: WriteCommentResponse
        Log.d("Comment", "${response.code()}")
        if(response.isSuccessful) {
            Log.d("TAG","Success to write comments")
            data = response.body()!!
            comments = data.data
        }else{
            Log.d("TAG", "Fail to write comments")
            comments = Comments("",User("","","","",0,0,0,false),"",-1,-1,true,"","")
        }
        callback(comments)
    }

    //템플릿에 댓글달기
    suspend fun writeComment(comment: CommentBody, postId: String, callback: (comments: Comments) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.writeComments(
            userId,
            postId,
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
            comments = Comments("",User("","","","",0,0,0,false),"",-1,-1,true,"","")
        }
        callback(comments)
    }

    suspend fun deleteComment(writerId : String, commentId : String, callback: (deleted: Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.deleteComment(
            writerId,
            commentId,
        ).execute()

        val data: EditCommentResponse
        val deleted: Boolean

        if(response.isSuccessful){
            data = response.body()!!
            deleted = data.success
        }else{
            deleted = false
        }
        callback(deleted)
    }

    suspend fun reportComment(writerId : String, commentId : String, callback: (deleted: Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.reportComment(
            writerId,
            commentId,
        ).execute()

        val data: EditCommentResponse
        val deleted: Boolean

        if(response.isSuccessful){
            data = response.body()!!
            deleted = data.success
        }else{
            deleted = false
        }
        callback(deleted)
    }

    //댓글 한개 수정
    suspend fun chageComment(writerId: String, commentId: String, body: PatchCommentBody, callback: (changed: Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.changeComment(
            writerId,
            commentId,
            body
        ).execute()
        val data: EditCommentResponse
        var changed = false
        if(response.isSuccessful){
            data = response.body()!!
            changed = data.success
        }else{
            changed = false
        }
        callback(changed)
    }

    //댓글 여러개 수정
    suspend fun chageComments(imageId: String, body: ChangedComments, callback: (changed: Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = postService.changeComments(
            com.example.haru.utils.User.id,
            imageId,
            body
        ).execute()
        val data: EditCommentResponse
        var changed = false
        if(response.isSuccessful){
            data = response.body()!!
            changed = data.success
        }else{
            changed = false
        }
        callback(changed)
    }

}