# HydroCheck

HydroCheck is a planned Android utility app for CP3406 Assignment 1. The app is designed to help users quickly check their daily hydration progress and receive simple water intake recommendations based on weather conditions and personal activity settings.

The goal of the app is to provide focused, at-a-glance information. Users should be able to open the app and immediately understand their daily water goal, how much they have already recorded, and whether the current weather suggests drinking more water.

## Core Features

The planned app will include:

- A daily hydration goal displayed in millilitres or litres.
- A progress display showing how much water has been recorded for the day.
- Quick add buttons for common cup sizes, such as 250 ml and 500 ml.
- A weather-aware recommendation based on the current temperature.
- A settings screen for adjusting city, activity level, cup size, and temperature unit.

## Screens

HydroCheck will use a simple two-screen structure:

- **Hydration screen**: shows the current hydration goal, progress, weather information, recommendation, and quick water logging buttons.
- **Settings screen**: lets the user adjust preferences that affect the hydration screen.

The assignment does not require settings to be persistent, so the first implementation may keep settings only while the app is running.

## Technical Plan

The app is planned to use the following Android technologies and development practices:

- Kotlin
- Jetpack Compose
- Material Design 3
- ViewModel for UI state management
- Repository pattern for data handling
- Retrofit for web API requests
- Open-Meteo API for weather data

## Development Status

Current progress:

- Blank Android project created.
- Project connected to GitHub.
- Initial README added.

Pending work:

- Build the Hydration screen UI.
- Build the Settings screen UI.
- Add state management for water intake and settings.
- Add weather API integration.
- Refine the user interface using Material Design 3.
- Write the self-reflection for the assignment submission.

## GitHub Progress

This repository will be updated regularly as features are implemented. Commits will be used to show continuous progress through the design, implementation, testing, and refinement stages of the assignment.

