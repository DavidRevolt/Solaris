package com.davidrevolt.core.model

import java.time.Instant

data class Weather(
    val id: Long,
    val latitude: String,
    val longitude: String,
    val units: String,
    val dailyForecasts: List<DailyForecast>,
)

data class DailyForecast(
    val date: Instant, // e.g. "2025-05-06"
    val longDescription: String, // e.g.  "Sunny, more clouds in the afternoon. Temperature 19/28 °C.",
    val shortDescription: String, // e.g. "partly_sunny"
    val icon: Int,
    val temperature: Int,
    val temperatureMin: Int,
    val temperatureMax: Int,
    val windSpeed: Double,
    /** The dominant wind direction (e.g., "N", "SW"). */
    val windDirection: String,
    val windAngle: Int,
    /** Percentage of sky covered by clouds. */
    val cloudCoverTotal: Int,
    val precipitationTotal: Double,
    /** The type of precipitation (e.g., "rain", "snow", "none"). */
    val precipitationType: String
)