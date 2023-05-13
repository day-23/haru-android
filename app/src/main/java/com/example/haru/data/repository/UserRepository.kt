package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.api.UserService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.data.retrofit.RetrofitClient.profileService
import com.example.haru.data.retrofit.RetrofitClient.userService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository() {
    private val userService = RetrofitClient.userService
    suspend fun getUser(userId: Int): User {
        return userService.getUser(userId)
    }
    suspend fun requestFollowing(body: Followbody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.requestFollowing(
            "jts",
            body
        ).execute()
        val result: FollowResponse
        val data: Boolean
        if(response.isSuccessful){
            result = response.body()!!
            data = result.response!!
        } else{
            data = false
        }
        callback(data)
    }

    suspend fun requestunFollowing(body: UnFollowbody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.requestUnFollowing(
            "jts",
            body
        ).execute()
        val result: FollowResponse
        val data: Boolean
        if(response.isSuccessful){
            result = response.body()!!
            data = result.response
        } else{
            data = false
        }
        callback(data)
    }
}
