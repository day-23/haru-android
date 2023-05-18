package com.example.haru

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk

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

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}