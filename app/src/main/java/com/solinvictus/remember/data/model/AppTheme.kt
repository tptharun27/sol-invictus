package com.solinvictus.remember.data.model

enum class ThemeMode(val label: String) {
    SYSTEM("Follow System"),
    DARK_AMOLED("Pure Black (AMOLED)"),
    SLATE_DARK("Slate Dark"),
    CLEAN_WHITE("Crisp White Light")
}

enum class AccentTheme(val label: String, val primaryHex: String, val secondaryHex: String) {
    SOLAR_GOLD("Solar Gold", "#FFB800", "#FF9100"),
    ELECTRIC_VIOLET("Electric Violet", "#A855F7", "#7C3AED"),
    CYBER_CYAN("Cyber Cyan", "#06B6D4", "#0284C7"),
    EMERALD_GREEN("Emerald", "#10B981", "#059669"),
    SUNSET_ROSE("Sunset Rose", "#F43F5E", "#E11D48")
}
