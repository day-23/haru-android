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
import com.example.haru.view.calendar.CalendarFragment.Companion.TAG
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class TimetableViewModel(val context : Context): ViewModel() {
    private val scheduleRepository = ScheduleRepository()
    private val categoryRepository = CategoryRepository()

    private val _Dates = MutableLiveData<ArrayList<String>>()
    val Dates : LiveData<ArrayList<String>>
        get() = _Dates

    private val _Days = MutableLiveData<ArrayList<String>>()
    val Days : LiveData<ArrayList<String>>
        get() = _Days

    private val _Selected = MutableLiveData<Timetable_date>()
    val Selected : LiveData<Timetable_date>
        get() = _Selected

    private val _Colors = MutableLiveData<ArrayList<String>>()
    val Colors : LiveData<ArrayList<String>>
        get() = _Colors

    private val _liveCategoryList = MutableLiveData<List<Category>>()
    val liveCategoryList: MutableLiveData<List<Category>> get() = _liveCategoryList

    private val _Schedules = MutableLiveData<ArrayList<ArrayList<Schedule>>>()
    val Schedules : LiveData<ArrayList<ArrayList<Schedule>>>
        get() = _Schedules

    private val _SchedulesAllday = MutableLiveData<ArrayList<Schedule>>()
    val SchedulesAllday: LiveData<ArrayList<Schedule>>
        get() = _SchedulesAllday

    val categoryAndSchedulesCombinedData: LiveData<Pair<List<Category>?, ArrayList<ArrayList<Schedule>>>> =
        Transformations.switchMap(_liveCategoryList) { categoryList: List<Category>? ->
            Transformations.map(_Schedules) { schedules ->
                Pair(categoryList, schedules)
            }
        }

    val categoryAndSchedulesAlldayCombinedData: LiveData<Pair<List<Category>?, ArrayList<Schedule>>> =
        Transformations.switchMap(_liveCategoryList) { categoryList: List<Category>? ->
            Transformations.map(_SchedulesAllday) { schedulesAllday ->
                Pair(categoryList, schedulesAllday)
            }
        }


    private val _TodayDay = MutableLiveData<String>()
    val TodayDay : LiveData<String>
        get() = _TodayDay

    private val _MoveDate = MutableLiveData<String>()
    val MoveDate: LiveData<String>
        get() = _MoveDate

    private val _MoveView = MutableLiveData<View>()
    val MoveView: LiveData<View>
        get() = _MoveView

    val calendar = Calendar.getInstance()
    var colorlist: ArrayList<String> = ArrayList()
    var dayslist: ArrayList<String> = ArrayList()
    var Datelist: ArrayList<String> = ArrayList()
    var IndexList: ArrayList<ArrayList<Schedule>> = ArrayList()
    var IndexList_allday: ArrayList<Schedule> = ArrayList()
    var timetable_today = ""
    init {
        timetable_today = SimpleDateFormat("yyyyMMdd").format(Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))
        _TodayDay.value = calendar.get(Calendar.DAY_OF_MONTH).toString()
    }
    fun init_value() {
        _Selected.value = Timetable_date(calendar.get(Calendar.YEAR).toString()+"년" , (calendar.get(Calendar.MONTH)+1).toString()+"월", calendar.get(Calendar.DAY_OF_MONTH).toString())
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
    fun buttonClick(){
        showDatePickerDialog()
    }

    //오늘 날짜로
    fun todayClick(){
        val calendar = Calendar.getInstance()
        Daylist(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        _Days.value = dayslist
        _Selected.value = Timetable_date(calendar.get(Calendar.YEAR).toString()+"년" , (calendar.get(Calendar.MONTH)+1).toString()+"월", calendar.get(Calendar.DAY_OF_MONTH).toString())
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
                _Selected.value = Timetable_date(year.toString()+"년", (month+1).toString()+"월", day.toString())
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
        var QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month, day-1))

        Log.d("20191627", dayOfWeek)

        when(dayOfWeek){
            "일","Sun"-> d
            "월","Mon"-> d -= 1
            "화","Tue"-> d -= 2
            "수","Wed"-> d -= 3
            "목","Thu"-> d -= 4
            "금","Fri"-> d -= 5
            "토","Sat"-> d -= 6
        }

        var addday = 0
        for (i: Int in 1 .. 7){
            if(d < 1){ //날짜가 지난달일때
                addday = (lastOfMonth - abs(d))
                QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month-1, addday))
            }
            else if(d > currentOfMonth){ //날짜가 다음달일때
                addday = (d - currentOfMonth)
                QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month+1, addday))
            }
            else{ // 날짜가 이번달일때
                addday = d
                QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month, addday))
            }

            //글자색 바인딩
            if(colorlist.size == 0) {
                if(d == addday) colorlist.add("#F71E58")
                else colorlist.add("#FDBBCD")
            } //일요일 붉은색
            else if (colorlist.size == 6) {
                if (d == addday) colorlist.add("#1DAFFF")
                else colorlist.add("#BBE7FF")
            }
            //토요일 푸른색
            else if(d == addday) colorlist.add("#646464") // 일반 검정색
            else colorlist.add("#DBDBDB") //지난달 다음달 회색

            //선택한 날짜 '년월일', '7일' 형식 리스트 작성
            Datelist.add(QueryDate)
            dayslist.add(addday.toString())
            d += 1
        }
        getSchedule(Datelist)
    }

    //스케줄 쿼리문 전송
    fun getSchedule(date : ArrayList<String>){
        viewModelScope.launch {
            val emptyschedule = Schedule(0,"", "dummy", "", false, "", "", "", "" , "", Category("","","",false), emptyList(), null, null,)
            IndexList = arrayListOf( arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(),)
            val startDate = "${date[0].slice(IntRange(0, 3))}" + "-" + "${date[0].slice(IntRange(4,5))}" + "-" + "${date[0].slice(IntRange(6,7))}" + "T00:00:00+09:00"
            val endDate = "${date[6].slice(IntRange(0, 3))}" + "-" + "${date[6].slice(IntRange(4,5))}" + "-" + "${date[6].slice(IntRange(6,7))}" + "T23:59:59+09:00"
            val body = ScheduleRequest(startDate, endDate)

            scheduleRepository.getScheduleByDates(date[0], date[6], body) {
                val TodoList = it

                //내용 추출
                for(data in TodoList.schedules){
                    d("Schedules", "getSchedule:${data.repeatStart} ${data.repeatEnd}, ${data.content}, here smell")
                    data.repeatStart = zuluTimeStringToKoreaTimeString(data.repeatStart!!)
                    data.repeatEnd = zuluTimeStringToKoreaTimeString(data.repeatEnd!!)
                    d("Schedules", "getSchedule:${data.repeatStart} ${data.repeatEnd}, ${data.content}, convert after here smell")

                    val year_start = data.repeatStart?.slice(IntRange(0,3))
                    val month_start = data.repeatStart?.slice(IntRange(5,6))
                    val day_start = data.repeatStart?.slice(IntRange(8,9))
                    val result_start = year_start+month_start+day_start

                    val year_end = data.repeatEnd?.slice(IntRange(0,3))
                    val month_end = data.repeatEnd?.slice(IntRange(5,6))
                    val day_end = data.repeatEnd?.slice(IntRange(8,9))
                    val result_end = year_end+month_end+day_end
                    Log.d("ALLDAYsss", "$data")

                    if(data.repeatStart?.slice(IntRange(11,15)) != data.repeatEnd?.slice(IntRange(11,15)) && result_start == result_end){ //하루치 일정
                        when(result_start){
                            date[0] -> IndexList[0].add(data)
                            date[1] -> IndexList[1].add(data)
                            date[2] -> IndexList[2].add(data)
                            date[3] -> IndexList[3].add(data)
                            date[4] -> IndexList[4].add(data)
                            date[5] -> IndexList[5].add(data)
                            date[6] -> IndexList[6].add(data)
                        }
                    }
                    else{
                        IndexList_allday.add(data)// 하루종일 or 하루이상 일정
                    }
                }
                Log.d("ALLDAYsss", "index : $IndexList")
                Log.d("ALLDAYsss", "index : $IndexList_allday")
            }
            d(TAG, "init_value: ${IndexList}")

            _Schedules.value = IndexList
            _SchedulesAllday.value = IndexList_allday
        }
    }

    fun sortSchedule(data : Schedule){
        val year_start = data.repeatStart?.slice(IntRange(0,3))
        val month_start = data.repeatStart?.slice(IntRange(5,6))
        val day_start = data.repeatStart?.slice(IntRange(8,9))
        val result_start = year_start+month_start+day_start

        val sortedIndex: ArrayList<Schedule>
        //하루치 일정
        when(result_start){
            Datelist[0] -> {
                IndexList[0].add(data)
                sortedIndex = ArrayList(IndexList[0].sortedBy { it.repeatStart})
                IndexList[0] = sortedIndex
            }
            Datelist[1] -> {
                IndexList[1].add(data)
                sortedIndex = ArrayList(IndexList[1].sortedBy { it.repeatStart})
                IndexList[1] = sortedIndex
            }
            Datelist[2] -> {
                IndexList[2].add(data)
                sortedIndex = ArrayList(IndexList[2].sortedBy { it.repeatStart})
                IndexList[2] = sortedIndex
            }
            Datelist[3] -> {
                IndexList[3].add(data)
                sortedIndex = ArrayList(IndexList[3].sortedBy { it.repeatStart})
                IndexList[3] = sortedIndex
            }
            Datelist[4] -> {
                IndexList[4].add(data)
                sortedIndex = ArrayList(IndexList[4].sortedBy { it.repeatStart})
                IndexList[4] = sortedIndex
            }
            Datelist[5] -> {
                IndexList[5].add(data)
                sortedIndex = ArrayList(IndexList[5].sortedBy { it.repeatStart})
                IndexList[5] = sortedIndex
            }
            Datelist[6] -> {
                IndexList[6].add(data)
                sortedIndex = ArrayList(IndexList[6].sortedBy { it.repeatStart})
                IndexList[6] = sortedIndex
            }
        }
    }

    fun getDates() : ArrayList<String>{
        return Datelist
    }

    fun getToday() : String{
        return timetable_today
    }

    fun scheduleMoved(margin : Int, view: View, index: Int){
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

        d("Schedules", "newDateInfo: ${newDateInfo}")

        _MoveDate.value = newDateInfo
        _MoveView.value = view
    }

    suspend fun patchMoved(newRepeatStart: String, preScheduleData: Schedule){
        //"2023-03-07T18:30:00.000Z" X -> "2023-05-07T00:00:00+09:00"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

        // Parse the strings to ZonedDateTime instances
        val preRepeatStart = ZonedDateTime.parse(preScheduleData.repeatStart, formatter)
        val preRepeatEnd = ZonedDateTime.parse(preScheduleData.repeatEnd, formatter)

        // Calculate the difference (offset) between the two dates
        val diff = Duration.between(preRepeatStart, preRepeatEnd)

        // calculate the offset in seconds
        val offsetInSeconds = diff.seconds
        val movedStartSeconds = ZonedDateTime.parse(newRepeatStart, formatter).toEpochSecond()
        val newDateSeconds = movedStartSeconds + offsetInSeconds

        // convert the new date seconds to ZonedDateTime with the time zone "Asia/Seoul"
        val newRepeatEndTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(newDateSeconds), ZoneId.of("Asia/Seoul"))
        val newRepeatEnd = newRepeatEndTime.format(formatter)

        //기존 데이터 제거
        for(lists in IndexList){
            if(lists.contains(preScheduleData)){
                lists.remove(preScheduleData)
                break
            }
        }

        preScheduleData.repeatStart = newRepeatStart
        preScheduleData.repeatEnd = newRepeatEnd

        sortSchedule(preScheduleData)
        _Schedules.value = IndexList

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
    }


    fun getCategories(){
        viewModelScope.launch {
            categoryRepository.getCategories {
                _liveCategoryList.postValue(it)
            }
        }
    }


    private fun zuluTimeStringToKoreaTimeString(dateTimeStr : String) : String{
        val korFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

        // Parse the date/time string to an Instant instance
        val dateTimeInstant = Instant.parse(dateTimeStr)

        // Convert the Instant to a ZonedDateTime at "+09:00" timezone
        val korDateTime = dateTimeInstant.atZone(ZoneId.of("Asia/Seoul"))

        // Format the date/time as a string
        val korDateTimeStr = korDateTime.format(korFormatter)

        return korDateTimeStr
    }
}