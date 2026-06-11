package com.example.cp3406assignment1_utilityapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

// Lazily renders individual drinks and daily totals as history grows.
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState()
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PageHeader(
                title = "History",
                subtitle = "Review individual drinks and daily totals."
            )
        }

        item { HistorySectionTitle("Today's drink log") }
        if (uiState.drinkLog.isEmpty()) {
            item { EmptyHistoryCard("No drinks logged yet today.") }
        } else {
            items(uiState.drinkLog) { entry ->
                HistoryRowCard(
                    leadingText = "+${entry.amount} ml",
                    trailingText = entry.time
                )
            }
        }

        item { HistorySectionTitle("Daily totals") }
        items(
            items = uiState.dailyTotals,
            key = DailyTotalEntry::date
        ) { entry ->
            HistoryRowCard(
                leadingText = entry.date,
                trailingText = "${entry.totalAmount} ml"
            )
        }
    }
}

@Composable
private fun HistorySectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DeepText
    )
}

@Composable
private fun HistoryRowCard(
    leadingText: String,
    trailingText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
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
}

@Composable
private fun EmptyHistoryCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText
        )
    }
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
