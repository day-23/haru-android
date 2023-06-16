package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.haru.utils.User as UserObject

class UserRepository() {
    private val userService = RetrofitClient.userService
    val userId = com.example.haru.utils.User.id

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
    suspend fun requestunFriend(targetId: String, body: UnFollowbody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.requestUnFriend(
            targetId,
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
    suspend fun requestDelFriend(body: DelFriendBody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.requestDelFriend(
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

    //친구 수락
    suspend fun acceptFriend(body: Friendbody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.acceptFriend(
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

    //유저 차단
    suspend fun blockUser(body: BlockBody, callback: (result : Boolean) -> Unit) = withContext(
        Dispatchers.IO){
        val response = userService.blockUser(
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
        Dispatchers.IO) {
        val response = userService.getFirstRequestList(userId, "1").execute()
        val result: FriendsResponse

        if (response.isSuccessful) {
            result = response.body()!!
        } else {
            result = FriendsResponse(false, arrayListOf(), pagination())
        }
        callback(result)
    }

    suspend fun updateUserInfo(body: Any, callback: (successFail: SuccessFail?) -> Unit) =
        withContext(Dispatchers.IO) {
            val response = userService.updateUserInfo(UserObject.id, body).execute()
            var data = response.body()

            val successFail: SuccessFail? = if (response.isSuccessful) {
                Log.d("TAG", "Success to UpdateUserInfo")
                data
            } else {
                Log.d("TAG", "Fail to UpdateUserInfo")
                val error = response.errorBody()?.string()
                val gson = Gson()
                data = gson.fromJson(error, SuccessFail::class.java)
                data
            }
            callback(successFail)
        }

    suspend fun deleteUserAccount(callback: (successFail: SuccessFail?) -> Unit) =
        withContext(Dispatchers.IO) {
            val response = userService.deleteUserAccount(UserObject.id).execute()
            var data = response.body()

            val successFail: SuccessFail? = if (response.isSuccessful) {
                Log.d("20191627", "Success to DeleteAccount")
                data
            } else {
                Log.d("20191627", "Fail to DeleteAccount")
                val error = response.errorBody()?.string()
                val gson = Gson()
                data = gson.fromJson(error, SuccessFail::class.java)
                data
            }
            callback(successFail)
        }

    suspend fun getSearchUserInfo(targetId: String, callback: (it: UserResponse?) -> Unit) = withContext(Dispatchers.IO){
        val response = userService.getSearchUserInfo(UserObject.id, targetId).execute()
        var data = response.body()

        val it = if (response.isSuccessful){
            Log.d("20191627", "Success to getSearch UserInfo")
            data
        } else {
            Log.d("20191627", "Fail to getSearch UserInfo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, UserResponse::class.java)
            data
        }
        callback(it)
    }
}


