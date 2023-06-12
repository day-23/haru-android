package com.example.haru.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

object GetPastDate {

    @SuppressLint("SimpleDateFormat")
    fun getPastDate(date : String) : String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var past = date.replace("T", " ")
        past = past.substring(0, 19)
        val pastDate = dateFormat.parse(past)
        val pastCal = Calendar.getInstance()

        pastCal.time = pastDate
        pastCal.add(Calendar.HOUR_OF_DAY, 9)
        val korPastDate = pastCal.time //한국 현지화 시간

        val now = Calendar.getInstance()
        val minutesDiff = TimeUnit.MILLISECONDS.toMinutes(now.timeInMillis - korPastDate.time)
        var timeAgo = ""
        if(minutesDiff < 1)
            timeAgo = "방금 전"
        else if(minutesDiff < 60)
            timeAgo = "${minutesDiff}분 전"
        else if(minutesDiff < 1440)
            timeAgo = "${minutesDiff / 60}시간 전"
        else if(minutesDiff < 1440 * 7 * 4)
            timeAgo = "${minutesDiff / 1440 * 7}주 전"
        else if(minutesDiff < 1440 * 7 * 4 * 12)
            timeAgo = "${minutesDiff / 1440 * 7 * 4}개월 전"
        else timeAgo = "${minutesDiff / 1440 * 7 * 4 * 12}년 전"

        return timeAgo
    }
}