package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.api.CategoryService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository {
    private val CategoryService = RetrofitClient.categoryService

    suspend fun getCategories(callback:(categoryData : List<Category>) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.getCategories(
            "ysr",
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
        ).execute()

        val data: CategoryResponse
        val categoryData: List<Category>

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get categories")
            data = response.body()!!
            categoryData = data.data
        } else {
            Log.d("TAG", "Fail to get categories")
            categoryData = emptyList()
        }
        callback(categoryData)
    }
}