package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NetworkTelemetry
import com.example.data.VpnConnectionState
import com.example.data.VpnServerEntity
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElevatedCarbon
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.HandshakeAmber
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MidnightSlate
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.ShieldEmerald
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextPrimaryIce
import com.example.ui.theme.TextSecondarySteel

/**
 * Cyber-grid world node map backdrop with live encrypted route trajectory arc.
 */
@Composable
fun WorldNodeBackdropCanvas(
    servers: List<VpnServerEntity>,
    selectedServer: VpnServerEntity?,
    connectionState: VpnConnectionState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "map_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dash_phase"
    )

    val activeColor = when (connectionState) {
        VpnConnectionState.CONNECTED -> ShieldEmerald
        VpnConnectionState.CONNECTING, VpnConnectionState.RECONNECTING -> HandshakeAmber
        VpnConnectionState.KILL_SWITCH_LOCKED -> AlertCrimson
        VpnConnectionState.DISCONNECTED -> CyberCyan
    }

    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val w = size.width
        val h = size.height

        // Subtle latitude & longitude cyber grid
        val cols = 8
        val rows = 4
        for (i in 1 until cols) {
            val x = (w / cols) * i
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
        }
        for (j in 1 until rows) {
            val y = (h / rows) * j
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
        }

        // Origin node (User Device - South/Southeast Asia default hub)
        val origin = Offset(w * 0.72f, h * 0.52f)
        drawCircle(
            color = CyberCyan.copy(alpha = 0.35f),
            radius = 8f,
            center = origin
        )
        drawCircle(
            color = CyberCyan,
            radius = 3.5f,
            center = origin
        )

        // Plot global VPN servers
        servers.forEach { srv ->
            val pt = Offset(w * srv.mapX, h * srv.mapY)
            val isSelected = srv.id == selectedServer?.id
            drawCircle(
                color = if (isSelected) activeColor else TextMutedSlate.copy(alpha = 0.45f),
                radius = if (isSelected) 5.5f else 3f,
                center = pt
            )
            if (isSelected) {
                drawCircle(
                    color = activeColor.copy(alpha = (1f - pulseRadius / 22f).coerceIn(0f, 0.7f)),
                    radius = pulseRadius,
                    center = pt,
                    style = Stroke(width = 2f)
                )

                // Encrypted tunnel trajectory curve between user origin and selected server
                val routePath = Path().apply {
                    moveTo(origin.x, origin.y)
                    val ctrlX = (origin.x + pt.x) / 2f
                    val ctrlY = minOf(origin.y, pt.y) - h * 0.28f
                    quadraticTo(ctrlX, ctrlY, pt.x, pt.y)
                }
                drawPath(
                    path = routePath,
                    color = activeColor.copy(alpha = 0.75f),
                    style = Stroke(
                        width = 2.4f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), dashPhase)
                    )
                )
            }
        }
    }
}

/**
 * Centerpiece 188dp One-Tap Connect Power Orb with concentric glowing rings and real state feedback.
 */
