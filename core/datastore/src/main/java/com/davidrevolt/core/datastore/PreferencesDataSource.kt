package com.davidrevolt.core.datastore

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    /**
     * Sets the given location ID as the new current location.
     *
     * @param locationId The ID of the location to be set as the current location.
     * @param keepPersisting Indicates whether Location should be kept in DB after setting
     * new current location.
     *
     */
    suspend fun setCurrentLocation(locationId: String, keepPersisting: Boolean)
    fun currentLocationId(): Flow<String?>

    /** Keep persisting current Location data in DB even after setting new current location*/
    suspend fun keepPersisting(keepPersisting: Boolean)
    suspend fun keepPersisting(): Boolean

}