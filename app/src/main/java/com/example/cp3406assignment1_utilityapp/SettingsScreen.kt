package com.example.cp3406assignment1_utilityapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cp3406assignment1_utilityapp.model.ActivityLevel
import com.example.cp3406assignment1_utilityapp.model.CityOption
import com.example.cp3406assignment1_utilityapp.model.CupSize
import com.example.cp3406assignment1_utilityapp.model.HydrationUiState
import com.example.cp3406assignment1_utilityapp.model.TemperatureUnit
import com.example.cp3406assignment1_utilityapp.ui.theme.CardWhite
import com.example.cp3406assignment1_utilityapp.ui.theme.CP3406Assignment1UtilityAppTheme
import com.example.cp3406assignment1_utilityapp.ui.theme.DeepText
import com.example.cp3406assignment1_utilityapp.ui.theme.PrimaryTeal

// Lets the user select typed preferences that affect hydration planning.
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: HydrationUiState = HydrationUiState(),
    onCitySelected: (CityOption) -> Unit = {},
    onActivityLevelSelected: (ActivityLevel) -> Unit = {},
    onCupSizeSelected: (CupSize) -> Unit = {},
    onTemperatureUnitSelected: (TemperatureUnit) -> Unit = {}
) {
    AppPage(modifier = modifier) {
        PageHeader(
            title = "Settings",
            subtitle = "Adjust the preferences that will shape your hydration goal."
        )

        SettingsOptionGroup(
            title = "City",
            selectedOption = uiState.selectedCity,
            options = CityOption.entries,
            optionLabel = CityOption::label,
            onOptionSelected = onCitySelected
        )
        SettingsOptionGroup(
            title = "Activity level",
            selectedOption = uiState.selectedActivityLevel,
            options = ActivityLevel.entries,
            optionLabel = ActivityLevel::label,
            onOptionSelected = onActivityLevelSelected
        )
        SettingsOptionGroup(
            title = "Preferred cup size",
            selectedOption = uiState.selectedCupSize,
            options = CupSize.entries,
            optionLabel = CupSize::label,
            onOptionSelected = onCupSizeSelected
        )
        SettingsOptionGroup(
            title = "Temperature unit",
            selectedOption = uiState.selectedTemperatureUnit,
            options = TemperatureUnit.entries,
            optionLabel = TemperatureUnit::label,
            onOptionSelected = onTemperatureUnitSelected
        )
    }
}

@Composable
private fun <T> SettingsOptionGroup(
    title: String,
    selectedOption: T,
    options: List<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit
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
                        SettingsChoiceChip(
                            text = optionLabel(option),
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

@Composable
private fun SettingsChoiceChip(
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

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    CP3406Assignment1UtilityAppTheme {
        SettingsScreen()
    }
}
