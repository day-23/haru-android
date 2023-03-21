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
import com.example.haru.data.repository.TodoRepository
import com.example.haru.databinding.FragmentChecklistRepeatBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TodoAddViewModel(checkListViewModel: CheckListViewModel) : ViewModel() {
    private val checklistViewModel : CheckListViewModel

    private val repeatOptionList = listOf<String>("매일", "매주", "2주마다", "매월", "매년")

    private val _flagTodo = MutableLiveData<Boolean>(false)
    val flagTodo: LiveData<Boolean> = _flagTodo

    private val _todayTodo = MutableLiveData<Boolean>(false)
    val todayTodo: LiveData<Boolean> = _todayTodo

    private val _isSelectedEndDateTime = MutableLiveData<Boolean>(false)
    val isSelectedEndDateTime : LiveData<Boolean> = _isSelectedEndDateTime

    private val _endTimeSwitch = MutableLiveData<Boolean>(false)
    val endTimeSwitch : LiveData<Boolean> = _endTimeSwitch

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> = _endDate

    private val _endTime = MutableLiveData<Date?>()
    val endTime: LiveData<Date?> = _endTime

    private val _alarmSwitch = MutableLiveData<Boolean>(false)
    val alarmSwitch : LiveData<Boolean> = _alarmSwitch

    private val _repeatSwitch = MutableLiveData<Boolean>(false)
    val repeatSwitch = _repeatSwitch

    private val _repeatOption = MutableLiveData<Int?>()
    val repeatOption: LiveData<Int?> = _repeatOption

    private val _repeatValue = MutableLiveData<String?>()
    val repeatValue : LiveData<String?> = _repeatValue

//    private val _repeatDay = MutableLiveData<List<Boolean>>(List(7) { false })
//    val repeatDay: LiveData<List<Boolean>> = _repeatDay


//    private val _repeatOptionStr = MutableLiveData<String?>()
//    val repeatOptionStr: LiveData<String?> = _repeatOptionStr
//
//    private val _repeatDayStr = MutableLiveData<String?>()
//    val repeatDayStr: LiveData<String?> = _repeatDayStr


    private val _alarmTime = MutableLiveData<Date?>()
    val alarmTime: LiveData<Date?> = _alarmTime

    private val _alarmDate = MutableLiveData<Date?>()
    val alarmDate: LiveData<Date?> = _alarmDate

    private val _repeatEndDate = MutableLiveData<Date?>()
    val repeatEndDate: LiveData<Date?> = _repeatEndDate

//    val tag = MutableLiveData<String>("")

    var tagList: MutableList<String> = mutableListOf()
    var subTodos: MutableList<String> = mutableListOf()

    var tag :String = ""
    var content: String = ""

    var memo: String = ""
    var endDateStr: String = ""
    var alarmDateStr: String = ""
    var endTimeStr: String = ""
    var alarmTimeStr: List<String> = mutableListOf()
    var repeatEndDateStr: String = ""

    init {
        this.checklistViewModel = checkListViewModel
    }


    fun setFlagTodo() {
        _flagTodo.value = (_flagTodo.value == false)
    }

    fun setTodayTodo() {
        _todayTodo.value = (_todayTodo.value == false)
    }

    fun setIsSelectedEndDateTime(){
        _isSelectedEndDateTime.value = (_isSelectedEndDateTime.value == false)
    }

    fun setEndTimeSwitch(){
        _endTimeSwitch.value = (_endTimeSwitch.value == false)
    }

    fun setAlarmSwitch(){
        _alarmSwitch.value = (_alarmSwitch.value == false)
    }

    fun setRepeatSwitch(){
        _repeatSwitch.value = (_repeatSwitch.value == false)
    }

//    fun setRepeatOption(option: Int) {
//        _repeatOption.value = option
//        if (_repeatOption.value != 1)
//            _repeatDay.value = List(7) { false }
//    }
//
//    fun setRepeatDay(day: Int) {
//        if (_repeatOption.value == 1)
//            if (_repeatDay.value!!.get(day) == false)
//                _repeatDay.value =
//                    _repeatDay.value!!.subList(0, day) + true + _repeatDay.value!!.subList(
//                        day + 1,
//                        7
//                    )
//            else _repeatDay.value =
//                _repeatDay.value!!.subList(0, day) + false + _repeatDay.value!!.subList(day + 1, 7)
//    }
//
//    fun setRepeatOptionWithDay() {
//        var str = ""
//        for (i in 0 until 7)
//            str += if (repeatDay.value!!.get(i) == true) "1" else "0"
//        _repeatDayStr.value = str
//        _repeatOptionStr.value = repeatOptionList.get(_repeatOption.value!!)
//    }
//
//    fun setRepeatOptionWithoutDay() {
//        _repeatOptionStr.value = repeatOptionList.get(_repeatOption.value!!)
//    }
//
//    fun setRepeatClear() {
//        _repeatOption.value = null
//        _repeatDay.value = List(7) { false }
//        _repeatOptionStr.value = null
//        _repeatDayStr.value = null
//    }
//
//
    fun setTime(id: Int, date: Date) {
        if (id == 0) _endTime.value = date else _alarmTime.value = date
    }
//
    fun setDate(id: Int, date: Date) {
        if (id == 0) _endDate.value = date
        else if (id == 1) _alarmDate.value = date
        else _repeatEndDate.value = date
    }
//
//    fun formatDate(date: Date): String {
//        val submitDateFormat =
//            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
//        return submitDateFormat.format(date)
//    }
//
//    fun clearSubmitTodo() {
//        _repeatOption.value = null
//        _repeatDay.value = List(7) { false }
//        _flagTodo.value = false
//        _todayTodo.value = false
//        _repeatOptionStr.value = null
//        _repeatDayStr.value = null
//        _alarmTime.value = null
//        _endTime.value = null
//        _endDate.value = null
//        _alarmDate.value = null
//        _repeatEndDate.value = null
//        tagList = mutableListOf()
//        subTodos = mutableListOf()
//
//        content = ""
//        memo = ""
//        tag = ""
//        endDateStr = ""
//        alarmDateStr = ""
//        endTimeStr = ""
//        alarmTimeStr = mutableListOf()
//        repeatEndDateStr = ""
//
//    }
//    fun readyToSubmit() {
//        if (tag == "" || tag.replace(" ", "") == "")
//            tagList = mutableListOf()
//        else tagList = tag.split(" ")
//        if (endDate.value != null) endDateStr = formatDate(endDate.value!!)
//        if (endTime.value != null) endTimeStr = formatDate(endTime.value!!)
//        if (alarmTime.value != null) alarmTimeStr = mutableListOf(formatDate(alarmTime.value!!))
//        if (repeatEndDate.value != null) repeatEndDateStr = formatDate(repeatEndDate.value!!)
//    }
//
//    private fun createTodoData(): TodoRequest {
//        return TodoRequest(
//            content,
//            memo,
//            todayTodo.value!!,
//            flagTodo.value!!,
//            endDateStr,
//            endTimeStr,
//            repeatOptionStr.value,
//            repeatDayStr.value,
//            repeatDayStr.value,
//            repeatEndDateStr,
//            tagList,
//            subTodos,s
//            alarmTimeStr
//        )
//    }

//    fun addTodo(callback: () -> Unit){
//        checklistViewModel.addTodo(createTodoData()){
//            Log.d("20191627", "여기는 왜 안돼")
//            callback()
//        }
//
//    }


}