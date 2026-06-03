package com.example.cp3406assignment1_utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Holds all hydration and settings values needed by the UI.
data class HydrationUiState(
    // Current amount of water recorded for today.
    val waterDrunk: Int = 1200,
    // User-selected city used by the temporary weather display.
    val selectedCity: String = "Singapore",
    // User-selected activity level used to calculate the daily water goal.
    val selectedActivityLevel: String = "Medium",
    // User-selected cup size used as the first quick add option.
    val selectedCupSize: String = "250 ml",
    // User-selected temperature unit used by the weather card.
    val selectedTemperatureUnit: String = "Celsius"
) {
    // Daily water target based on the selected activity level.
    val waterGoal: Int
        get() = when (selectedActivityLevel) {
            "Low" -> 2000
            "High" -> 3000
            else -> 2500
        }

    // Temporary local temperature values used before connecting a real weather API.
    val cityTemperatureCelsius: Int
        get() = when (selectedCity) {
            "Cairns" -> 29
            "Brisbane" -> 26
            else -> 32
        }

    // Temperature text shown on the weather card.
    val displayedTemperature: String
        get() {
            return if (selectedTemperatureUnit == "Fahrenheit") {
                "${(cityTemperatureCelsius * 9 / 5) + 32} F"
            } else {
                "$cityTemperatureCelsius C"
            }
        }

    // Numeric cup size used by the Quick add section.
    val preferredCupSize: Int
        get() = selectedCupSize.removeSuffix(" ml").toIntOrNull() ?: 250

    // Progress value for the progress bar, capped at 100%.
    val progress: Float
        get() = (waterDrunk / waterGoal.toFloat()).coerceIn(0f, 1f)
}

// ViewModel that owns app state and exposes events for the Compose UI.
class HydrationViewModel : ViewModel() {
    // Compose observes this state and redraws the UI when it changes.
    var uiState by mutableStateOf(HydrationUiState())
        private set

    // Adds a selected water amount to today's total.
    fun addWater(amount: Int) {
        uiState = uiState.copy(waterDrunk = uiState.waterDrunk + amount)
    }

    // Clears today's recorded water intake.
    fun resetWater() {
        uiState = uiState.copy(waterDrunk = 0)
    }

    // Updates the city setting selected by the user.
    fun selectCity(city: String) {
        uiState = uiState.copy(selectedCity = city)
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
}
