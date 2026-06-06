package com.example.cp3406assignment1_utilityapp

import com.example.cp3406assignment1_utilityapp.data.remote.CurrentWeatherResponse
import com.example.cp3406assignment1_utilityapp.data.remote.OpenMeteoApi
import com.example.cp3406assignment1_utilityapp.data.remote.OpenMeteoResponse
import com.example.cp3406assignment1_utilityapp.data.repository.NetworkWeatherRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

// Verifies that the repository maps app cities and network responses correctly.
class WeatherRepositoryTest {
    @Test
    fun getCurrentWeather_returnsTemperatureFromApi() = runBlocking {
        val fakeApi = FakeOpenMeteoApi(temperatureCelsius = 31.5)
        val repository = NetworkWeatherRepository(fakeApi)

        val weather = repository.getCurrentWeather("Singapore")

        assertEquals(31.5, weather.temperatureCelsius, 0.0)
        assertEquals(1.3521, fakeApi.requestedLatitude, 0.0)
        assertEquals(103.8198, fakeApi.requestedLongitude, 0.0)
    }

    @Test
    fun getCurrentWeather_usesSingaporeForUnknownCity() = runBlocking {
        val fakeApi = FakeOpenMeteoApi(temperatureCelsius = 27.0)
        val repository = NetworkWeatherRepository(fakeApi)

        repository.getCurrentWeather("Unknown")

        assertEquals(1.3521, fakeApi.requestedLatitude, 0.0)
        assertEquals(103.8198, fakeApi.requestedLongitude, 0.0)
    }
}

// Test double that avoids making a real network request.
private class FakeOpenMeteoApi(
    private val temperatureCelsius: Double
) : OpenMeteoApi {
    var requestedLatitude: Double = 0.0
        private set
    var requestedLongitude: Double = 0.0
        private set

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        current: String
    ): OpenMeteoResponse {
        requestedLatitude = latitude
        requestedLongitude = longitude
        return OpenMeteoResponse(
            current = CurrentWeatherResponse(temperatureCelsius)
        )
    }
}
