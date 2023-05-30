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

    suspend fun deleteUserAccount(callback: (successFail: SuccessFail?) -> Unit) = withContext(Dispatchers.IO) {
        val response = userService.deleteUserAccount(UserObject.id).execute()
        var data = response.body()

        val successFail : SuccessFail? = if (response.isSuccessful) {
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
}
