package com.example.haru.data.model

import android.net.Uri

data class ExternalImages(
    val id: Long,
    val name: String,
    var path: String,
    var absuri: Uri,
)
