package com.example.cp3406assignment1_utilityapp

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Owns app state and exposes it as an observable stream for the Compose UI.
class HydrationViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState: StateFlow<HydrationUiState> = _uiState.asStateFlow()

    init {
        refreshWeather()
    }

    fun addWater(amount: Int) {
        refreshDailyIntakeIfNeeded()
        _uiState.update { state ->
            val newTotal = state.waterDrunk + amount
            state.copy(
                waterDrunk = newTotal,
                drinkLog = listOf(
                    DrinkLogEntry(
                        amount = amount,
                        time = currentTimeLabel(),
                        date = state.recordedDate
                    )
                ) + state.drinkLog,
                dailyTotals = updateDailyTotal(state.dailyTotals, state.recordedDate, newTotal)
            )
        }
    }

    fun resetWater() {
        val today = todayDateKey()
        _uiState.update { state ->
            state.copy(
                waterDrunk = 0,
                recordedDate = today,
                drinkLog = emptyList(),
                dailyTotals = updateDailyTotal(state.dailyTotals, today, 0)
            )
        }
    }

    fun selectCity(city: CityOption) {
        _uiState.update { it.copy(selectedCity = city) }
        refreshWeather()
    }

    fun selectActivityLevel(activityLevel: ActivityLevel) {
        _uiState.update { it.copy(selectedActivityLevel = activityLevel) }
    }

    fun selectCupSize(cupSize: CupSize) {
        _uiState.update { it.copy(selectedCupSize = cupSize) }
    }

    fun selectTemperatureUnit(temperatureUnit: TemperatureUnit) {
        _uiState.update { it.copy(selectedTemperatureUnit = temperatureUnit) }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val requestedCity = _uiState.value.selectedCity
            _uiState.update { it.copy(isWeatherLoading = true, weatherError = null) }

            runCatching {
                weatherRepository.getCurrentWeather(requestedCity)
            }.onSuccess { weather ->
                // Ignore a late response if the user selected another city meanwhile.
                _uiState.update { state ->
                    if (state.selectedCity == requestedCity) {
                        state.copy(
                            temperatureCelsius = weather.temperatureCelsius,
                            isWeatherLoading = false,
                            weatherError = null
                        )
                    } else {
                        state
                    }
                }
            }.onFailure {
                _uiState.update { state ->
                    if (state.selectedCity == requestedCity) {
                        state.copy(
                            isWeatherLoading = false,
                            weatherError =
                                "Unable to load current weather. Check your connection and try again."
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    private fun refreshDailyIntakeIfNeeded() {
        val today = todayDateKey()
        _uiState.update { state ->
            if (state.recordedDate != today) {
                state.copy(
                    waterDrunk = 0,
                    recordedDate = today,
                    drinkLog = emptyList(),
                    dailyTotals = ensureDailyTotalExists(state.dailyTotals, today)
                )
            } else {
                state
            }
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
