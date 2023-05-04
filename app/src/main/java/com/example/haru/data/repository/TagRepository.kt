package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagRepository() {
    private val tagService = RetrofitClient.tagService

    suspend fun getTag(): List<Tag> = withContext(Dispatchers.IO) {
        val response = tagService.getTag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data: TagResponse
        var tagData: List<Tag>
        Log.d("20191627", "여기는 getTag")

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get tags")
            data = response.body()!!
            tagData = data.data
        } else {
            Log.d("TAG", "Fail to get tags")
            tagData = emptyList()
        }
        tagData
    }

    suspend fun createTag(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        content: Content,
        callback: (successData : SuccessFailTag) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = tagService.createTag(userId, content).execute()
        val data = response.body()!!

        val successData : SuccessFailTag = if (response.isSuccessful){
            Log.d("TAG", "Success to Create Tag")
            data
        } else{
            Log.d("TAG", "Fail to Create Tag")
            data
        }
        callback(successData)
    }
}