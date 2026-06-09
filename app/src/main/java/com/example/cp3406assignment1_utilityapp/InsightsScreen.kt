package com.example.cp3406assignment1_utilityapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cp3406assignment1_utilityapp.model.HydrationUiState
import com.example.cp3406assignment1_utilityapp.ui.theme.CardWhite
import com.example.cp3406assignment1_utilityapp.ui.theme.CP3406Assignment1UtilityAppTheme
import com.example.cp3406assignment1_utilityapp.ui.theme.DeepText
import com.example.cp3406assignment1_utilityapp.ui.theme.MutedText
import com.example.cp3406assignment1_utilityapp.ui.theme.PrimaryTeal
import com.example.cp3406assignment1_utilityapp.ui.theme.SoftTrack

// Summarises the user's current hydration plan and progress.
@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState()
) {
    val completedPercent = (uiState.progress * 100).toInt()
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
                value = "${uiState.remainingWater} ml",
                modifier = Modifier.weight(1f)
            )
            InsightMetricCard(
                label = "Next drink",
                value = "${uiState.suggestedNextDrink} ml",
                modifier = Modifier.weight(1f)
            )
        }

        InsightMetricCard(
            label = "Plan basis",
            value = "${uiState.baseWaterGoal} + ${uiState.weatherGoalAdjustment} ml",
            supportingText = uiState.goalExplanation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InsightMetricCard(
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
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = MutedText)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DeepText
            )
            supportingText?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MutedText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightsScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        InsightsScreen()
    }
}
