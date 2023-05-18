package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TagRepository() {
    private val tagService = RetrofitClient.tagService

    suspend fun getTag(callback: (tagResponse : TagResponse?) -> Unit) = withContext(Dispatchers.IO) {
        val response = tagService.getTag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data = response.body()

        val tagResponse : TagResponse? = if (response.isSuccessful) {
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
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        content: Content,
        callback: (successData: SuccessFailTag?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.createTag(userId, content).execute()
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
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        tagIdList: TagIdList,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.deleteTagList(userId, tagIdList).execute()
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
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        tagId: String,
        updateTag: TagUpdate,
        callback: (successData: SuccessFailTag?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.updateTag(userId, tagId, updateTag).execute()
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
}