package com.example.haru.data.api

import com.example.haru.data.model.CategoryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryService {
    @GET("category/{userId}/categories")
    fun getCategories(@Path("userId") userId: String) : Call<CategoryResponse>

}