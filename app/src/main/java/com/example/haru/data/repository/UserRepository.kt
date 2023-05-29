package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.api.UserService
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.data.retrofit.RetrofitClient.profileService
import com.example.haru.data.retrofit.RetrofitClient.userService
import com.kakao.sdk.talk.model.Friend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository() {
    private val userService = RetrofitClient.userService
    val userId = com.example.haru.utils.User.id
    suspend fun getUser(userId: Int): User {
        return userService.getUser(userId)
    }
    //친구 요청
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
    //친구 요청 취소
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
    //친구 삭제 요청
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

    //친구리스트 요청
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

    //친구요청 목록 요청
    suspend fun getRequestList(userId: String, lastCreatedAt: String, callback: (friends: FriendsResponse) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.getRequestList(userId, lastCreatedAt).execute()
        val result : FriendsResponse

        if(response.isSuccessful){
            result = response.body()!!
        }else{
            result = FriendsResponse(false, arrayListOf(), pagination())
        }
        callback(result)
    }

    suspend fun getFirstRequestList(userId: String, callback: (friends: FriendsResponse) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.getFirstRequestList(userId, "1").execute()
        val result : FriendsResponse

        if(response.isSuccessful){
            result = response.body()!!
        }else{
            result = FriendsResponse(false, arrayListOf(), pagination())
        }
        callback(result)
    }
}
