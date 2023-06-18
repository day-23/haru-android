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
import com.example.haru.viewmodel.CheckListViewModel
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
        val checkListViewModel = CheckListViewModel()

        calendarViewModel.getAlldo(startDate, endDate, 4)
        checkListViewModel.dataInit()

        calendarViewModel.liveScheduleCalendarList.observe(lifecycle){liveschedule->
            checkListViewModel.todoDataList.observe(lifecycle) {livetodo->
//                var todoIds = ArrayList<String>()
                addAlarm(context)

//                for (todos in livetodo) {
//                    for (todo in todos.todos) {
//                        if (todo.alarms.size > 0 && !todoIds.contains(todo.id)) {
//                            addAlarm(todo.copy(), context)
//                        }
//                    }
//                }

                for (todo in livetodo){
                    if(todo.alarms.size > 0){
                        addAlarm(todo.copy(), context)
                    }
                }

                for (schedule in liveschedule) {
                    if (schedule.schedule.alarms.size > 0) {
                        val calendar = Calendar.getInstance()
                        val repeatstart =
                            FormatDate.strToDatecalendar(schedule.schedule.repeatStart)
                        calendar.time = Date()
                        calendar.add(Calendar.DATE, schedule.position)

                        calendar.apply {
                            set(Calendar.HOUR_OF_DAY, repeatstart!!.hours)
                            set(Calendar.MINUTE, repeatstart.minutes)
                            set(Calendar.SECOND, repeatstart.seconds)
                        }

                        addAlarm(
                            schedule.schedule.copy(),
                            calendar.time.clone() as Date,
                            context
                        )
                    }
                }
            }
        }
    }

    fun amPmAlarmEdit(context: Context){
        deleteAmPmAlarm(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmWorker::class.java)

        if (User.id != "") {
            intent.putExtra("userId", User.id)
            intent.putExtra("requestCode", "0")

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val timeformatter = SimpleDateFormat("a h:mm", Locale.KOREA)

            val calendar = Calendar.getInstance()
            calendar.time = Date()

            val amTime = timeformatter.parse(User.amAlarmDate)

            amTime.year = calendar.time.year
            amTime.month = calendar.time.month
            amTime.date = calendar.time.date

            val pmTime = timeformatter.parse(User.pmAlarmDate)

            pmTime.year = calendar.time.year
            pmTime.month = calendar.time.month
            pmTime.date = calendar.time.date

            if(calendar.time.after(pmTime)){
                calendar.apply {
                    time = amTime
                    add(Calendar.DATE, 1)
                }
            }
            else if (calendar.time.after(amTime)){
                calendar.apply {
                    time = pmTime
                }
            } else {
                calendar.apply {
                    time = amTime
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

    private fun addAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmWorker::class.java)

        if (User.id != "") {
            intent.putExtra("userId", User.id)
            intent.putExtra("requestCode", "0")

            calendarMainData.alarmCnt++

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val timeformatter = SimpleDateFormat("a h:mm", Locale.KOREA)

            val calendar = Calendar.getInstance()
            calendar.time = Date()


            val amTime = timeformatter.parse(User.amAlarmDate)

            amTime.year = calendar.time.year
            amTime.month = calendar.time.month
            amTime.date = calendar.time.date

            val pmTime = timeformatter.parse(User.pmAlarmDate)

            pmTime.year = calendar.time.year
            pmTime.month = calendar.time.month
            pmTime.date = calendar.time.date

            if (calendar.time.after(pmTime)) {
                calendar.apply {
                    time = amTime
                    add(Calendar.DATE, 1)
                }
            } else if (calendar.time.after(amTime)) {
                calendar.apply {
                    time = pmTime
                }
            } else {
                calendar.apply {
                    time = amTime
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
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        calendarMainData.alarmCnt++

        val calendar = Calendar.getInstance()
        calendar.time = FormatDate.strToDate(todo.alarms[0].time)

        if(calendar.time.after(Date())) {
            Log.d("알람추가", "투두 알림: ${todo.content}")
            Log.d("알람추가", calendar.time.toString())

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
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        calendarMainData.alarmCnt++

        val calendar = Calendar.getInstance()
        calendar.time = date

        if(calendar.time.after(Date())) {
            Log.d("알람추가", "일정 알림: ${schedule.content}")
            Log.d("알람추가", calendar.time.toString())

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
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            if(pendingIntent != null){
                alarmManager.cancel(pendingIntent)
            }
        }

        calendarMainData.alarmCnt = 0
    }

    private fun deleteAmPmAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmWorker::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if(pendingIntent != null){
            alarmManager.cancel(pendingIntent)
        }

        calendarMainData.alarmCnt = 0
    }
}