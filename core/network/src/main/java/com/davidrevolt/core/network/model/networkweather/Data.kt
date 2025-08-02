package com.davidrevolt.core.network.model.networkweather

data class Data(
    val afternoon: Any,
    val all_day: AllDay,
    val day: String,
    val evening: Any,
    val icon: Int,
    val morning: Any,
    val summary: String,
    val weather: String
)