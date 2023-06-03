package com.example.haru.viewmodel

import android.content.Context
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.*
import com.example.haru.data.model.*
import com.example.haru.data.repository.CategoryRepository
import com.example.haru.data.repository.ScheduleRepository
import com.example.haru.utils.FormatDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class TimetableViewModel(val context: Context) : ViewModel() {
    private val scheduleRepository = ScheduleRepository()
    private val categoryRepository = CategoryRepository()

    private val _Dates = MutableLiveData<ArrayList<String>>()
    val Dates: LiveData<ArrayList<String>>
        get() = _Dates

    private val _Days = MutableLiveData<ArrayList<String>>()
    val Days: LiveData<ArrayList<String>>
        get() = _Days

    private val _Selected = MutableLiveData<Timetable_date>()
    val Selected: LiveData<Timetable_date>
        get() = _Selected

    private val _Colors = MutableLiveData<ArrayList<String>>()
    val Colors: LiveData<ArrayList<String>>
        get() = _Colors

    private val _liveCategoryList = MutableLiveData<List<Category>>()
    val liveCategoryList: MutableLiveData<List<Category>> get() = _liveCategoryList

    private val _Schedules = MutableLiveData<ArrayList<ArrayList<Schedule>>>()
    val Schedules: LiveData<ArrayList<ArrayList<Schedule>>>
        get() = _Schedules

    private val _SchedulesAllday = MutableLiveData<ArrayList<ScheduleCalendarData>>()
    val SchedulesAllday: LiveData<ArrayList<ScheduleCalendarData>>
        get() = _SchedulesAllday

    val categoryAndSchedulesCombinedData: LiveData<Pair<List<Category>?, ArrayList<ArrayList<Schedule>>>> =
        Transformations.switchMap(_liveCategoryList) { categoryList: List<Category>? ->
            Transformations.map(_Schedules) { schedules ->
                Pair(categoryList, schedules)
            }
        }

    val categoryAndSchedulesAlldayCombinedData: LiveData<Pair<List<Category>?, ArrayList<ScheduleCalendarData>>> =
        Transformations.switchMap(_liveCategoryList) { categoryList: List<Category>? ->
            Transformations.map(_SchedulesAllday) { schedulesAllday ->
                Pair(categoryList, schedulesAllday)
            }
        }


    private val _TodayDay = MutableLiveData<String>()
    val TodayDay: LiveData<String>
        get() = _TodayDay

    private val _MoveDate = MutableLiveData<String>()
    val MoveDate: LiveData<String>
        get() = _MoveDate

    private val _PreMoveDate = MutableLiveData<String>()
    val PreMoveDate: LiveData<String>
        get() = _PreMoveDate

    private val _MoveView = MutableLiveData<View>()
    val MoveView: LiveData<View>
        get() = _MoveView

    val calendar = Calendar.getInstance()
    var colorlist: ArrayList<String> = ArrayList()
    var dayslist: ArrayList<String> = ArrayList()
    var Datelist: ArrayList<String> = ArrayList()
    var IndexList: ArrayList<ArrayList<Schedule>> = ArrayList()
    var IndexList_allday: ArrayList<ScheduleCalendarData> = ArrayList()
    var timetable_today = ""

    init {
        timetable_today = SimpleDateFormat("yyyyMMdd").format(
            Date(
                calendar.get(Calendar.YEAR) - 1900,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )
        _TodayDay.value = calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    fun init_value() {
        _Selected.value = Timetable_date(
            calendar.get(Calendar.YEAR).toString() + "년",
            (calendar.get(Calendar.MONTH) + 1).toString() + "월",
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        )
        Daylist(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        _Days.value = dayslist
        _Colors.value = colorlist
        _Dates.value = Datelist
        _Schedules.value = IndexList

        getCategories()
    }

    //날짜정보//
    fun buttonClick() {
        showDatePickerDialog()
    }

    //오늘 날짜로
    fun todayClick() {
        val calendar = Calendar.getInstance()
        Daylist(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        _Days.value = dayslist
        _Selected.value = Timetable_date(
            calendar.get(Calendar.YEAR).toString() + "년",
            (calendar.get(Calendar.MONTH) + 1).toString() + "월",
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        )
        _Colors.value = colorlist
        _Dates.value = Datelist
        _Schedules.value = IndexList
    }

    fun showDatePickerDialog() {
        val datePicker = DatePicker(context)
        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(datePicker)
            .setPositiveButton("apply") { dialog, _ ->
                val year = datePicker.year
                val month = datePicker.month
                var day = datePicker.dayOfMonth
                Daylist(year, month, day)
                _Days.value = dayslist
                _Colors.value = colorlist
                _Selected.value = Timetable_date(
                    year.toString() + "년",
                    (month + 1).toString() + "월",
                    day.toString()
                )
                _Dates.value = Datelist
//                _Schedules.value = IndexList
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private val zuluFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
    private val koreaFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)

    fun Daylist(year: Int, month: Int, day: Int) {
        val dayList = ArrayList<String>()
        var d = day

        dayslist.clear()
        colorlist.clear()
        Datelist.clear()

        calendar.set(year, month - 1, day)
        val lastOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day)
        val currentOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = SimpleDateFormat("E").format(Date(year - 1900, month, day))
        var QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month, day - 1))

        Log.d("20191627", dayOfWeek)

        when (dayOfWeek) {
            "일", "Sun" -> d
            "월", "Mon" -> d -= 1
            "화", "Tue" -> d -= 2
            "수", "Wed" -> d -= 3
            "목", "Thu" -> d -= 4
            "금", "Fri" -> d -= 5
            "토", "Sat" -> d -= 6
        }

        var addday = 0
        for (i: Int in 1..7) {
            if (d < 1) { //날짜가 지난달일때
                addday = (lastOfMonth - abs(d))
                QueryDate =
                    SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month - 1, addday))
            } else if (d > currentOfMonth) { //날짜가 다음달일때
                addday = (d - currentOfMonth)
                QueryDate =
                    SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month + 1, addday))
            } else { // 날짜가 이번달일때
                addday = d
                QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month, addday))
            }

            //글자색 바인딩
            if (colorlist.size == 0) {
                if (d == addday) colorlist.add("#F71E58")
                else colorlist.add("#FDBBCD")
            } //일요일 붉은색
            else if (colorlist.size == 6) {
                if (d == addday) colorlist.add("#1DAFFF")
                else colorlist.add("#BBE7FF")
            }
            //토요일 푸른색
            else if (d == addday) colorlist.add("#646464") // 일반 검정색
            else colorlist.add("#DBDBDB") //지난달 다음달 회색

            //선택한 날짜 '년월일', '7일' 형식 리스트 작성
            Datelist.add(QueryDate)
            dayslist.add(addday.toString())
            d += 1
        }
        getSchedule(Datelist)
    }

    //스케줄 쿼리문 전송 - 반복 적용 전 함수 - date : 20230528
    fun getSchedule(date: ArrayList<String>) {
        viewModelScope.launch {

            IndexList = arrayListOf(
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
            )
            val startDate = "${date[0].slice(IntRange(0, 3))}" + "-" + "${
                date[0].slice(
                    IntRange(
                        4,
                        5
                    )
                )
            }" + "-" + "${date[0].slice(IntRange(6, 7))}" + "T00:00:00+09:00"
            val endDate = "${date[6].slice(IntRange(0, 3))}" + "-" + "${
                date[6].slice(
                    IntRange(
                        4,
                        5
                    )
                )
            }" + "-" + "${date[6].slice(IntRange(6, 7))}" + "T23:59:59+09:00"
            val body = ScheduleRequest(startDate, endDate)

            scheduleRepository.getScheduleByDates(body) { data ->
                /* repeat data parsing */
                val parsedScheduleList = parseRepeatSchedule(data.schedules, body.startDate, startDate, endDate)

                for (parsedSchedule in parsedScheduleList) {
                    val (schedule, position, cnt, timeInterval) = parsedSchedule

                    //format ex 20230531 20230601
                    val scheduleStartDate =
                        schedule.repeatStart?.slice(IntRange(0, 3)) + schedule.repeatStart?.slice(
                            IntRange(5, 6)
                        ) + schedule.repeatStart?.slice(IntRange(8, 9))
                    val scheduleEndDate =
                        schedule.repeatEnd?.slice(IntRange(0, 3)) + schedule.repeatEnd?.slice(
                            IntRange(5, 6)
                        ) + schedule.repeatEnd?.slice(IntRange(8, 9))

                    d("getSchedule", "${schedule.content}, ${scheduleStartDate}, ${scheduleEndDate}, ${schedule.isAllDay}, ${schedule.repeatOption}, ${position}: ${koreaFormatter.parse(schedule.repeatStart)}, ${schedule.location}")

                    /* 일정이 하루 종일이거나, 반복 일정이 아니면서 시작 날짜와 끝 날짜가 다를 경우. */
                    if (schedule.isAllDay || schedule.repeatOption == null && scheduleStartDate != scheduleEndDate) {
                        IndexList_allday.add(parsedSchedule)
                        continue
                    }

                    /* 일정이 반복 일정일 경우 */
                    if (schedule.repeatOption != null && schedule.repeatValue != null) {
                        if (schedule.repeatValue[0] != 'T') {/* 단일 날짜 일정 */
                            IndexList[position].add(schedule)

                        } else {/* 2일 연속 일정 timeInterval 존재하면 무조건 연속된 일정 */
                            d("parsedSchedule", "getSchedule: timeInterval: ${timeInterval}")
                            IndexList_allday.add(parsedSchedule)
                        }

                        continue
                    }

                    when (scheduleStartDate) { /* 하루치 일정 */
                        date[0] -> IndexList[0].add(schedule)
                        date[1] -> IndexList[1].add(schedule)
                        date[2] -> IndexList[2].add(schedule)
                        date[3] -> IndexList[3].add(schedule)
                        date[4] -> IndexList[4].add(schedule)
                        date[5] -> IndexList[5].add(schedule)
                        date[6] -> IndexList[6].add(schedule)
                    }
                }
            }

            _Schedules.value = IndexList
            _SchedulesAllday.value = IndexList_allday


            d("20191630", "after getSchedule: ${IndexList}")
        }
    }

    fun sortSchedule(data: Schedule) {
        val year_start = data.repeatStart?.slice(IntRange(0, 3))
        val month_start = data.repeatStart?.slice(IntRange(5, 6))
        val day_start = data.repeatStart?.slice(IntRange(8, 9))
        val result_start = year_start + month_start + day_start

        val sortedIndex: ArrayList<Schedule>
        //하루치 일정
        when (result_start) {
            Datelist[0] -> {
                IndexList[0].add(data)
                sortedIndex = ArrayList(IndexList[0].sortedBy { it.repeatStart })
                IndexList[0] = sortedIndex
            }
            Datelist[1] -> {
                IndexList[1].add(data)
                sortedIndex = ArrayList(IndexList[1].sortedBy { it.repeatStart })
                IndexList[1] = sortedIndex
            }
            Datelist[2] -> {
                IndexList[2].add(data)
                sortedIndex = ArrayList(IndexList[2].sortedBy { it.repeatStart })
                IndexList[2] = sortedIndex
            }
            Datelist[3] -> {
                IndexList[3].add(data)
                sortedIndex = ArrayList(IndexList[3].sortedBy { it.repeatStart })
                IndexList[3] = sortedIndex
            }
            Datelist[4] -> {
                IndexList[4].add(data)
                sortedIndex = ArrayList(IndexList[4].sortedBy { it.repeatStart })
                IndexList[4] = sortedIndex
            }
            Datelist[5] -> {
                IndexList[5].add(data)
                sortedIndex = ArrayList(IndexList[5].sortedBy { it.repeatStart })
                IndexList[5] = sortedIndex
            }
            Datelist[6] -> {
                IndexList[6].add(data)
                sortedIndex = ArrayList(IndexList[6].sortedBy { it.repeatStart })
                IndexList[6] = sortedIndex
            }
        }
    }

    fun getDates(): ArrayList<String> {
        return Datelist
    }

    fun getToday(): String {
        return timetable_today
    }

    fun scheduleMoved(margin : Int, view: View, index: Int, preIndex: Int){
        val hour = (margin / 72).toInt()
        val min = ((margin % 72) / 6) * 5.toInt()

        val stringHour = "%02d".format(hour)
        val stringMin = "%02d".format(min)

        //moveDate ex: 20230530
        val moveDate = Datelist[index]

        //"2023-04-01T00:00:00.000Z"
        val year = moveDate.substring(0, 4)
        val month = moveDate.substring(4, 6)
        val day = moveDate.substring(6, 8)

        val newDateInfo = "${year}-${month}-${day}T${stringHour}:${stringMin}:00+09:00"

        val preMoveDate = Datelist[preIndex]
        val preYear = preMoveDate.substring(0, 4)
        val preMonth = preMoveDate.substring(4, 6)
        val preDay = preMoveDate.substring(6, 8)
        val preDateInfo = "${preYear}-${preMonth}-${preDay}T00:00:00+09:00"

        d("20191630", "scheduleMoved:${preIndex} ${preDateInfo}, ${newDateInfo})")

        _PreMoveDate.value = preDateInfo
        _MoveDate.value = newDateInfo
        _MoveView.value = view
    }

    /*TODO 로케이션 계산, 시간 계산 확인 */
    suspend fun patchMoved(newRepeatStart: String, preScheduleData: Schedule){
        //"2023-03-07T18:30:00.000Z" X -> "2023-05-07T00:00:00+09:00"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

        // Parse the strings to ZonedDateTime instances
        val preRepeatStart = ZonedDateTime.parse(preScheduleData.repeatStart, formatter).toLocalTime()
        val preRepeatEnd = ZonedDateTime.parse(preScheduleData.repeatEnd, formatter).toLocalTime()


        // Calculate the difference (offset) between the two dates -> 새로운 repeatEnd를 구하기 위함
        val diff = Duration.between(preRepeatStart, preRepeatEnd)

        // calculate the offset in seconds
        val offsetInSeconds = diff.seconds
        val movedStartSeconds = ZonedDateTime.parse(newRepeatStart, formatter).toEpochSecond()
        val newDateSeconds = movedStartSeconds + offsetInSeconds

        // convert the new date seconds to ZonedDateTime with the time zone "Asia/Seoul"
        val newRepeatEndTime =
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(newDateSeconds), ZoneId.of("Asia/Seoul"))
        val newRepeatEnd = newRepeatEndTime.format(formatter)

        //기존 데이터 제거
        for (lists in IndexList) {
            if (lists.contains(preScheduleData)) {
                lists.remove(preScheduleData)
                break
            }
        }

        d("20191630", "patchMoved: preMoveDate : ${PreMoveDate.value}, newRepeatStart : ${newRepeatStart}, newRepeatEnd : ${newRepeatEnd}")
        /* api 호출 */
        if(preScheduleData.repeatOption == null){ /* 단일 일정인 경우 */
            /* 시간 변경 */
            preScheduleData.repeatStart = newRepeatStart
            preScheduleData.repeatEnd = newRepeatEnd

            val body = PostSchedule(
                preScheduleData.content,
                preScheduleData.memo,
                preScheduleData.isAllDay,
                preScheduleData.repeatStart!!,
                preScheduleData.repeatEnd,
                preScheduleData.repeatOption,
                preScheduleData.repeatValue,
                preScheduleData.category?.id,
                emptyList()
            )
            scheduleRepository.submitSchedule(preScheduleData.id, body) {}
        }else{ /* 반복 일정인 경우 */
            val schedule = preScheduleData
            val preDate = PreMoveDate.value!!

            /* 로케이션 계산 */
            calculateLocation(schedule, preDate)

            d("20191630", "patchMoved: ${schedule.location}, ${newRepeatStart}, ${newRepeatEnd}")

            //시간 변경
            callUpdateRepeatScheduleAPI(schedule, preDate, newRepeatStart, newRepeatEnd){
                getSchedule(Datelist)
            }
        }

        sortSchedule(preScheduleData)
        _Schedules.value = IndexList
    }

    private fun callUpdateRepeatScheduleAPI(schedule : Schedule, preChangedDateString : String, newRepeatStart : String, newRepeatEnd : String, callback:(Boolean) -> Unit){
        val serverFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)

        var preChangedDate = koreaFormatter.parse((preChangedDateString))

        d("20191630", "callUpdateRepeatScheduleAPI: schedule.location ${schedule.location} ")

        when(schedule.location){
            0->{//front
                val next = schedule.repeatStart
                var nextDate:Date? = null

                when(schedule.repeatOption){
                    "매일"->{
                        nextDate = FormatDate.nextStartDate(
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "매주"->{
                        nextDate = FormatDate.nextStartDateEveryWeek(
                            schedule.repeatValue!!,
                            1,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "격주"->{
                        nextDate = FormatDate.nextStartDateEveryWeek(
                            schedule.repeatValue!!,
                            2,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "매달"->{
                        nextDate = FormatDate.nextStartDateEveryMonth(
                            schedule.repeatValue!!,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "매년"->{
                        nextDate = FormatDate.nextStartDateEveryYear(
                            schedule.repeatValue!!,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }
                }

                if(nextDate != null) {
                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                            "T"
                        )
                    ) {
                        val repeatStartDate =
                            koreaFormatter.parse(schedule.repeatStart)
                        val datediff = dateDifference(
                            repeatStartDate,
                            nextDate!!
                        )

                        when (schedule.repeatOption) {
                            "매주" -> {
                                val cal = Calendar.getInstance()
                                cal.time = nextDate
                                cal.add(Calendar.DATE, -(datediff % 7))
                                nextDate = cal.time
                            }

                            "격주" -> {
                                val cal = Calendar.getInstance()
                                cal.time = nextDate
                                cal.add(Calendar.DATE, -(datediff % 14))
                                nextDate = cal.time
                            }

                            "매달" -> {
                                if (repeatStartDate.date != nextDate!!.date) {
                                    nextDate!!.date = repeatStartDate.date
                                }
                            }

                            "매년" -> {
                                if (repeatStartDate.month != nextDate!!.month) {
                                    nextDate!!.month = repeatStartDate.month
                                }

                                if (repeatStartDate.date != nextDate!!.date) {
                                    nextDate!!.date = repeatStartDate.date
                                }
                            }
                        }
                    }

                    val calendarviewmodel = CalendarViewModel()

                    d("20191630", "callUpdateRepeatScheduleAPI: ${addTimeOfFirstDateStringToNextDate(schedule.repeatStart!!, nextDate!!)}, $nextDate")
                    calendarviewmodel.submitScheduleFront(
                        schedule.id, PostScheduleFront(
                            schedule.content,
                            schedule.memo.toString(),
                            schedule.category?.id,
                            emptyList(),
                            schedule.isAllDay,
                            null,
                            null,
                            newRepeatStart,
                            newRepeatEnd,
                            addTimeOfFirstDateStringToNextDate(schedule.repeatStart!!, nextDate!!)
                        )
                    ) {
                        callback(true)
                    }
                } else {
                    val calendarviewmodel = CalendarViewModel()

                    calendarviewmodel.submitSchedule(
                        schedule.id, PostSchedule(
                            schedule.content,
                            schedule.memo.toString(),
                            schedule.isAllDay,
                            newRepeatStart,
                            newRepeatEnd,
                            null,
                            null,
                            schedule.category?.id,
                            emptyList()
                        )
                    ) {
                        callback(true)
                    }
                }
            }

            1->{//middle
                var next = koreaFormatter.format(preChangedDate)
                var nextDate:Date? = null


                d("20191630", "callUpdateRepeatScheduleAPI: before ${preChangedDate}")

                when(schedule.repeatOption){
                    "매일"->{
                        nextDate = FormatDate.nextStartDate(
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "매주"->{
                        nextDate = FormatDate.nextStartDateEveryWeek(
                            schedule.repeatValue!!,
                            1,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "격주"->{
                        nextDate = FormatDate.nextStartDateEveryWeek(
                            schedule.repeatValue!!,
                            2,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "매달"->{
                        nextDate = FormatDate.nextStartDateEveryMonth(
                            schedule.repeatValue!!,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }

                    "매년"->{
                        nextDate = FormatDate.nextStartDateEveryYear(
                            schedule.repeatValue!!,
                            next!!,
                            schedule.repeatEnd!!
                        )
                    }
                }

                if(nextDate != null) {
                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                            "T"
                        )
                    ) {
                        val repeatStartDate =
                            koreaFormatter.parse(schedule.repeatStart)

                        val datediff = dateDifference(
                            repeatStartDate,
                            nextDate!!
                        )

                        val datediff2 = dateDifference(
                            repeatStartDate,
                            preChangedDate
                        )

                        when (schedule.repeatOption) {
                            "매주" -> {
                                val cal = Calendar.getInstance()
                                cal.time = nextDate
                                cal.add(Calendar.DATE, -(datediff % 7))
                                nextDate = cal.time

                                cal.time = preChangedDate
                                cal.add(Calendar.DATE, -(datediff2 % 7))
                                preChangedDate = cal.time
                            }

                            "격주" -> {
                                val cal = Calendar.getInstance()
                                cal.time = nextDate
                                cal.add(Calendar.DATE, -(datediff % 14))
                                nextDate = cal.time

                                cal.time = preChangedDate
                                cal.add(Calendar.DATE, -(datediff2 % 14))
                                preChangedDate = cal.time
                            }

                            "매달" -> {
                                if (repeatStartDate.date != nextDate!!.date) {
                                    nextDate!!.date = repeatStartDate.date
                                }

                                if (repeatStartDate.date != preChangedDate.date) {
                                    preChangedDate.date = repeatStartDate.date
                                }
                            }

                            "매년" -> {
                                if (repeatStartDate.month != nextDate!!.month) {
                                    nextDate!!.month = repeatStartDate.month
                                }

                                if (repeatStartDate.month != preChangedDate.month) {
                                    preChangedDate.month = repeatStartDate.month
                                }

                                if (repeatStartDate.date != nextDate!!.date) {
                                    nextDate!!.date = repeatStartDate.date
                                }

                                if (repeatStartDate.date != preChangedDate.date) {
                                    preChangedDate.date = repeatStartDate.date
                                }
                            }
                        }
                    }

                    d("20191630", "callUpdateRepeatScheduleAPI: after ${preChangedDate}")

                    val calendarviewmodel = CalendarViewModel()

                    calendarviewmodel.submitScheduleMiddle(
                        schedule.id, PostScheduleMiddle(
                            schedule.content,
                            schedule.memo.toString(),
                            schedule.category?.id,
                            emptyList(),
                            schedule.isAllDay,
                            null,
                            null,
                            newRepeatStart,
                            newRepeatEnd,
                            addTimeOfFirstDateStringToNextDate(schedule.repeatStart!!, preChangedDate),
                            addTimeOfFirstDateStringToNextDate(schedule.repeatStart!!, nextDate!!)
                        )
                    ) {
                        callback(true)
                    }
                }
            }

            2->{//back
                var pre = koreaFormatter.format(preChangedDate)
                var preDate:Date? = null

                preDate = FormatDate.preStartDate(pre, schedule.repeatOption,schedule.repeatValue)

                if(preDate != null) {
                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                            "T"
                        )
                    ) {
                        val repeatStartDate =
                            koreaFormatter.parse(schedule.repeatStart)
                        val datediff = dateDifference(
                            repeatStartDate,
                            preDate!!
                        )

                        when (schedule.repeatOption) {
                            "매주" -> {
                                val cal = Calendar.getInstance()
                                cal.time = preDate
                                cal.add(Calendar.DATE, -(datediff % 7))
                                preDate = cal.time
                            }

                            "격주" -> {
                                val cal = Calendar.getInstance()
                                cal.time = preDate
                                cal.add(Calendar.DATE, -(datediff % 14))
                                preDate = cal.time
                            }

                            "매달" -> {
                                if (repeatStartDate.date != preDate!!.date) {
                                    preDate!!.date = repeatStartDate.date
                                }
                            }

                            "매년" -> {
                                if (repeatStartDate.month != preDate!!.month) {
                                    preDate!!.month = repeatStartDate.month
                                }

                                if (repeatStartDate.date != preDate!!.date) {
                                    preDate!!.date = repeatStartDate.date
                                }
                            }
                        }
                    }

                    val calendarviewmodel = CalendarViewModel()
                    calendarviewmodel.submitScheduleBack(
                        schedule.id, PostScheduleBack(
                            schedule.content,
                            schedule.memo.toString(),
                            schedule.category?.id,
                            emptyList(),
                            schedule.isAllDay,
                            null,
                            null,
                            newRepeatStart,
                            newRepeatEnd,
                            addTimeOfFirstDateStringToNextDate(schedule.repeatEnd!!, preDate!!)
                        )
                    ) {
                        callback(true)
                    }
                }
            }
        }
    }

    private fun addTimeOfFirstDateStringToNextDate(repeatStart: String, nextDate:Date): String{
        val serverFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)
        val firstDateString = repeatStart
        val secondDateString = serverFormatter.format(nextDate)

        // Parse them into ZonedDateTime instances
        val firstDateTime = ZonedDateTime.parse(firstDateString)
        val secondDateTime = ZonedDateTime.parse(secondDateString)

        // Get the time of the first date
        val firstTime = firstDateTime.toLocalTime()

        // Create a new ZonedDateTime instance with the date of the second instance and time of the first instance
        return ZonedDateTime.of(secondDateTime.toLocalDate(), firstTime, secondDateTime.zone).toString()
    }


    private fun calculateLocation(schedule: Schedule, todayTodo: String){
        d("20191630", "calculateLocation: ${schedule.repeatStart}, ${schedule.repeatStart}, ${schedule.content}")
        if(schedule.repeatValue != null && schedule.repeatValue.contains("T")){
            val startDate = koreaFormatter.parse(schedule.repeatStart)
            val scheduleCalendar = Calendar.getInstance()
            scheduleCalendar.time = startDate

            scheduleCalendar.add(
                Calendar.MILLISECOND,
                schedule.repeatValue.replace("T","").toInt()
            )

            // 2023-05-28T00:00:00+09:00
            val today = koreaFormatter.parse(todayTodo)

            // startDate - today - scheduleCalendar -> 이땐 프론트
            if(date_comparison(startDate, today)<=0 &&
                date_comparison(scheduleCalendar.time, today) >= 0){
                schedule.location = 0
                Log.d("20191630", "스케줄 프론트 here")

                return
            }
        } else {
            val startDate = koreaFormatter.parse(schedule.repeatStart)
            val today = koreaFormatter.parse(todayTodo)

            if (startDate.year == today.year &&
                startDate.month == today.month &&
                startDate.date == today.date
            ) {
                schedule.location = 0
                Log.d("20191630", "스케줄 프론트 here")

                return
            }
        }

        val repeatEndDate = koreaFormatter.parse(schedule.repeatEnd)

        if(schedule.repeatOption != null && schedule.repeatValue != null && repeatEndDate.year < 200) {
            var nextData: Date? = null

            when (schedule.repeatOption) {
                "매일" -> {
                    nextData = FormatDate.nextStartDate(todayTodo, schedule.repeatEnd!!)
                }

                "매주" -> {
                    nextData = FormatDate.nextStartDateEveryWeek(
                        schedule.repeatValue,
                        1,
                        todayTodo,
                        schedule.repeatEnd!!)
                }

                "격주" -> {
                    nextData = FormatDate.nextStartDateEveryWeek(
                        schedule.repeatValue,
                        2,
                        todayTodo,
                        schedule.repeatEnd!!)
                }

                "매달" -> {
                    nextData = FormatDate.nextStartDateEveryMonth(
                        schedule.repeatValue,
                        todayTodo,
                        schedule.repeatEnd!!)
                }

                "매년" -> {
                    nextData = FormatDate.nextStartDateEveryYear(
                        schedule.repeatValue,
                        todayTodo,
                        schedule.repeatEnd!!)
                }
            }

            Log.d("20191630", "nextData:"+nextData.toString())

            if(nextData == null){
                schedule.location = 2
                Log.d("20191630","스케줄 백")

                return
            }
        }

        schedule.location = 1
        Log.d("20191630","스케줄 미들")
        return
    }



    fun getCategories(){
        viewModelScope.launch {
            categoryRepository.getCategories {
                _liveCategoryList.postValue(it)
            }
        }
    }

    private fun zuluTimeStringToKoreaTimeString(dateTimeStr: String): String {
        val korFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

        // Parse the date/time string to an Instant instance
        val dateTimeInstant = Instant.parse(dateTimeStr)

        // Convert the Instant to a ZonedDateTime at "+09:00" timezone
        val korDateTime = dateTimeInstant.atZone(ZoneId.of("Asia/Seoul"))

        // Format the date/time as a string
        val korDateTimeStr = korDateTime.format(korFormatter)

        return korDateTimeStr
    }

    private fun date_comparison(first_date: Date, second_date: Date): Int {
        first_date.hours = 0
        first_date.minutes = 0
        first_date.seconds = 0

        second_date.hours = 0
        second_date.minutes = 0
        second_date.seconds = 0

        return first_date.compareTo(second_date)
    }

    fun dateDifference(d1: Date, d2: Date): Int{
        // Create calendar objects
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        calendar1.time = d1
        calendar2.time = d2

        // Set the dates for the calendar objects
        calendar1.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        calendar2.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val differenceInMillis = Math.abs(calendar2.timeInMillis - calendar1.timeInMillis)

        val differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24)

        return differenceInDays.toInt()
    }


    private fun parseRepeatSchedule(schedules : List<Schedule>, searchDate:String, startDate : String, endDate : String) : ArrayList<ScheduleCalendarData>{
        val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
        val dateformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.KOREAN)

        val parsedScheduleList = ArrayList<ScheduleCalendarData>()

        for (schedule in schedules) {
            /* 기존 상록이 코드 */
            schedule.repeatStart = FormatDate.calendarFormat(schedule.repeatStart!!)
            schedule.repeatEnd = FormatDate.calendarFormat(schedule.repeatEnd!!)

            var repeatStart: Date? = null
            var repeatEnd: Date? = null

            val repeatOption = schedule.repeatOption
            val repeatValue = schedule.repeatValue

            repeatEnd = schedule.repeatEnd?.let { serverformat.parse(it) }
            repeatStart = schedule.repeatStart?.let { serverformat.parse(it) }


            if (repeatOption != null && repeatValue != null) {
                if (!repeatValue.contains("T")) {
                    val calendar = Calendar.getInstance()

                    when (repeatOption) {
                        "매일" -> {
                            Log.d("매일", "개수")
                            var cnt = 0

                            calendar.time = dateformat.parse(startDate) as Date

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                if (date_comparison(calendar.time, repeatStart!!) >= 0) {
                                    parsedScheduleList.add(
                                        ScheduleCalendarData(
                                            schedule.copy(),
                                            cnt,
                                            1
                                        )
                                    )
                                }

                                cnt++
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        "매주" -> {
                            var weeklycnt = 0
                            var cnt = 0

                            calendar.time = dateformat.parse(startDate)

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                if (date_comparison(calendar.time, repeatStart!!) >= 0) {
                                    if (repeatValue[weeklycnt] == '1') {
                                        parsedScheduleList.add(
                                            ScheduleCalendarData(
                                                schedule.copy(),
                                                cnt,
                                                1
                                            )
                                        )
                                    }
                                }

                                weeklycnt++
                                cnt++

                                if (weeklycnt == 7) weeklycnt = 0

                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        "격주" -> {
                            var weeklycnt = 0
                            var cnt = 0
                            var twoweek = true

                            calendar.time = dateformat.parse(startDate)

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                if (date_comparison(calendar.time, repeatStart!!) >= 0) {
                                    if (repeatValue[weeklycnt] == '1' && twoweek) {
                                        parsedScheduleList.add(
                                            ScheduleCalendarData(
                                                schedule.copy(),
                                                cnt,
                                                1
                                            )
                                        )
                                    }
                                }

                                cnt++
                                weeklycnt++

                                if (weeklycnt == 7) {
                                    weeklycnt = 0
                                    twoweek = !twoweek
                                }

                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        "매달" -> {
                            var cnt = 0
                            calendar.time = dateformat.parse(startDate)

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                if (date_comparison(calendar.time, repeatStart!!) >= 0) {
                                    if (repeatValue[calendar.time.date - 1] == '1') {
                                        parsedScheduleList.add(
                                            ScheduleCalendarData(
                                                schedule.copy(),
                                                cnt,
                                                1
                                            )
                                        )
                                    }
                                }

                                cnt++
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        "매년" -> {
                            var cnt = 0

                            val tempStartDate = dateformat.parse(startDate)

                            calendar.time = tempStartDate

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                if (date_comparison(calendar.time, repeatStart!!) >= 0) {
                                    if (repeatValue[calendar.get(Calendar.MONTH)] == '1') {
                                        if (calendar.get(Calendar.DAY_OF_MONTH) == repeatStart.date)
                                            parsedScheduleList.add(
                                                ScheduleCalendarData(
                                                    schedule.copy(),
                                                    cnt,
                                                    1
                                                )
                                            )
                                    }
                                }

                                cnt++

                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }
                    }
                } else {
                    val newRepeatValue = repeatValue.replace("T", "")
                    val repeatstart = serverformat.parse(schedule.repeatStart)
                    val calendar = Calendar.getInstance()

                    calendar.time = repeatstart

                    calendar.add(Calendar.SECOND, newRepeatValue.toInt())

                    val intervaldate = calendar.timeInMillis - repeatstart.time

                    when (repeatOption) {
                        "매주" -> {
                            var cnt = 0
                            val tempStartDate = dateformat.parse(startDate)
                            calendar.time = tempStartDate

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                val startCalendar = Calendar.getInstance()
                                startCalendar.time = repeatStart

                                while (date_comparison(startCalendar.time, calendar.time) < 0) {
                                    startCalendar.add(Calendar.DAY_OF_MONTH, 7)
                                }

                                if (date_comparison(calendar.time, startCalendar.time) == 0) {
                                    parsedScheduleList.add(
                                        ScheduleCalendarData(
                                            schedule.copy(),
                                            cnt,
                                            null,
                                            intervaldate.toInt()
                                        )
                                    )
                                }

                                cnt++
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        "격주" -> {
                            var cnt = 0
                            val tempStartDate = dateformat.parse(startDate)
                            calendar.time = tempStartDate

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                val startCalendar = Calendar.getInstance()
                                startCalendar.time = repeatStart

                                while (date_comparison(startCalendar.time, calendar.time) < 0) {
                                    startCalendar.add(Calendar.DAY_OF_MONTH, 14)
                                }

                                if (date_comparison(calendar.time, startCalendar.time!!) == 0) {
                                    parsedScheduleList.add(
                                        ScheduleCalendarData(
                                            schedule.copy(),
                                            cnt,
                                            null,
                                            intervaldate.toInt()
                                        )
                                    )
                                }

                                cnt++
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        "매달" -> {
                            var cnt = 0
                            val tempStartDate = dateformat.parse(startDate)
                            calendar.time = tempStartDate

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                val startCalendar = Calendar.getInstance()
                                startCalendar.time = repeatStart

                                while (date_comparison(startCalendar.time, calendar.time) < 0) {
                                    startCalendar.add(Calendar.MONTH, 1)
                                }

                                if (date_comparison(calendar.time, startCalendar.time!!) == 0) {
                                    parsedScheduleList.add(
                                        ScheduleCalendarData(
                                            schedule.copy(),
                                            cnt,
                                            null,
                                            intervaldate.toInt()
                                        )
                                    )
                                }

                                cnt++
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        "매년" -> {
                            var cnt = 0
                            val tempStartDate = dateformat.parse(startDate)
                            calendar.time = tempStartDate

                            while ((repeatEnd == null || date_comparison(
                                    calendar.time,
                                    repeatEnd
                                ) <= 0)
                                && date_comparison(calendar.time, dateformat.parse(endDate)) <= 0
                            ) {

                                val startCalendar = Calendar.getInstance()
                                startCalendar.time = repeatStart

                                while (date_comparison(startCalendar.time, calendar.time) < 0) {
                                    startCalendar.add(Calendar.YEAR, 1)
                                }

                                if (date_comparison(calendar.time, startCalendar.time!!) == 0) {
                                    parsedScheduleList.add(
                                        ScheduleCalendarData(
                                            schedule.copy(),
                                            cnt,
                                            null,
                                            intervaldate.toInt()
                                        )
                                    )
                                }

                                cnt++
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }
                    }
                }
            } else {
                var start = false
                val calendar = Calendar.getInstance()

                var startcnt = 0
                var daycnt = 0
                var cnt = 0
                val tempStartDate = dateformat.parse(startDate)
                calendar.time = tempStartDate

                while (date_comparison(calendar.time, dateformat.parse(endDate)) <= 0) {
                    if (date_comparison(calendar.time, repeatStart!!) >= 0
                        && (repeatEnd == null || date_comparison(calendar.time, repeatEnd) <= 0)
                    ) {
                        if (!start) {
                            startcnt = cnt
                            start = true
                        }

                        daycnt++
                    }

                    cnt++

                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                parsedScheduleList.add(ScheduleCalendarData(schedule.copy(), startcnt, daycnt))
            }
        }

        /* 시간 잘못 변환한거 올바르게 재변환 */
        for(scheduleCalendarData in parsedScheduleList){
            val schedule = scheduleCalendarData.schedule
            var startDate = schedule.repeatStart!!
            var endDate = schedule.repeatEnd!!

            schedule.repeatStart = koreaFormatter.format(zuluFormat.parse(startDate))
            schedule.repeatEnd = koreaFormatter.format(zuluFormat.parse(endDate))
        }

        return parsedScheduleList
    }
}