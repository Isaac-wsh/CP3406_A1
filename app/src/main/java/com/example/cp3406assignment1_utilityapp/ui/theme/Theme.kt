package com.example.cp3406assignment1_utilityapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HydroCheckColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    secondary = SunAccent,
    background = AppBackground,
    surface = CardWhite,
    onPrimary = CardWhite,
    onBackground = DeepText,
    onSurface = DeepText
)

@Composable
fun CP3406Assignment1UtilityAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HydroCheckColorScheme,
        typography = Typography,
        content = content
    )
}
