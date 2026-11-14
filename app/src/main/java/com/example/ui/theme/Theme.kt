package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = EmeraldDeep,
    secondary = SaffronSecondary,
    onSecondary = Color(0xFF1F1600),
    secondaryContainer = SaffronContainer,
    onSecondaryContainer = SaffronDeep,
    tertiary = IndigoTertiary,
    onTertiary = Color.White,
    tertiaryContainer = IndigoContainer,
    onTertiaryContainer = IndigoDeep,
    background = WarmIvoryBackground,
    onBackground = InkOnSurface,
    surface = PureCardSurface,
    onSurface = InkOnSurface,
    surfaceVariant = SoftCreamSurfaceVariant,
    onSurfaceVariant = MutedInk,
    outline = WarmBorderOutline,
    error = GentleErrorRed,
    onError = Color.White,
    errorContainer = GentleErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2DD4BF),
    onPrimary = EmeraldDeep,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldLight,
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF271900),
    secondaryContainer = SaffronDeep,
    onSecondaryContainer = SaffronContainer,
    tertiary = Color(0xFF818CF8),
    onTertiary = IndigoDeep,
    tertiaryContainer = IndigoDeep,
    onTertiaryContainer = IndigoContainer,
    background = DeepSlateBackground,
    onBackground = LightInk,
    surface = DarkCardSurface,
    onSurface = LightInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MutedLightInk,
    outline = DarkOutline,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun EnglishSeekhenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
