package com.example.haru.utils

import android.util.Log
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.reflect.typeOf

object FormatDate {
    // 그리니치 시간과 Local시간의 차이
    private val diff = initDiff()

    // LocslDateTime을 String으로 변환할 formatter
    private val localTimeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)
    private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.US)

    //// DatePicker와 TimePicker로 받는 값들은 Date이므로 SimpleDateFormat으로 서버로 보낼 형식으로 변환하는 formatter
    private val dateFormatterToServer = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

    private val simpleFormatterDate = SimpleDateFormat("yyyy.MM.dd", Locale.US)
    private val simpleFormatterTime = SimpleDateFormat("a h:mm", Locale.KOREA)

    private val simpleFormatterKorea = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREA)

    val cal = Calendar.getInstance()


    private fun initDiff(): Long {
        val gmtZoneId = ZoneId.of("GMT")
        val gmtDateTime = LocalDateTime.now(gmtZoneId)
        val localTime = LocalDateTime.now()
        return ChronoUnit.HOURS.between(gmtDateTime, localTime)
    }

    // Today 화면에서 쿼리로 보내는 endDate를 해당 날짜의 자정으로 날린다.

    // LocalDateTime을 넣으면 formatter에 정의된 패턴 형식, 타입은 String으로 반환
//    fun localTimeToStr(time: LocalDateTime): String {
//        return time.format(localTimeFormatter)
//    }

    // LocalDateTime을 넣으면 formatter에 정의된 패턴 형식, 타입은 String으로 반환
//    fun localDateToStr(date: LocalDateTime): String {
//        return date.format(localDateFormatter)
//    }

    // 서버에서 받은 그리니치 시간대에 Local시간과의 차이를 더해서 String으로 반환
    fun todoDateToStr(str: String): String {
        val date = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(diff)
        return date.format(localDateFormatter)
    }

    // 서버에서 받은 그리니치 시간대에 Local시간과의 차이를 더해서 String으로 반환
    fun todoTimeToStr(str: String): String {
        val time = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(diff)
        return time.format(localTimeFormatter)
    }

    fun dateToGMT(str: String) : String{
        val time = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(-diff)
        return time.toString()
    }

    // Date를 서버로 보낼 형식으로 변환하여 String으로 반환
    fun dateToStr(date: Date): String {
        return dateFormatterToServer.format(date)
    }

    fun simpleDateToStr(date: Date): String {
        return simpleFormatterDate.format(date)
    }

    fun simpleTimeToStr(date: Date): String {
        return simpleFormatterTime.format(date)
    }

    fun simpleTodayToStr(date: Date) : String{
        return simpleFormatterKorea.format(date)
    }

    // String으로 된 날짜 정보에 local Date와 그리니치 시간대의 차이를 더해서 Date 타입으로 반환
    fun strToDate(str: String) : Date{
        val date = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(diff)
        val instant = date.atZone(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    fun nextEndDateEveryWeek(repeatValue : String, repeatOption : Int?) : Date {
        var plusValue = 1
        if (repeatOption == 2){
            plusValue = 8
        }
        val nWeek = cal.get(Calendar.DAY_OF_WEEK)
        val idx = nWeek - 1
        var flag = false
        for (i in idx until 7)
            if (repeatValue[i] == '1') {
                cal.add(Calendar.DATE, i - idx)
                flag = true
                break
            }
        if (!flag) {
            for (i in 0 until idx)
                if (repeatValue[i] == '1') {
                    val value = 6 - idx + i + plusValue
                    cal.add(Calendar.DATE, value)
                    break
                }
        }
        return cal.time
    }

    fun nextEndDateEveryMonth(repeatValue : String) : Date {
        val idx = cal.get(Calendar.DAY_OF_MONTH) - 1
        var flag = false
        var days = 32
        for (i in idx until 31)
            if (repeatValue[i] == '1') {
                days = i + 1
                flag = true
                break
            }
        if (!flag) {
            for (i in 0 until idx)
                if (repeatValue[i] == '1') {
                    days = i + 1
                    break
                }
        }

        if (days < idx + 1) {
            cal.add(Calendar.MONTH, 1)
            if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days)
                cal.add(Calendar.MONTH, 1)
            cal.set(Calendar.DAY_OF_MONTH, days)
        } else {
            if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days)
                cal.add(Calendar.MONTH, 1)
            cal.set(Calendar.DAY_OF_MONTH, days)
        }
        return cal.time
    }

    fun nextEndDateEveryYear(repeatValue: String) : Date {
        val idx = cal.get(Calendar.MONTH)
        val days = cal.get(Calendar.DAY_OF_MONTH)
        var month = 13
        var flag = false
        for (i in idx until 12)
            if (repeatValue[i] == '1') {
                month = i
                cal.set(Calendar.MONTH, month)
                if (days <= cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    flag = true
                    break
                }
            }
        if (!flag) {
            cal.add(Calendar.YEAR, 1)
            for (i in 0 until idx)
                if (repeatValue[i] == '1') {
                    month = i
                    cal.set(Calendar.MONTH, month)
                    if (days <= cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        break
                    }
                }
        }
        return cal.time
    }
}