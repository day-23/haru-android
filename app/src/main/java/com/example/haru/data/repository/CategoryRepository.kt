package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.api.CategoryService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.utils.Tags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository {
    private val CategoryService = RetrofitClient.categoryService

    suspend fun getCategories(callback:(categoryData : List<Category>) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.getCategories(
            com.example.haru.utils.User.id,
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
        ).execute()

        val data: CategoryResponse
        val categoryData: List<Category>

        if (response.isSuccessful) {
            Log.d(Tags.log, "Success to get categories")
            data = response.body()!!
            categoryData = data.data
        } else {
            Log.d(Tags.log, "Fail to get categories")
            categoryData = emptyList()
        }
        callback(categoryData)
    }

    suspend fun postCategory(content:String, color:String, callback:(category: Category?) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.postCategory(
                com.example.haru.utils.User.id,
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
            PostCategory(content, color)
        ).execute()

        val data: PostCategoryResponse
        val categoryData: Category

        if (response.isSuccessful) {
            Log.d(Tags.log, "Success to get categories")
            data = response.body()!!
            categoryData = data.data!!
            callback(categoryData)
        } else {
            Log.d(Tags.log, "Fail to get categories")
            callback(null)
        }
    }

    suspend fun deleteCategory(categoryId: String, callback:(Success: Boolean) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.deleteCategory(
                com.example.haru.utils.User.id,
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
            CategoryDelete(listOf(categoryId))
        ).execute()

        if (response.isSuccessful) {
            Log.d(Tags.log, "Success to delete category")
            callback(true)
        } else {
            Log.d(Tags.log, "Fail to delete category")
            callback(false)
        }
    }

    suspend fun updateCategory(category:Category, callback:(Success: Boolean) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.updateCategory(
                com.example.haru.utils.User.id,
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
            category.id,
            UpdateCategory(
                category.content,
                category.color,
                category.isSelected
            )
        ).execute()

        if (response.isSuccessful) {
            Log.d(Tags.log, "Success to update category")
            callback(true)
        } else {
            Log.d(Tags.log, "Fail to update category")
            callback(false)
        }
    }

    suspend fun selectedCategories(categoryBody:CategoriesUpdate, callback:(Success: Boolean) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = CategoryService.selectedCategories(
            com.example.haru.utils.User.id,
//            "005224c0-eec1-4638-9143-58cbfc9688c5"
            categoryBody
        ).execute()

        if (response.isSuccessful) {
            Log.d(Tags.log, "Success to update category")
            callback(true)
        } else {
            Log.d(Tags.log, "Fail to update category")
            callback(false)
        }
    }
}