package com.example.cp3406assignment1_utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cp3406assignment1_utilityapp.ui.theme.CP3406Assignment1UtilityAppTheme

private val AppBackground = Color(0xFFF4FAFC)
private val PrimaryTeal = Color(0xFF0E7490)
private val DeepText = Color(0xFF163B4D)
private val MutedText = Color(0xFF5F7280)
private val SoftBlue = Color(0xFFE2F3FA)
private val SoftTrack = Color(0xFFD4EEF5)
private val WarningFill = Color(0xFFFFF4D6)
private val WarningText = Color(0xFF6B4E00)
private val CardWhite = Color.White

// Main entry point for the HydroCheck Android app.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val weatherRepository =
            (application as HydroCheckApplication).appContainer.weatherRepository
        setContent {
            CP3406Assignment1UtilityAppTheme {
                HydroCheckApp(
                    viewModelFactory = HydrationViewModel.factory(weatherRepository)
                )
            }
        }
    }
}

// Represents the bottom navigation destinations in the app.
enum class HydroCheckTab(
    val label: String
) {
    Hydration("Home"),
    Insights("Insights"),
    History("History"),
    Settings("Settings")
}

// Root composable that connects the ViewModel state to the app screens.
@Composable
fun HydroCheckApp(
    viewModelFactory: ViewModelProvider.Factory,
    hydrationViewModel: HydrationViewModel = viewModel(factory = viewModelFactory)
) {
    var selectedTab by rememberSaveable { mutableStateOf(HydroCheckTab.Hydration) }
    val uiState = hydrationViewModel.uiState

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        bottomBar = {
            NavigationBar(
                containerColor = CardWhite
            ) {
                HydroCheckTab.entries.forEach { tab ->
                    // Bottom navigation item for switching between Hydration and Settings.
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        icon = {},
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = SoftBlue,
                            unselectedTextColor = MutedText
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            // Pass current ViewModel state into the Hydration screen so the main content updates.
            HydroCheckTab.Hydration -> HydroCheckScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onAddWater = hydrationViewModel::addWater,
                onResetWater = hydrationViewModel::resetWater,
                onRefreshWeather = hydrationViewModel::refreshWeather
            )

            HydroCheckTab.Insights -> InsightsScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState
            )

            HydroCheckTab.History -> HistoryScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState
            )

            HydroCheckTab.Settings -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onCitySelected = hydrationViewModel::selectCity,
                onActivityLevelSelected = hydrationViewModel::selectActivityLevel,
                onCupSizeSelected = hydrationViewModel::selectCupSize,
                onTemperatureUnitSelected = hydrationViewModel::selectTemperatureUnit
            )
        }
    }
}

// Shared page layout that keeps every screen visually consistent.
@Composable
fun AppPage(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        content = content
    )
}

// Shared page title style for secondary screens.
@Composable
fun PageHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = DeepText
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MutedText
        )
    }
}

// Main hydration screen that shows water progress, quick actions, and advice.
@Composable
fun HydroCheckScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState(),
    onAddWater: (Int) -> Unit = {},
    onResetWater: () -> Unit = {},
    onRefreshWeather: () -> Unit = {}
) {
    AppPage(modifier = modifier) {
        HeaderSection()
        WeatherSummaryCard(
            city = uiState.selectedCity,
            temperature = uiState.displayedTemperature,
            recommendation = uiState.weatherRecommendation,
            isLoading = uiState.isWeatherLoading,
            errorMessage = uiState.weatherError,
            onRetry = onRefreshWeather
        )
        HydrationProgressCard(
            waterDrunk = uiState.waterDrunk,
            waterGoal = uiState.waterGoal,
            progress = uiState.progress
        )
        QuickAddSection(
            preferredCupSize = uiState.preferredCupSize,
            onAddWater = onAddWater,
            onReset = onResetWater
        )
        DailyTipCard(
            selectedActivityLevel = uiState.selectedActivityLevel,
            selectedCupSize = uiState.selectedCupSize
        )
    }
}

// Header area with the app name and short purpose statement.
@Composable
fun HeaderSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hydrocheck_logo),
                contentDescription = "HydroCheck logo",
                modifier = Modifier.size(58.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "HydroCheck",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepText
                )
                Text(
                    text = "Daily hydration, quickly checked.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }
    }
}

