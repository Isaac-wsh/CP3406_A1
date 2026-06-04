package com.example.cp3406assignment1_utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
