package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.EtcRepository
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.data.repository.UserRepository
import com.example.haru.utils.FormatDate
import com.example.haru.utils.User
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EtcViewModel : ViewModel() {
    private val etcRepository = EtcRepository()
    private val userRepository = UserRepository()
    private val profileRepository = ProfileRepository()

    private val _todayYearMonth = MutableLiveData<String>()
    val todayYearMonth: LiveData<String> = _todayYearMonth

    private val _itemCount = MutableLiveData<Pair<Int?, Int?>>()
    val itemCount: LiveData<Pair<Int?, Int?>> = _itemCount

    private val _withHaru = MutableLiveData<Long>()
    val withHaru: LiveData<Long> = _withHaru

    // 기본 프사 지정이 되어야함.
    private val _profileImage = MutableLiveData<String>()
    val profileImage : LiveData<String> = _profileImage

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _introduction = MutableLiveData<String>()
    val introduction : LiveData<String> = _introduction

    private val _postCount = MutableLiveData<Int>()
    val postCount : LiveData<Int> = _postCount

    private val _friendCount = MutableLiveData<Int>()
    val friendCount : LiveData<Int> = _friendCount

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _haruId = MutableLiveData<String>()
    val haruId: LiveData<String> = _haruId

    private val _isPublicAccount = MutableLiveData<Boolean>()
    val isPublicAccount: LiveData<Boolean> = _isPublicAccount

    private val _isPostBrowsingEnabled = MutableLiveData<Boolean>()
    val isPostBrowsingEnabled: LiveData<Boolean> = _isPostBrowsingEnabled

    private val _isAllowFeedLike = MutableLiveData<Int>()
    val isAllowFeedLike: LiveData<Int> = _isAllowFeedLike

    private val _isAllowFeedComment = MutableLiveData<Int>()
    val isAllowFeedComment: LiveData<Int> = _isAllowFeedComment

    private val _isAllowSearch = MutableLiveData<Boolean>()
    val isAllowSearch: LiveData<Boolean> = _isAllowSearch


    var year: Int = 0
    var month: Int = 0

    init {
        _name.value = User.name
        _email.value = User.email
        _haruId.value = User.haruId
        _isPublicAccount.value = User.isPublicAccount
        _isPostBrowsingEnabled.value = User.isPostBrowsingEnabled
        _isAllowFeedLike.value = User.isAllowFeedLike
        _isAllowFeedComment.value = User.isAllowFeedComment
        _isAllowSearch.value = User.isAllowSearch
    }

    // itemCount에 값 넣어주는 함수
    private fun getTodoStatistics(body: ScheduleRequest, callback: () -> Unit) {
        viewModelScope.launch {
            etcRepository.getTodoStatistics(body) {
                if (it?.success == true) {
                    _itemCount.postValue(Pair(it.data?.completed, it.data?.totalItems))
                } else {
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }

    // ProfileRepository
    // MypageViewModel -> getUserInfo
    fun getSnsInfo() {
        viewModelScope.launch {
            profileRepository.getUserInfo(User.id){
                Log.e("20191627", it.toString())
                if (it.id != ""){
                    _name.postValue(it.name)
                    _introduction.postValue(it.introduction)
                    _profileImage.postValue(it.profileImage)
                    _postCount.postValue(it.postCount)
                    _friendCount.postValue(it.friendCount)
                } else {
                    Log.e("20191627", "Fail to getSnsInfo")
                    _introduction.postValue("소개를 작성해주세요")
                    _postCount.postValue(0)
                    _friendCount.postValue(0)
                    _profileImage.postValue("")
                }
            }
        }
    }

    // 오늘 날짜 설정
    fun setTodayYearMonth() {
        val startDate: Date
        val endDate: Date
        FormatDate.cal.apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_MONTH, 1)
            year = get(Calendar.YEAR)
            month = get(Calendar.MONTH) + 1

            startDate = time

            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            endDate = time
        }
        getTodoStatistics(body = ScheduleRequest(startDate.toString(), endDate.toString())) {
            _todayYearMonth.postValue(year.toString() + month.toString())
        }
    }

    // 달 더하고 빼고
    fun addSubTodayYearMonth(type: Boolean) { // type = true면 덧셈, false면 뺄셈
        val tmp = if (type) 1 else -1
        val startDate: Date
        val endDate: Date

        FormatDate.cal.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            add(Calendar.MONTH, tmp)
            year = FormatDate.cal.get(Calendar.YEAR)
            month = FormatDate.cal.get(Calendar.MONTH) + 1

            startDate = time

            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            endDate = time
        }
        getTodoStatistics(body = ScheduleRequest(startDate.toString(), endDate.toString())) {
            _todayYearMonth.postValue(year.toString() + month.toString())
        }
    }

    // 하루와 함께한 날짜 계산
    fun calculateWithHaru() {

        val startDate = FormatDate.cal.apply {
            time = FormatDate.strToDate(User.createdAt)!!
            Log.e("20191627", time.toString())
        }.time.time

        val today = FormatDate.cal.apply {
            time = Date()
            Log.e("20191627", time.toString())
        }.time.time
        _withHaru.value = (today - startDate) / (24 * 60 * 60 * 1000) + 1
    }

    fun deleteUserAccount(callback: (it: SuccessFail?) -> Unit) {
        viewModelScope.launch {
            userRepository.deleteUserAccount {
                callback(it)
            }
        }
    }

    fun updateUserInfo(body: Any, callback: (it : SuccessFail?) -> Unit) {
        viewModelScope.launch {
            userRepository.updateUserInfo(body) {
                if (it?.success == true) {
                    when (body) {
                        is UpdateEmail -> {
                            _email.postValue(body.email)
                            User.email = body.email
                        }
                        is UpdateHaruId -> {
                            _haruId.postValue(body.haruId)
                            User.haruId = body.haruId
                        }
                        is UpdateIsAllowFeedComment -> {
                            _isAllowFeedComment.postValue(body.isAllowFeedComment)
                            User.isAllowFeedComment = body.isAllowFeedComment
                        }
                        is UpdateIsAllowFeedLike -> {
                            _isAllowFeedLike.postValue(body.isAllowFeedLike)
                            User.isAllowFeedLike = body.isAllowFeedLike
                        }
                        is UpdateIsAllowSearch -> {
                            _isAllowSearch.postValue(body.isAllowSearch)
                            User.isAllowSearch = body.isAllowSearch
                        }
                        is UpdateIsPostBrowsingEnabled -> {
                            _isPostBrowsingEnabled.postValue(body.isPostBrowsingEnabled)
                            User.isPostBrowsingEnabled = body.isPostBrowsingEnabled
                        }
                        is UpdateIsPublicAccount -> {
                            _isPublicAccount.postValue(body.isPublicAccount)
                            User.isPublicAccount = body.isPublicAccount
                        }
                    }
                } else {
                    Log.e("20191627", it.toString())
                }
                callback(it)
            }
        }
    }

    fun submitIsPublicAccount(callback: (it : SuccessFail?) -> Unit) {
        if (isPublicAccount.value == null)
            return
        val flag = !isPublicAccount.value!!

        updateUserInfo(UpdateIsPublicAccount(flag)){
            callback(it)
        }
    }

    fun submitIsPostBrowsingEnabled(callback: (it: SuccessFail?) -> Unit) {
        if (isPostBrowsingEnabled.value == null)
            return
        val flag = !isPostBrowsingEnabled.value!!

        updateUserInfo(UpdateIsPostBrowsingEnabled(flag)){
            callback(it)
        }
    }

    fun submitIsAllowSearch(callback: (it: SuccessFail?) -> Unit) {
        if (isAllowSearch.value == null)
            return
        val flag = !isAllowSearch.value!!

        updateUserInfo(UpdateIsAllowSearch(flag)){
            callback(it)
        }
    }

    fun submitIsAllowFeedLike(str : String, callback: (it: SuccessFail?) -> Unit) {
        if (isAllowFeedLike.value == null)
            return

        val type = when (str){
            "허용 안 함" -> 0
            "친구만" -> 1
            "모두 허용" -> 2
            else -> 0
        }
        updateUserInfo(UpdateIsAllowFeedLike(type)){
            callback(it)
        }
    }

    fun submitIsAllowFeedComment(str : String, callback: (it: SuccessFail?) -> Unit) {
        if (isAllowFeedComment.value == null)
            return
        val type = when (str){
            "허용 안 함" -> 0
            "친구만" -> 1
            "모두 허용" -> 2
            else -> 0
        }
        updateUserInfo(UpdateIsAllowFeedComment(type)){
            callback(it)
        }
    }

}