package com.example.haru.view.auth

import BaseActivity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.haru.App
import com.example.haru.data.model.UserKakaoAuthResponse
import com.example.haru.data.model.UserVerifyResponse
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.databinding.ActivityLoginBinding
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.calendar.CalendarFragment.Companion.TAG
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* 카카오 로그인 버튼 */
        binding.kakaoLoginBtn.setOnClickListener {
            updateKakaoLoginUi()
        }

        /* 기존 로그인 버튼 */
        binding.loginBtn.setOnClickListener {
            /* 로그인 성공 */
            //하드코딩된 값 쓰고 싶으면 여기 넣으면됨
//            User.id = "005224c0-eec1-4638-9143-58cbfc9688c5"
            User.id = "jts"
            User.createdAt = "2023-05-28T00:00:00.000Z"
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //카카오 로그인 과정
    private fun updateKakaoLoginUi() {// 카카오계정으로 로그인 공통 callback 구성
        binding.kakaoLoginBtn.isEnabled = false

        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("LOGIN", "카카오계정으로 로그인 실패", error)
                binding.kakaoLoginBtn.isEnabled = true
            } else if (token != null) {
                Log.i("LOGIN", "카카오계정으로 로그인 성공 ${token.accessToken}")
                validateUserWithServer(token)
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity) { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
                } else if (token != null) {
                    Log.i("LOGIN", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    validateUserWithServer(token)
                }
            }
        } else {
            // Try Kakao account login
            UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
        }
    }



    // 카카오 accessToken을 통해 서버에 해당 유저가 유효한지 확인
    private fun validateUserWithServer(token: OAuthToken) {
        // Call your API
        val call = RetrofitClient.apiService.validateKakaoUser(mapOf("authorization" to "Bearer ${token.accessToken}"))

        // Enqueue the call
        call.enqueue(object : Callback<UserKakaoAuthResponse> {
            override fun onResponse(call: Call<UserKakaoAuthResponse>, response: Response<UserKakaoAuthResponse>) {
                if (response.isSuccessful) {
                    Log.i("LOGIN", "Server login successful, received JWT: ${response.body().toString()}")

                    // Fetch user email from Kakao SDK
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("LOGIN", "Error fetching user email from Kakao", error)
                        } else if (user != null) {
                            //서버에 저장된 사용자 id, name 불러오기
                            User.id = response.body()?.data?.id.toString()
                            User.name = response.body()?.data?.name.toString()

                            Log.d(TAG, "onResponse: UserApiClient ${User.id}")

                            // Save JWT and user's email
                            val sharedPreferences = SharedPrefsManager.getSharedPrefs(App.instance)

                            val accessToken = response.body()?.data?.accessToken.toString()
                            val refreshToken = response.body()?.data?.refreshToken.toString()

                            with (sharedPreferences.edit()) {
                                putString("accessToken", accessToken)
                                putString("refreshToken", refreshToken)
                                commit()
                            }

                            Log.d(TAG, "onResponse username: ${User.name}")

                            val _call = RetrofitClient.apiService.validateUser(
                                mapOf(
                                    "accessToken" to accessToken,
                                    "refreshToken" to refreshToken
                                )
                            )


                            // 서버에 유저정보 데이터 요청
                            _call.enqueue(object : Callback<UserVerifyResponse> {
                                override fun onResponse(
                                    call: Call<UserVerifyResponse>,
                                    response: Response<UserVerifyResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.d(TAG, "splash onResponse: ${response.body()}")

                                        //user 정보 저장
                                        User.id = response.body()?.data?.user?.id.toString()
                                        User.name = response.body()?.data?.user?.name.toString()
                                        User.isPublicAccount =
                                            response.body()?.data?.user?.isPublicAccount!!
                                        User.haruId = response.body()?.data?.haruId.toString()
                                        User.email = response.body()?.data?.email.toString()
                                        User.socialAccountType =
                                            response.body()?.data?.socialAccountType.toString()
                                        User.isPostBrowsingEnabled =
                                            response.body()?.data?.isPostBrowsingEnabled!!
                                        User.isAllowFeedLike =
                                            response.body()?.data?.isAllowFeedLike!!
                                        User.isAllowFeedComment =
                                            response.body()?.data?.isAllowFeedComment!!
                                        User.isAllowSearch = response.body()?.data?.isAllowSearch!!
                                        User.createdAt = response.body()?.data?.createdAt.toString()
                                        User.accessToken =
                                            response.body()?.data?.accessToken.toString()

                                        //새로운 accessToken을 저장한다.
                                        with(sharedPreferences.edit()) {
                                            putString(
                                                "accessToken",
                                                response.body()?.data?.accessToken
                                            )
                                            commit()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<UserVerifyResponse>, t: Throwable) {
                                    Log.d(TAG, "onFailure: ${t.message}")
                                }
                            })

                            // If user is not registered, go to sign up page
                            Log.d(TAG, "자동로그인 onResponse: ${User.name} ${User.email} ${User.id}")

                            if(User.name == ""){
                                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                finish()
                            } else{
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                finish()
                            }
                        }
                    }
                } else {
                    Log.e("LOGIN", "Server login failed with response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserKakaoAuthResponse>, t: Throwable) {
                // i want to know server response json
                Log.e("LOGIN", "Server login failed with error: ${t.message}")
                binding.kakaoLoginBtn.isEnabled = true
                updateKakaoLoginUi()
            }
        })
    }
}