package com.example.haru.data.repository

import android.media.Image
import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.data.retrofit.RetrofitClient.postService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileRepository() {
    private val profileService = RetrofitClient.profileService

    suspend fun editProfile(imageFile: MultipartBody.Part, name:String, introduction:String ,callback:(user : User) -> Unit) = withContext(
        Dispatchers.IO) {
        try {
            val name = RequestBody.create(MediaType.parse("text/plain"), name)
            val introduction = RequestBody.create(MediaType.parse("text/plain"), introduction)
            val response = profileService.editProfile(
                "jts",
                imageFile,
                name,
                introduction
            ).execute()
            val user: User
            val data: GetProfileResponse
            if (response.isSuccessful) {
                Log.d("EDITTAG", "Success to update profile")
                data = response.body()!!
                user = data.data

            } else {
                Log.d("EDITTAG", "Fail to update Profile: $response")
                user = User("","","","", false,0,0,0)
            }
            callback(user)
        }
        catch (e: Exception) {
            Log.e("EDITTAG", "Error occurred while editing profile", e)
        }
    }

    suspend fun editProfileName(name:String, introduction:String ,callback:(user : User) -> Unit) = withContext(
        Dispatchers.IO) {
        try {
            val response = profileService.editProfileName(
                "jts",
                EditBody(name, introduction)
            ).execute()
            val user: User
            val data: GetProfileResponse
            if (response.isSuccessful) {
                Log.d("EDITTAG", "Success to update profile")
                data = response.body()!!
                user = data.data

            } else {
                Log.d("EDITTAG", "Fail to update Profile: $response")
                user = User("","","","", false,0,0,0)
            }
            callback(user)
        }
        catch (e: Exception) {
            Log.e("EDITTAG", "Error occurred while editing profile", e)
        }
    }

    suspend fun getUserInfo(targetId: String, callback: (user:User) -> Unit) = withContext(
        Dispatchers.IO){
        Log.d("TAG", "-----------------$targetId")
        val response = profileService.getUserInfo(
            "jts",
            targetId
        ).execute()
        val user: User
        val data: UserResponse
        if(response.isSuccessful){
            Log.d("TAG", "Success to get User")
            data = response.body()!!
            user = data.data
        } else{
            Log.d("TAG", "Fail to get User")
            user = User("","","","",false,0,0,0)
        }
        callback(user)
    }
}