@Composable
fun OneTapConnectOrb(
    connectionState: VpnConnectionState,
    onToggleConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_transition")
    val sweepRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_rotation"
    )
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    val targetColor by animateColorAsState(
        targetValue = when (connectionState) {
            VpnConnectionState.CONNECTED -> ShieldEmerald
            VpnConnectionState.CONNECTING, VpnConnectionState.RECONNECTING -> HandshakeAmber
            VpnConnectionState.KILL_SWITCH_LOCKED -> AlertCrimson
            VpnConnectionState.DISCONNECTED -> CyberCyan
        },
        animationSpec = tween(450),
        label = "orb_color"
    )

    val statusSubLabel = when (connectionState) {
        VpnConnectionState.CONNECTED -> "TAP TO DISCONNECT"
        VpnConnectionState.CONNECTING -> "HANDSHAKING…"
        VpnConnectionState.RECONNECTING -> "RECONNECTING…"
        VpnConnectionState.KILL_SWITCH_LOCKED -> "LOCKDOWN ACTIVE"
        VpnConnectionState.DISCONNECTED -> "ONE-TAP CONNECT"
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(188.dp)
            .clip(CircleShape)
            .semantics {
                role = Role.Button
                contentDescription = "One-Tap VPN Connect Button. Current state: ${connectionState.name}"
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = targetColor),
                onClick = onToggleConnect
            )
            .testTag("one_tap_connect_button")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)
            val maxRadius = w / 2f

            // Outer breathing halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        targetColor.copy(alpha = 0.26f * breathingScale),
                        targetColor.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius
                ),
                radius = maxRadius * breathingScale,
                center = center
            )

            // Outer technical ring
            drawCircle(
                color = targetColor.copy(alpha = 0.28f),
                radius = maxRadius * 0.86f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Rotating arc during CONNECTING / RECONNECTING or solid shield arc when CONNECTED
            if (connectionState == VpnConnectionState.CONNECTING ||
                connectionState == VpnConnectionState.RECONNECTING
            ) {
                val arcSize = maxRadius * 1.72f
                val topLeft = Offset(center.x - arcSize / 2f, center.y - arcSize / 2f)
                drawArc(
                    color = targetColor,
                    startAngle = sweepRotation,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = targetColor.copy(alpha = 0.5f),
                    startAngle = sweepRotation + 180f,
                    sweepAngle = 80f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            } else if (connectionState == VpnConnectionState.CONNECTED) {
                drawCircle(
                    color = ShieldEmerald,
                    radius = maxRadius * 0.86f,
                    center = center,
                    style = Stroke(width = 3.5.dp.toPx())
                )
            }

            // Inner metallic core button
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ElevatedCarbon,
                        MidnightSlate,
                        ObsidianVoid
                    )
                ),
                radius = maxRadius * 0.72f,
                center = center
            )

            drawCircle(
                color = targetColor.copy(alpha = 0.55f),
                radius = maxRadius * 0.72f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (connectionState) {
                    VpnConnectionState.CONNECTED -> Icons.Default.Security
                    VpnConnectionState.KILL_SWITCH_LOCKED -> Icons.Default.WarningAmber
                    else -> Icons.Default.PowerSettingsNew
                },
                contentDescription = null,
                tint = targetColor,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusSubLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                ),
                color = TextPrimaryIce
            )
        }
    }
}

/**
 * Real-Time Speed Meter (Download & Upload Speed Monitor) with live dual-sparkline graph.
 */
@Composable
fun RealTimeSpeedMeterCard(
    telemetry: NetworkTelemetry,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val (downVal, downUnit) = formatSpeed(telemetry.downloadBps)
    val (upVal, upUnit) = formatSpeed(telemetry.uploadBps)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
            .testTag("speed_meter_card"),
        color = MidnightSlate,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Download Metric Column
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ShieldEmerald.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Download Speed",
                            tint = ShieldEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DOWNLOAD • TOTAL ${formatTotalBytes(telemetry.sessionRxBytes)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondarySteel
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = downVal,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimaryIce
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = downUnit,
                                style = MaterialTheme.typography.labelMedium,
                                color = ShieldEmerald,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(38.dp)
                        .background(GlassBorderSubtle)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Upload Metric Column
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberCyan.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upload Speed",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "UPLOAD • TOTAL ${formatTotalBytes(telemetry.sessionTxBytes)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondarySteel
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = upVal,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimaryIce
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = upUnit,
                                style = MaterialTheme.typography.labelMedium,
                                color = CyberCyan,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live Sparkline Waveform
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianVoid.copy(alpha = 0.65f))
            ) {
                val w = size.width
                val h = size.height
                val downPoints = telemetry.downloadHistory
                val upPoints = telemetry.uploadHistory
                val maxSample = (downPoints.maxOrNull() ?: 1f).coerceAtLeast(1.5f)

                if (downPoints.size > 1) {
                    val downPath = Path()
                    val stepX = w / (downPoints.size - 1).coerceAtLeast(1)
                    downPoints.forEachIndexed { idx, v ->
                        val x = idx * stepX
                        val normalized = (v / maxSample).coerceIn(0.05f, 0.92f)
                        val y = h - (normalized * h)
                        if (idx == 0) downPath.moveTo(x, y) else downPath.lineTo(x, y)
                    }
                    drawPath(
                        path = downPath,
                        color = if (isConnected) ShieldEmerald else TextMutedSlate.copy(alpha = 0.5f),
                        style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                if (upPoints.size > 1) {
                    val upPath = Path()
                    val stepX = w / (upPoints.size - 1).coerceAtLeast(1)
                    upPoints.forEachIndexed { idx, v ->
                        val x = idx * stepX
                        val normalized = (v / maxSample).coerceIn(0.03f, 0.85f)
                        val y = h - (normalized * h)
                        if (idx == 0) upPath.moveTo(x, y) else upPath.lineTo(x, y)
                    }
                    drawPath(
                        path = upPath,
                        color = if (isConnected) CyberCyan.copy(alpha = 0.85f) else TextMutedSlate.copy(alpha = 0.3f),
                        style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
