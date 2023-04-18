package com.example.haru.viewmodel

import android.os.Build.VERSION_CODES.P
import android.provider.ContactsContract.Profile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MyPageViewModel(): ViewModel() {
    private val ProfileRepository = ProfileRepository()

    private val _Profile = MutableLiveData<com.example.haru.data.model.Profile>()
    val Profile : LiveData<com.example.haru.data.model.Profile>
        get() = _Profile

    var profile_info = com.example.haru.data.model.Profile("","","","")

    init{
        getProfile()
    }
    fun updateProfile(image: MultipartBody.Part){
        viewModelScope.launch{
            ProfileRepository.editProfile(image) {
                if(it.id != "") profile_info = it
            }
            _Profile.value = profile_info
        }
    }

    fun getProfile(){
        viewModelScope.launch {
            ProfileRepository.getProfile {
                if(it.id != "") profile_info = it
            }
            _Profile.value = profile_info
        }
    }
}