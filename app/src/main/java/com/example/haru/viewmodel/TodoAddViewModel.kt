package com.example.haru.viewmodel

import android.app.Application
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.App
import com.example.haru.R
import com.example.haru.data.model.TodoRequest
import com.example.haru.databinding.FragmentChecklistRepeatBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TodoAddViewModel : ViewModel() {
    private val repeatOptionList = listOf<String>("매일", "매주", "2주마다", "매월", "매년")

    private val _repeatOption = MutableLiveData<Int?>()
    val repeatOption: LiveData<Int?> = _repeatOption

    private val _repeatDay = MutableLiveData<List<Boolean>>(List(7) { false })
    val repeatDay: LiveData<List<Boolean>> = _repeatDay

    private val _flagTodo = MutableLiveData<Boolean>(false)
    val flagTodo: LiveData<Boolean> = _flagTodo

    private val _todayTodo = MutableLiveData<Boolean>(false)
    val todayTodo: LiveData<Boolean> = _todayTodo

    private val _repeatOptionStr = MutableLiveData<String?>()
    val repeatOptionStr: LiveData<String?> = _repeatOptionStr

    private val _repeatDayStr = MutableLiveData<String?>()
    val repeatDayStr: LiveData<String?> = _repeatDayStr

    private val _endDateTime = MutableLiveData<Date?>()
    val endDateTime: LiveData<Date?> = _endDateTime

    private val _alarmDateTime = MutableLiveData<Date?>()
    val alarmDateTime : LiveData<Date?> = _alarmDateTime


    fun setRepeatOption(option: Int) {
        _repeatOption.value = option
        Log.d("20191627", repeatOption.value.toString())
        if (_repeatOption.value != 1)
            _repeatDay.value = List(7) { false }
    }

    fun setRepeatDay(day: Int) {
        if (_repeatOption.value == 1)
            if (_repeatDay.value!!.get(day) == false)
                _repeatDay.value =
                    _repeatDay.value!!.subList(0, day) + true + _repeatDay.value!!.subList(
                        day + 1,
                        7
                    )
            else _repeatDay.value =
                _repeatDay.value!!.subList(0, day) + false + _repeatDay.value!!.subList(day + 1, 7)
        Log.d("20191627", repeatDay.value.toString())
    }

    fun setRepeatOptionWithDay() {
        var str = ""
        for (i in 0 until 7)
            str += if (repeatDay.value!!.get(i) == true) "1" else "0"
        _repeatDayStr.value = str
        _repeatOptionStr.value = repeatOptionList.get(_repeatOption.value!!)
    }

    fun setRepeatOptionWithoutDay() {
        _repeatOptionStr.value = repeatOptionList.get(_repeatOption.value!!)
    }

    fun setRepeatClear() {
        _repeatOption.value = null
        _repeatDay.value = List(7) { false }
        _repeatOptionStr.value = null
        _repeatDayStr.value = null
    }

    fun setFlagTodo() {
        _flagTodo.value = (_flagTodo.value == false)
    }

    fun setTodayTodo() {
        _todayTodo.value = (_todayTodo.value == false)
    }

    fun setTime(id: Int, date: Date) {
        if (id == 0) _endDateTime.value = date else _alarmDateTime.value = date
    }

    fun formatDate(date: Date) : String {
        val submitDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        return submitDateFormat.format(date)
    }
//
//    fun getRepeatOption(): Int? {
//        return repeatOption.value
//    }
//
//    fun clickRepeatDay() {
//
//    }
//
//    fun clickRepeatOption() {
//
//    }
}