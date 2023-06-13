package com.example.haru.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.example.haru.data.model.Schedule
import com.example.haru.data.model.Todo
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.view.etc.AlarmWorker
import com.example.haru.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

object Alarm {
    fun initAlarm(lifecycle: LifecycleOwner, context: Context){
        deleteAlarm(context)

        val endcalendar = Calendar.getInstance()
        endcalendar.time = Date()
        endcalendar.add(Calendar.DATE, 34)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)
        val startDate = dateFormat.format(Date())
        val endDate = dateFormat.format(endcalendar.time)

        val calendarViewModel = CalendarViewModel()
        calendarViewModel.getAlldo(startDate, endDate, 4)

        calendarViewModel.liveTodoCalendarList.observe(lifecycle){livetodo->
            calendarViewModel.liveScheduleCalendarList.observe(lifecycle){liveschedule->
                var todoIds = ArrayList<String>()

                addAlarm(context)

                for(todos in livetodo){
                    for (todo in todos.todos){
                        if(todo.alarms.size > 0 && !todoIds.contains(todo.id)){
                            addAlarm(todo.copy(), context)
                        }
                    }
                }

                for(schedule in liveschedule){
                    if(schedule.schedule.alarms.size > 0){
                        val calendar = Calendar.getInstance()
                        val repeatstart = FormatDate.strToDatecalendar(schedule.schedule.repeatStart)
                        calendar.time = Date()
                        calendar.add(Calendar.DATE, schedule.position)

                        calendar.apply {
                            set(Calendar.HOUR_OF_DAY, repeatstart!!.hours)
                            set(Calendar.MINUTE, repeatstart.minutes)
                            set(Calendar.SECOND, repeatstart.seconds)
                        }

                        addAlarm(schedule.schedule.copy(), calendar.time.clone() as Date, context)
                    }
                }
            }
        }
    }

    private fun addAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmWorker::class.java)

        if (User.id != "") {
            intent.putExtra("userId", User.id)
            intent.putExtra("requestCode", "0")

            calendarMainData.alarmCnt++

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val timeformatter = SimpleDateFormat("a h:mm", Locale.KOREA)

            val calendar = Calendar.getInstance()

            val amTime = timeformatter.parse(User.amAlarmDate)
            val pmTime = timeformatter.parse(User.pmAlarmDate)

            if(calendar.time.after(pmTime)){
                calendar.apply {
                    time = Date()
                    set(Calendar.HOUR_OF_DAY, amTime.hours)
                    set(Calendar.MINUTE, amTime.minutes)
                    set(Calendar.SECOND, amTime.seconds)
                    add(Calendar.DATE, 1)
                }
            }
            else if (calendar.time.after(amTime)){
                calendar.apply {
                    time = Date()
                    set(Calendar.HOUR_OF_DAY, pmTime.hours)
                    set(Calendar.MINUTE, pmTime.minutes)
                    set(Calendar.SECOND, pmTime.seconds)
                }
            } else {
                calendar.apply {
                    time = Date()
                    set(Calendar.HOUR_OF_DAY, amTime.hours)
                    set(Calendar.MINUTE, amTime.minutes)
                    set(Calendar.SECOND, amTime.seconds)
                }
            }

            Log.d("알람추가", "아침저녁 알림")
            Log.d("알람추가", calendar.time.toString())

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun addAlarm(todo: Todo, context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmWorker::class.java)

        intent.putExtra("userId", User.id)
        intent.putExtra("requestCode", calendarMainData.alarmCnt.toString())

        intent.putExtra("body", todo.content)

        val pendingIntent = PendingIntent.getBroadcast(
            context, calendarMainData.alarmCnt, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        calendarMainData.alarmCnt++

        val calendar = Calendar.getInstance()
        calendar.time = FormatDate.strToDate(todo.alarms[0].time)

        Log.d("알람추가", "투두 알림: ${todo.content}")
        Log.d("알람추가", calendar.time.toString())

        if(calendar.time.after(Date())) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun addAlarm(schedule: Schedule, date: Date, context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmWorker::class.java)

        intent.putExtra("userId", User.id)
        intent.putExtra("requestCode", calendarMainData.alarmCnt.toString())

        intent.putExtra("body", schedule.content)

        val pendingIntent = PendingIntent.getBroadcast(
            context, calendarMainData.alarmCnt, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        calendarMainData.alarmCnt++

        val calendar = Calendar.getInstance()
        calendar.time = date

        Log.d("알람추가", "일정 알림: ${schedule.content}")
        Log.d("알람추가", calendar.time.toString())

        if(calendar.time.after(Date())) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun deleteAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmWorker::class.java)

        for (i in 0 until  calendarMainData.alarmCnt-1) {
            val pendingIntent = PendingIntent.getBroadcast(
                context, i, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            if(pendingIntent != null){
                alarmManager.cancel(pendingIntent)
            }
        }

        calendarMainData.alarmCnt = 0
    }
}