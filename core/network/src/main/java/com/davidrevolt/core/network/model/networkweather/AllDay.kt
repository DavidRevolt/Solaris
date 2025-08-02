package com.davidrevolt.core.network.model.networkweather

data class AllDay(
    val cloud_cover: CloudCover,
    val icon: Int,
    val precipitation: Precipitation,
    val temperature: Double,
    val temperature_max: Double,
    val temperature_min: Double,
    val weather: String,
    val wind: Wind
)