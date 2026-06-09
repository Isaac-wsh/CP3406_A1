package com.example.cp3406assignment1_utilityapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cp3406assignment1_utilityapp.model.DailyTotalEntry
import com.example.cp3406assignment1_utilityapp.model.DrinkLogEntry
import com.example.cp3406assignment1_utilityapp.model.HydrationUiState
import com.example.cp3406assignment1_utilityapp.ui.theme.CardWhite
import com.example.cp3406assignment1_utilityapp.ui.theme.CP3406Assignment1UtilityAppTheme
import com.example.cp3406assignment1_utilityapp.ui.theme.DeepText
import com.example.cp3406assignment1_utilityapp.ui.theme.MutedText

// Displays individual drinks and daily totals in separate sections.
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

@Composable
private fun DrinkLogSection(drinkLog: List<DrinkLogEntry>) {
    HistorySectionCard(title = "Today's drink log") {
        if (drinkLog.isEmpty()) {
            EmptyHistoryText(text = "No drinks logged yet today.")
        } else {
            drinkLog.forEach { entry ->
                HistoryRow(leadingText = "+${entry.amount} ml", trailingText = entry.time)
            }
        }
    }
}

@Composable
private fun DailyTotalsSection(dailyTotals: List<DailyTotalEntry>) {
    HistorySectionCard(title = "Daily totals") {
        dailyTotals.forEach { entry ->
            HistoryRow(leadingText = entry.date, trailingText = "${entry.totalAmount} ml")
        }
    }
}

@Composable
private fun HistorySectionCard(
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

@Composable
private fun HistoryRow(
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
        Text(text = trailingText, style = MaterialTheme.typography.bodyMedium, color = MutedText)
    }
}

@Composable
private fun EmptyHistoryText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MutedText)
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
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
