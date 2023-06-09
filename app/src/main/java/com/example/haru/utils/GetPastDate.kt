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
        val now = Calendar.getInstance()
        val minutesDiff = TimeUnit.MILLISECONDS.toMinutes(now.timeInMillis - pastDate.time)

        var timeAgo = ""
        if(minutesDiff < 1)
            timeAgo = "방금 전"
        else if(minutesDiff < 60)
            timeAgo = "${minutesDiff}분 전"
        else if(minutesDiff < 1440)
            timeAgo = "${minutesDiff / 60}시간 전"
        else
            timeAgo = "${minutesDiff / 1440}일 전"
        return timeAgo
    }
}