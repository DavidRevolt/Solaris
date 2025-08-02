package com.davidrevolt.core.data.repository

import com.davidrevolt.core.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    // Room, DataStore - backed | Offline First
    fun getAllLocations(): Flow<List<Location>>
    fun getLocation(id: String): Flow<Location?>
    suspend fun addLocation(location: Location)
    suspend fun deleteLocation(id: String)
    suspend fun deleteAllLocations()

    /** Get the location id of the current device location.*/
    fun getCurrentLocationId(): Flow<String?>

    // Network
    /**
     * Search the network for location, e.g: "New York"
     * */
    suspend fun searchLocations(searchQuery: String): Result<List<Location>>


    /** Syncs Current Device Location data,
     * [Get device coordinates -> Fetch Location Data from network -> Store at local].*/
    suspend fun syncCurrentLocation(): Result<Location>

}