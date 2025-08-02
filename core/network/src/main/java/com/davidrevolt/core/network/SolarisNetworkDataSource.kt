package com.davidrevolt.core.network

import com.davidrevolt.core.network.model.NetworkLocation
import com.davidrevolt.core.network.model.networkweather.NetworkWeather

interface SolarisNetworkDataSource {
    /** @return NetworkWeather with sorted daily forecasts by date. */
    suspend fun getWeather(latitude: String, longitude: String): Result<NetworkWeather>
    suspend fun searchLocations(searchQuery: String): Result<List<NetworkLocation>>
    suspend fun getLocation(latitude: String, longitude: String): Result<NetworkLocation>
}