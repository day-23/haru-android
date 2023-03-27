package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.model.TodoRequest
import com.example.haru.utils.FormatDate
import java.util.*

class TodoAddViewModel(checkListViewModel: CheckListViewModel) : ViewModel() {
    private val checklistViewModel: CheckListViewModel

    private val repeatOptionList = listOf<String>("매일", "매주", "2주마다", "매월", "매년")

    private val _flagTodo = MutableLiveData<Boolean>(false)
    val flagTodo: LiveData<Boolean> = _flagTodo

    private val _todayTodo = MutableLiveData<Boolean>(false)
    val todayTodo: LiveData<Boolean> = _todayTodo

    private val _isSelectedEndDateTime = MutableLiveData<Boolean>(false)
    val isSelectedEndDateTime: LiveData<Boolean> = _isSelectedEndDateTime

    private val _endDateSwitch = MutableLiveData<Boolean>()
    val endDateSwitch: LiveData<Boolean> = _endDateSwitch

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> = _endDate

    private val _endTime = MutableLiveData<Date?>()
    val endTime: LiveData<Date?> = _endTime

    private val _alarmSwitch = MutableLiveData<Boolean>(false)
    val alarmSwitch: LiveData<Boolean> = _alarmSwitch

    private val _repeatSwitch = MutableLiveData<Boolean>()
    val repeatSwitch: LiveData<Boolean> = _repeatSwitch

    private val _repeatEndDateSwitch = MutableLiveData<Boolean>(false)
    val repeatEndDateSwitch: LiveData<Boolean> = _repeatEndDateSwitch

    private val _repeatOption = MutableLiveData<Int?>()
    val repeatOption: LiveData<Int?> = _repeatOption

    private val _alarmDate = MutableLiveData<Date?>()
    val alarmDate: LiveData<Date?> = _alarmDate

    private val _alarmTime = MutableLiveData<Date?>()
    val alarmTime: LiveData<Date?> = _alarmTime

    private val _repeatEndDate = MutableLiveData<Date?>()
    val repeatEndDate: LiveData<Date?> = _repeatEndDate

    var tagList: MutableList<String> = mutableListOf()
    var subTodos: MutableList<String> = mutableListOf()

    var tag: String = ""
    var content: String = ""

    var memo: String = ""
    var repeatValue: String? = null
    var endDateStr: String? = null
    var alarmDateTimeStr: String? = null
    var repeatEndDateStr: String? = null

    var endTimeLayoutHeight: Int = 0

    var repeatSetLayoutHeight: Int = 0
    var repeatOptionHeight: Int = 0
    var repeatWeekHeight: Int = 0
    var repeatEndDateHeight: Int = 0
    var gridMonthHeight: Int = 0
    var gridYearHeight: Int = 0

    init {
        this.checklistViewModel = checkListViewModel
    }


    fun setFlagTodo() {
        _flagTodo.value = (_flagTodo.value == false)
    }

    fun setTodayTodo() {
        _todayTodo.value = (_todayTodo.value == false)
    }

    fun setIsSelectedEndDateTime() {
        _isSelectedEndDateTime.value = (_isSelectedEndDateTime.value == false)
    }

    fun setEndDateSwitch() {
        if (_endDateSwitch.value == null) _endDateSwitch.value = true
        else _endDateSwitch.value = (_endDateSwitch.value == false)

        if (endDateSwitch.value == false) _isSelectedEndDateTime.value = false
    }

    fun setAlarmSwitch() {
        _alarmSwitch.value = (_alarmSwitch.value == false)
    }

    fun setRepeatSwitch() {
        if (_repeatSwitch.value == null) _repeatSwitch.value = true
        else _repeatSwitch.value = (_repeatSwitch.value == false)

    }

    fun setRepeatEndSwitch() {
        _repeatEndDateSwitch.value = (_repeatEndDateSwitch.value == false)
    }

    fun setRepeatOpt(num: Int) {
        _repeatOption.value = num
    }

    fun setTime(id: Int, date: Date) {
        if (id == 0) _endTime.value = date else _alarmTime.value = date
    }

    fun setDate(id: Int, date: Date) {
        when (id) {
            0 -> _endDate.value = date
            1 -> _alarmDate.value = date
            else -> _repeatEndDate.value = date
        }
    }

    fun setEndTimeHeight(h: Int) {
        endTimeLayoutHeight = h
    }

    fun setRepeatSetLayouH(h: Int) {
        repeatSetLayoutHeight = h
    }

    fun setRepeatOptionH(h: Int) {
        repeatOptionHeight = h
    }

    fun setWeekHeight(h: Int) {
        repeatWeekHeight = h
    }

    fun setMonthHeight(h: Int) {
        gridMonthHeight = h
    }

    fun setYearHeight(h: Int) {
        gridYearHeight = h
    }

    fun setRepeatEndDateH(h: Int) {
        repeatEndDateHeight = h
    }

    fun readyToSubmit() {
        if (tag == "" || tag.replace(" ", "") == "")
            tagList = mutableListOf()
        else tagList = tag.split(" ") as MutableList<String>

        endDateStr = if (endDateSwitch.value == true) {
            if (!isSelectedEndDateTime.value!!) FormatDate.dateToStr(endDate.value!!)
            else {
                FormatDate.dateToStr(endDate.value!!).substring(
                    0,
                    10
                ) + FormatDate.dateToStr(endTime.value!!).substring(10)
            }
        } else {
            null
        }


        alarmDateTimeStr =
            if (alarmSwitch.value == true && alarmDate.value != null && alarmTime.value != null)
                FormatDate.dateToStr(alarmDate.value!!).substring(
                    0,
                    10
                ) + FormatDate.dateToStr(alarmTime.value!!).substring(10)
            else null

        repeatEndDateStr =
            if (repeatSwitch.value == true && repeatEndDateSwitch.value == true && repeatEndDate.value != null)
                FormatDate.dateToStr(repeatEndDate.value!!)
            else null
    }

    fun setRepeatVal(value: String?) {
        repeatValue = value
    }

    //
    private fun createTodoData(): TodoRequest {
        return TodoRequest(
            content = content,
            memo = memo,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isSelectedEndDateTime = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) repeatOptionList[repeatOption.value!!] else "null",
            repeatValue = repeatValue,
            repeatEnd = repeatEndDateStr,
            tags = tagList,
            subTodos = subTodos,
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!)
        )
    }

    fun addTodo(callback: () -> Unit) {
        checklistViewModel.addTodo(createTodoData()) {
            callback()
        }

    }


}