package com.example.haru.data.repository

import com.example.haru.data.api.TagService
import com.example.haru.data.model.Tag
import com.example.haru.data.model.User

class TagRepository(private val tagService: TagService) {
    suspend fun getTag(user: User): List<Tag> {
        return tagService.getTag(user)
    }
}