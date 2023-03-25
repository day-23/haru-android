package com.example.haru.viewmodel

import android.content.Context
import android.provider.ContactsContract.RawContacts.Data
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.Timetable_date
import com.example.haru.data.model.User
import com.example.haru.data.model.timetable_data
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class TimetableViewModel(val context : Context): ViewModel() {

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

    // #F71E58 빨
    // #DBDBDB 회
    // #1DAFFF 파
    // #191919 검

    val calendar = Calendar.getInstance()
    var colorlist: ArrayList<String> = ArrayList()
    var dayslist: ArrayList<String> = ArrayList()
    var Datelist: ArrayList<String> = ArrayList()

    init {
        _Selected.value = Timetable_date(calendar.get(Calendar.YEAR).toString()+"년" , (calendar.get(Calendar.MONTH)+1).toString()+"월", calendar.get(Calendar.DAY_OF_MONTH).toString()+"일")

        Daylist(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        _Days.value = dayslist
        _Colors.value = colorlist
        _Dates.value = Datelist

    }

    //날짜정보//
    fun buttonClick(){
        showDatePickerDialog()
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
                _Selected.value = Timetable_date(year.toString()+"년", (month+1).toString()+"월", day.toString()+"일")
                _Dates.value = Datelist
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
            if(colorlist.size == 0) colorlist.add("#F71E58") //일요일 붉은색
            else if(colorlist.size == 6) colorlist.add("#1DAFFF") //토요일 푸른색
            else if(d == addday) colorlist.add("#191919") // 일반 검정색
            else colorlist.add("#DBDBDB") //지난달 다음달 회색

            //선택한 날짜 '년월일', '7일' 형식 리스트 작성
            Datelist.add(QueryDate)
            dayslist.add(addday.toString())
            d += 1
        }
    }
    //날짜정보//

}