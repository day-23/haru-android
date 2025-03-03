package com.example.haru.utils

import android.util.Log
import java.nio.channels.Pipe
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

object FormatDate {
    // 그리니치 시간과 Local시간의 차이
    private val diff = initDiff()

    // LocalDateTime을 String으로 변환할 formatter
    //24시간으로 할지 아니면 오전, 오후로 12시간제로 하는지
    private val localTimeFormatter = DateTimeFormatter.ofPattern("a h:mm까지")
    private val localDateFormatter = DateTimeFormatter.ofPattern("M월dd일까지")

    private val calendarDateFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA)

    //// DatePicker와 TimePicker로 받는 값들은 Date이므로 SimpleDateFormat으로 서버로 보낼 형식으로 변환하는 formatter
    private val dateFormatterToServer = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

    private val simpleFormatterDate = SimpleDateFormat("yyyy.MM.dd E", Locale.KOREA)
    private val simpleFormatterTime = SimpleDateFormat("a h:mm", Locale.KOREA)

    private val simpleFormatterKorea = SimpleDateFormat("M월 dd일 E요일", Locale.KOREA)

    private val simpleFormatterCalendar = SimpleDateFormat("M월 d일 a h:mm", Locale.KOREA)

    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)

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

    fun checkToday(date: Date?): Boolean? {
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

    fun calendarFormat(str: String): String {
        val date = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(diff)
        return date.format(calendarDateFormatter)
    }

    fun calendarBackFormat(str: String): String {
        val date = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(-diff)
        return date.format(calendarDateFormatter)
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

    fun simpleCalendarToStr(date: Date): String {
        return simpleFormatterCalendar.format(date)
    }

    // String으로 된 날짜 정보에 local Date와 그리니치 시간대의 차이를 더해서 Date 타입으로 반환
    fun strToDate(str: String?): Date? {
        if (str == null)
            return null
        val date = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).plusHours(diff)
        val instant = date.atZone(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    fun strToDatecalendar(str: String?): Date? {
        if (str == null)
            return null
        val date = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME)
        val instant = date.atZone(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    fun preEndDate(endDateStr: String?, repeatOption: String?, repeatValue: String?): Date? {
        if (endDateStr == null || repeatOption == null || repeatValue == null)
            return null
        val endDate = strToDate(endDateStr)
        cal.time = endDate!!
        val preEndDate = when (repeatOption) {
            "매일" -> { // 매일
                cal.add(Calendar.DATE, -1)
                cal.time
            }
            "매주", "격주" -> { // 매주
                val nWeek = cal.get(Calendar.DAY_OF_WEEK) // endDate가 해당하는 주차의 요일
                var idx = nWeek - 1 // nWeek는 일 ~ 토, 1 ~ 7 이므로 인덱스로 사용하기 위해서 -1

                if (idx != 0) // 오늘이 포함되면 안되기 때문에 -1을 해준다
                    idx--
                else { // idx가 0일 때 -1을하면 에러이므로 직접 토요일로 지정
                    idx = 6
                }

                cal.add(Calendar.DATE, -1) // 오늘을 포함하면 안되므로 -1

                var flag = false

                val plusValue = if (repeatOption == "매주") 1 else 8

                for (i in idx downTo 0) {
                    if (repeatValue[i] == '1') {
                        cal.add(Calendar.DATE, i - idx)
                        flag = true
                        break
                    }
                }
                if (!flag) {
                    for (i in 6 downTo idx + 1)
                        if (repeatValue[i] == '1') {
                            val value = -(idx + plusValue + (6 - i))
                            cal.add(Calendar.DATE, value)
                            break
                        }
                }
                cal.time
            }
            "매달" -> { // 매달
                // 0 ~ 30
                val idx = cal.get(Calendar.DAY_OF_MONTH) - 1 // endDate가 해당하는 달의 날짜를 인덱스화
                var flag = false
                var days = 32
                for (i in idx - 1 downTo 0) // endDate 해당 날은 포함 X이므로 -1
                    if (repeatValue[i] == '1') {
                        days = i + 1
                        flag = true
                        break
                    }

                if (!flag)
                    for (i in 30 downTo idx)
                        if (repeatValue[i] == '1') {
                            days = i + 1
                            break
                        }

                // 만약 31일인 상태에서 3월에서 + 1하면 5월 1일로 간다. 그렇기 때문에 날짜를 1로 설정해줌
                cal.set(Calendar.DAY_OF_MONTH, 1)

                if (flag) { // flag가 true이면 days가 idx + 1보다 작은 것이다.
//                    cal.set(Calendar.DAY_OF_MONTH, days)
                } else { // flag가 false라면 days가 idx + 1보다 크거나 같은 것이다.
                    cal.add(Calendar.MONTH, -1)
                    if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days)
                        cal.add(Calendar.MONTH, -1)
                }
                cal.set(Calendar.DAY_OF_MONTH, days)
                cal.time
            }
            "매년" -> { // 매년
                val idx = cal.get(Calendar.MONTH)
                val days = cal.get(Calendar.DAY_OF_MONTH)

                var flag = false
                var month: Int
                for (i in idx - 1 downTo 0) {
                    if (repeatValue[i] == '1') {
                        month = i
                        cal.set(Calendar.DAY_OF_MONTH, 1)
                        cal.set(Calendar.MONTH, i)
                        if (days <= cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            flag = true
                            cal.set(Calendar.DAY_OF_MONTH, days)
                            break
                        }
                    }
                }
                while (!flag) {
                    cal.add(Calendar.YEAR, -1)
                    for (i in 11 downTo 0) {
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
                cal.time
            }
            else -> {
                null
            }
        }
        return preEndDate
    }

    fun preStartDate(endDateStr: String?, repeatOption: String?, repeatValue: String?): Date? {
        if (endDateStr == null || repeatOption == null || repeatValue == null)
            return null

        if (repeatValue.contains("T")) {
            val endDate = strToDatecalendar(endDateStr)
            cal.time = endDate!!

            val preStartDate = when (repeatOption) {
                "매주" -> {
                    cal.add(Calendar.DATE, -7)
                    cal.time.clone() as Date
                }

                "격주" -> {
                    cal.add(Calendar.DATE, -14)
                    cal.time.clone() as Date
                }

                "매달" -> {
                    cal.add(Calendar.MONTH, -1)
                    cal.time.clone() as Date
                }

                "매년" -> {
                    cal.add(Calendar.YEAR, -1)
                    cal.time.clone() as Date
                }

                else -> {
                    null
                }
            }

            return preStartDate
        } else {
            val endDate = strToDatecalendar(endDateStr)
            cal.time = endDate!!
            val preEndDate = when (repeatOption) {
                "매일" -> { // 매일
                    cal.add(Calendar.DATE, -1)
                    cal.time.clone() as Date
                }
                "매주", "격주" -> { // 매주
                    val nWeek = cal.get(Calendar.DAY_OF_WEEK) // endDate가 해당하는 주차의 요일
                    var idx = nWeek - 1 // nWeek는 일 ~ 토, 1 ~ 7 이므로 인덱스로 사용하기 위해서 -1

                    if (idx != 0) // 오늘이 포함되면 안되기 때문에 -1을 해준다
                        idx--
                    else { // idx가 0일 때 -1을하면 에러이므로 직접 토요일로 지정
                        idx = 6
                    }

                    cal.add(Calendar.DATE, -1) // 오늘을 포함하면 안되므로 -1

                    var flag = false

                    val plusValue = if (repeatOption == "매주") 1 else 8

                    for (i in idx downTo 0) {
                        if (repeatValue[i] == '1') {
                            cal.add(Calendar.DATE, i - idx)
                            flag = true
                            break
                        }
                    }
                    if (!flag) {
                        for (i in 6 downTo idx + 1)
                            if (repeatValue[i] == '1') {
                                val value = -(idx + plusValue + (6 - i))
                                cal.add(Calendar.DATE, value)
                                break
                            }
                    }
                    cal.time.clone() as Date
                }
                "매달" -> { // 매달
                    // 0 ~ 30
                    val idx = cal.get(Calendar.DAY_OF_MONTH) - 1 // endDate가 해당하는 달의 날짜를 인덱스화
                    var flag = false
                    var days = 32
                    for (i in idx - 1 downTo 0) // endDate 해당 날은 포함 X이므로 -1
                        if (repeatValue[i] == '1') {
                            days = i + 1
                            flag = true
                            break
                        }

                    if (!flag)
                        for (i in 30 downTo idx)
                            if (repeatValue[i] == '1') {
                                days = i + 1
                                break
                            }

                    // 만약 31일인 상태에서 3월에서 + 1하면 5월 1일로 간다. 그렇기 때문에 날짜를 1로 설정해줌
                    cal.set(Calendar.DAY_OF_MONTH, 1)

                    if (flag) { // flag가 true이면 days가 idx + 1보다 작은 것이다.
//                    cal.set(Calendar.DAY_OF_MONTH, days)
                    } else { // flag가 false라면 days가 idx + 1보다 크거나 같은 것이다.
                        cal.add(Calendar.MONTH, -1)
                        if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days)
                            cal.add(Calendar.MONTH, -1)
                    }
                    cal.set(Calendar.DAY_OF_MONTH, days)
                    cal.time.clone() as Date
                }
                "매년" -> { // 매년
                    val idx = cal.get(Calendar.MONTH)
                    val days = cal.get(Calendar.DAY_OF_MONTH)

                    var flag = false
                    var month: Int
                    for (i in idx - 1 downTo 0) {
                        if (repeatValue[i] == '1') {
                            month = i
                            cal.set(Calendar.DAY_OF_MONTH, 1)
                            cal.set(Calendar.MONTH, i)
                            if (days <= cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                                flag = true
                                cal.set(Calendar.DAY_OF_MONTH, days)
                                break
                            }
                        }
                    }
                    while (!flag) {
                        cal.add(Calendar.YEAR, -1)
                        for (i in 11 downTo 0) {
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
                    cal.time.clone() as Date
                }
                else -> {
                    null
                }
            }
            return preEndDate
        }
    }

    fun nextEndDate(endDateStr: String?, repeatEndDateStr: String?): Date? {
        if (endDateStr == null)
            return null
        val endDate = strToDate(endDateStr)
        cal.apply {
            time = endDate!!
            add(Calendar.DATE, 1)
        }
        val nextEndDate = cal.time

        if (repeatEndDateStr != null) {
            cal.apply {
                time = strToDate(repeatEndDateStr)!!
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
        } else { // days >= idx + 1 -> 해당 달에 있을 수 있다.
            if (endDateStr != null) { // endDate 당일을 포함하면 안되는 조건
                if (days == idx + 1) { // 같은 일이면 무조건 +1달
                    cal.add(Calendar.MONTH, 1)
                }

                if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days) {
                    cal.add(Calendar.MONTH, 1)
                    if (repeatValue.substring(0, days - 1).contains('1'))
                        days = repeatValue.indexOf('1') + 1
                }
            } else { // 추가할 때라서 endDate 당일이 포함되도 상관없음.
                if (cal.getActualMaximum(Calendar.DAY_OF_MONTH) < days) { // 해당 달의 최대 일보다 days가 크다.
                    cal.add(Calendar.MONTH, 1)
                    if (repeatValue.substring(0, days - 1).contains('1'))
                        days = repeatValue.indexOf('1') + 1
                }
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

    //이 다음 4개의 next 함수는 스케줄 용입니다.
    fun nextStartDate(endDateStr: String, repeatEndDateStr: String? = null): Date? {
        val endDate = strToDatecalendar(endDateStr)
        val calendar = Calendar.getInstance()

        calendar.apply {
            time = endDate!!
            add(Calendar.DATE, 1)
        }
        val nextEndDate = calendar.time.clone() as Date

        if (repeatEndDateStr == null) return nextEndDate

        calendar.apply {
            time = strToDatecalendar(repeatEndDateStr)!!
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        val repeatEndDate = calendar.time
        return if (nextEndDate.after(repeatEndDate))
            null
        else nextEndDate
    }

    fun nextStartDateEveryWeek(
        repeatValue: String,
        repeatOption: Int,
        endDateStr: String? = null,
        repeatEndDateStr: String? = null
    ): Date? {
        val calendar = Calendar.getInstance()

        if (!repeatValue.contains("T")) {
            val plusValue = if (repeatOption == 2) 8 else 1

            if(endDateStr != null) {
                calendar.time =
                    strToDatecalendar(endDateStr) // endDateStr이 null이 아니라면 Todo를 완료하기 위해 다음 endDate를 구하기 위한 과정
            } else {
                calendar.time = Date()
                calendar.add(Calendar.DATE, -1)
            }

            val nWeek = calendar.get(Calendar.DAY_OF_WEEK)

            val idx = nWeek - 1
            val idxPlus = 1  // Todo를 완료하기 위해서 다음 endDate를 구할때는 해당 날이 포함되서는 안된다.
            var flag = false

            for (i in idx + idxPlus until 7)
                if (repeatValue[i] == '1') {
                    calendar.add(Calendar.DATE, i - idx)
                    flag = true
                    break
                }
            if (!flag) {
                for (i in 0 until idx + idxPlus)
                    if (repeatValue[i] == '1') {
                        val value = 6 - idx + i + plusValue
                        calendar.add(Calendar.DATE, value)
                        break
                    }
            }
            val nextEndDate = calendar.time.clone() as Date

            if (repeatEndDateStr == null) return nextEndDate

            calendar.apply {
                time = strToDatecalendar(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatEndDate = calendar.time
            return if (nextEndDate.after(repeatEndDate)) // 반복 마감일보다 다음 마감일이 더 뒤라면 반복 종료
                null
            else nextEndDate
        } else {
            calendar.time =
                strToDatecalendar(endDateStr)

            if (repeatOption == 1) {
                calendar.add(Calendar.DAY_OF_MONTH, 7)
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, 14)
            }

            val nextStartDate = calendar.time.clone() as Date

            calendar.apply {
                time = strToDatecalendar(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatStartDate = calendar.time

            return if (nextStartDate.after(repeatStartDate))
                null
            else nextStartDate
        }
    }

    fun nextStartDateEveryMonth(
        repeatValue: String, endDateStr: String? = null,
        repeatEndDateStr: String? = null
    ): Date? {
        val calendar = Calendar.getInstance()

        if (!repeatValue.contains("T")) {

            if(endDateStr != null) {
                calendar.time =
                    strToDatecalendar(endDateStr) // endDateStr이 null이 아니라면 Todo를 완료하기 위해 다음 endDate를 구하기 위한 과정
            } else {
                calendar.time = Date()
                calendar.add(Calendar.DATE, -1)
            }

            val idx = calendar.get(Calendar.DAY_OF_MONTH) - 1
            val idxPlus = 1
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
            calendar.set(
                Calendar.DAY_OF_MONTH,
                1
            )  // 만약 31일인 상태에서 3월에서 + 1하면 5월 1일로 간다. 그렇기 때문에 날짜를 1로 설정해줌

            //5월 1일

            //10 < 10
            if (days < idx + 1) {
                calendar.add(Calendar.MONTH, 1)
                if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) < days)
                    calendar.add(Calendar.MONTH, 1)
                calendar.set(Calendar.DAY_OF_MONTH, days)
            } else {
                //31 < 10
                if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) < days) {
                    calendar.add(Calendar.MONTH, 1)
                    if (repeatValue.substring(0, days - 1).contains('1')) {
                        days = repeatValue.indexOf('1') + 1
                    }
                }
                calendar.set(Calendar.DAY_OF_MONTH, days)
            }
            val nextEndDate = calendar.time.clone() as Date

            Log.d("반복투두", nextEndDate.toString())

            if (repeatEndDateStr == null) return nextEndDate

            calendar.apply {
                time = strToDatecalendar(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }

            val repeatEndDate = calendar.time
            return if (nextEndDate.after(repeatEndDate)) // 반복 마감일보다 다음 마감일이 더 뒤라면 반복 종료
                null
            else nextEndDate
        } else {
            calendar.time =
                strToDatecalendar(endDateStr)

            calendar.add(Calendar.MONTH, 1)

            val nextStartDate = calendar.time.clone() as Date

            calendar.apply {
                time = strToDatecalendar(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatStartDate = calendar.time

            return if (nextStartDate.after(repeatStartDate))
                null
            else nextStartDate
        }
    }

    fun nextStartDateEveryYear(
        repeatValue: String,
        endDateStr: String? = null,
        repeatEndDateStr: String? = null,    // endDateStr을 하면 현재 시간으로 값을 정하지만 만약 사용자가 직접 날짜를 설정한다면????? 방법 강구하기
        day: Int? = null
    ): Date? {                       // todoAddViewModel에 사용자가 직접 endDate를 설정한 것을 표시할 수 있는 값 만들기???
        val calendar = Calendar.getInstance()

        if (!repeatValue.contains("T")) {
            if(endDateStr != null) {
                calendar.time =
                    strToDatecalendar(endDateStr) // endDateStr이 null이 아니라면 Todo를 완료하기 위해 다음 endDate를 구하기 위한 과정
            } else {
                calendar.time = Date()
                calendar.add(Calendar.DATE, -1)
            }

            val idx = calendar.get(Calendar.MONTH)
            val idxPlus = 1
            val days = day ?: calendar.get(Calendar.DAY_OF_MONTH)

            var month: Int
            var flag = false
            for (i in idx + idxPlus until 12)
                if (repeatValue[i] == '1') {
                    month = i
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.MONTH, month)
                    if (days <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        flag = true
                        calendar.set(Calendar.DAY_OF_MONTH, days)
                        break
                    }
                }
            while (!flag) {
                calendar.add(Calendar.YEAR, 1)
                for (i in 0 until 12) {
                    if (repeatValue[i] == '1') {
                        month = i
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.MONTH, month)
                        if (days <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            flag = true
                            calendar.set(Calendar.DAY_OF_MONTH, days)
                            break
                        }
                    }
                }
            }
            val nextEndDate = calendar.time.clone() as Date

            if (repeatEndDateStr == null) return nextEndDate

            calendar.apply {
                time = strToDatecalendar(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatEndDate = calendar.time
            return if (nextEndDate.after(repeatEndDate)) // 반복 마감일보다 다음 마감일이 더 뒤라면 반복 종료
                null
            else nextEndDate

        } else {
            calendar.time =
                strToDatecalendar(endDateStr)

            calendar.add(Calendar.YEAR, 1)

            val nextStartDate = calendar.time.clone() as Date

            calendar.apply {
                time = strToDatecalendar(repeatEndDateStr)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val repeatStartDate = calendar.time

            return if (nextStartDate.after(repeatStartDate))
                null
            else nextStartDate
        }
    }

    fun getIntervalDate(
        interval: Int,
        repeatStartStr: String,
        repeatEndDateStr: String,
        repeatOption: String,
        today: Date
    ): Pair<Date, Date> {
        val repeatStart = dateFormatterToServer.parse(repeatStartStr)!!
        val repeatEnd = dateFormatterToServer.parse(repeatEndDateStr)!!

        cal.time = repeatStart

        if (today < cal.time) {
            val startDate = cal.time
            cal.add(Calendar.SECOND, interval)
            Log.e("20191627", "1startDate : $startDate, endDAte : ${cal.time}")
            return Pair(startDate, cal.time)
        }

        while (today > cal.time) {

            when (repeatOption) {
                "매주" -> cal.add(Calendar.DAY_OF_MONTH, 7)
                "격주" -> cal.add(Calendar.DAY_OF_MONTH, 14)
                "매달" -> {
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    cal.add(Calendar.MONTH, 1)
                    var maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    while (day > maxDay) {
                        cal.add(Calendar.MONTH, 1)
                        maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    }
                    cal.set(Calendar.DAY_OF_MONTH, day)
                }
                "매년" -> cal.add(Calendar.YEAR, 1)
            }
        }
        if (cal.time < repeatEnd) {
            val startDate = cal.time
            cal.add(Calendar.SECOND, interval)
            Log.e("20191627", "2startDate : $startDate, endDAte : ${cal.time}")
            return Pair(startDate, cal.time)
        }

        when (repeatOption) {
            "매주" -> cal.add(Calendar.DAY_OF_MONTH, -7)
            "격주" -> cal.add(Calendar.DAY_OF_MONTH, -14)
            "매달" -> {
                val day = cal.get(Calendar.DAY_OF_MONTH)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.add(Calendar.MONTH, -1)
                var maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                while (day > maxDay) {
                    cal.add(Calendar.MONTH, -1)
                    maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                }
                cal.set(Calendar.DAY_OF_MONTH, day)
            }
            "매년" -> cal.add(Calendar.YEAR, -1)
        }

        val startDate = cal.time
        cal.add(Calendar.SECOND, interval)
        Log.e("20191627", "3startDate : $startDate, endDAte : ${cal.time}")
        return Pair(startDate, cal.time)
    }

    fun nextStartSchedule(
        repeatOption: String,
        repeatValue: String,
        repeatStartStr: String,
        repeatEndDateStr: String,
        today: Date
    ): Pair<Date, Date> {
        val repeatStart = dateFormatterToServer.parse(repeatStartStr)!!
        val repeatEnd = dateFormatterToServer.parse(repeatEndDateStr)!!

        cal.time = repeatEnd
        val endHour = cal.get(Calendar.HOUR_OF_DAY)
        val endMinute = cal.get(Calendar.MINUTE)

        cal.time = repeatStart

        if (today < cal.time) {
            val startDate = cal.time
            cal.apply {
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
            }
            return Pair(startDate, cal.time)
        }

        while (today > cal.time) {
            when (repeatOption) {
                "매일" -> {
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                }
                "매주" -> {
                    val idx = cal.get(Calendar.DAY_OF_WEEK) - 1
                    for (i in idx until 7) {
                        if (repeatValue[i] == '1')
                            if (today < cal.time)
                                break
                        cal.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                "격주" -> {
                    val idx = cal.get(Calendar.DAY_OF_WEEK) - 1
                    var flag = false
                    for (i in idx until 7) {
                        if (repeatValue[i] == '1')
                            if (today < cal.time) {
                                flag = true
                                break
                            }
                        cal.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    if (!flag)
                        cal.add(Calendar.DAY_OF_MONTH, 7)
                }
                "매달" -> {
                    val idx = cal.get(Calendar.DAY_OF_MONTH) - 1
                    val endIdx = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    for (i in idx until endIdx) {
                        if (repeatValue[i] == '1')
                            if (today < cal.time)
                                break
                        cal.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                "매년" -> {
                    val idx = cal.get(Calendar.MONTH)
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    for (i in idx until 12) {
                        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                        if (repeatValue[i] == '1') {
                            if (today < cal.time) {
                                if (day <= maxDay)
                                    break
                            }
                        }
                        cal.add(Calendar.MONTH, 1)
                    }
                    cal.set(Calendar.DAY_OF_MONTH, day)
                }
            }
        }

        if (cal.time < repeatEnd) {
            val startDate = cal.time
            cal.apply {
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
            }
            return Pair(startDate, cal.time)
        }

        when (repeatOption) {
            "매일" -> cal.add(Calendar.DAY_OF_MONTH, -1)
            "매주" -> { // 여기부터 시작
                val idx = cal.get(Calendar.DAY_OF_WEEK) - 1
                var flag = false
                for (i in idx - 1 downTo 0) {
                    if (repeatValue[i] == '1') {
                        flag = true
                        break
                    }
                    cal.add(Calendar.DAY_OF_MONTH, -1)
                }
                if (!flag)
                    for (i in 6 downTo idx) {
                        if (repeatValue[i] == '1')
                            break
                        cal.add(Calendar.DAY_OF_MONTH, -1)
                    }
            }
            "격주" -> {
                val idx = cal.get(Calendar.DAY_OF_WEEK) - 1
                var flag = false
                for (i in idx - 1 downTo 0) {
                    if (repeatValue[i] == '1') {
                        flag = true
                        break
                    }
                    cal.add(Calendar.DAY_OF_MONTH, -1)
                }
                if (!flag) {
                    cal.add(Calendar.DAY_OF_MONTH, -7)
                    for (i in 6 downTo idx) {
                        if (repeatValue[i] == '1')
                            break
                        cal.add(Calendar.DAY_OF_MONTH, -1)
                    }
                }
            }
            "매달" -> {
                val idx = cal.get(Calendar.DAY_OF_MONTH) - 1
                val endIdx = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                var flag = false
                for (i in idx - 1 downTo 0) {
                    if (repeatValue[i] == '1') {
                        flag = true
                        break
                    }
                    cal.add(Calendar.DAY_OF_MONTH, -1)
                }
                if (!flag) {
                    for (i in endIdx - 1 downTo idx) {
                        if (repeatValue[i] == '1')
                            break
                        cal.add(Calendar.DAY_OF_MONTH, -1)
                    }
                }
            }
            "매년" -> {
                val idx = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)
                var flag = false
                cal.set(Calendar.DAY_OF_MONTH, 1)
                for (i in idx - 1 downTo 0) {
                    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (repeatValue[i] == '1')
                        if (day <= maxDay) {
                            flag = true
                            break
                        }
                    cal.add(Calendar.MONTH, -1)
                }
                if (!flag) {
                    for (i in 11 downTo idx) {
                        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                        if (repeatValue[i] == '1')
                            if (day <= maxDay)
                                break
                        cal.add(Calendar.MONTH, -1)
                    }
                }
                cal.set(Calendar.DAY_OF_MONTH, day)
            }
        }

        val startDate = cal.time
        cal.apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
        }
        return Pair(startDate, cal.time)
    }

}