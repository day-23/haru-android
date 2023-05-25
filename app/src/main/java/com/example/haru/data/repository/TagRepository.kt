package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.utils.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TagRepository() {
    private val tagService = RetrofitClient.tagService

    suspend fun getTag(callback: (tagResponse: TagResponse?) -> Unit) =
        withContext(Dispatchers.IO) {
            val response = tagService.getTag(User.id).execute()
            var data = response.body()

            val tagResponse: TagResponse? = if (response.isSuccessful) {
                Log.d("TAG", "Success to get tags")
                data
            } else {
                Log.d("TAG", "Fail to get tags")
                val error = response.errorBody()?.string()
                val gson = Gson()
                data = gson.fromJson(error, TagResponse::class.java)
                data
            }
            callback(tagResponse)
        }

    suspend fun createTag(
        content: Content,
        callback: (successData: SuccessFailTag?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.createTag(User.id, content).execute()
        var data = response.body()

        val successData: SuccessFailTag? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Create Tag")
            data
        } else {
            Log.d("TAG", "Fail to Create Tag")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFailTag::class.java)
            data
        }
        callback(successData)
    }

    suspend fun deleteTagList(
        tagIdList: TagIdList,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.deleteTagList(User.id, tagIdList).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete TagList")
            data
        } else {
            Log.d("TAG", "Fail to Delete TagList")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun updateTag(
        tagId: String,
        updateTag: TagUpdate,
        callback: (successData: SuccessFailTag?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.updateTag(User.id, tagId, updateTag).execute()
        var data = response.body()

        val successData: SuccessFailTag? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Tag")
            data
        } else {
            Log.d("TAG", "Fail to Update Tag")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFailTag::class.java)
            data
        }
        callback(successData)
    }

    suspend fun getRelatedTodoCount(
        tagId: String,
        callback: (successData : SuccessFailCount?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.getRelatedTodoCount(User.id, tagId).execute()
        var data = response.body()

        val successData : SuccessFailCount? = if (response.isSuccessful){
            Log.d("TAG", "Success to Get RelatedTodo Count")
            data
        } else {
            Log.d("TAG", "Fail to Get RelatedTodo Count")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFailCount::class.java)
            data
        }
        callback(successData)
    }
}