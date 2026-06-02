package com.example.cp3406assignment1_utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cp3406assignment1_utilityapp.ui.theme.CP3406Assignment1UtilityAppTheme

// Main entry point for the HydroCheck Android app.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP3406Assignment1UtilityAppTheme {
                HydroCheckApp()
            }
        }
    }
}

// Represents the two bottom navigation destinations in the app.
enum class HydroCheckTab(
    val label: String
) {
    Hydration("Hydration"),
    Settings("Settings")
}

// Root composable that stores shared screen state and switches between tabs.
@Composable
fun HydroCheckApp() {
    var selectedTab by rememberSaveable { mutableStateOf(HydroCheckTab.Hydration) }

    // Settings values are saved across recompositions and basic configuration changes.
    var selectedCity by rememberSaveable { mutableStateOf("Singapore") }
    var selectedActivityLevel by rememberSaveable { mutableStateOf("Medium") }
    var selectedCupSize by rememberSaveable { mutableStateOf("250 ml") }
    var selectedTemperatureUnit by rememberSaveable { mutableStateOf("Celsius") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5FAFD),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                HydroCheckTab.entries.forEach { tab ->
                    // Bottom navigation item for switching between Hydration and Settings.
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        icon = {}
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            // Pass current settings into the Hydration screen so the main content updates.
            HydroCheckTab.Hydration -> HydroCheckScreen(
                modifier = Modifier.padding(innerPadding),
                selectedCity = selectedCity,
                selectedActivityLevel = selectedActivityLevel,
                selectedCupSize = selectedCupSize,
                selectedTemperatureUnit = selectedTemperatureUnit
            )

            HydroCheckTab.Settings -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                selectedCity = selectedCity,
                selectedActivityLevel = selectedActivityLevel,
                selectedCupSize = selectedCupSize,
                selectedTemperatureUnit = selectedTemperatureUnit,
                onCitySelected = { selectedCity = it },
                onActivityLevelSelected = { selectedActivityLevel = it },
                onCupSizeSelected = { selectedCupSize = it },
                onTemperatureUnitSelected = { selectedTemperatureUnit = it }
            )
        }
    }
}

// Main hydration screen that shows water progress, quick actions, and advice.
@Composable
fun HydroCheckScreen(
    modifier: Modifier = Modifier,
    selectedCity: String = "Singapore",
    selectedActivityLevel: String = "Medium",
    selectedCupSize: String = "250 ml",
    selectedTemperatureUnit: String = "Celsius"
) {
    // Settings are translated into the values shown on the main screen.
    val waterGoal = hydrationGoalForActivity(selectedActivityLevel)
    val cityTemperature = temperatureForCity(selectedCity)
    val displayedTemperature = formatTemperature(cityTemperature, selectedTemperatureUnit)
    val preferredCupSize = selectedCupSize.removeSuffix(" ml").toIntOrNull() ?: 250

    // Current water intake is local UI state for this early app version.
    var waterDrunk by rememberSaveable { mutableStateOf(1200) }

    // The progress bar is capped at 100% even if the user drinks more than the goal.
    val progress = (waterDrunk / waterGoal.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            // Scrolling prevents the recommendation card from being clipped on small screens.
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeaderSection()
        WeatherSummaryCard(
            city = selectedCity,
            temperature = displayedTemperature
        )
        HydrationProgressCard(
            waterDrunk = waterDrunk,
            waterGoal = waterGoal,
            progress = progress
        )
        QuickAddSection(
            preferredCupSize = preferredCupSize,
            onAddWater = { amount -> waterDrunk += amount },
            onReset = { waterDrunk = 0 }
        )
        DailyTipCard(
            selectedActivityLevel = selectedActivityLevel,
            selectedCupSize = selectedCupSize
        )
    }
}

// Converts the selected activity level into a daily hydration goal.
fun hydrationGoalForActivity(activityLevel: String): Int {
    return when (activityLevel) {
        "Low" -> 2000
        "High" -> 3000
        else -> 2500
    }
}

// Provides temporary city temperature values until the weather API is connected.
fun temperatureForCity(city: String): Int {
    return when (city) {
        "Cairns" -> 29
        "Brisbane" -> 26
        else -> 32
    }
}

// Formats the temperature based on the selected unit.
fun formatTemperature(celsius: Int, unit: String): String {
    return if (unit == "Fahrenheit") {
        "${(celsius * 9 / 5) + 32}°F"
    } else {
        "$celsius°C"
    }
}

// Header area with the app name and short purpose statement.
@Composable
fun HeaderSection() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "HydroCheck",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF163B4D)
        )
        Text(
            text = "Your daily hydration at a glance",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF5F7280)
        )
    }
}

// Weather summary card that reflects the selected city and temperature unit.
@Composable
fun WeatherSummaryCard(
    city: String,
    temperature: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2F3FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color(0xFFFFD166), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = temperature,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163B4D)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = city,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF163B4D)
                )
                Text(
                    text = "Warm weather detected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5F7280)
                )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = Color(0xFF5F7280)
                    )
                    Text(
                        text = "${waterDrunk} ml",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0E7490)
                    )
                }
                Text(
                    text = "Goal ${waterGoal / 1000.0} L",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF163B4D)
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = Color(0xFF0E7490),
                trackColor = Color(0xFFD4EEF5)
            )

            Text(
                text = "You have completed ${(progress * 100).toInt()}% of your daily goal.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5F7280)
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
            color = Color(0xFF163B4D)
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490))
                    ) {
                        Text("+${amount} ml")
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth()
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
        color = Color(0xFFFFF4D6)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Today's recommendation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6B4E00)
            )
            Text(
                text = "$selectedActivityLevel activity is used for today's goal. Your preferred quick add amount is $selectedCupSize.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B4E00)
            )
        }
    }
}

// Settings screen where the user can choose preferences for hydration planning.
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    selectedCity: String = "Singapore",
    selectedActivityLevel: String = "Medium",
    selectedCupSize: String = "250 ml",
    selectedTemperatureUnit: String = "Celsius",
    onCitySelected: (String) -> Unit = {},
    onActivityLevelSelected: (String) -> Unit = {},
    onCupSizeSelected: (String) -> Unit = {},
    onTemperatureUnitSelected: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            // Scrolling keeps all setting groups reachable on smaller devices.
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF163B4D)
            )
            Text(
                text = "Adjust the preferences that will shape your hydration goal.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF5F7280)
            )
        }

        // Each settings group shows one selected value and lets the user pick another.
        SettingsOptionGroup(
            title = "City",
            selectedOption = selectedCity,
            options = listOf("Singapore", "Cairns", "Brisbane"),
            onOptionSelected = onCitySelected
        )
        SettingsOptionGroup(
            title = "Activity level",
            selectedOption = selectedActivityLevel,
            options = listOf("Low", "Medium", "High"),
            onOptionSelected = onActivityLevelSelected
        )
        SettingsOptionGroup(
            title = "Preferred cup size",
            selectedOption = selectedCupSize,
            options = listOf("150 ml", "250 ml", "350 ml", "500 ml"),
            onOptionSelected = onCupSizeSelected
        )
        SettingsOptionGroup(
            title = "Temperature unit",
            selectedOption = selectedTemperatureUnit,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                color = Color(0xFF163B4D)
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
    val containerColor = if (selected) Color(0xFF0E7490) else Color.White
    val contentColor = if (selected) Color.White else Color(0xFF163B4D)

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
        HydroCheckScreen()
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
