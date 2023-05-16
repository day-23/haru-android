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

    suspend fun postCategory(content:String, color:String, callback:(category: Category?) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.postCategory(
            "ysr",
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
            PostCategory(content, color)
        ).execute()

        val data: PostCategoryResponse
        val categoryData: Category

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get categories")
            data = response.body()!!
            categoryData = data.data!!
            callback(categoryData)
        } else {
            Log.d("TAG", "Fail to get categories")
            callback(null)
        }
    }

    suspend fun deleteCategory(categoryId: String, callback:(Success: Boolean) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.deleteCategory(
            "ysr",
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
            CategoryDelete(listOf(categoryId))
        ).execute()

        if (response.isSuccessful) {
            Log.d("TAG", "Success to delete category")
            callback(true)
        } else {
            Log.d("TAG", "Fail to delete category")
            callback(false)
        }
    }

    suspend fun updateCategory(category:Category, callback:(Success: Boolean) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.updateCategory(
            "ysr",
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
            category.id,
            UpdateCategory(
                category.content,
                category.color,
                category.isSelected
            )
        ).execute()

        if (response.isSuccessful) {
            Log.d("TAG", "Success to update category")
            callback(true)
        } else {
            Log.d("TAG", "Fail to update category")
            callback(false)
        }
    }
}