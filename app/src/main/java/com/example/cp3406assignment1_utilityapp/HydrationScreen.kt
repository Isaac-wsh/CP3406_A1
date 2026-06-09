package com.example.cp3406assignment1_utilityapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cp3406assignment1_utilityapp.model.ActivityLevel
import com.example.cp3406assignment1_utilityapp.model.CupSize
import com.example.cp3406assignment1_utilityapp.model.HydrationUiState
import com.example.cp3406assignment1_utilityapp.ui.theme.CardWhite
import com.example.cp3406assignment1_utilityapp.ui.theme.CP3406Assignment1UtilityAppTheme
import com.example.cp3406assignment1_utilityapp.ui.theme.DeepText
import com.example.cp3406assignment1_utilityapp.ui.theme.MutedText
import com.example.cp3406assignment1_utilityapp.ui.theme.PrimaryTeal
import com.example.cp3406assignment1_utilityapp.ui.theme.SoftBlue
import com.example.cp3406assignment1_utilityapp.ui.theme.SoftTrack
import com.example.cp3406assignment1_utilityapp.ui.theme.SunAccent
import com.example.cp3406assignment1_utilityapp.ui.theme.WarningFill
import com.example.cp3406assignment1_utilityapp.ui.theme.WarningText

// Main utility screen containing live weather and hydration actions.
@Composable
fun HydrationScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState(),
    onAddWater: (Int) -> Unit = {},
    onResetWater: () -> Unit = {},
    onRefreshWeather: () -> Unit = {}
) {
    AppPage(modifier = modifier) {
        HeaderSection()
        WeatherSummaryCard(
            city = uiState.selectedCity.label,
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
            preferredCupSize = uiState.selectedCupSize.millilitres,
            onAddWater = onAddWater,
            onReset = onResetWater
        )
        DailyTipCard(
            activityLevel = uiState.selectedActivityLevel,
            cupSize = uiState.selectedCupSize
        )
    }
}

@Composable
private fun HeaderSection() {
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

@Composable
private fun WeatherSummaryCard(
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
        colors = CardDefaults.cardColors(containerColor = SoftBlue)
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
                        .background(SunAccent, CircleShape),
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

@Composable
private fun HydrationProgressCard(
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
                        text = "$waterDrunk ml",
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

@Composable
private fun QuickAddSection(
    preferredCupSize: Int,
    onAddWater: (Int) -> Unit,
    onReset: () -> Unit
) {
    val quickAddOptions = (listOf(preferredCupSize, 150, 250, 350, 500)).distinct()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick add - Preferred $preferredCupSize ml",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = DeepText
        )

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
                        Text("+$amount ml")
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

@Composable
private fun DailyTipCard(
    activityLevel: ActivityLevel,
    cupSize: CupSize
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
                text = "${activityLevel.label} activity is used for today's goal. " +
                    "Your preferred quick add amount is ${cupSize.label}.",
                style = MaterialTheme.typography.bodyMedium,
                color = WarningText
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HydrationScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        HydrationScreen(uiState = HydrationUiState(temperatureCelsius = 31.0))
    }
}
