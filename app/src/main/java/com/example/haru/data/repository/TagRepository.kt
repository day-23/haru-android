package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.Tag
import com.example.haru.data.model.TagResponse
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagRepository() {
    private val tagService = RetrofitClient.tagService

    suspend fun getTag(): List<Tag> = withContext(Dispatchers.IO){
        val response = tagService.getTag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data: TagResponse
        var tagData: List<Tag>

        if (response.isSuccessful){
            Log.d("TAG", "Success to get tags")
            data = response.body()!!
            tagData = data.data
        } else {
            Log.d("TAG", "Fail to get tags")
            tagData = emptyList()
        }
        tagData
    }
}