# REFLECTION.md - HW3 Weather App

## What features or sensors did your app use?

- **OpenWeatherMap API (2.5 Current Weather)** - get weather data by city name or by GPS coordinates
- **GPS / Location** - Fused Location Provider from Google Play Services to get device latitude and longitude (bonus requirement)
- **Internet** - Retrofit HTTP calls need network connection
- **Network state check** - ConnectivityManager to detect offline before calling API
- **No physical sensors on phone** - I do not use accelerometer or camera. Only location which is kind of sensor data from GPS

**App features:**

- Search weather by city name
- Auto load weather on start for current location (or Seattle if no permission)
- Show temp, condition, min/max, sunrise, sunset, wind, pressure, humidity, weather icon
- Error messages for blank input, bad city, no internet
- Material 3 UI with gradient hero card

## How did Gemini / AI agent help you?

- AI (Cursor Composer agent) help me setup whole project structure fast when I only had empty Hello World template
- It suggest use Current Weather API instead of One Call 3.0 so I do not need pay or credit card
- It write Retrofit interface, Gson data classes, Repository pattern, and ViewModel with StateFlow - these take long time if I write alone
- It create Figma screens matching Android layout so I have design part for homework
- It fix compile errors like ViewBinding type mismatch and Gradle JDK path problem
- It help write README and this reflection file

AI is like a tutor who give example code, but I still need read and run the app myself.

## What errors, weaknesses, or missing pieces did you find in AI output?

- **ViewBinding bug** - First version use `View` for detail cards but include layout actually return `ItemWeatherDetailBinding`. App not compile until fix types
- **Gradle build** - AI code was correct but my Mac terminal use wrong Java from VS Code extension, cause jlink error. Need extra gradle.properties line
- **Location load logic** - ViewModel had small redundant code for permission callback (load weather twice same way). Not big bug but not clean
- **Network check** - `NET_CAPABILITY_VALIDATED` sometimes strict on emulator; might show no internet even when wifi on (I did not change this because it still work on my test)
- **Figma design** - AI Figma screens look good but not pixel perfect same as Android app (close enough for homework)
- AI did not add unit tests - assignment did not require but would be nice

## What did you change or fix?

- Fixed MainActivity to use `ItemWeatherDetailBinding` for detail card includes
- Added `org.gradle.java.home` in gradle.properties so `./gradlew assembleDebug` work on my laptop
- Removed unused variable in permission launcher callback
- Tested search with London, empty string, fake city name, and airplane mode on emulator
- Read through WeatherRepository to make sure error messages match assignment exact wording
- Checked API key in local.properties not committed to git (.gitignore already has it)

## What did you learn about using AI in software development?

- AI is very fast for repetitive tasks: Gradle dependencies, XML layout, Retrofit boilerplate, data model from JSON fields
- AI can make small mistakes (wrong type, environment-specific build issue) so **I must always compile and run** before submit
- Explaining homework requirements clearly in prompt give better result than vague "make weather app"
- I learn MVVM pattern better because AI structure the code in folders (ui, data, util) and I trace the flow from button click → ViewModel → Repository → API
- AI cannot replace understanding - if professor ask "why use coroutines?" I still need know answer myself
- Good practice: use AI for first draft, then test all error cases from assignment checklist myself
- For design homework, AI + Figma MCP save time but I should still look at final Figma link and be able to explain my UI choices

**Overall:** AI help me finish homework in ~4 hours instead of maybe 20+ hours alone. But the learning come from debugging AI mistakes and reading the code, not just clicking accept all.