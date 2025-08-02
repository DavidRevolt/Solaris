package com.davidrevolt.core.database.model.weather

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "daily_forecasts",
    foreignKeys = [ForeignKey(
        entity = WeatherEntity::class,
        parentColumns = ["id"],
        childColumns = ["weatherId"],
        onDelete = ForeignKey.CASCADE // will be automatically deleted if parent deleted
    )],
    indices = [Index("weatherId")]
)
data class DailyForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weatherId: Long, // foreign key to Weather
    val date: Instant, // e.g. "2025-05-06"
    val longDescription: String, // e.g.  "Sunny, more clouds in the afternoon. Temperature 19/28 °C.",
    val shortDescription: String, // e.g. "partly_sunny"
    val icon: Int,
    val temperature: Int,
    val temperatureMin: Int,
    val temperatureMax: Int,
    val windSpeed: Double,
    val windDirection: String,
    val windAngle: Int,
    val cloudCoverTotal: Int,
    val precipitationTotal: Double,
    val precipitationType: String
)