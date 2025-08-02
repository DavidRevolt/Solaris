package com.davidrevolt.core.domain

import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.data.repository.POIRepository
import com.davidrevolt.core.data.repository.WeatherRepository
import com.davidrevolt.core.model.LocationWithRelatedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case to get all locations with related data.
 * [combine data streams from all repositories]
 * */
class GetLocationsWithRelatedDataUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val poiRepository: POIRepository
) {
    operator fun invoke(): Flow<List<LocationWithRelatedData>> =
        combine(
            locationRepository.getAllLocations(),
            weatherRepository.getAllWeather(),
            poiRepository.getAllPOI()
        ) { locations, weather, pointsOfInterest ->
            val weatherMap = weather.associateBy { it.latitude to it.longitude }
            val pointsOfInterestMap = pointsOfInterest.groupBy { it.latitude to it.longitude }

            locations.map { location ->
                val relatedWeather = weatherMap[location.latitude to location.longitude]
                val relatedPointsOfInterest =
                    pointsOfInterestMap[location.latitude to location.longitude] ?: emptyList()
                LocationWithRelatedData(location, relatedWeather, relatedPointsOfInterest)
            }
        }
}
