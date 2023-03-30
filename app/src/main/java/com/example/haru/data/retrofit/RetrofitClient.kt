package com.example.haru.data.retrofit

import android.util.Log
import com.example.haru.data.api.ScheduleService
import com.example.haru.data.api.TagService
import com.example.haru.data.api.TodoService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://api.23haru.com/"
//    192.168.0.42

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100,TimeUnit.SECONDS)
        .writeTimeout(100,TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)   // retrofit encoding 될 때 null값이 들어가지 않는 현상 때문에 아래 코드 필요
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .client(okHttpClient)
        .build()

    val todoService: TodoService by lazy {
        retrofit.create(TodoService::class.java)
    }

    val scheduleService: ScheduleService by lazy {
        retrofit.create(ScheduleService::class.java)
    }
    
    val tagService: TagService by lazy {
        retrofit.create(TagService::class.java)
    }
}