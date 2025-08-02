package com.davidrevolt.core.database.model.weather

import androidx.room.Embedded
import androidx.room.Relation

// (1) WeatherEntity (1) ─── (many) DailyForecastEntity
data class WeatherWithDailyForecastsEntity(
    @Embedded val weather: WeatherEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherId"
    )
    val dailyForecasts: List<DailyForecastEntity>
)