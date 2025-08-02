package com.davidrevolt.core.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.davidrevolt.core.data.mapper.asEntity
import com.davidrevolt.core.data.mapper.asExternalModel
import com.davidrevolt.core.data.util.FusedLocation
import com.davidrevolt.core.database.dao.LocationDao
import com.davidrevolt.core.database.model.LocationEntity
import com.davidrevolt.core.datastore.PreferencesDataSource
import com.davidrevolt.core.model.Location
import com.davidrevolt.core.network.SolarisNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class LocationRepositoryImpl @Inject constructor(
    @Named("IO")
    private val ioDispatcher: CoroutineDispatcher,
    private val locationDao: LocationDao,
    private val solarisNetwork: SolarisNetworkDataSource,
    private val fusedLocation: FusedLocation,
    private val preferences: PreferencesDataSource
) : LocationRepository {

    override fun getAllLocations(): Flow<List<Location>> =
        locationDao.getAllLocations().map { it.map { it.asExternalModel() } }


    override fun getLocation(id: String): Flow<Location> =
        locationDao.getLocation(id).map(LocationEntity::asExternalModel)


    override suspend fun addLocation(location: Location) {
        if (preferences.currentLocationId().first() == location.id) {
            preferences.keepPersisting(true)
        }
        locationDao.insertLocation(location.asEntity())
    }


    override suspend fun deleteLocation(id: String) =
        locationDao.deleteLocation(id)


    override suspend fun deleteAllLocations() =
        locationDao.deleteAllLocations()


    override fun getCurrentLocationId(): Flow<String?> =
        preferences.currentLocationId()


    override suspend fun searchLocations(searchQuery: String): Result<List<Location>> =
        solarisNetwork.searchLocations(searchQuery).map { it.map { it.asExternalModel() } }


    @RequiresPermission(
        anyOf =
            [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
    )
    override suspend fun syncCurrentLocation(): Result<Location> =
        withContext(ioDispatcher) {
            val deviceCoordinates =
                runCatching { fusedLocation.getCurrentLocationCoordinates() }.getOrElse {
                    return@withContext Result.failure(it) // permission exception, etc..
                }

            // no gps signal, etc...
            if (deviceCoordinates == null) return@withContext Result.failure(Exception("Couldn't get device coordinates"))

            val networkResponse = solarisNetwork.getLocation(
                deviceCoordinates.latitude.toString(),
                deviceCoordinates.longitude.toString()
            ).map { it.asExternalModel() }

            // Attempt to fetch and set new current location
            networkResponse.fold(
                onSuccess = { newLocation ->
                    val previousLocationId = preferences.currentLocationId().first()
                    // Remove previous location if it shouldn't be kept
                    previousLocationId?.let { id ->
                        if (!preferences.keepPersisting()) {
                            deleteLocation(id)
                        }
                    }

                    // settings new the current location
                    preferences.setCurrentLocation(
                        locationId = newLocation.id,
                        keepPersisting = locationDao.containsLocation(newLocation.id)
                    )
                    addLocation(newLocation)
                    return@withContext Result.success(newLocation)
                },
                onFailure = { return@withContext Result.failure(it) }
            )
        }
}