package com.pulasthi.tapit.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TapItColorScheme = lightColorScheme(
    primary = TapItBluePrimary,
    onPrimary = TapItWhite,
    background = TapItWhite,
    surface = TapItWhite,
    onBackground = TapItTextPrimary,
    onSurface = TapItTextPrimary,
)

@Composable
fun TapItTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = TapItBlueDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = TapItColorScheme,
        typography = Typography,
        content = content,
    )
}
