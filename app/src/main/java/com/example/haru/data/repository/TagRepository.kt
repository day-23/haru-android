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

    suspend fun getTag(callback: (tagData: List<Tag>?) -> Unit) = withContext(Dispatchers.IO) {
        val response = tagService.getTag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data: TagResponse?
        val tagData: List<Tag>?
        Log.d("20191627", "여기는 getTag")

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get tags")
            data = response.body()
            tagData = data?.data
        } else {
            Log.d("TAG", "Fail to get tags")
            tagData = response.body()?.data
        }
        callback(tagData)
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
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFailTag::class.java)
            Log.d("TAG", "Fail to Create Tag")
            Log.e("20191627", "error - body : $data")
            data
        }
        callback(successData)
    }

//    suspend fun createTagList(
//        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
//        contents: ContentList,
//        callback: (successData: SuccessFailTagList) -> Unit
//    ) = withContext(Dispatchers.IO) {
//        val response = tagService.createTagList(userId, contents).execute()
//        val data = response.body()!!
//
//        val successData: SuccessFailTagList = if (response.isSuccessful) {
//            Log.d("TAG", "Success to Create TagList")
//            data
//        } else {
//            Log.d("TAG", "Fail to Create TagList")
//            data
//        }
//        callback(successData)
//    }

    suspend fun deleteTagList(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        tagIdList: TagIdList,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.deleteTagList(userId, tagIdList).execute()
        val data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete TagList")
            data
        } else {
            Log.d("TAG", "Fail to Delete TagList")
            data
        }
        callback(data)
    }

    suspend fun updateTag(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        tagId: String,
        updateTag: TagUpdate,
        callback: (successData: SuccessFailTag?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.updateTag(userId, tagId, updateTag).execute()
        val data = response.body()

        val successData: SuccessFailTag? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Tag")
            data
        } else {
            Log.d("TAG", "Fail to Update Tag")
            data
        }
        callback(data)
    }
}