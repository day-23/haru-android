package com.example.haru.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.haru.App
import com.example.haru.R
import com.example.haru.data.model.UserVerifyResponse
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.utils.User
import com.example.haru.view.auth.LoginActivity
import com.example.haru.view.calendar.CalendarFragment.Companion.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_DURATION = 2200L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        // Check for JWT and email in Shared Preferences
        val sharedPreferences = SharedPrefsManager.getSharedPrefs(App.instance)
        val accessToken = sharedPreferences.getString("accessToken", null)
        val refreshToken = sharedPreferences.getString("refreshToken", null)


//        Log.d(TAG, "splasy onCreate: accessToken: $accessToken")

        // If JWT and email exist, validate with server
        if (accessToken != null && refreshToken != null) {
            // make call object use accessToken, refreshToken
            val call = RetrofitClient.apiService.validateUser(
                mapOf(
                    "accessToken" to accessToken,
                    "refreshToken" to refreshToken
                )
            )

            call.enqueue(object : Callback<UserVerifyResponse> {
                override fun onResponse(
                    call: Call<UserVerifyResponse>,
                    response: Response<UserVerifyResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "splash onResponse: ${response.body()}")

                        //user 정보 저장
                        User.id = response.body()?.data?.user?.id.toString()
                        User.name = response.body()?.data?.user?.name.toString()
                        User.isPublicAccount = response.body()?.data?.user?.isPublicAccount!!
                        User.haruId = response.body()?.data?.haruId.toString()
                        User.email = response.body()?.data?.email.toString()
                        User.socialAccountType = response.body()?.data?.socialAccountType.toString()
                        User.isPostBrowsingEnabled = response.body()?.data?.isPostBrowsingEnabled!!
                        User.isAllowFeedLike = response.body()?.data?.isAllowFeedLike!!
                        User.isAllowFeedComment = response.body()?.data?.isAllowFeedComment!!
                        User.isAllowSearch = response.body()?.data?.isAllowSearch!!
                        User.createdAt = response.body()?.data?.createdAt.toString()
                        User.accessToken = response.body()?.data?.accessToken.toString()

                        //새로운 accessToken을 저장한다.
                        with (sharedPreferences.edit()) {
                            putString("accessToken", response.body()?.data?.accessToken)
                            commit()
                        }

                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d(TAG, "onResponse Fail: ${response.errorBody()}")

                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            finish()
                        }, SPLASH_DURATION)
                    }
                }

                override fun onFailure(call: Call<UserVerifyResponse>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                }
            })
        } else{

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }, SPLASH_DURATION)
        }
    }
}
