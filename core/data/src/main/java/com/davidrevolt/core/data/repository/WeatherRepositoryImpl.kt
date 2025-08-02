package com.davidrevolt.core.data.repository

import com.davidrevolt.core.data.mapper.asEntity
import com.davidrevolt.core.data.mapper.asExternalModel
import com.davidrevolt.core.database.dao.WeatherDao
import com.davidrevolt.core.network.SolarisNetworkDataSource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    private val solarisNetwork: SolarisNetworkDataSource
) : WeatherRepository {
    override fun getAllWeather() =
        weatherDao.getAllWeather().map { it.map { it.asExternalModel() } }

    override fun getWeather(latitude: String, longitude: String) =
        weatherDao.getWeather(latitude, longitude).map { it.asExternalModel() }

    override fun deleteWeather(latitude: String, longitude: String) =
        weatherDao.deleteWeather(latitude, longitude)

    override fun deleteAllWeather() = weatherDao.deleteAllWeather()

    override suspend fun sync(latitude: String, longitude: String): Result<Unit> =
        solarisNetwork.getWeather(latitude, longitude)
            .fold(
                onSuccess = { networkWeather ->
                    val weatherWithDailyForecasts = networkWeather.asEntity()
                    weatherDao.insertWeatherWithForecasts(
                        weatherWithDailyForecasts.weather,
                        weatherWithDailyForecasts.dailyForecasts
                    )
                    Result.success(Unit)
                },
                onFailure = { exception ->
                    Result.failure(exception) // propagate the original failure from solarisNetwork
                }
            )
}



