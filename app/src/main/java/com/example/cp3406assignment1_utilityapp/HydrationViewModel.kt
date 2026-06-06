package com.example.cp3406assignment1_utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cp3406assignment1_utilityapp.data.repository.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

// Records one water logging action with its amount and display time.
data class DrinkLogEntry(
    val amount: Int,
    val time: String,
    val date: String
)

// Records the total amount of water logged for one day.
data class DailyTotalEntry(
    val date: String,
    val totalAmount: Int
)

// Holds all hydration and settings values needed by the UI.
data class HydrationUiState(
    // Current amount of water recorded for today.
    val waterDrunk: Int = 0,
    // Date key used to detect when the daily record should reset.
    val recordedDate: String = todayDateKey(),
    // List of water entries logged today.
    val drinkLog: List<DrinkLogEntry> = emptyList(),
    // List of daily totals for today and past days in this app session.
    val dailyTotals: List<DailyTotalEntry> = listOf(DailyTotalEntry(todayDateKey(), 0)),
    // User-selected city used when requesting current weather.
    val selectedCity: String = "Singapore",
    // User-selected activity level used to calculate the daily water goal.
    val selectedActivityLevel: String = "Medium",
    // User-selected cup size used as the first quick add option.
    val selectedCupSize: String = "250 ml",
    // User-selected temperature unit used by the weather card.
    val selectedTemperatureUnit: String = "Celsius",
    // Latest temperature loaded from the Open-Meteo API.
    val temperatureCelsius: Double? = null,
    // Indicates that a weather request is currently running.
    val isWeatherLoading: Boolean = false,
    // User-friendly message shown if the weather request fails.
    val weatherError: String? = null
) {
    // Daily water target based on the selected activity level.
    val waterGoal: Int
        get() = when (selectedActivityLevel) {
            "Low" -> 2000
            "High" -> 3000
            else -> 2500
        }

    // Temperature text shown on the weather card.
    val displayedTemperature: String
        get() {
            val currentTemperature = temperatureCelsius ?: return "--"
            return if (selectedTemperatureUnit == "Fahrenheit") {
                "${Math.round((currentTemperature * 9 / 5) + 32)} F"
            } else {
                "${Math.round(currentTemperature)} C"
            }
        }

    // Weather-aware message based on the live temperature.
    val weatherRecommendation: String
        get() = when {
            temperatureCelsius == null -> "Weather-based advice will appear when current data is available."
            temperatureCelsius >= 30 -> "Hot weather detected. Consider adding an extra drink today."
            temperatureCelsius >= 24 -> "Warm weather detected. Keep water nearby throughout the day."
            else -> "Cool weather detected. Continue drinking regularly."
        }

    // Numeric cup size used by the Quick add section.
    val preferredCupSize: Int
        get() = selectedCupSize.removeSuffix(" ml").toIntOrNull() ?: 250

    // Progress value for the progress bar, capped at 100%.
    val progress: Float
        get() = (waterDrunk / waterGoal.toFloat()).coerceIn(0f, 1f)
}

// ViewModel that owns app state and exposes events for the Compose UI.
class HydrationViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    // Compose observes this state and redraws the UI when it changes.
    var uiState by mutableStateOf(HydrationUiState())
        private set

    init {
        refreshWeather()
    }

    // Adds a selected water amount to today's total.
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

    // Clears today's recorded water intake.
    fun resetWater() {
        val today = todayDateKey()
        uiState = uiState.copy(
            waterDrunk = 0,
            recordedDate = today,
            drinkLog = emptyList(),
            dailyTotals = updateDailyTotal(uiState.dailyTotals, today, 0)
        )
    }

    // Updates the city setting selected by the user.
    fun selectCity(city: String) {
        uiState = uiState.copy(selectedCity = city)
        refreshWeather()
    }

    // Updates the activity level that controls the daily goal.
    fun selectActivityLevel(activityLevel: String) {
        uiState = uiState.copy(selectedActivityLevel = activityLevel)
    }

    // Updates the preferred cup size used by Quick add.
    fun selectCupSize(cupSize: String) {
        uiState = uiState.copy(selectedCupSize = cupSize)
    }

    // Updates whether temperatures are shown in Celsius or Fahrenheit.
    fun selectTemperatureUnit(temperatureUnit: String) {
        uiState = uiState.copy(selectedTemperatureUnit = temperatureUnit)
    }

    // Requests fresh weather data for the currently selected city.
    fun refreshWeather() {
        viewModelScope.launch {
            uiState = uiState.copy(isWeatherLoading = true, weatherError = null)
            runCatching {
                weatherRepository.getCurrentWeather(uiState.selectedCity)
            }.onSuccess { weather ->
                uiState = uiState.copy(
                    temperatureCelsius = weather.temperatureCelsius,
                    isWeatherLoading = false,
                    weatherError = null
                )
            }.onFailure {
                uiState = uiState.copy(
                    isWeatherLoading = false,
                    weatherError = "Unable to load current weather. Check your connection and try again."
                )
            }
        }
    }

    // Resets the daily water amount if the app is still open on a new day.
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
        // Factory supplies the repository dependency when Compose creates the ViewModel.
        fun factory(weatherRepository: WeatherRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HydrationViewModel(weatherRepository) as T
                }
            }
    }
}

// Creates a stable local date key for daily reset checks.
private fun todayDateKey(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

// Creates a readable time label for each drink log entry.
private fun currentTimeLabel(): String {
    return SimpleDateFormat("h:mm a", Locale.US).format(Date())
}

// Updates the total for a date while keeping previous daily summaries.
private fun updateDailyTotal(
    dailyTotals: List<DailyTotalEntry>,
    date: String,
    totalAmount: Int
): List<DailyTotalEntry> {
    val withoutDate = dailyTotals.filterNot { it.date == date }
    return listOf(DailyTotalEntry(date, totalAmount)) + withoutDate
}

// Adds a zero total for a new date if it is not already in the summary list.
private fun ensureDailyTotalExists(
    dailyTotals: List<DailyTotalEntry>,
    date: String
): List<DailyTotalEntry> {
    return if (dailyTotals.any { it.date == date }) {
        dailyTotals
    } else {
        listOf(DailyTotalEntry(date, 0)) + dailyTotals
    }
}
