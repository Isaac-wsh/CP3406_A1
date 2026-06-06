package com.example.cp3406assignment1_utilityapp.data.repository

import com.example.cp3406assignment1_utilityapp.data.remote.OpenMeteoApi

// Simple weather value returned to the ViewModel without exposing network models.
data class WeatherData(
    val temperatureCelsius: Double
)

// Repository contract keeps the ViewModel independent from Retrofit.
interface WeatherRepository {
    suspend fun getCurrentWeather(city: String): WeatherData
}

// Retrofit-backed repository that selects coordinates and fetches live weather.
class NetworkWeatherRepository(
    private val openMeteoApi: OpenMeteoApi
) : WeatherRepository {
    override suspend fun getCurrentWeather(city: String): WeatherData {
        val location = cityLocations[city] ?: cityLocations.getValue("Singapore")
        val response = openMeteoApi.getCurrentWeather(
            latitude = location.latitude,
            longitude = location.longitude
        )
        return WeatherData(temperatureCelsius = response.current.temperatureCelsius)
    }
}

// Coordinates for the cities available on the Settings screen.
private data class CityLocation(
    val latitude: Double,
    val longitude: Double
)

private val cityLocations = mapOf(
    "Singapore" to CityLocation(latitude = 1.3521, longitude = 103.8198),
    "Cairns" to CityLocation(latitude = -16.9186, longitude = 145.7781),
    "Brisbane" to CityLocation(latitude = -27.4698, longitude = 153.0251)
)
