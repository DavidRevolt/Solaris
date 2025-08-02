package com.davidrevolt.core.data.repository

import com.davidrevolt.core.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    // Room - backed | Offline First
    fun getAllWeather(): Flow<List<Weather>>
    fun getWeather(latitude: String, longitude: String): Flow<Weather>
    fun deleteWeather(latitude: String, longitude: String)
    fun deleteAllWeather()

    /** Syncs weather data for given Coordinates, [Fetch from network -> Store at local].
     * @param latitude The latitude coordinate.
     * @param longitude The longitude coordinate.
     * @return A [Result] indicating the success or failure of the sync operation.*/
    suspend fun sync(latitude: String, longitude: String): Result<Unit>
}