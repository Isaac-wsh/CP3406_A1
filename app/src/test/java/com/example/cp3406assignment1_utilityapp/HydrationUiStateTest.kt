package com.example.cp3406assignment1_utilityapp

import com.example.cp3406assignment1_utilityapp.model.ActivityLevel
import com.example.cp3406assignment1_utilityapp.model.CupSize
import com.example.cp3406assignment1_utilityapp.model.HydrationUiState
import com.example.cp3406assignment1_utilityapp.model.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Verifies the pure calculations that drive hydration information in the UI.
class HydrationUiStateTest {
    @Test
    fun waterGoal_matchesSelectedActivityLevel() {
        assertEquals(
            2000,
            HydrationUiState(selectedActivityLevel = ActivityLevel.Low).waterGoal
        )
        assertEquals(
            2500,
            HydrationUiState(selectedActivityLevel = ActivityLevel.Medium).waterGoal
        )
        assertEquals(
            3000,
            HydrationUiState(selectedActivityLevel = ActivityLevel.High).waterGoal
        )
    }

    @Test
    fun displayedTemperature_convertsCelsiusToFahrenheit() {
        val state = HydrationUiState(
            temperatureCelsius = 30.0,
            selectedTemperatureUnit = TemperatureUnit.Fahrenheit
        )

        assertEquals("86 F", state.displayedTemperature)
    }

    @Test
    fun progress_isCappedAtOneHundredPercent() {
        val state = HydrationUiState(
            waterDrunk = 4000,
            selectedActivityLevel = ActivityLevel.Low
        )

        assertEquals(1f, state.progress)
    }

    @Test
    fun weatherRecommendation_changesForHotWeather() {
        val state = HydrationUiState(temperatureCelsius = 32.0)

        assertTrue(state.weatherRecommendation.startsWith("Hot weather"))
    }

    @Test
    fun waterGoal_addsWeatherAdjustmentToActivityGoal() {
        val warmState = HydrationUiState(
            selectedActivityLevel = ActivityLevel.Medium,
            temperatureCelsius = 27.0
        )
        val hotState = HydrationUiState(
            selectedActivityLevel = ActivityLevel.Medium,
            temperatureCelsius = 32.0
        )

        assertEquals(2750, warmState.waterGoal)
        assertEquals(3000, hotState.waterGoal)
    }

    @Test
    fun hydrationStatus_prioritisesRemainingAmountAndCompletion() {
        val inProgress = HydrationUiState(waterDrunk = 1000, temperatureCelsius = 20.0)
        val completed = HydrationUiState(waterDrunk = 3000, temperatureCelsius = 20.0)

        assertEquals("1500 ml remaining", inProgress.hydrationStatus)
        assertEquals("Daily goal reached", completed.hydrationStatus)
    }

    @Test
    fun suggestedNextDrink_doesNotExceedRemainingAmount() {
        val state = HydrationUiState(
            waterDrunk = 2400,
            selectedCupSize = CupSize.Medium,
            temperatureCelsius = 20.0
        )

        assertEquals(100, state.suggestedNextDrink)
    }

    @Test
    fun suggestedNextDrink_isZeroAfterGoalIsReached() {
        val state = HydrationUiState(waterDrunk = 3000, temperatureCelsius = 20.0)

        assertEquals(0, state.suggestedNextDrink)
    }
}
