package com.example.haru.data.repository

import android.media.Image
import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.data.retrofit.RetrofitClient.postService
import com.example.haru.utils.User.id
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileRepository() {
    private val profileService = RetrofitClient.profileService
    val userId = com.example.haru.utils.User.id

    suspend fun editProfile(
        imageFile: MultipartBody.Part,
        name: String,
        introduction: String,
        callback: (user: User) -> Unit
    ) = withContext(
        Dispatchers.IO
    ) {
        try {
            val name = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val introduction = RequestBody.create("text/plain".toMediaTypeOrNull(), introduction)
            val response = profileService.editProfile(
                userId,
                imageFile,
                name,
                introduction
            ).execute()
            val user: User
            val data: GetProfileResponse
            if (response.isSuccessful) {
                Log.d("EDITTAG", "Success to update profile")
                data = response.body()!!
                user = data.data!!

            } else {
                Log.d("EDITTAG", "Fail to update Profile: $response")
                user = User("", "", "", "", 0, 0, 0, false)
            }
            callback(user)
        } catch (e: Exception) {
            Log.e("EDITTAG", "Error occurred while editing profile", e)
        }
    }

    suspend fun editProfileName(
        name: String,
        introduction: String,
        callback: (user: User) -> Unit
    ) = withContext(
        Dispatchers.IO
    ) {
        try {
            val response = profileService.editProfileName(
                userId,
                EditBody(name, introduction)
            ).execute()
            val user: User
            val data: GetProfileResponse
            if (response.isSuccessful) {
                Log.d("EDITTAG", "Success to update profile")
                data = response.body()!!
                user = data.data!!

            } else {
                Log.d("EDITTAG", "Fail to update Profile: $response")
                user = User("", "", "", "", 0, 0, 0, false)
            }
            callback(user)
        } catch (e: Exception) {
            Log.e("EDITTAG", "Error occurred while editing profile", e)
        }
    }


    suspend fun editProfileInit(name: String, haruId: String, callback: () -> Unit) = withContext(
        Dispatchers.IO
    ) {
        try {
            val response = profileService.editProfileInit(
                com.example.haru.utils.User.id,
                ProfileInitBody(name, haruId)
            ).execute()

            val data: UserVerifyResponse
            if (response.isSuccessful) {
                Log.d("EDITTAG", "Success to update profile")
                com.example.haru.utils.User.id = response.body()?.data?.user?.id.toString()
                com.example.haru.utils.User.name = response.body()?.data?.user?.name.toString()
                com.example.haru.utils.User.isPublicAccount =
                    response.body()?.data?.user?.isPublicAccount!!
                com.example.haru.utils.User.haruId = response.body()?.data?.haruId.toString()
                com.example.haru.utils.User.email = response.body()?.data?.email.toString()
                com.example.haru.utils.User.socialAccountType =
                    response.body()?.data?.socialAccountType.toString()
                com.example.haru.utils.User.isPostBrowsingEnabled =
                    response.body()?.data?.isPostBrowsingEnabled!!
                com.example.haru.utils.User.isAllowFeedLike =
                    response.body()?.data?.isAllowFeedLike!!
                com.example.haru.utils.User.isAllowFeedComment =
                    response.body()?.data?.isAllowFeedComment!!
                com.example.haru.utils.User.isAllowSearch = response.body()?.data?.isAllowSearch!!
                com.example.haru.utils.User.createdAt = response.body()?.data?.createdAt.toString()
                callback()
            } else {
                Log.d("EDITTAG", "Fail to update Profile: $response")

            }
        } catch (e: Exception) {
            Log.e("EDITTAG", "Error occurred while editing profile", e)
        }
    }

    suspend fun getUserInfo(targetId: String, callback: (user: User) -> Unit) = withContext(
        Dispatchers.IO
    ) {
        Log.d("TAG", "-----------------$targetId")
        val response = profileService.getUserInfo(
            userId,
            targetId
        ).execute()
        val user: User
        val data: UserResponse
        if (response.isSuccessful) {
            Log.d("TAG", "Success to get User")
            data = response.body()!!
            user = data.data!!
        } else {
            Log.d("TAG", "Fail to get User")
            user = User("", "", "", "", 0, 0, 0, false)
        }
        callback(user)
    }

    suspend fun testName(name: UpdateName, callback: (successData: TestInitResponse?) -> Unit) =
        withContext(Dispatchers.IO) {
            val response = profileService.testName(userId, name).execute()
            var data = response.body()

            val successData = if (response.isSuccessful) {
                Log.d("TAG", "Success to Check Name")
                data
            } else {
                Log.d("TAG", "Fail to Check Name")
                val error = response.errorBody()?.string()
                val gson = Gson()
                data = gson.fromJson(error, TestInitResponse::class.java)
                data
            }
            callback(successData)
        }

    suspend fun testHaruId(
        haruId: UpdateHaruId,
        callback: (successData: TestInitResponse?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = profileService.testHaruId(userId, haruId).execute()
        var data = response.body()

        val successData = if (response.isSuccessful) {
            Log.d("TAG", "Success to Check haruId")
            data
        } else {
            Log.d("TAG", "Fail to Check haruId")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, TestInitResponse::class.java)
            data
        }
        callback(successData)
    }
}