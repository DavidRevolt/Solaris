package com.davidrevolt.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davidrevolt.core.database.model.weather.DailyForecastEntity
import com.davidrevolt.core.database.model.weather.WeatherEntity
import com.davidrevolt.core.database.model.weather.WeatherWithDailyForecastsEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant

// @Transaction -> op inside the function run atomically—either all succeed or all fail together.
// Because Forecast entity is cascading with Weather we use @Transaction (the functions use 2-ops on the DB)
// Delete op don't needs Transaction on one to many

@Dao
interface WeatherDao {

    @Transaction
    @Query("SELECT * FROM weather")
    fun getAllWeather(): Flow<List<WeatherWithDailyForecastsEntity>>

    @Transaction
    @Query("SELECT * FROM weather WHERE latitude=:latitude AND longitude=:longitude")
    fun getWeather(latitude: String, longitude: String): Flow<WeatherWithDailyForecastsEntity>

    @Query("DELETE FROM weather")
    fun deleteAllWeather()

    @Query("DELETE FROM weather WHERE latitude=:latitude AND longitude=:longitude")
    fun deleteWeather(latitude: String, longitude: String)

    @Transaction
    suspend fun insertWeatherWithForecasts(
        weather: WeatherEntity,
        forecasts: List<DailyForecastEntity>
    ) {
        val weatherId = insertWeather(weather)
        val newForecasts = forecasts.map { it.copy(weatherId = weatherId) }
        insertForecasts(newForecasts)
    }


    // Replace, [unlike @Upsert] handle unique fields, It deletes the old row and inserts the new one.
    // That will cause auto deletion of the old children(forecasts) because CASCADE.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity): Long

    @Insert
    suspend fun insertForecasts(forecasts: List<DailyForecastEntity>)


    // for future sync all outdated use
    /**
     * Returns all weather entities with a timestamp value before specified target time.
     * @param target The Instant representing the cutoff time
     * @return A list of WeatherEntity objects with timestamp before the target instant
     */
    @Query("SELECT * FROM weather WHERE timestamp < :target")
    @Transaction
    suspend fun getAllWeatherWithTimestampUpTo(target: Instant): List<WeatherEntity>
}