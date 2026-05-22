# EEP523 HW3 - Weather App

My name is Daniel Fang. This is my homework app for EEP523. The app show weather information from OpenWeatherMap API. User can search city name or see weather for current location when open the app.

## How to Run

1. Open the project folder `HW3` in Android Studio.
2. Make sure `local.properties` has your API key:
   ```
   OPENWEATHER_API_KEY=your_key_here
   ```
   You can get free key from OpenWeather website.
3. Connect emulator or real phone (Android 11+).
4. Click Run button.
5. Allow location permission when app ask (for bonus feature).

Build from terminal:
```
./gradlew assembleDebug
```

## How the App Works

When user open app, app ask location permission. If user allow, app use GPS (Fused Location Provider) to get latitude and longitude, then call OpenWeather API with lat/lon. If user deny permission or location not available, app use Seattle coordinates as default so app still show data on emulator.

User can type city name in text field and press Search button. App call this API:
```
https://api.openweathermap.org/data/2.5/weather?q={city}&units=metric&appid={API_KEY}
```

For location weather:
```
https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&appid={API_KEY}
```

App use **MVVM** structure:
- `MainActivity` - show UI and handle button click, permission
- `WeatherViewModel` - manage loading state with StateFlow
- `WeatherRepository` - call API on background thread (Dispatchers.IO)
- `OpenWeatherApi` - Retrofit interface
- `WeatherResponse` - Gson model for JSON

UI update on main thread using `lifecycleScope` and `repeatOnLifecycle`.

### Data Shown on Screen

- City name
- Current temperature (Celsius)
- Weather condition (like cloudy, clear sky)
- Min and max temperature
- Sunrise and sunset time (convert from Unix time)
- Wind speed, pressure, humidity
- Weather icon from OpenWeather image URL (load with Coil library)

### Error Handling

| Problem | Message |
|---------|---------|
| Empty city name | City Name cannot be blank (show on text field) |
| Wrong city name | City not found. Please check the name. (Snackbar) |
| No internet | Please connect to internet (Snackbar) |

App check network with `ConnectivityManager` before API call.

## Figma Design

I design the screen in Figma before coding Android layout.

Figma file link: https://www.figma.com/design/cD3KYHKA33j8BZs0m1lq8O

Frames include: default weather screen, loading overlay, blank city error, no internet error, city not found error.

## Project Structure

```
app/src/main/java/com/example/hw3/
├── MainActivity.kt
├── ui/WeatherViewModel.kt
├── data/WeatherRepository.kt
├── data/api/OpenWeatherApi.kt
├── data/api/ApiClient.kt
├── data/model/WeatherResponse.kt
├── data/model/WeatherUiModel.kt
└── util/LocationHelper.kt, NetworkUtils.kt, DateTimeUtils.kt
```

## Time Spent

About **14 hours** total:
- 3 hours read assignment and OpenWeather API doc
- 4 hours design UI in Figma and layout XML
- 5 hours write Kotlin code (Retrofit, ViewModel, location)
- 2 hours test on emulator and fix bugs

## Most Challenging Parts

1. **Location permission flow** - I need use Activity Result API and still load Seattle weather when permission denied. Took me some time to understand FusedLocationProviderClient.

2. **Retrofit error handling** - 404 for wrong city return HttpException, not normal response. I need catch this separately from no internet error.

3. **ViewBinding with include layout** - Detail cards use `<include>` tag. At first I use `findViewById` but binding give `ItemWeatherDetailBinding` type. I fix by use binding object directly.

4. **Gradle build on Mac** - Command line build fail because wrong JDK path. I add `org.gradle.java.home` in gradle.properties point to Android Studio JBR.

## Resources and References

1. OpenWeatherMap Current Weather API (2.5)  
   https://openweathermap.org/current

2. OpenWeatherMap API documentation (one call reference from class)  
   https://openweathermap.org/api/one-call-api

3. Android Developers - Request location permissions  
   https://developer.android.com/training/location/permissions

4. Android Developers - Fused Location Provider  
   https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient

5. Retrofit documentation  
   https://square.github.io/retrofit/

6. Kotlin Coroutines on Android  
   https://developer.android.com/kotlin/coroutines

7. Material Design 3 components (TextInputLayout, Snackbar)  
   https://m3.material.io/

8. Coil image loading library  
   https://coil-kt.github.io/coil/

9. Android ViewBinding guide  
   https://developer.android.com/topic/libraries/view-binding

10. Course assignment instructions (EEP523 HW3)

11. Cursor AI agent - used for coding help and Figma screen creation (see PROMPT.md)

12. Figma - UI mockup file  
    https://www.figma.com/design/cD3KYHKA33j8BZs0m1lq8O
