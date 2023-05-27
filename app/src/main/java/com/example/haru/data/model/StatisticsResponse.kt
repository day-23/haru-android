package com.example.haru.data.model

data class StatisticsResponse(
    val success : Boolean,
    val data : StatisticsData? = null,
    val error : Error? = null
)

data class StatisticsData(
    val completed : Int,
    val totalItems : Int,
    val startDate : String,
    val endDate : String
)
