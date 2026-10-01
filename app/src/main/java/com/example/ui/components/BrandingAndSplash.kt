package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElevatedCarbon
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MidnightSlate
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.ShieldEmerald
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextPrimaryIce
import com.example.ui.theme.TextSecondarySteel
import java.util.Locale

/**
 * Custom geometric N VPN Shield & Monogram Logo with optional "Fast. Safe. Unlimited." slogan.
 */
@Composable
fun NVpnBrandEmblem(
    modifier: Modifier = Modifier,
    emblemSize: Dp = 88.dp,
    showSlogan: Boolean = true,
    isConnected: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "brand_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val primaryGlow = if (isConnected) ShieldEmerald else CyberCyan

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(emblemSize)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f, h / 2f)

                // Ambient radial aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryGlow.copy(alpha = 0.28f * glowAlpha),
                            Color.Transparent
                        ),
                        center = center,
                        radius = w * 0.56f
                    ),
                    radius = w * 0.56f,
                    center = center
                )

                // Faceted Hex-Shield Path
                val shieldPath = Path().apply {
                    moveTo(w * 0.50f, h * 0.08f)
                    lineTo(w * 0.86f, h * 0.22f)
                    lineTo(w * 0.86f, h * 0.54f)
                    cubicTo(
                        w * 0.86f, h * 0.74f,
                        w * 0.69f, h * 0.87f,
                        w * 0.50f, h * 0.94f
                    )
                    cubicTo(
                        w * 0.31f, h * 0.87f,
                        w * 0.14f, h * 0.74f,
                        w * 0.14f, h * 0.54f
                    )
                    lineTo(w * 0.14f, h * 0.22f)
                    close()
                }

                drawPath(
                    path = shieldPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ElevatedCarbon,
                            MidnightSlate,
                            ObsidianVoid
                        )
                    )
                )

                drawPath(
                    path = shieldPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CyberCyan,
                            ShieldEmerald
                        )
                    ),
                    style = Stroke(width = w * 0.045f, join = StrokeJoin.Round)
                )

                // Bold Geometric 'N' inside the shield
                val nPath = Path().apply {
                    moveTo(w * 0.34f, h * 0.66f)
                    lineTo(w * 0.34f, h * 0.34f)
                    lineTo(w * 0.66f, h * 0.66f)
                    lineTo(w * 0.66f, h * 0.34f)
                }

                drawPath(
                    path = nPath,
                    brush = Brush.linearGradient(
                        colors = listOf(CyberCyan, TextPrimaryIce, ShieldEmerald)
                    ),
                    style = Stroke(
                        width = w * 0.085f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Explicit Brand Name: "N VPN"
        Text(
            text = "N VPN",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp
            ),
            color = TextPrimaryIce
        )

        if (showSlogan) {
            Spacer(modifier = Modifier.height(4.dp))
            // Exact Slogan directly underneath the logo: "Fast. Safe. Unlimited."
            Text(
                text = "Fast. Safe. Unlimited.",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.4.sp
                ),
                color = CyberCyan,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Full-screen dark-mode Splash Screen displaying the "N VPN" logo and "Fast. Safe. Unlimited."
 * directly underneath it, along with cryptographic engine initialization feedback.
 */
@Composable
fun NVpnSplashScreen(
    onDismissSplash: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianVoid,
                        MidnightSlate,
                        ObsidianVoid
                    )
                )
            )
            .clickable { onDismissSplash() }
            .padding(24.dp)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NVpnBrandEmblem(
                emblemSize = 128.dp,
                showSlogan = true,
                isConnected = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                color = ElevatedCarbon.copy(alpha = 0.75f),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.border(1.dp, GlassBorderSubtle, RoundedCornerShape(999.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Zero-Log Verified",
                        tint = ShieldEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WireGuard® • OpenVPN • V2Ray • Zero-Log RAM Core",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondarySteel
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp)
                    .clip(CircleShape),
                color = CyberCyan,
                trackColor = ElevatedCarbon
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextMutedSlate,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Initializing ChaCha20-Poly1305 Key Exchange…",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedSlate
                )
            }
        }
    }
}

fun formatSpeed(bytesPerSecond: Long): Pair<String, String> {
    return when {
        bytesPerSecond >= 1024L * 1024L -> {
            val mbps = bytesPerSecond / (1024.0 * 1024.0)
            String.format(Locale.US, "%.2f", mbps) to "MB/s"
        }
        bytesPerSecond >= 1024L -> {
            val kbps = bytesPerSecond / 1024.0
            String.format(Locale.US, "%.1f", kbps) to "KB/s"
        }
        else -> {
            "$bytesPerSecond" to "B/s"
        }
    }
}

fun formatTotalBytes(bytes: Long): String {
    return when {
        bytes >= 1024L * 1024L * 1024L ->
            String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024L * 1024L ->
            String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
        bytes >= 1024L ->
            String.format(Locale.US, "%.0f KB", bytes / 1024.0)
        else -> "$bytes B"
    }
}

fun formatSessionDuration(totalSeconds: Long): String {
    val hrs = totalSeconds / 3600
    val mins = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
}
