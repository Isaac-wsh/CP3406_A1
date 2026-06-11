package com.example.cp3406assignment1_utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cp3406assignment1_utilityapp.ui.theme.AppBackground
import com.example.cp3406assignment1_utilityapp.ui.theme.CardWhite
import com.example.cp3406assignment1_utilityapp.ui.theme.CP3406Assignment1UtilityAppTheme
import com.example.cp3406assignment1_utilityapp.ui.theme.MutedText
import com.example.cp3406assignment1_utilityapp.ui.theme.PrimaryTeal
import com.example.cp3406assignment1_utilityapp.ui.theme.SoftBlue

// Android entry point that supplies app dependencies to the Compose UI.
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

// Destinations displayed in the bottom navigation bar.
enum class HydroCheckTab(
    val label: String,
    val icon: ImageVector
) {
    Hydration("Home", Icons.Rounded.Home),
    Insights("Insights", Icons.Rounded.Insights),
    History("History", Icons.Rounded.History),
    Settings("Settings", Icons.Rounded.Settings)
}

// Connects ViewModel state and events to the four app screens.
@Composable
fun HydroCheckApp(
    viewModelFactory: ViewModelProvider.Factory,
    hydrationViewModel: HydrationViewModel = viewModel(factory = viewModelFactory)
) {
    var selectedTab by rememberSaveable { mutableStateOf(HydroCheckTab.Hydration) }
    val uiState by hydrationViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        bottomBar = {
            NavigationBar(containerColor = CardWhite) {
                HydroCheckTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
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
            HydroCheckTab.Hydration -> HydrationScreen(
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
