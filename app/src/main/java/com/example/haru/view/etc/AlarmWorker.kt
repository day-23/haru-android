package com.example.haru.view.etc

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.haru.R
import com.example.haru.data.model.AlldoBodyCategory
import com.example.haru.data.model.AlldoData
import com.example.haru.data.model.PostAllDoResponse
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.utils.FormatDate
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class AlarmWorker : BroadcastReceiver(){
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("알람","알람 받음")
        val userId = intent?.getStringExtra("userId")
        notificationManager = context!!.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        deliverNotification(context, userId)
    }

    fun date_comparison(first_date: Date, second_date: Date): Int{
        first_date.hours = 0
        first_date.minutes = 0
        first_date.seconds = 0

        second_date.hours = 0
        second_date.minutes = 0
        second_date.seconds = 0

        return first_date.compareTo(second_date)
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

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent2 = Intent(context, AlarmWorker::class.java)

        if (userId != "") {
            intent2.putExtra("userId", userId)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent2,
                PendingIntent.FLAG_MUTABLE
            )

            val calendar = Calendar.getInstance()
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                add(Calendar.HOUR_OF_DAY, 12)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

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

                var startDate = format.format(calendar.time)

                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 55)
                }

                var endDate = format.format(calendar.time)

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

                    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)
                    val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)

                    startDate = todayDateFormat.format(serverformat.parse(startDate))
                    endDate = todayDateFormat.format(serverformat.parse(endDate))

                    val startdateFormat = todayDateFormat.parse(startDate)
                    var result = ""

                    for(i in 0 until alldoData.todos.size){
                        alldoData.todos[i].endDate = FormatDate.calendarFormat(alldoData.todos[i].endDate!!)

                        if(alldoData.todos[i].repeatEnd != null){
                            alldoData.todos[i].repeatEnd = FormatDate.calendarFormat(alldoData.todos[i].repeatEnd!!)
                        }

                        if (alldoData.todos[i].endDate != null){
                            var today: String = alldoData.todos[i].endDate!!
                            var todayDate = serverformat.parse(today)

                            if(alldoData.todos[i].repeatOption == null ||
                                alldoData.todos[i].repeatValue == null ||
                                alldoData.todos[i].repeatOption == "매일" ){
                                result += alldoData.todos[i].content+"\n"
                            } else {
                                Log.d("반복투두", alldoData.todos[i].toString())
                                while (todayDate != null && date_comparison(todayDate, startdateFormat) < 0) {
                                    when (alldoData.todos[i].repeatOption) {
                                        "매주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                alldoData.todos[i].repeatValue!!,
                                                1,
                                                today,
                                                alldoData.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "격주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                alldoData.todos[i].repeatValue!!,
                                                2,
                                                today,
                                                alldoData.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매달" -> {
                                            Log.d("반복투두", todayDate.toString())
                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                alldoData.todos[i].repeatValue!!,
                                                today,
                                                alldoData.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매년" -> {
                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                alldoData.todos[i].repeatValue!!,
                                                today,
                                                alldoData.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }
                                    }
                                }

                                if(todayDate == null){
                                    todayDate = serverformat.parse(today)
                                }

                                if(todayDate != null && date_comparison(todayDate, startdateFormat) == 0){
                                    result += alldoData.todos[i].content+"\n"
                                }
                            }
                        } else {
                            result += alldoData.todos[i].content+"\n"
                        }
                    }

                    for(i in 0 until alldoData.schedules.size){
                        val schedule = alldoData.schedules[i]
                        schedule.repeatStart = FormatDate.calendarFormat(schedule.repeatStart!!)
                        schedule.repeatEnd = FormatDate.calendarFormat(schedule.repeatEnd!!)

                        var today: String
                        var todayDate: Date?

                        if(schedule.repeatOption == null ||
                            schedule.repeatValue == null ||
                            schedule.repeatOption == "매일"){
                            if(schedule.repeatOption == null){
                                val endtime = serverformat.parse(schedule.repeatEnd!!)

                                today = schedule.repeatStart!!
                                todayDate = serverformat.parse(today)

                                schedule.startTime = todayDate
                                schedule.endTime = endtime
                            } else if(schedule.repeatOption == "매일"){
                                val endtime = serverformat.parse(schedule.repeatEnd!!)

                                today = schedule.repeatStart!!
                                todayDate = serverformat.parse(today)

                                schedule.startTime = todayDate
                                todayDate.hours = endtime.hours
                                todayDate.minutes = endtime.minutes
                                todayDate.seconds = endtime.seconds
                                schedule.endTime = todayDate
                            }

                            result += schedule.content + "\n"
                        } else {
                            if(schedule.repeatValue.contains("T")){
                                today = schedule.repeatStart!!
                                todayDate = serverformat.parse(today)

                                while (true) {
                                    when (schedule.repeatOption) {
                                        "매주" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                result += schedule.content + "\n"
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                1,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "격주" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                result += schedule.content + "\n"
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                2,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매달" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                result += schedule.content + "\n"
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매년" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                result += schedule.content + "\n"
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryYear(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }
                                    }
                                }
                            } else {
                                today = schedule.repeatStart!!
                                val endtime = serverformat.parse(schedule.repeatEnd!!)
                                todayDate = serverformat.parse(today)

                                while (todayDate != null && date_comparison(
                                        todayDate,
                                        startdateFormat
                                    ) < 0
                                ) {
                                    when (schedule.repeatOption) {
                                        "매주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                1,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "격주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                2,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매달" -> {
                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매년" -> {
                                            todayDate = FormatDate.nextStartDateEveryYear(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }
                                    }
                                }

                                today = FormatDate.calendarFormat(today)

                                if(todayDate == null){
                                    todayDate = serverformat.parse(today)
                                }

                                if (todayDate != null && date_comparison(
                                        todayDate,
                                        startdateFormat
                                    ) == 0
                                ) {
                                    schedule.startTime = todayDate.clone() as Date
                                    todayDate.hours = endtime.hours
                                    todayDate.minutes = endtime.minutes
                                    todayDate.seconds = endtime.seconds
                                    schedule.endTime = todayDate
                                    result += schedule.content + "\n"
                                }
                            }
                        }
                    }

                    Log.d("알람", alldoData.toString())

                    val builder = NotificationCompat.Builder(context, "notification_channel")
                        .setSmallIcon(R.drawable.app_icon_foreground) // 아이콘
                        .setContentTitle("오늘의 하루") // 제목
                        .setContentText(result) // 내용
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