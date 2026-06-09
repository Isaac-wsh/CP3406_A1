package com.example.cp3406assignment1_utilityapp.model

import java.time.LocalDate
import kotlin.math.roundToInt

// A supported city and the coordinates required by the weather API.
enum class CityOption(
    val label: String,
    val latitude: Double,
    val longitude: Double
) {
    Singapore("Singapore", 1.3521, 103.8198),
    Cairns("Cairns", -16.9186, 145.7781),
    Brisbane("Brisbane", -27.4698, 153.0251)
}

// Activity choices and their corresponding daily hydration goals.
enum class ActivityLevel(
    val label: String,
    val dailyGoalMl: Int
) {
    Low("Low", 2000),
    Medium("Medium", 2500),
    High("High", 3000)
}

// Cup sizes available in Settings and the Quick add section.
enum class CupSize(
    val label: String,
    val millilitres: Int
) {
    Small("150 ml", 150),
    Medium("250 ml", 250),
    Large("350 ml", 350),
    Bottle("500 ml", 500)
}

// Temperature units supported by the weather card.
enum class TemperatureUnit(val label: String) {
    Celsius("Celsius"),
    Fahrenheit("Fahrenheit")
}

// One water logging action with its amount, display time, and date.
data class DrinkLogEntry(
    val amount: Int,
    val time: String,
    val date: String
)

// Total amount of water logged for one date.
data class DailyTotalEntry(
    val date: String,
    val totalAmount: Int
)

// Immutable state containing all values displayed by the app.
data class HydrationUiState(
    val waterDrunk: Int = 0,
    val recordedDate: String = LocalDate.now().toString(),
    val drinkLog: List<DrinkLogEntry> = emptyList(),
    val dailyTotals: List<DailyTotalEntry> =
        listOf(DailyTotalEntry(LocalDate.now().toString(), 0)),
    val selectedCity: CityOption = CityOption.Singapore,
    val selectedActivityLevel: ActivityLevel = ActivityLevel.Medium,
    val selectedCupSize: CupSize = CupSize.Medium,
    val selectedTemperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
    val temperatureCelsius: Double? = null,
    val isWeatherLoading: Boolean = false,
    val weatherError: String? = null
) {
    // Activity level provides the starting point for today's hydration target.
    val baseWaterGoal: Int
        get() = selectedActivityLevel.dailyGoalMl

    // Live warm or hot weather adds a small, easy-to-understand adjustment.
    val weatherGoalAdjustment: Int
        get() = when {
            temperatureCelsius == null -> 0
            temperatureCelsius >= 30 -> 500
            temperatureCelsius >= 24 -> 250
            else -> 0
        }

    val waterGoal: Int
        get() = baseWaterGoal + weatherGoalAdjustment

    val remainingWater: Int
        get() = (waterGoal - waterDrunk).coerceAtLeast(0)

    val suggestedNextDrink: Int
        get() = if (remainingWater == 0) {
            0
        } else {
            selectedCupSize.millilitres.coerceAtMost(remainingWater)
        }

    // One concise status line gives the user the most important information first.
    val hydrationStatus: String
        get() = when {
            waterDrunk >= waterGoal -> "Daily goal reached"
            waterDrunk == 0 -> "$waterGoal ml planned for today"
            else -> "$remainingWater ml remaining"
        }

    val goalExplanation: String
        get() = if (weatherGoalAdjustment > 0) {
            "${selectedActivityLevel.label} activity sets a $baseWaterGoal ml base goal, " +
                "plus $weatherGoalAdjustment ml for today's weather."
        } else {
            "${selectedActivityLevel.label} activity sets today's $baseWaterGoal ml goal."
        }

    val displayedTemperature: String
        get() {
            val temperature = temperatureCelsius ?: return "--"
            return when (selectedTemperatureUnit) {
                TemperatureUnit.Celsius -> "${temperature.roundToInt()} C"
                TemperatureUnit.Fahrenheit -> "${((temperature * 9 / 5) + 32).roundToInt()} F"
            }
        }

    val weatherRecommendation: String
        get() = when {
            temperatureCelsius == null ->
                "Weather-based advice will appear when current data is available."
            temperatureCelsius >= 30 ->
                "Hot weather adds 500 ml to today's hydration goal."
            temperatureCelsius >= 24 ->
                "Warm weather adds 250 ml to today's hydration goal."
            else ->
                "Cool weather detected. No weather adjustment is needed."
        }

    val progress: Float
        get() = (waterDrunk / waterGoal.toFloat()).coerceIn(0f, 1f)
}
