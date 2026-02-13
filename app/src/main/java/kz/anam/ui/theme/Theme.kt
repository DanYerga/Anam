package kz.anam.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// LIGHT THEME - Обновлённая нежная палитра
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
private val LightColorScheme = lightColorScheme(
    primary = MediumPurple,
    onPrimary = PureWhite,
    primaryContainer = SoftLavender,
    onPrimaryContainer = DarkPurple,

    secondary = PeachPink,
    onSecondary = PureWhite,
    secondaryContainer = SoftPeach,
    onSecondaryContainer = DarkPurple,

    tertiary = DeepPurple,
    onTertiary = PureWhite,
    tertiaryContainer = LightPurple,
    onTertiaryContainer = DarkPurple,

    background = PureWhite,
    onBackground = TextPrimary,

    surface = OffWhite,
    onSurface = TextPrimary,
    surfaceVariant = SoftLavender,
    onSurfaceVariant = TextSecondary,

    surfaceTint = MediumPurple,

    error = LightCoral,
    onError = PureWhite,
    errorContainer = LightCoral.copy(alpha = 0.1f),
    onErrorContainer = DarkPurple,

    outline = LightGray,
    outlineVariant = MediumGray.copy(alpha = 0.3f),

    scrim = DarkPurple.copy(alpha = 0.32f),

    inverseSurface = DarkPurple,
    inverseOnSurface = PureWhite,
    inversePrimary = LightPurple,
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// DARK THEME - Нежная тёмная тема (опционально)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
private val DarkColorScheme = darkColorScheme(
    primary = LightPurple,
    onPrimary = DarkPurple,
    primaryContainer = DeepPurple,
    onPrimaryContainer = SoftLavender,

    secondary = SoftPeach,
    onSecondary = DarkPurple,
    secondaryContainer = PeachPink,
    onSecondaryContainer = PureWhite,

    tertiary = MediumPurple,
    onTertiary = PureWhite,

    background = DarkPurple,
    onBackground = PureWhite,

    surface = RichPurple,
    onSurface = PureWhite,
    surfaceVariant = DarkPurple,
    onSurfaceVariant = PureWhite.copy(alpha = 0.7f),

    error = LightCoral,
    onError = PureWhite,

    outline = PureWhite.copy(alpha = 0.2f),
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// MAIN THEME COMPOSABLE
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun AnamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}