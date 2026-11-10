# Sol Invictus ☀️
> **"Remember Everything" — Modern Calendar, Forever Memories, Daily Alarms & Work Planner**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Code Style](https://img.shields.io/badge/Vide%20Coded-Verified-blue)]()

---

## 🌟 Overview

**Sol Invictus** (*The Unconquered Sun*) is a personalized, aesthetic mobile application designed to ensure you never forget what matters. It seamlessly bridges two essential pillars of life:
1. **Forever Memories (Life Vault)**: Record life milestones, promises, golden life lessons, and anniversaries that you want to remember forever. It features an automated **"On This Day"** recall engine that celebrates past moments every year!
2. **Work & Daily Tasks (Todo & Alarm Engine)**: Organize multiple daily tasks with exact alarms, recurring daily routine alarms, priorities (Urgent, High, Medium, Low), checklists, and actionable heads-up notifications (Snooze 10m / Mark Done).

The app features a **live digital clock synchronized with mobile device time**, a fluid **interactive calendar grid**, deep **aesthetic customization** (AMOLED Pure Black, Slate Dark, Crisp White Light, and 5 accent color schemes), **analytics & productivity streaks**, and **100% offline JSON backup & restore**.

---

## 📱 Standalone APK Download

The compiled, verified APK is directly available in this repository:
- 📦 **Download APK**: [`SolInvictus.apk`](SolInvictus.apk) (or [`SolInvictus-v1.0.apk`](SolInvictus-v1.0.apk))
- **File Size**: ~10.7 MB (Optimized release build)
- **Minimum Android Version**: Android 8.0 (API 26)
- **Target Android Version**: Android 14 (API 34)

### How to Install:
1. Download `SolInvictus.apk` to your Android device.
2. Tap the file in your downloads.
3. Allow "Install unknown apps" if prompted.
4. Open **Sol Invictus** and enjoy your personalized companion!

---

## ✨ Features

### 📅 1. System Time-Synced Calendar
- **Live Digital Clock**: Real-time clock updating every second, synced with mobile system time and timezone.
- **Dynamic Personalized Greeting**: Warm context-aware greetings ("Good Morning, Tharun!", "Good Afternoon!", "Good Evening!", "Good Night!").
- **Interactive Month Grid**: Navigate effortlessly across months with instant "Jump to Today" shortcut.
- **Visual Activity Dots**:
  - 🟡 **Gold / Accent dot**: Scheduled tasks for that day.
  - 🟢 **Green dot**: All scheduled tasks completed!
  - 🟣 **Violet / Rose dot**: Forever Memory or anniversary recorded on that date.
- **Day Agenda**: Tap any date to view and manage its dedicated tasks and memories.

### 🏆 2. Forever Memories (Life Vault)
- **Record Milestones**: Save moments, promises, achievements, lessons, and personal milestones forever.
- **Mood & Category Tags**: Categorize memories with emoji moods (🌟, ❤️, 🏆, 💡, 🎉, 🕊️, 🚀) and categories.
- **"On This Day" Anniversary Recall**: When today's date matches the month and day of a past memory, Sol Invictus shines an anniversary celebration spotlight banner on your home screen!
- **Time Elapsed Badges**: Automatically calculates "X years ago", "X days ago", or "Anniversary Today!".
- **Pin to Top**: Pin your most cherished memories to stay permanently featured.

### ⏰ 3. Work & Daily Tasks (TODO & Alarm Subsystem)
- **Multi-Task Scheduling**: Mark and organize multiple tasks in any given day.
- **Exact Alarms (`AlarmManager`)**: Exact waking alarms (`RTC_WAKEUP` / `AlarmClockInfo`) that alert you even when the screen is off or app is closed.
- **Recurring Daily Alarm**: Toggle "Daily Recurring Alarm" to trigger reminders every day at the same time (e.g. daily workout, study routine, daily review).
- **Interactive Notifications**:
  - ✅ **Mark Done**: Instantly marks the task complete and dismisses the alarm.
  - 💤 **Snooze 10m**: Postpones the alarm for 10 minutes.
  - ❌ **Dismiss**: Dismisses the notification.
- **Priority Levels**: Color-coded badges for **Urgent** 🔴, **High** 🟠, **Medium** 🔵, and **Low** ⚪.
- **Subtask Checklists**: Break complex tasks down into bite-sized actionable items.
- **Boot Persistence**: Automatically reschedules all active alarms if your phone restarts via `BootReceiver`.

### 🎨 4. Aesthetic Theming & Customization
- **Theme Modes**:
  - 🖤 **Pure Black (AMOLED)**: True `#000000` AMOLED dark theme for maximum battery saving and contrast.
  - 🌑 **Slate Dark**: Sleek deep-navy dark mode.
  - ☀️ **Crisp White Light**: Ultra-clean, modern light theme.
  - ⚙️ **Follow System**: Adapts seamlessly to Android system night mode.
- **5 Accent Color Schemes**:
  - 🟡 **Solar Gold** (Default Sol Invictus emblem)
  - 🟣 **Electric Violet**
  - 🔵 **Cyber Cyan**
  - 🟢 **Emerald Green**
  - 🔴 **Sunset Rose**
- **Preferences**: 12-Hour AM/PM vs 24-Hour time format toggle, Sunday vs Monday week start toggle, Master Daily Routine alarm toggle.

### 📊 5. Productivity Insights & Streaks
- **Completion Rate**: Visual progress bar showing tasks completed vs pending.
- **Productivity Streaks**: Counts consecutive days with completed tasks to keep your motivation high.
- **Life Vault Counters**: Total archived memories, pinned memories, and anniversaries.

### 💾 6. Offline JSON Backup & Restore
- **100% Private & Offline**: All SQLite data remains securely on your device.
- **Export to JSON**: Generate a portable JSON snapshot with one click.
- **Import from JSON**: Paste and restore your entire task and memory database anytime.

---

## 🏗️ Architecture & Technology Stack

```
Sol Invictus
├── app
│   ├── src/main/java/com/solinvictus/remember
│   │   ├── alarm/             # AlarmScheduler, AlarmReceiver, BootReceiver
│   │   ├── data/
│   │   │   ├── db/            # DatabaseHelper (SQLite), TaskDao, MemoryDao, BackupManager
│   │   │   ├── model/         # TaskItem, ForeverMemory, Priority, Themes
│   │   │   └── preferences/   # UserPreferences (SharedPreferences & StateFlows)
│   │   ├── ui/
│   │   │   ├── components/    # LiveHeader, CalendarView, TaskCard, MemoryCard
│   │   │   ├── dialogs/       # TaskEditDialog, MemoryEditDialog
│   │   │   ├── screens/       # CalendarScreen, TasksScreen, MemoriesScreen, StatsScreen, SettingsScreen
│   │   │   └── theme/         # Color, Theme (Material 3 Dynamic), Type
│   │   ├── util/              # DateTimeUtils (Date parsing, epoch math, greetings)
│   │   ├── MainActivity.kt    # Root Scaffold, NavHost, Permission handlers
│   │   └── SolInvictusApp.kt  # Notification Channels setup
│   └── src/test/java/         # Unit Test Suite (DataModelTest, DateTimeUtilsTest)
├── SolInvictus.apk            # Direct Installable Release APK
└── tasks/                     # Architecture plan & todo checklist
```

- **Language**: Kotlin 1.9.22
- **UI Toolkit**: Jetpack Compose with Material 3
- **Local Storage**: Android SQLite with OpenHelper & Indexing
- **Alarming**: Android `AlarmManager` with `setAlarmClock` / `setExactAndAllowWhileIdle`
- **Notifications**: `NotificationCompat` with High Priority Sound & Vibration channels
- **Build System**: Android Gradle Plugin 8.2.2 with Gradle 8.5, JDK 17

---

## 🧪 Testing & Verification

Comprehensive unit tests cover date formatting, timezone calculations, recurring alarms, Gson serialization, and enum mappings:

```powershell
# Run unit tests
.\gradlew.bat testDebugUnitTest

# Assemble Release APK
.\gradlew.bat assembleRelease
```

**Test Results:**
- `DataModelTest.kt`: 4 passed (100%)
- `DateTimeUtilsTest.kt`: 6 passed (100%)
- **Zero compile or runtime warnings / errors.**

---

## 📄 License & Credits

- **License**: [MIT Open Source License](LICENSE)
- **Author**: Tharun Pranav (Vide Coded)
- **App Name**: Sol Invictus — *Remember Everything*
