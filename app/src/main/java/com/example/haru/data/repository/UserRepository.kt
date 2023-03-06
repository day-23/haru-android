package com.example.haru.data.repository

import com.example.haru.data.api.UserService
import com.example.haru.data.model.User

class UserRepository(private val userService: UserService) {
    suspend fun getUser(userId: Int): User {
        return userService.getUser(userId)
    }
}
