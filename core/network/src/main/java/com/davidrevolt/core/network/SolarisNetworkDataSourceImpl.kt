package com.davidrevolt.core.network

import com.davidrevolt.core.network.model.NetworkLocation
import com.davidrevolt.core.network.model.networkweather.NetworkWeather
import com.davidrevolt.core.network.retrofit.RetrofitMeteosourceNetwork
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

// Retrofit use its own Background Dispatcher so no need for Dispatcher.IO
class SolarisNetworkDataSourceImpl @Inject constructor(private val retrofitMeteosourceNetwork: RetrofitMeteosourceNetwork) :
    SolarisNetworkDataSource {

    override suspend fun getWeather(latitude: String, longitude: String): Result<NetworkWeather> =
        handleRetrofitApiCall { retrofitMeteosourceNetwork.getWeather(latitude, longitude) }

    override suspend fun searchLocations(searchQuery: String): Result<List<NetworkLocation>> =
        handleRetrofitApiCall { retrofitMeteosourceNetwork.searchLocations(searchQuery) }

    override suspend fun getLocation(
        latitude: String,
        longitude: String
    ): Result<NetworkLocation> =
        handleRetrofitApiCall { retrofitMeteosourceNetwork.searchLocation(latitude, longitude) }


    // Generic function to handle Retrofit API calls
    private suspend fun <T> handleRetrofitApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> =
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body() // The returned object [null is also a valid response]
                if (body != null) {
                    Timber.i("Network module Successfully fetched data")
                    Result.success(body)
                } else { // Server responded but returned a null object
                    val msg =
                        "Server Responded with null obj [Code: ${response.code()} ${response.message()}]"
                    Timber.e(msg)
                    Result.failure(Exception(msg))
                }
            } else { // Server responded but with error status (e.g., 404, 403)
                val msg =
                    "Server Responded with error [Code: ${response.code()} ${response.message()}]"
                Timber.e(msg)
                Result.failure(Exception("Code: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) { // Exceptions like TimeOut [not responding!] or UnknownHostException
            Timber.e(e, "Network module error")
            Result.failure(e)
        }

}