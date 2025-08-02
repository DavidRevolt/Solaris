package com.davidrevolt.core.domain

import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.data.repository.POIRepository
import com.davidrevolt.core.data.repository.WeatherRepository
import com.davidrevolt.core.model.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class DeleteLocationWithRelatedDataUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val poiRepository: POIRepository,
    @Named("IO")
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(location: Location) {
        // Alternatively can cascade in room db
        withContext(ioDispatcher) {
            locationRepository.deleteLocation(location.id)
            weatherRepository.deleteWeather(location.latitude, location.longitude)
            poiRepository.deleteNearbyPOI(location.latitude, location.longitude)
        }
    }
}
