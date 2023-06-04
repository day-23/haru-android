package com.example.haru.data.api

import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface CategoryService {
    @GET("category/{userId}/categories")
    fun getCategories(@Path("userId") userId: String) : Call<CategoryResponse>

    @POST("category/{userId}")
    fun postCategory(@Path("userId") userId: String, @Body postCategory: PostCategory) : Call<PostCategoryResponse>

    @PATCH("category/{userId}/{categoryId}")
    fun updateCategory(@Path("userId") userId: String, @Path("categoryId") categoryId:String, @Body category: UpdateCategory) : Call<SuccessFail>

    @HTTP(method = "DELETE", path="category/{userId}/categories", hasBody = true)
    fun deleteCategory(@Path("userId") userId: String, @Body categoryIds: CategoryDelete) : Call<SuccessFail>

    @PATCH("category/{userId}/order/categories")
    fun selectedCategories(@Path("userId") userId: String, @Body categoryBody: CategoriesUpdate): Call<SuccessFail>
}