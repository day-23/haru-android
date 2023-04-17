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
            "dd62593d-161b-45cb-9534-346cd5b5e556",
            imageFile,
        ).execute()
        val profile: Profile
        val data: GetProfileResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to update profile")
            data = response.body()!!
            profile = data.data

        } else {
            Log.d("TAG", "Fail to update Profile")
            profile = Profile("","","","")
        }
        callback(profile)
    }

    suspend fun getProfile()
}