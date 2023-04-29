package com.example.haru.data.repository

import android.media.Image
import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class ProfileRepository() {
    private val profileService = RetrofitClient.profileService

    suspend fun editProfile(imageFile: MultipartBody.Part, callback:(profile : Profile) -> Unit) = withContext(
        Dispatchers.IO) {
        val response = profileService.editProfile(
            "jts",
            imageFile,
        ).execute()
        val profile: Profile
        val data: GetProfileResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to update profile")
            data = response.body()!!
            profile = data.data

        } else {
            Log.d("TAG", "Fail to update Profile: $response")
            profile = Profile("","","","")
        }
        callback(profile)
    }

    suspend fun getProfile(callback: (profile: Profile) -> Unit) = withContext(
        Dispatchers.IO){
        val response = profileService.getProfile(
            "jts",
        ).execute()
        val profile: Profile
        val data: ProfileListResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get profile")
            data = response.body()!!
            profile = data.data[0]
        } else{
            Log.d("TAG", "Fail to get Profile")
            profile = Profile("","","","")
        }
        callback(profile)
    }
}