package com.solinvictus.remember.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.solinvictus.remember.data.model.AccentTheme
import com.solinvictus.remember.data.model.ThemeMode

@Composable
fun SolInvictusTheme(
    themeMode: ThemeMode = ThemeMode.DARK_AMOLED,
    accentTheme: AccentTheme = AccentTheme.SOLAR_GOLD,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.DARK_AMOLED, ThemeMode.SLATE_DARK -> true
        ThemeMode.CLEAN_WHITE -> false
    }

    val isAmoled = themeMode == ThemeMode.DARK_AMOLED

    // Select Accent Colors
    val (primaryColor, secondaryColor, containerColor) = when (accentTheme) {
        AccentTheme.SOLAR_GOLD -> Triple(
            SolarGoldPrimary,
            SolarGoldSecondary,
            if (isDark) SolarGoldContainerDark else SolarGoldContainerLight
        )
        AccentTheme.ELECTRIC_VIOLET -> Triple(
            VioletPrimary,
            VioletSecondary,
            if (isDark) VioletContainerDark else VioletContainerLight
        )
        AccentTheme.CYBER_CYAN -> Triple(
            CyanPrimary,
            CyanSecondary,
            if (isDark) CyanContainerDark else CyanContainerLight
        )
        AccentTheme.EMERALD_GREEN -> Triple(
            EmeraldPrimary,
            EmeraldSecondary,
            if (isDark) EmeraldContainerDark else EmeraldContainerLight
        )
        AccentTheme.SUNSET_ROSE -> Triple(
            RosePrimary,
            RoseSecondary,
            if (isDark) RoseContainerDark else RoseContainerLight
        )
    }

    val colorScheme = if (isDark) {
        val bg = if (isAmoled) DarkBackground else SlateBackground
        val surface = if (isAmoled) DarkSurface else SlateSurface
        val card = if (isAmoled) DarkCard else SlateCard
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.Black,
            primaryContainer = containerColor,
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            onSecondary = Color.Black,
            background = bg,
            onBackground = DarkTextPrimary,
            surface = surface,
            onSurface = DarkTextPrimary,
            surfaceVariant = card,
            onSurfaceVariant = DarkTextSecondary,
            outline = DarkBorder,
            error = PriorityUrgent,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.Black,
            primaryContainer = containerColor,
            onPrimaryContainer = Color.Black,
            secondary = secondaryColor,
            onSecondary = Color.White,
            background = LightBackground,
            onBackground = LightTextPrimary,
            surface = LightSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = LightCardElevated,
            onSurfaceVariant = LightTextSecondary,
            outline = LightBorder,
            error = PriorityUrgent,
            onError = Color.White
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !isDark
                controller.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
