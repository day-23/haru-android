package com.example.haru.data.model

import android.net.Uri

data class ExternalImages(
    val id: Long,
    val name: String,
    val path: String,
    val absuri: Uri,
)
