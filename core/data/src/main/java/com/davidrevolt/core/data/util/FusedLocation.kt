package com.davidrevolt.core.data.util

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * wrapper around the system service FusedLocationProviderClient.
 * Used withContext(ioDispatcher)
 * */
interface FusedLocation {
    /**
     * Returns the most recent cached location from the past currently available.
     *
     * @return A Android sdk's [Location] object if successful.
     *         Returns `null` if no such cached location is available.
     *
     * @throws SecurityException If the app does not have the necessary location permissions
     *         (ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION).
     *         It's crucial to ensure these permissions are granted before calling this function.
     *
     */
    suspend fun getLastLocationCoordinates(): Location?


    /**
     * Returns a single location fix representing the best estimate of the current location of the device.
     *
     * @return A [Location] object containing the latitude and longitude of the current
     *         location, or null if the location could not be determined.
     *
     * @throws SecurityException if the necessary location permissions (ACCESS_FINE_LOCATION or
     *         ACCESS_COARSE_LOCATION) are not granted.
     *
     */
    suspend fun getCurrentLocationCoordinates(): Location?
}


class FusedLocationImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FusedLocation {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(
        anyOf =
            [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
    )
    override suspend fun getLastLocationCoordinates(): Location? =
        locationClient.lastLocation.await()


    @RequiresPermission(
        anyOf =
            [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
    )
    override suspend fun getCurrentLocationCoordinates(): Location? {
        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        val getCurrentLocationTask = locationClient.getCurrentLocation(
            locationRequest,
            CancellationTokenSource().token
        )
            .await() // await to task to complete, extension function of kotlinx-coroutines-play-services

        if (getCurrentLocationTask != null) {
            val locationInfo = "Current device coordinates are \n" +
                    "lat : ${getCurrentLocationTask.latitude}\n" +
                    "long : ${getCurrentLocationTask.longitude}\n" +
                    "fetched at ${System.currentTimeMillis()}"
            Timber.i(locationInfo)
        } else {
            Timber.i("getCurrentLocation returned null")
        }
        return getCurrentLocationTask
    }

}