package com.davidrevolt.core.data.mapper

import com.davidrevolt.core.database.model.weather.DailyForecastEntity
import com.davidrevolt.core.database.model.weather.WeatherEntity
import com.davidrevolt.core.database.model.weather.WeatherWithDailyForecastsEntity
import com.davidrevolt.core.model.DailyForecast
import com.davidrevolt.core.model.Weather
import com.davidrevolt.core.network.model.networkweather.NetworkWeather
import java.time.LocalDate
import java.util.TimeZone

internal fun WeatherWithDailyForecastsEntity.asExternalModel() =
    Weather(
        id = weather.id,
        latitude = weather.latitude,
        longitude = weather.longitude,
        units = weather.units,
        dailyForecasts = dailyForecasts.map(DailyForecastEntity::asExternalModel)
    )

private fun DailyForecastEntity.asExternalModel() =
    DailyForecast(
        date = date,
        longDescription = longDescription,
        shortDescription = shortDescription,
        icon = icon,
        temperature = temperature.toInt(),
        temperatureMin = temperatureMin.toInt(),
        temperatureMax = temperatureMax.toInt(),
        windSpeed = windSpeed,
        windDirection = windDirection,
        windAngle = windAngle,
        cloudCoverTotal = cloudCoverTotal,
        precipitationTotal = precipitationTotal,
        precipitationType = precipitationType
    )

internal fun NetworkWeather.asEntity() =
    WeatherWithDailyForecastsEntity(
        weather = WeatherEntity(
            latitude = lat,
            longitude = lon,
            units = units
        ),
        dailyForecasts = daily?.data?.map { dailyData ->
            DailyForecastEntity(
                weatherId = 0, // will be updated after inserting `weather` in DAO and getting its ID
                date = LocalDate.parse(dailyData.day)
                    .atStartOfDay(TimeZone.getDefault().toZoneId())
                    .toInstant(),
                longDescription = dailyData.summary,
                shortDescription = dailyData.weather.replace("_", " ")
                    .replaceFirstChar(Char::titlecase),
                icon = dailyData.icon,
                temperature = dailyData.all_day.temperature.toInt(),
                temperatureMin = dailyData.all_day.temperature_min.toInt(),
                temperatureMax = dailyData.all_day.temperature_max.toInt(),
                windSpeed = dailyData.all_day.wind.speed,
                windDirection = dailyData.all_day.wind.dir,
                windAngle = dailyData.all_day.wind.angle,
                cloudCoverTotal = dailyData.all_day.cloud_cover.total,
                precipitationTotal = dailyData.all_day.precipitation.total,
                precipitationType = dailyData.all_day.precipitation.type
            )
        } ?: emptyList()
    )


