package com.davidrevolt.core.database.model.weather

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "weather",
    indices = [ // combination of latitude and longitude must be unique.
        Index(value = ["latitude", "longitude"], unique = true)
    ]
)
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: String,
    val longitude: String,
    val units: String,
    val timestamp: Instant = Instant.now()
)
