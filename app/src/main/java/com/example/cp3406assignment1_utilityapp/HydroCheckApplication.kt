package com.example.cp3406assignment1_utilityapp

import android.app.Application
import com.example.cp3406assignment1_utilityapp.data.remote.OpenMeteoApi
import com.example.cp3406assignment1_utilityapp.data.repository.NetworkWeatherRepository
import com.example.cp3406assignment1_utilityapp.data.repository.WeatherRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Application entry point that creates app-wide dependencies once.
class HydroCheckApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer()
    }
}

// Dependency injection container exposing repositories required by the app.
interface AppContainer {
    val weatherRepository: WeatherRepository
}

// Manual dependency injection implementation used by the production app.
class DefaultAppContainer : AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val openMeteoApi = retrofit.create(OpenMeteoApi::class.java)

    override val weatherRepository: WeatherRepository =
        NetworkWeatherRepository(openMeteoApi)
}