// Weather summary card that reflects the selected city and temperature unit.
@Composable
fun WeatherSummaryCard(
    city: String,
    temperature: String,
    recommendation: String,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SoftBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(Color(0xFFFFD166), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLoading) "..." else temperature,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepText
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = city,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepText
                    )
                    Text(
                        text = when {
                            isLoading -> "Loading current weather..."
                            errorMessage != null -> "Weather unavailable"
                            else -> "Live weather from Open-Meteo"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                }
            }

            Text(
                text = errorMessage ?: recommendation,
                style = MaterialTheme.typography.bodyMedium,
                color = if (errorMessage == null) DeepText else WarningText
            )

            if (errorMessage != null) {
                OutlinedButton(onClick = onRetry) {
                    Text("Retry weather")
                }
            }
        }
    }
}

// Card that displays the daily water intake amount, goal, and progress bar.
@Composable
fun HydrationProgressCard(
    waterDrunk: Int,
    waterGoal: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.labelLarge,
                        color = MutedText
                    )
                    Text(
                        text = "${waterDrunk} ml",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                }
                Text(
                    text = "Goal ${waterGoal / 1000.0} L",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepText
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = PrimaryTeal,
                trackColor = SoftTrack
            )

            Text(
                text = "You have completed ${(progress * 100).toInt()}% of your daily goal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
        }
    }
}

// Quick action section for adding common water amounts or resetting the day.
@Composable
fun QuickAddSection(
    preferredCupSize: Int,
    onAddWater: (Int) -> Unit,
    onReset: () -> Unit
) {
    // Put the preferred cup size first, then add the standard options without duplicates.
    val quickAddOptions = (listOf(preferredCupSize, 150, 250, 350, 500)).distinct()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick add - Preferred ${preferredCupSize} ml",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = DeepText
        )

        // Display quick add options in two-button rows to keep the layout compact.
        quickAddOptions.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { amount ->
                    Button(
                        onClick = { onAddWater(amount) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("+${amount} ml")
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Reset today")
        }
    }
}

// Recommendation card that explains how settings affect the current plan.
@Composable
fun DailyTipCard(
    selectedActivityLevel: String,
    selectedCupSize: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = WarningFill
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Today's recommendation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = WarningText
            )
            Text(
                text = "$selectedActivityLevel activity is used for today's goal. Your preferred quick add amount is $selectedCupSize.",
                style = MaterialTheme.typography.bodyMedium,
                color = WarningText
            )
        }
    }
}

// Insights screen that summarizes hydration progress and selected settings.
@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState()
) {
    val completedPercent = (uiState.progress * 100).toInt()
    val remainingWater = (uiState.waterGoal - uiState.waterDrunk).coerceAtLeast(0)
    val suggestedNextDrink = uiState.preferredCupSize.coerceAtMost(remainingWater.takeIf { it > 0 } ?: uiState.preferredCupSize)

    AppPage(modifier = modifier) {
        PageHeader(
            title = "Insights",
            subtitle = "A quick summary of today's hydration plan."
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "$completedPercent% complete",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = PrimaryTeal,
                    trackColor = SoftTrack
                )
                Text(
                    text = "${uiState.waterDrunk} ml recorded out of ${uiState.waterGoal} ml.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InsightMetricCard(
                label = "Remaining",
                value = "$remainingWater ml",
                modifier = Modifier.weight(1f)
            )
            InsightMetricCard(
                label = "Next drink",
                value = "$suggestedNextDrink ml",
                modifier = Modifier.weight(1f)
            )
        }

        InsightMetricCard(
            label = "Plan basis",
            value = "${uiState.selectedActivityLevel} activity in ${uiState.selectedCity}",
            supportingText = "Weather card currently shows ${uiState.displayedTemperature}.",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Small card used by the Insights screen for one summary metric.
@Composable
fun InsightMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MutedText
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DeepText
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }
    }
}

// History screen with separate sections for today's entries and daily totals.
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState()
) {
    AppPage(modifier = modifier) {
        PageHeader(
            title = "History",
            subtitle = "Review individual drinks and daily totals."
        )

        DrinkLogSection(drinkLog = uiState.drinkLog)
        DailyTotalsSection(dailyTotals = uiState.dailyTotals)
    }
}

