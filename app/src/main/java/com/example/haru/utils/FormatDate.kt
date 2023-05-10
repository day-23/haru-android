package com.example.haru.utils

import android.util.Log
import androidx.core.util.rangeTo
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

object FormatDate {
    // 그리니치 시간과 Local시간의 차이
    private val diff = initDiff()

    // LocslDateTime을 String으로 변환할 formatter
    private val localTimeFormatter = DateTimeFormatter.ofPattern("H:mm까지")  //24시간으로 할지 아니면 오전, 오후로 12시간제로 하는지
    private val localDateFormatter = DateTimeFormatter.ofPattern("M월dd일까지")


    //// DatePicker와 TimePicker로 받는 값들은 Date이므로 SimpleDateFormat으로 서버로 보낼 형식으로 변환하는 formatter
    private val dateFormatterToServer = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

    private val simpleFormatterDate = SimpleDateFormat("yyyy.MM.dd E", Locale.KOREA)
    private val simpleFormatterTime = SimpleDateFormat("a h:mm", Locale.KOREA)

    private val simpleFormatterKorea = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREA)

    private val compareStrFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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

    fun checkToday(date : Date?) : Boolean?{
        if (date == null)
            return null
        val today = Date()
        cal.time = today
        val todayYear = cal.get(Calendar.YEAR)
        val todayMonth = cal.get(Calendar.MONTH)
        val todayDay = cal.get(Calendar.DAY_OF_MONTH)
        cal.time = date
        val dateYear = cal.get(Calendar.YEAR)
        val dateMonth = cal.get(Calendar.MONTH)
        val dateDay = cal.get(Calendar.DAY_OF_MONTH)
        return (todayYear == dateYear && todayMonth == dateMonth && todayDay == dateDay)
    }

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

    fun dateToGMT(str: String): String {
        val time = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(-diff)
        return time.toString()
    }

    // Date를 서버로 보낼 형식으로 변환하여 String으로 반환
    fun dateToStr(date: Date?): String? {
        if (date == null)
            return null
        return dateFormatterToServer.format(date)
    }

    fun simpleDateToStr(date: Date): String {
        return simpleFormatterDate.format(date)
    }

    fun simpleTimeToStr(date: Date): String {
        return simpleFormatterTime.format(date)
    }

    fun simpleTodayToStr(date: Date): String {
        return simpleFormatterKorea.format(date)
    }

    // String으로 된 날짜 정보에 local Date와 그리니치 시간대의 차이를 더해서 Date 타입으로 반환
    fun strToDate(str: String?): Date? {
        if (str == null)
            return null
        val date = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(diff)
        val instant = date.atZone(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    fun nextEndDate(endDateStr: String?, repeatEndDateStr: String?): Date? {
        if (endDateStr == null)
            return null
        val endDate = strToDate(endDateStr)
        cal.apply {
            time = endDate
            add(Calendar.DATE, 1)
        }
        val nextEndDate = cal.time

        if (repeatEndDateStr != null) {
            cal.apply {
                time = strToDate(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatEndDate = cal.time
            return if (nextEndDate.after(repeatEndDate))
                null
            else nextEndDate
        }
        return nextEndDate
    }

    fun nextEndDateEveryWeek(
        repeatValue: String?,
        repeatOption: Int?,
        endDateStr: String?,
        repeatEndDateStr: String?
    ): Date? {
        if (repeatValue == null || repeatOption == null)
            return null

        val plusValue = if (repeatOption == 2) 8 else 1

        if (endDateStr == null)  // endDateStr이 null이라면 Todo를 추가하는 과정
            cal.time = Date()
        else cal.time =
            strToDate(endDateStr) // endDateStr이 null이 아니라면 Todo를 완료하기 위해 다음 endDate를 구하기 위한 과정

        val nWeek = cal.get(Calendar.DAY_OF_WEEK)

        val idx = nWeek - 1
        val idxPlus =
            if (endDateStr == null) 0 else 1  // Todo를 완료하기 위해서 다음 endDate를 구할때는 해당 날이 포함되서는 안된다.
        var flag = false

        for (i in idx + idxPlus until 7)
            if (repeatValue[i] == '1') {
                cal.add(Calendar.DATE, i - idx)
                flag = true
                break
            }
        if (!flag) {
            for (i in 0 until idx + idxPlus)
                if (repeatValue[i] == '1') {
                    val value = 6 - idx + i + plusValue
                    cal.add(Calendar.DATE, value)
                    break
                }
        }
        val nextEndDate = cal.time

        if (repeatEndDateStr != null) {
            cal.apply {
                time = strToDate(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatEndDate = cal.time
            return if (nextEndDate.after(repeatEndDate)) // 반복 마감일보다 다음 마감일이 더 뒤라면 반복 종료
                null
            else nextEndDate
        }
        return nextEndDate
    }

    fun nextEndDateEveryMonth(
        repeatValue: String, endDateStr: String?,
        repeatEndDateStr: String?
    ): Date? {
        if (endDateStr == null)
            cal.time = Date()
        else cal.time = strToDate(endDateStr)

        val idx = cal.get(Calendar.DAY_OF_MONTH) - 1
        val idxPlus = if (endDateStr == null) 0 else 1
        var flag = false
        var days = 32
        for (i in idx + idxPlus until 31)
            if (repeatValue[i] == '1') {
                days = i + 1
                flag = true
                break
            }
        if (!flag) {
            for (i in 0 until idx + idxPlus)
                if (repeatValue[i] == '1') {
                    days = i + 1
                    break
                }
        }
        cal.set(Calendar.DAY_OF_MONTH, 1)  // 만약 31일인 상태에서 3월에서 + 1하면 5월 1일로 간다. 그렇기 때문에 날짜를 1로 설정해줌

        if (days < idx + 1) {
            cal.add(Calendar.MONTH, 1)
            if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days)
                cal.add(Calendar.MONTH, 1)
            cal.set(Calendar.DAY_OF_MONTH, days)
        } else {
            if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days) {
                cal.add(Calendar.MONTH, 1)
                if (repeatValue.substring(0, days - 1).contains('1')) {
                    days = repeatValue.indexOf('1') + 1
                }
            } else {
                if (endDateStr != null)
                    cal.add(Calendar.MONTH, 1)
            }
            cal.set(Calendar.DAY_OF_MONTH, days)
        }
        val nextEndDate = cal.time
        Log.d("20191627", "nextEndDate : ${nextEndDate}")

        if (repeatEndDateStr != null) {
            cal.apply {
                time = strToDate(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatEndDate = cal.time
            return if (nextEndDate.after(repeatEndDate)) // 반복 마감일보다 다음 마감일이 더 뒤라면 반복 종료
                null
            else nextEndDate
        }
        return nextEndDate
    }

    fun nextEndDateEveryYear(
        repeatValue: String,
        endDateStr: String?,
        repeatEndDateStr: String?,    // endDateStr을 하면 현재 시간으로 값을 정하지만 만약 사용자가 직접 날짜를 설정한다면????? 방법 강구하기
        day: Int? = null
    ): Date? {                       // todoAddViewModel에 사용자가 직접 endDate를 설정한 것을 표시할 수 있는 값 만들기???
        if (endDateStr == null)      // endDatePick의 클릭이벤트를 통해서 가능
            cal.time = Date()
        else cal.time = strToDate(endDateStr)

        Log.d("20191627", "day : $day")
        val idx = cal.get(Calendar.MONTH)
        val idxPlus = if (endDateStr == null) 0 else 1
        val days = day ?: cal.get(Calendar.DAY_OF_MONTH)

        var month: Int
        var flag = false
        for (i in idx + idxPlus until 12)
            if (repeatValue[i] == '1') {
                month = i
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.MONTH, month)
                if (days <= cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    flag = true
                    cal.set(Calendar.DAY_OF_MONTH, days)
                    break
                }
            }
        while (!flag) {
            cal.add(Calendar.YEAR, 1)
            for (i in 0 until 12) {
                if (repeatValue[i] == '1') {
                    month = i
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    cal.set(Calendar.MONTH, month)
                    if (days <= cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        flag = true
                        cal.set(Calendar.DAY_OF_MONTH, days)
                        break
                    }
                }
            }
        }
        val nextEndDate = cal.time

        if (repeatEndDateStr != null) {
            cal.apply {
                time = strToDate(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatEndDate = cal.time
            return if (nextEndDate.after(repeatEndDate)) // 반복 마감일보다 다음 마감일이 더 뒤라면 반복 종료
                null
            else nextEndDate
        }
        return nextEndDate
    }
}