package com.example.cp3406assignment1_utilityapp.data.repository

import com.example.cp3406assignment1_utilityapp.data.remote.OpenMeteoApi
import com.example.cp3406assignment1_utilityapp.model.CityOption

// Simple weather value returned to the ViewModel without exposing network models.
data class WeatherData(
    val temperatureCelsius: Double
)

// Repository contract keeps the ViewModel independent from Retrofit.
interface WeatherRepository {
    suspend fun getCurrentWeather(city: CityOption): WeatherData
}

// Retrofit-backed repository that selects coordinates and fetches live weather.
class NetworkWeatherRepository(
    private val openMeteoApi: OpenMeteoApi
) : WeatherRepository {
    override suspend fun getCurrentWeather(city: CityOption): WeatherData {
        val response = openMeteoApi.getCurrentWeather(
            latitude = city.latitude,
            longitude = city.longitude
        )
        return WeatherData(temperatureCelsius = response.current.temperatureCelsius)
    }
}