// Shows each water entry recorded today with its amount and time.
@Composable
fun DrinkLogSection(drinkLog: List<DrinkLogEntry>) {
    HistorySectionCard(title = "Today's drink log") {
        if (drinkLog.isEmpty()) {
            EmptyHistoryText(text = "No drinks logged yet today.")
        } else {
            drinkLog.forEach { entry ->
                HistoryRow(
                    leadingText = "+${entry.amount} ml",
                    trailingText = entry.time
                )
            }
        }
    }
}

// Shows daily water totals for today and previous days in this app session.
@Composable
fun DailyTotalsSection(dailyTotals: List<DailyTotalEntry>) {
    HistorySectionCard(title = "Daily totals") {
        dailyTotals.forEach { entry ->
            HistoryRow(
                leadingText = entry.date,
                trailingText = "${entry.totalAmount} ml"
            )
        }
    }
}

// Reusable card container for each History section.
@Composable
fun HistorySectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DeepText
            )
            content()
        }
    }
}

// One row in a History section.
@Composable
fun HistoryRow(
    leadingText: String,
    trailingText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leadingText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = DeepText
        )
        Text(
            text = trailingText,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )
    }
}

// Placeholder text for an empty History section.
@Composable
fun EmptyHistoryText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MutedText
    )
}

// Settings screen where the user can choose preferences for hydration planning.
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState(),
    onCitySelected: (String) -> Unit = {},
    onActivityLevelSelected: (String) -> Unit = {},
    onCupSizeSelected: (String) -> Unit = {},
    onTemperatureUnitSelected: (String) -> Unit = {}
) {
    AppPage(modifier = modifier) {
        PageHeader(
            title = "Settings",
            subtitle = "Adjust the preferences that will shape your hydration goal."
        )

        // Each settings group shows one selected value and lets the user pick another.
        SettingsOptionGroup(
            title = "City",
            selectedOption = uiState.selectedCity,
            options = listOf("Singapore", "Cairns", "Brisbane"),
            onOptionSelected = onCitySelected
        )
        SettingsOptionGroup(
            title = "Activity level",
            selectedOption = uiState.selectedActivityLevel,
            options = listOf("Low", "Medium", "High"),
            onOptionSelected = onActivityLevelSelected
        )
        SettingsOptionGroup(
            title = "Preferred cup size",
            selectedOption = uiState.selectedCupSize,
            options = listOf("150 ml", "250 ml", "350 ml", "500 ml"),
            onOptionSelected = onCupSizeSelected
        )
        SettingsOptionGroup(
            title = "Temperature unit",
            selectedOption = uiState.selectedTemperatureUnit,
            options = listOf("Celsius", "Fahrenheit"),
            onOptionSelected = onTemperatureUnitSelected
        )
    }
}

// Reusable card for one settings category, such as city or cup size.
@Composable
fun SettingsOptionGroup(
    title: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DeepText
            )
            options.chunked(2).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowOptions.forEach { option ->
                        // Selecting a chip updates the matching state in HydroCheckApp.
                        SettingsChoiceChip(
                            text = option,
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowOptions.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// Clickable setting chip that visually highlights the currently selected option.
@Composable
fun SettingsChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) PrimaryTeal else CardWhite
    val contentColor = if (selected) CardWhite else DeepText

    OutlinedCard(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

// Preview for checking the Hydration screen in Android Studio.
@Preview(showBackground = true)
@Composable
fun HydroCheckScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        HydroCheckScreen(
            uiState = HydrationUiState(temperatureCelsius = 31.0)
        )
    }
}

// Preview for checking the Insights screen in Android Studio.
@Preview(showBackground = true)
@Composable
fun InsightsScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        InsightsScreen()
    }
}

// Preview for checking the History screen in Android Studio.
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        HistoryScreen(
            uiState = HydrationUiState(
                waterDrunk = 750,
                drinkLog = listOf(
                    DrinkLogEntry(amount = 250, time = "9:15 AM", date = "2026-06-04"),
                    DrinkLogEntry(amount = 500, time = "11:40 AM", date = "2026-06-04")
                ),
                dailyTotals = listOf(
                    DailyTotalEntry(date = "2026-06-04", totalAmount = 750),
                    DailyTotalEntry(date = "2026-06-03", totalAmount = 2100)
                )
            )
        )
    }
}

// Preview for checking the Settings screen in Android Studio.
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        SettingsScreen()
    }
}
