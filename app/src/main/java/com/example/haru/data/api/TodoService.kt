package com.example.haru.data.api

import com.example.haru.data.model.TodoRequest
import com.example.haru.data.model.GetTodoResponse
import com.example.haru.data.model.PostTodoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoService {
    @GET("todo/{userId}/todos")
    fun getTodo(@Path("userId") userId: String) : Call<GetTodoResponse>

    @GET("todo/{userId}/todos/date")
    fun getTodoDates(@Path("userId") userId: String, @Query("startDate") startDate:String, @Query("endDate") endDate:String) : Call<GetTodoResponse>

    @POST("todo/{userId}")
    fun createTodo(@Path("userId") userId: String, @Body todoRequest: TodoRequest) : Call<PostTodoResponse>
}