package com.example.cp3406assignment1_utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cp3406assignment1_utilityapp.data.repository.WeatherRepository
import com.example.cp3406assignment1_utilityapp.model.ActivityLevel
import com.example.cp3406assignment1_utilityapp.model.CityOption
import com.example.cp3406assignment1_utilityapp.model.CupSize
import com.example.cp3406assignment1_utilityapp.model.DailyTotalEntry
import com.example.cp3406assignment1_utilityapp.model.DrinkLogEntry
import com.example.cp3406assignment1_utilityapp.model.HydrationUiState
import com.example.cp3406assignment1_utilityapp.model.TemperatureUnit
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

// Owns app state and handles user actions and weather requests.
class HydrationViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    var uiState by mutableStateOf(HydrationUiState())
        private set

    init {
        refreshWeather()
    }

    fun addWater(amount: Int) {
        refreshDailyIntakeIfNeeded()
        val newTotal = uiState.waterDrunk + amount
        uiState = uiState.copy(
            waterDrunk = newTotal,
            drinkLog = listOf(
                DrinkLogEntry(
                    amount = amount,
                    time = currentTimeLabel(),
                    date = uiState.recordedDate
                )
            ) + uiState.drinkLog,
            dailyTotals = updateDailyTotal(uiState.dailyTotals, uiState.recordedDate, newTotal)
        )
    }

    fun resetWater() {
        val today = todayDateKey()
        uiState = uiState.copy(
            waterDrunk = 0,
            recordedDate = today,
            drinkLog = emptyList(),
            dailyTotals = updateDailyTotal(uiState.dailyTotals, today, 0)
        )
    }

    fun selectCity(city: CityOption) {
        uiState = uiState.copy(selectedCity = city)
        refreshWeather()
    }

    fun selectActivityLevel(activityLevel: ActivityLevel) {
        uiState = uiState.copy(selectedActivityLevel = activityLevel)
    }

    fun selectCupSize(cupSize: CupSize) {
        uiState = uiState.copy(selectedCupSize = cupSize)
    }

    fun selectTemperatureUnit(temperatureUnit: TemperatureUnit) {
        uiState = uiState.copy(selectedTemperatureUnit = temperatureUnit)
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val requestedCity = uiState.selectedCity
            uiState = uiState.copy(isWeatherLoading = true, weatherError = null)
            runCatching {
                weatherRepository.getCurrentWeather(requestedCity)
            }.onSuccess { weather ->
                // Ignore a late response if the user selected another city meanwhile.
                if (uiState.selectedCity == requestedCity) {
                    uiState = uiState.copy(
                        temperatureCelsius = weather.temperatureCelsius,
                        isWeatherLoading = false,
                        weatherError = null
                    )
                }
            }.onFailure {
                if (uiState.selectedCity == requestedCity) {
                    uiState = uiState.copy(
                        isWeatherLoading = false,
                        weatherError =
                            "Unable to load current weather. Check your connection and try again."
                    )
                }
            }
        }
    }

    private fun refreshDailyIntakeIfNeeded() {
        val today = todayDateKey()
        if (uiState.recordedDate != today) {
            uiState = uiState.copy(
                waterDrunk = 0,
                recordedDate = today,
                drinkLog = emptyList(),
                dailyTotals = ensureDailyTotalExists(uiState.dailyTotals, today)
            )
        }
    }

    companion object {
        fun factory(weatherRepository: WeatherRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HydrationViewModel(weatherRepository) as T
                }
            }
    }
}

private fun todayDateKey(): String = LocalDate.now().toString()

private fun currentTimeLabel(): String =
    LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))

private fun updateDailyTotal(
    dailyTotals: List<DailyTotalEntry>,
    date: String,
    totalAmount: Int
): List<DailyTotalEntry> {
    val totalsForOtherDates = dailyTotals.filterNot { it.date == date }
    return listOf(DailyTotalEntry(date, totalAmount)) + totalsForOtherDates
}

private fun ensureDailyTotalExists(
    dailyTotals: List<DailyTotalEntry>,
    date: String
): List<DailyTotalEntry> =
    if (dailyTotals.any { it.date == date }) {
        dailyTotals
    } else {
        listOf(DailyTotalEntry(date, 0)) + dailyTotals
    }
