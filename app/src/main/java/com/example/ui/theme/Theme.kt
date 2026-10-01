package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NVpnDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = ObsidianVoid,
    primaryContainer = ElevatedCarbonHighlight,
    onPrimaryContainer = TextPrimaryIce,
    secondary = ShieldEmerald,
    onSecondary = ObsidianVoid,
    secondaryContainer = ElevatedCarbon,
    onSecondaryContainer = ShieldEmerald,
    tertiary = VipGold,
    onTertiary = ObsidianVoid,
    background = ObsidianVoid,
    onBackground = TextPrimaryIce,
    surface = MidnightSlate,
    onSurface = TextPrimaryIce,
    surfaceVariant = ElevatedCarbon,
    onSurfaceVariant = TextSecondarySteel,
    error = AlertCrimson,
    onError = TextPrimaryIce,
    outline = GlassBorderSubtle
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // N VPN uses an intentional, atmospheric Dark Mode Cyber-Security theme
    MaterialTheme(
        colorScheme = NVpnDarkColorScheme,
        typography = Typography,
        content = content
    )
}
