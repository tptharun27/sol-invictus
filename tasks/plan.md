# Implementation Plan: Sol Invictus - Mobile Calendar, Forever Memories & Task/Alarm App

Sol Invictus is a personalized Android application built from scratch with Kotlin and Jetpack Compose (Material 3). It unites a device-synced calendar with two dedicated pillars: "Forever Memories" (permanent life events, milestones, anniversaries with memory recall) and "Work & Daily Tasks" (rich TODO system with exact alarms, recurring daily alarms, notifications, snooze, and priority badges). It features deep aesthetic customization (Dark AMOLED / Clean White Light / Custom Accent Themes), search, statistics, and full JSON backup/restore.

## User Requirements & Architecture
- **Calendar & System Time Sync**: Live clock synced to device time, Month grid, Week view, Agenda view, date indicator dots for events and tasks.
- **Forever Memories**: Life milestones, anniversaries, memories with tags, moods, photo/note attachments, yearly repeat ("On This Day" reminder), and time countdown/since counters.
- **Task & Todo Management with Alarms**:
  - Multi-task per day scheduling.
  - Priority levels (Urgent, High, Medium, Low).
  - Exact alarms via Android `AlarmManager` (`RTC_WAKEUP`) with notifications, ringtone/sound, and vibration.
  - Recurring Daily Alarms (e.g. daily morning reminder, daily review, daily medicine/habits).
  - Snooze (10 mins) and Mark Done action buttons on notifications.
  - `BootReceiver` to automatically reschedule alarms after device reboot.
- **Aesthetic Customization**:
  - Dark Mode (True AMOLED Black & Deep Slate) and White Light Mode.
  - Accent color presets: Solar Gold (Default), Electric Violet, Cyber Cyan, Emerald Green, Sunset Crimson.
  - 12h / 24h format and Week start day configuration.
- **Extra Features**:
  - Global Search across all memories and tasks.
  - Statistics & Productivity dashboard (completion rate, streaks, memory totals).
  - Data Export & Import (JSON) for full offline backup.
- **Deliverables**:
  - Fully working and tested Android project with Gradle wrapper.
  - Production-ready APK (`SolInvictus-release.apk` / `SolInvictus-debug.apk`) generated in project root.
  - Git repository initialized with MIT Open Source license, README.md, and published to GitHub.

---

## Technical Stack & Architecture
- **Language**: Kotlin 1.9.22
- **UI Framework**: Android Jetpack Compose with Material 3
- **Local Persistence**: Room SQLite Database + DataStore Preferences
- **Alarm & Notification Subsystem**: Android `AlarmManager` (`setExactAndAllowWhileIdle`), `BroadcastReceiver`, `NotificationCompat` with High Priority channel
- **Build System**: Android Gradle Plugin 8.2.2 with Gradle 8.5, JDK 17, Target SDK 34 (Android 14)
- **Architecture**: MVVM with unidirectional data flow (StateFlow), Room DAOs, and Alarm Repository

---

## Task Breakdown

### Phase 1: Project Setup & Build Environment
- [ ] Task 1: Initialize Android project structure (`settings.gradle.kts`, root `build.gradle.kts`, `app/build.gradle.kts`, `gradle.properties`, `gradle-wrapper`).
- [ ] Task 2: Configure Android Manifest, App permissions (`SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`, `VIBRATE`, `WAKE_LOCK`), and application icon/branding.
- [ ] Checkpoint: Verify project compiles and runs a blank Compose screen.

### Phase 2: Core Data Layer (Database, Entities & Preferences)
- [ ] Task 3: Room Database setup: `ForeverMemory` entity, `TaskItem` entity, DAOs, TypeConverters for Date/Time.
- [ ] Task 4: Theme & Settings DataStore / Repository: Theme modes (Dark/Light/AMOLED), accent colors, 12h/24h format, notification preferences.
- [ ] Task 5: Backup & Restore Manager: JSON serialization and deserialization of all memories, tasks, and settings.
- [ ] Checkpoint: Unit tests pass for Room database operations and JSON backup/restore.

### Phase 3: Alarm & Notification Engine
- [ ] Task 6: Implement `AlarmScheduler`, `AlarmReceiver`, and `BootReceiver` for exact one-time and recurring daily alarms.
- [ ] Task 7: Notification Builder with sound, vibration, high-priority heads-up display, and action buttons (`Snooze`, `Mark Done`).
- [ ] Checkpoint: Verify alarm calculations, recurring daily alarm logic, and notification triggering.

### Phase 4: UI & Jetpack Compose Screens
- [ ] Task 8: Aesthetic Design System: Color palettes, typography, Dark & Light Material 3 themes, animated transitions.
- [ ] Task 9: Live Header & Calendar View: Real-time clock synced to mobile, Month calendar grid with task/memory indicators, quick date picker, today shortcut.
- [ ] Task 10: Task & Todo Screen: Day task list, Priority badges, Completion checkboxes, Daily Alarm toggle, New Task dialog with time & alarm picker.
- [ ] Task 11: Forever Memories Screen: Life milestone timeline, Category tags, "On This Day" anniversary detector, Memory detail view & editor.
- [ ] Task 12: Global Search, Analytics & Productivity Stats Screen: Task completion metrics, streak counter, memory archives.
- [ ] Task 13: Settings Screen: Theme mode switcher (AMOLED Dark / White Light), Accent color picker, Sound/Vibrate toggles, Backup & Restore button.
- [ ] Task 14: Navigation & Main Activity: Bottom navigation bar or top tab navigation uniting Calendar, Tasks, Memories, and Settings.
- [ ] Checkpoint: Full UI builds and previews cleanly.

### Phase 5: Testing, APK Generation & GitHub Release
- [ ] Task 15: Automated Unit Tests for core business logic, date calculations, alarm triggers, and Room migrations.
- [ ] Task 16: Build debug & release APKs (`.apk`) using Gradle, verify manifest and assets.
- [ ] Task 17: Initialize Git repository, add MIT License + Open Source metadata, write comprehensive README.md.
- [ ] Task 18: Push to GitHub repository under user's account (`tptharun27/sol-invictus`).
- [ ] Checkpoint: Final APK ready for user installation, GitHub repository live.
