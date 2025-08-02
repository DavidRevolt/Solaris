package com.davidrevolt.core.network.retrofit


import com.davidrevolt.core.network.BuildConfig
import com.davidrevolt.core.network.model.NetworkLocation
import com.davidrevolt.core.network.model.networkweather.NetworkWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.TimeZone

const val SECTIONS = "daily"
const val LANGUAGE = "en"
const val UNITS = "metric"

interface RetrofitMeteosourceNetwork {

    @GET(value = "api/v1/free/point?")
    suspend fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("sections") sections: String = SECTIONS,
        @Query("timezone") timezone: String = TimeZone.getDefault().id,
        @Query("language") language: String = LANGUAGE,
        @Query("units") units: String = UNITS,
        @Query("key") apiKey: String = BuildConfig.METEOSOURCE_API_KEY
    ): Response<NetworkWeather>

    @GET(value = "api/v1/free/find_places_prefix?")
    suspend fun searchLocations(
        @Query("text") query: String,
        @Query("language") language: String = LANGUAGE,
        @Query("key") apiKey: String = BuildConfig.METEOSOURCE_API_KEY
    ): Response<List<NetworkLocation>>

    @GET(value = "api/v1/free/nearest_place?")
    suspend fun searchLocation(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("language") language: String = LANGUAGE,
        @Query("key") apiKey: String = BuildConfig.METEOSOURCE_API_KEY
    ): Response<NetworkLocation>
}