# HydroCheck

HydroCheck is an Android hydration utility app developed for CP3406 Assignment 1. It provides focused, at-a-glance information about daily water intake, hydration goals, live weather, and recent drinking history.

## Core Features

- Track today's water intake with several quick-add amounts.
- Calculate a daily hydration goal from the selected activity level.
- Fetch live temperature data for Singapore, Cairns, or Brisbane.
- Display weather-aware hydration recommendations.
- Change city, activity level, preferred cup size, and temperature unit.
- Review today's individual drink entries and daily totals.
- View hydration progress and suggested next drink on the Insights screen.
- Reset today's intake automatically when a new date is detected.

## Screens

- **Home:** Live weather, daily progress, quick-add actions, and recommendations.
- **Insights:** At-a-glance progress metrics and the current hydration plan.
- **History:** Today's drink log and daily total summaries.
- **Settings:** Preferences that immediately affect the Home and Insights screens.

## Architecture

HydroCheck follows a simple layered architecture suitable for the assignment:

- **Jetpack Compose** builds the user interface with reusable composables.
- **HydrationViewModel** owns UI state and handles user actions.
- **Typed domain models** prevent invalid city, activity, cup-size, and temperature-unit values.
- **WeatherRepository** separates weather data access from UI and business logic.
- **Retrofit** defines and performs asynchronous Open-Meteo web API requests.
- **Manual dependency injection** creates Retrofit and injects the repository into the ViewModel through an application-level container.
- **Focused screen files** keep each Compose screen and its private components independent.

The app also models loading, success, and error states for live weather. If a request fails, the rest of the hydration features remain available and the user can retry the request.

## Web API

HydroCheck uses the free [Open-Meteo API](https://open-meteo.com/) to fetch current temperature data. The selected city is mapped to its coordinates before the repository requests the current `temperature_2m` value.

No API key is required.

## Technologies

- Kotlin
- Jetpack Compose
- Material Design 3
- ViewModel
- Repository pattern
- Manual dependency injection
- Retrofit with Gson converter
- Open-Meteo API
- JUnit

## Testing

Repository unit tests use a fake API implementation so data mapping and city coordinates can be checked without making real network requests. Pure UI-state tests verify hydration goals, temperature conversion, progress limits, and weather recommendations.

To build and test the project:

```text
./gradlew testDebugUnitTest assembleDebug
```

## Current Limitations

- Hydration history and settings are stored only while the app process is running.
- The available city list is intentionally limited to three options.
- A network connection is required for live weather data.

## Assignment Notes

This project was created for CP3406 Assignment 1: Utility App. Development progress is documented through regular, focused Git commits. Settings persistence is not required by the assignment, so the current implementation keeps preferences in ViewModel state.
