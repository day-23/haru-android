package com.example.haru.data.retrofit

import com.example.haru.App
import com.example.haru.data.api.*
import com.example.haru.utils.SharedPrefsManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.23haru.com/v1/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val sharedPreferences = SharedPrefsManager.getSharedPrefs(App.instance)
            val accessToken = sharedPreferences.getString("accessToken", null)

            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(newRequest)
        }
        .addInterceptor(NetworkErrorInterceptor(App.instance))
        .connectTimeout(100, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .build()

    private val okHttpClientNotIncludeAccessToken = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(100, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)   // retrofit encoding 될 때 null값이 들어가지 않는 현상 때문에 아래 코드 필요
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .client(okHttpClient)
        .build()

    private val retrofitKakao = Retrofit.Builder()
        .baseUrl(BASE_URL)   // retrofit encoding 될 때 null값이 들어가지 않는 현상 때문에 아래 코드 필요
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .client(okHttpClientNotIncludeAccessToken)
        .build()

    val alldoService: AllDoService by lazy {
        retrofit.create(AllDoService::class.java)
    }

    val todoService: TodoService by lazy {
        retrofit.create(TodoService::class.java)
    }

    val scheduleService: ScheduleService by lazy {
        retrofit.create(ScheduleService::class.java)
    }

    val tagService: TagService by lazy {
        retrofit.create(TagService::class.java)
    }

    val profileService: ProfileService by lazy {
        retrofit.create(ProfileService::class.java)
    }

    val postService: PostService by lazy {
        retrofit.create(PostService::class.java)
    }

    val categoryService: CategoryService by lazy {
        retrofit.create(CategoryService::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val etcService : EtcService by lazy {
        retrofit.create(EtcService::class.java)
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiServiceKakao : ApiService by lazy {
        retrofitKakao.create(ApiService::class.java)
    }
}