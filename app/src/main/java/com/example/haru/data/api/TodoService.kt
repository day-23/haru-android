package com.example.haru.data.api

import com.example.haru.data.model.TodoRequest
import com.example.haru.data.model.GetMainTodoResponse
import com.example.haru.data.model.GetTodoByTag
import com.example.haru.data.model.PostTodoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoService {
    @GET("todo/{userId}/todos/main")
    fun getTodoMain(@Path("userId") userId: String) : Call<GetMainTodoResponse>

    @POST("todo/{userId}")
    fun createTodo(@Path("userId") userId: String, @Body todoRequest: TodoRequest) : Call<PostTodoResponse>

    @GET("todo/{userId}/todos/tag")
    fun getTodoByTag(@Path("userId") userId: String, @Query("tagId") tagId: String) : Call<GetTodoByTag>
}