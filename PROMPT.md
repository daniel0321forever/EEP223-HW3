# PROMPT.md - LLM Prompts for HW3 Weather App

I use **Cursor** editor with AI agent (Composer) to help me build this project. Below are the main prompts I typed during development. I also add short summary at the end.

---

## Prompt 1 - Start the project

```
I need to build a Kotlin weather app for EEP523 homework. 
Requirements:
- Search city and show weather from OpenWeatherMap API 2.5
- Show temperature, condition, min/max, sunrise, sunset, wind, pressure, humidity
- Bonus: show current device location weather on app start (Seattle fallback)
- Error messages: blank city, wrong city, no internet
- Design screen in Figma first, then build Android with XML and MVVM
- My API key is already in local.properties as OPENWEATHER_API_KEY
- Use professional UI like a designer

Please make a plan first then implement everything.
```

**What agent did:** Read my project files, made architecture plan (MVVM + Retrofit + Current Weather API 2.5), then created Kotlin files, XML layout, Gradle dependencies, and Figma file with 5 screens.

---

## Prompt 2 - Fix build and binding errors

```
Build failed with errors on MainActivity.kt about ItemWeatherDetailBinding vs View type.
Also gradle build fail with jlink / java path issue on Mac.
Please fix so assembleDebug works.
```

**What agent did:** Changed `bindDetail()` and `updateDetail()` to use `ItemWeatherDetailBinding` instead of generic View. Added `org.gradle.java.home` in gradle.properties for Android Studio JDK. Build became successful.

---

## Prompt 3 - Documentation for submission

```
Please write README.md, PROMPT.md, and REFLECTION.md for my zip submission.
README needs: how app works, hours spent, challenging parts, cite all websites used.
PROMPT.md needs at least 3 prompts and conversation summary.
REFLECTION.md needs answers about features, how AI helped, errors in AI output, what I fixed, what I learned.
Write in simple English like a student wrote it. I will submit directly without editing.
```

**What agent did:** Created these three markdown files based on the real project structure and development process.

---

## Prompt 4 - UI polish (extra prompt during work)

```
The hero card background should change color based on weather type - clear sky blue, rain darker, cloudy gray, night dark blue.
Also make sure search button disable when loading.
```

**What agent did:** Added `gradientForCondition()` function in MainActivity mapping weather main text (Clear, Rain, Clouds) to different gradient drawable. Loading overlay already disable search button.

---

## Prompt 5 - Test API connection

```
Can you verify my OpenWeather API key works with a test request to Seattle?
```

**What agent did:** Ran curl command with key from local.properties, confirmed JSON response with Seattle weather data returned successfully.

---

## Conversation Summary

I spend around 2-3 hours chatting with Cursor agent across one long session (plus some small fixes later).

**Beginning:** I paste the full homework requirements and tell agent my API key setup. Agent explore empty Android template project and propose plan: use Current Weather API 2.5 (not One Call 3.0 because need credit card), XML + ViewBinding + MVVM, Figma design first.

**Middle:** Agent create many files at once - Retrofit API interface, Gson models, Repository, ViewModel, MainActivity, activity_main.xml with hero card and detail grid, location helper with Seattle fallback, error strings matching assignment exactly. Agent also create Figma file with 5 frames (default, loading, errors).

**Problems we hit:**
- Kotlin compile error because include layouts generate ItemWeatherDetailBinding not View
- Gradle CLI build fail on Mac JDK path
- I ask agent to write submission docs (this file)

**End result:** Working debug APK, Figma link for design submission, all homework features including bonus location. I still read the generated code myself to understand ViewModel and Retrofit flow before submitting, because professor may ask in class.

**My opinion:** AI save a lot of time on boilerplate (Gradle deps, Retrofit setup, long XML layout). But I still need understand permission flow and error handling myself for interview or demo. I do not copy paste without reading.

---

## Tools Used

| Tool | Purpose |
|------|---------|
| Cursor (Composer agent) | Main coding assistant |
| Figma MCP in Cursor | Create UI mockup screens |
| Android Studio | Run emulator and debug |
| OpenWeatherMap website | API key and API documentation |
