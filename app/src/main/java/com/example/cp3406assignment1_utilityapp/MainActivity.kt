package com.example.cp3406assignment1_utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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

enum class HydroCheckTab(
    val label: String
) {
    Hydration("Hydration"),
    Settings("Settings")
}

@Composable
fun HydroCheckApp() {
    var selectedTab by rememberSaveable { mutableStateOf(HydroCheckTab.Hydration) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5FAFD),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                HydroCheckTab.entries.forEach { tab ->
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
            HydroCheckTab.Hydration -> HydroCheckScreen(
                modifier = Modifier.padding(innerPadding)
            )

            HydroCheckTab.Settings -> SettingsScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun HydroCheckScreen(modifier: Modifier = Modifier) {
    val waterGoal = 2500
    val waterDrunk = 1200
    val progress = waterDrunk / waterGoal.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeaderSection()
        WeatherSummaryCard()
        HydrationProgressCard(
            waterDrunk = waterDrunk,
            waterGoal = waterGoal,
            progress = progress
        )
        QuickAddSection()
        DailyTipCard()
    }
}

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

@Composable
fun WeatherSummaryCard() {
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
                    text = "32",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163B4D)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Singapore",
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

@Composable
fun QuickAddSection() {
    val quickAddOptions = listOf("+150 ml", "+250 ml", "+350 ml", "+500 ml")

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick add",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF163B4D)
        )

        quickAddOptions.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { option ->
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490))
                    ) {
                        Text(option)
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset today")
        }
    }
}

@Composable
fun DailyTipCard() {
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
                text = "Hot weather can increase water loss. Try adding one extra cup before your next study break.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B4E00)
            )
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
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

        SettingsOptionGroup(
            title = "City",
            selectedOption = "Singapore",
            options = listOf("Singapore", "Cairns", "Brisbane")
        )
        SettingsOptionGroup(
            title = "Activity level",
            selectedOption = "Medium",
            options = listOf("Low", "Medium", "High")
        )
        SettingsOptionGroup(
            title = "Preferred cup size",
            selectedOption = "250 ml",
            options = listOf("150 ml", "250 ml", "350 ml", "500 ml")
        )
        SettingsOptionGroup(
            title = "Temperature unit",
            selectedOption = "Celsius",
            options = listOf("Celsius", "Fahrenheit")
        )
    }
}

@Composable
fun SettingsOptionGroup(
    title: String,
    selectedOption: String,
    options: List<String>
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
                        SettingsChoiceChip(
                            text = option,
                            selected = option == selectedOption,
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

@Composable
fun SettingsChoiceChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) Color(0xFF0E7490) else Color.White
    val contentColor = if (selected) Color.White else Color(0xFF163B4D)

    OutlinedCard(
        modifier = modifier,
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

@Preview(showBackground = true)
@Composable
fun HydroCheckScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        HydroCheckScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        SettingsScreen()
    }
}
