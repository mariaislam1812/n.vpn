package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NetworkTelemetry
import com.example.data.VpnConnectionState
import com.example.data.VpnProtocol
import com.example.data.VpnServerEntity
import com.example.data.VpnSettingsEntity
import com.example.ui.components.NVpnBrandEmblem
import com.example.ui.components.OneTapConnectOrb
import com.example.ui.components.RealTimeSpeedMeterCard
import com.example.ui.components.WorldNodeBackdropCanvas
import com.example.ui.components.formatSessionDuration
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
import com.example.ui.theme.VipGold

@Composable
fun HomeConnectScreen(
    servers: List<VpnServerEntity>,
    selectedServer: VpnServerEntity?,
    settings: VpnSettingsEntity,
    isVipActive: Boolean,
    connectionState: VpnConnectionState,
    sessionDurationSeconds: Long,
    telemetry: NetworkTelemetry,
    onToggleConnect: () -> Unit,
    onOpenServerSelector: () -> Unit,
    onSelectProtocol: (VpnProtocol) -> Unit,
    onOpenVipScreen: () -> Unit,
    onReplaySplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeProtocol = runCatching {
        VpnProtocol.valueOf(settings.selectedProtocol)
    }.getOrDefault(VpnProtocol.WIREGUARD)

    val statusColor = when (connectionState) {
        VpnConnectionState.CONNECTED -> ShieldEmerald
        VpnConnectionState.CONNECTING, VpnConnectionState.RECONNECTING -> HandshakeAmber
        VpnConnectionState.KILL_SWITCH_LOCKED -> AlertCrimson
        VpnConnectionState.DISCONNECTED -> TextSecondarySteel
    }

    val statusHeadline = when (connectionState) {
        VpnConnectionState.CONNECTED -> "TUNNEL PROTECTED"
        VpnConnectionState.CONNECTING -> "ESTABLISHING TUNNEL…"
        VpnConnectionState.RECONNECTING -> "AUTO-RECONNECTING…"
        VpnConnectionState.KILL_SWITCH_LOCKED -> "KILL SWITCH LOCKDOWN"
        VpnConnectionState.DISCONNECTED -> "DISCONNECTED"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Brand Bar with Logo + Slogan ("Fast. Safe. Unlimited.") + VIP / Splash Replay Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NVpnBrandEmblem(
                    emblemSize = 44.dp,
                    showSlogan = false,
                    isConnected = connectionState == VpnConnectionState.CONNECTED
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "N VPN",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = TextPrimaryIce
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = if (isVipActive) VipGold.copy(alpha = 0.18f) else CyberCyan.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isVipActive) "VIP PRO" else "ZERO-LOG",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isVipActive) VipGold else CyberCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Fast. Safe. Unlimited.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            color = CyberCyan
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onReplaySplash,
                    modifier = Modifier.testTag("replay_splash_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Preview Splash Screen",
                        tint = CyberCyan
                    )
                }

                Surface(
                    color = if (isVipActive) VipGold.copy(alpha = 0.2f) else ElevatedCarbon,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .border(
                            1.dp,
                            if (isVipActive) VipGold.copy(alpha = 0.6f) else GlassBorderSubtle,
                            RoundedCornerShape(999.dp)
                        )
                        .clickable { onOpenVipScreen() }
                        .testTag("vip_badge_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP Access",
                            tint = VipGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isVipActive) "VIP Active" else "Go VIP",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isVipActive) VipGold else TextPrimaryIce
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Pill + Session Timer + Exit IP
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = statusHeadline,
                            style = MaterialTheme.typography.labelLarge,
                            color = statusColor
                        )
                        Text(
                            text = "IP: ${telemetry.publicExitIp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondarySteel
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatSessionDuration(sessionDurationSeconds),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimaryIce
                    )
                    Text(
                        text = if (settings.killSwitchEnabled) "KILL SWITCH: ON" else "KILL SWITCH: OFF",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (settings.killSwitchEnabled) ShieldEmerald else TextMutedSlate
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // World Map + One-Tap Connect Orb Stack
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            WorldNodeBackdropCanvas(
                servers = servers,
                selectedServer = selectedServer,
                connectionState = connectionState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                OneTapConnectOrb(
                    connectionState = connectionState,
                    onToggleConnect = onToggleConnect
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Protocol Quick-Switch Chips (WireGuard, OpenVPN, V2Ray, Shadowsocks)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VpnProtocol.entries.forEach { protocol ->
                val isSelected = protocol == activeProtocol
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectProtocol(protocol) },
                    label = {
                        Text(
                            text = protocol.shortTag,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Shield else Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan.copy(alpha = 0.18f),
                        selectedLabelColor = CyberCyan,
                        selectedLeadingIconColor = CyberCyan,
                        containerColor = MidnightSlate,
                        labelColor = TextSecondarySteel
                    ),
                    modifier = Modifier.testTag("protocol_chip_${protocol.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Server Card (Tappable to open full Server List)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
                .clickable { onOpenServerSelector() }
                .testTag("selected_server_card"),
            color = ElevatedCarbon,
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedServer?.flagEmoji ?: "🇸🇬",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedServer?.country ?: "Singapore",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimaryIce
                            )
                            if (selectedServer?.isVip == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = VipGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VipGold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${selectedServer?.city ?: "Marina Bay"} • ${activeProtocol.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondarySteel
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val ping = selectedServer?.pingMs ?: 22
                    val pingColor = when {
                        ping < 50 -> ShieldEmerald
                        ping <= 110 -> HandshakeAmber
                        else -> AlertCrimson
                    }
                    Surface(
                        color = pingColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NetworkCheck,
                                contentDescription = "Latency",
                                tint = pingColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${ping}ms",
                                style = MaterialTheme.typography.labelMedium,
                                color = pingColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Change Server",
                        tint = TextSecondarySteel
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Real-Time Speed Meter (Upload / Download Monitor + Sparkline)
        RealTimeSpeedMeterCard(
            telemetry = telemetry,
            isConnected = connectionState == VpnConnectionState.CONNECTED
        )

        // AdMob Banner Slot Preview (Displayed only for Free Tier; hidden for VIP users)
        if (!isVipActive) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                    .clickable { onOpenVipScreen() }
                    .testTag("admob_banner_slot"),
                color = ObsidianVoid,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = HandshakeAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "AdMob",
                                style = MaterialTheme.typography.labelSmall,
                                color = HandshakeAmber,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Watch a 5s Rewarded Ad for 24h Free VIP 10Gbps!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondarySteel
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = "Unlock VIP",
                        tint = VipGold
                    )
                }
            }
        }
    }
}
