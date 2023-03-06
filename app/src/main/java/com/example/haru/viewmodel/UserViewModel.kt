package com.example.haru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.User
import com.example.haru.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun fetchUser(userId: Int) {
        viewModelScope.launch {
            try {
                _user.value = userRepository.getUser(userId)
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}