package com.example.cp3406assignment1_utilityapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit interface describing the Open-Meteo request used by HydroCheck.
interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m"
    ): OpenMeteoResponse
}

// Network response containing the current weather values requested from Open-Meteo.
data class OpenMeteoResponse(
    val current: CurrentWeatherResponse
)

// Maps Open-Meteo's temperature_2m JSON property to a Kotlin-friendly name.
data class CurrentWeatherResponse(
    @SerializedName("temperature_2m")
    val temperatureCelsius: Double
)
