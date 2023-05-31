package com.example.haru.data.retrofit

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.haru.App
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

var isToastShown = false
class NetworkErrorInterceptor(private val app: App) : Interceptor {
    private val handler = Handler(Looper.getMainLooper())

    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (exception: IOException) {
            handler.post {
                if (!isToastShown) {
                    Toast.makeText(app, "서버와 통신이 원활하지 않습니다.", Toast.LENGTH_LONG).apply {
                        show()
                        isToastShown = true
                        // Toast가 사라진 후에 isToastShown을 다시 false로 설정
                        Handler(Looper.getMainLooper()).postDelayed({ isToastShown = false }, this.duration + 100L)
                    }
                }
            }

            // 실패 응답 생성
            return okhttp3.Response.Builder()
                .code(500)  // 아무 HTTP 상태 코드를 넣을 수 있습니다.
                .message(exception.message ?: "Unknown error")
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .request(chain.request())
                .body(
                    ResponseBody.create(
                        "application/json".toMediaTypeOrNull(),
                        "{\"success\": false, \"error\": {\"code\": 500, \"message\": \"server network error\"}}"
                        // 여기에 원하는 에러 메시지를 JSON 형태로 넣어줄 수 있습니다.
                    )
                )
                .build()
        }
    }
}