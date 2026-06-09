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
    val waterGoal: Int
        get() = selectedActivityLevel.dailyGoalMl

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
                "Hot weather detected. Consider adding an extra drink today."
            temperatureCelsius >= 24 ->
                "Warm weather detected. Keep water nearby throughout the day."
            else ->
                "Cool weather detected. Continue drinking regularly."
        }

    val progress: Float
        get() = (waterDrunk / waterGoal.toFloat()).coerceIn(0f, 1f)
}
