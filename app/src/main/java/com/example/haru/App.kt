package com.example.haru

import android.app.Application
import android.content.Context
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class App : Application() {
    companion object {
        lateinit var instance: App
            private set

        fun context(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        /* 디버그 설정을 위한 key url */
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}