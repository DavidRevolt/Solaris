package com.davidrevolt.core.network.model.networkweather

data class NetworkWeather(
    val current: Any,
    val daily: Daily?,
    val elevation: Int,
    val hourly: Any,
    val lat: String,
    val lon: String,
    val timezone: String,
    val units: String
)