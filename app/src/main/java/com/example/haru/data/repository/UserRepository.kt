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
    val userId = com.example.haru.utils.User.id
    suspend fun getUser(userId: Int): User {
        return userService.getUser(userId)
    }
    suspend fun requestFriend(body: Followbody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.requestFriend(
            userId,
            body
        ).execute()
        val result: FollowResponse
        val data: Boolean
        if(response.isSuccessful){
            result = response.body()!!
            data = result.success
        } else{
            data = false
        }
        callback(data)
    }

    suspend fun requestunFriend(body: UnFollowbody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.requestUnFriend(
            userId,
            body
        ).execute()
        val result: FollowResponse
        val data: Boolean
        if(response.isSuccessful){
            result = response.body()!!
            data = result.success
        } else{
            data = false
        }
        callback(data)
    }

    suspend fun requestDelFriend(body: UnFollowbody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.requestUnFriend(
            userId,
            body
        ).execute()
        val result: FollowResponse
        val data: Boolean
        if(response.isSuccessful){
            result = response.body()!!
            data = result.success
        } else{
            data = false
        }
        callback(data)
    }

    suspend fun requestFriendsList(targetId:String, lastCreatedAt:String, callback: (friends : FriendsResponse) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.getFriendsList(
            userId,
            targetId,
            lastCreatedAt
        ).execute()
        val result : FriendsResponse

        if(response.isSuccessful){
            result = response.body()!!
        }else{
            result = FriendsResponse(false, arrayListOf(), pagination())
        }
        callback(result)
    }

    suspend fun requestFirstFriendsList(targetId:String, callback: (friends : FriendsResponse) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.getFirstFriendsList(
            userId,
            targetId,
            "1"
        ).execute()
        val result : FriendsResponse

        if(response.isSuccessful){
            result = response.body()!!
        }else{
            result = FriendsResponse(false, arrayListOf(), pagination())
        }
        callback(result)
    }
}
