package com.davidrevolt.core.data.repository

import com.davidrevolt.core.model.PointOfInterest
import kotlinx.coroutines.flow.Flow

interface POIRepository {
    // Room - backed | Offline First
    fun getAllPOI(): Flow<List<PointOfInterest>>
    fun getNearbyPOI(latitude: String, longitude: String): Flow<List<PointOfInterest>>
    suspend fun deleteNearbyPOI(latitude: String, longitude: String)
    suspend fun deleteAllPOI()

    /** Syncs nearby POI data for given Coordinates, [Fetch from network -> Store at local].
     * @param latitude The latitude coordinate.
     * @param longitude The longitude coordinate.
     * @param extraPromptInfo Additional information to include in the prompt when syncing with Firebase AI.
     * @param limit The maximum number of POI to fetch.
     * @return A [Result] indicating the success or failure of the sync operation.*/
    suspend fun sync(
        latitude: String,
        longitude: String,
        extraPromptInfo: String,
        limit: Int = 3
    ): Result<Unit>

}