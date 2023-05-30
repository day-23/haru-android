package com.example.haru.view.etc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import com.example.haru.R
import com.example.haru.data.model.AlldoBodyCategory
import com.example.haru.data.model.AlldoData
import com.example.haru.data.model.PostAllDoResponse
import com.example.haru.data.repository.AllDoRepository
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.view.MainActivity
import com.example.haru.viewmodel.CalendarViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlarmWorker : BroadcastReceiver(){
    companion object{
        var lifecycle: LifecycleOwner? = null
    }

    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val userId = intent?.getStringExtra("userId")
        Log.d("알람","알람 받음")
        notificationManager = context!!.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        deliverNotification(context, userId)
    }

    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "notification_channel", // 채널의 아이디
                "오늘의 하루 알람", // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
                /*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
                 */
            )

            notificationChannel.enableLights(true) // 불빛
            notificationChannel.lightColor = Color.RED // 색상
            notificationChannel.enableVibration(true) // 진동 여부
            notificationChannel.description = "하루" //채널정보

            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }

    private fun deliverNotification(context: Context, userId: String?){
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0, // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_IMMUTABLE
        )

        Thread(Runnable {
            if(userId != null) {
                val calendar = Calendar.getInstance()
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA)

                val allDoService = RetrofitClient.alldoService

                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                val startDate = format.format(calendar.time)

                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 55)
                }

                val endDate = format.format(calendar.time)

                val response = allDoService.getAllDoDates(
                    userId,
                    AlldoBodyCategory(startDate, endDate),
                ).execute()

                val data: PostAllDoResponse
                val alldoData: AlldoData?

                Log.d("알람", response.toString())

                if (response.isSuccessful) {
                    Log.d("알람", "Success to get all datas")
                    data = response.body()!!
                    alldoData = data.data
                } else {
                    Log.d("알람", "Fail to get all datas")
                    alldoData = null
                }

                if (alldoData != null) {


                    Log.d("알람", alldoData.toString())

                    val builder = NotificationCompat.Builder(context, "notification_channel")
                        .setSmallIcon(R.drawable.app_icon_foreground) // 아이콘
                        .setContentTitle("오늘의 하루") // 제목
                        .setContentText("") // 내용
                        .setContentIntent(contentPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)

                    notificationManager.notify(0, builder.build())
                } else {
                    Log.d("알람", "null")
                }
            }
        }).start()
    }
}