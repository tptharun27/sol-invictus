# Task List: Sol Invictus Mobile App

## Phase 1: Project Setup & Build Environment
- [x] Task 1: Initialize Android project configuration (`settings.gradle.kts`, `build.gradle.kts`, `app/build.gradle.kts`, `gradle.properties`, wrapper)
  - Acceptance criteria:
    - Root and app build scripts configured for Kotlin 1.9.22 and AGP 8.2.2.
    - Android SDK 34, minSdk 26, targetSdk 34 set.
    - Jetpack Compose dependencies (Material 3, Icons extended, Navigation) declared.
- [x] Task 2: AndroidManifest.xml and Resource Configuration
  - Acceptance criteria:
    - Permissions declared (`POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED`, `VIBRATE`, `WAKE_LOCK`).
    - App name, theme, and receivers declared.
    - App icon and colors configured.

## Checkpoint 1: Project Foundation
- [x] Gradle build initializes and compiles successfully.

## Phase 2: Core Data Layer
- [x] Task 3: Room Database & Entities
  - Acceptance criteria:
    - `TaskItem` entity with id, title, description, date, time, priority, isCompleted, hasAlarm, isDailyAlarm, reminderTimeMillis.
    - `ForeverMemory` entity with id, title, notes, date, category, mood, isPinned, repeatYearly.
    - `AppDatabase` with DAOs for Tasks and Memories, supporting queries by date, search, and stats.
- [x] Task 4: User Preferences & Theme DataStore
  - Acceptance criteria:
    - Store ThemeMode (SYSTEM, DARK, LIGHT, AMOLED), AccentColor, 24h clock toggle, week start day.
- [x] Task 5: Backup & Restore Manager
  - Acceptance criteria:
    - Export all tasks and memories to clean JSON format.
    - Import JSON and restore database without duplicates or corruption.

## Checkpoint 2: Data & Persistence
- [x] Unit tests pass for database queries, converters, and JSON backup/restore.

## Phase 3: Alarm & Notification Engine
- [x] Task 6: AlarmScheduler & Receivers
  - Acceptance criteria:
    - Schedule exact alarm using `AlarmManager.setExactAndAllowWhileIdle`.
    - Support one-time alarms and daily recurring alarms.
    - Reschedule alarms on device reboot via `BootReceiver`.
- [x] Task 7: Notification Manager & Alert Actions
  - Acceptance criteria:
    - High-importance notification channel with sound & vibration.
    - Interactive action buttons: "Mark Done", "Snooze (10m)".
    - Snooze logic reschedules alarm for +10 minutes.

## Checkpoint 3: Alarm Subsystem
- [x] Alarm calculation and intent creation verified via tests.

## Phase 4: UI & Jetpack Compose Screens
- [x] Task 8: Aesthetic Design System & Theming
  - Acceptance criteria:
    - Material 3 theme supporting AMOLED Dark, Sleek Light, and 5 accent color schemes.
    - Responsive layouts with modern typography and card shapes.
- [x] Task 9: Live Clock Header & Interactive Calendar
  - Acceptance criteria:
    - Live digital time synced with device clock and dynamic greeting.
    - Month grid view with swipe/previous/next controls.
    - Event & task indicator dots on calendar dates.
    - Selected day schedule agenda.
- [x] Task 10: Task & Todo Screen with Daily Alarm Toggle
  - Acceptance criteria:
    - Day task list with checkboxes, priority chips, alarm time indicators.
    - Add/Edit task bottom sheet with date, time, alarm toggle, and "Repeat Daily" switch.
    - Filter tabs (All, Pending, Completed, Today).
- [x] Task 11: Forever Memories Screen & Milestone Vault
  - Acceptance criteria:
    - Timeline view of saved memories with category tags and mood icons.
    - "On This Day" anniversary spotlight for memories matching today's month/day.
    - Add/Edit memory bottom sheet with date, category, story/notes, and yearly reminder toggle.
- [x] Task 12: Global Search & Productivity Analytics
  - Acceptance criteria:
    - Instant search across all memories and tasks with highlight.
    - Productivity stats: task completion rate, streaks, memory count.
- [x] Task 13: Aesthetic Settings Screen
  - Acceptance criteria:
    - Theme selector (AMOLED Black, Crisp White, System).
    - Accent color palette selector (Gold, Violet, Cyan, Emerald, Rose).
    - Backup to JSON & Restore from JSON buttons.
- [x] Task 14: Navigation & Main Activity Integration
  - Acceptance criteria:
    - Bottom navigation bar linking Calendar, Tasks, Memories, Stats, Settings.
    - Floating action button with speed-dial for quick capture.

## Checkpoint 4: Complete UI & Integration
- [x] App builds cleanly, preview and navigation functional.

## Phase 5: Testing, APK Build & GitHub Publication
- [x] Task 15: Unit Tests execution and verification.
  - Acceptance criteria:
    - All tests pass: `./gradlew test`.
- [x] Task 16: Standalone APK Build (`.apk`)
  - Acceptance criteria:
    - `./gradlew assembleDebug` / `assembleRelease` produces valid APK file.
    - Copy final APK to root folder as `SolInvictus.apk`.
- [x] Task 17: Documentation & Licensing
  - Acceptance criteria:
    - MIT Open Source LICENSE file with copyright.
    - README.md with comprehensive documentation, features, screenshots/diagrams, and install guide.
- [x] Task 18: GitHub Repository Creation & Push
  - Acceptance criteria:
    - Git initialized, commits made.
    - GitHub repository created and pushed to `tptharun27/sol-invictus`.

## Checkpoint 5: Project Delivery
- [x] APK verified, GitHub repo live, all acceptance criteria satisfied.
