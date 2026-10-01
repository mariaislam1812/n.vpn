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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VpnProtocol
import com.example.data.VpnSettingsEntity
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
fun SecurityAndProtocolScreen(
    settings: VpnSettingsEntity,
    configPreview: String,
    onSelectProtocol: (VpnProtocol) -> Unit,
    onToggleKillSwitch: (Boolean) -> Unit,
    onToggleAutoReconnect: (Boolean) -> Unit,
    onToggleSplitTunneling: (Boolean) -> Unit,
    onSelectDns: (String) -> Unit,
    onTriggerKillSwitchTest: () -> Unit,
    onPurgeZeroLogData: () -> Unit,
    onCopiedConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val activeProtocol = runCatching {
        VpnProtocol.valueOf(settings.selectedProtocol)
    }.getOrDefault(VpnProtocol.WIREGUARD)

    val dnsOptions = listOf(
        "1.1.1.1 (Cloudflare Zero-Log)",
        "9.9.9.9 (Quad9 Threat Block)",
        "8.8.8.8 (Google Anycast DNS)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "VPN Core, Protocol & Security",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryIce
        )
        Text(
            text = "Configure WireGuard®, OpenVPN, V2Ray/VMess, Kill Switch lockdown, and RAM-only Zero-Log privacy.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondarySteel
        )

        // Protocol Engine Selector Card
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TUNNEL PROTOCOL ENGINE",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                VpnProtocol.entries.forEach { proto ->
                    val isSelected = proto == activeProtocol
                    Surface(
                        color = if (isSelected) ElevatedCarbon else ObsidianVoid.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                1.dp,
                                if (isSelected) CyberCyan else GlassBorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectProtocol(proto) }
                            .testTag("security_proto_${proto.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = proto.displayName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isSelected) CyberCyan else TextPrimaryIce
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Port ${proto.defaultPort}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ShieldEmerald
                                    )
                                }
                                Text(
                                    text = proto.cipherSuite,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondarySteel
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = proto.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMutedSlate
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Generated Cryptographic Configuration Preview
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE ${activeProtocol.shortTag} CONFIG INSPECTOR",
                        style = MaterialTheme.typography.labelLarge,
                        color = ShieldEmerald
                    )
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(configPreview))
                            onCopiedConfig()
                        },
                        modifier = Modifier.testTag("copy_config_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Config",
                            tint = CyberCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianVoid)
                        .padding(12.dp)
                ) {
                    Text(
                        text = configPreview,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            lineHeight = 16.sp
                        ),
                        color = TextPrimaryIce
                    )
                }
            }
        }

        // Kill Switch & Auto-Reconnect Controls
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "KILL SWITCH & AUTO-RECONNECT",
                    style = MaterialTheme.typography.labelLarge,
                    color = HandshakeAmber
                )

                SecurityToggleRow(
                    title = "Kill Switch (Block Traffic Without VPN)",
                    subtitle = "Disables VpnService.Builder.allowBypass() & locks out unencrypted ISP leaks if the tunnel drops.",
                    checked = settings.killSwitchEnabled,
                    onCheckedChange = onToggleKillSwitch,
                    tag = "kill_switch_toggle"
                )

                SecurityToggleRow(
                    title = "Auto-Reconnect on Network Handoff",
                    subtitle = "Monitors ConnectivityManager Wi-Fi ↔ 5G switches and re-establishes tunnel automatically.",
                    checked = settings.autoReconnectEnabled,
                    onCheckedChange = onToggleAutoReconnect,
                    tag = "auto_reconnect_toggle"
                )

                SecurityToggleRow(
                    title = "Split Tunneling (Bypass Local LAN)",
                    subtitle = "Allows local subnet 192.168.0.0/16 devices while routing all internet traffic via N VPN.",
                    checked = settings.splitTunnelingEnabled,
                    onCheckedChange = onToggleSplitTunneling,
                    tag = "split_tunnel_toggle"
                )

                Button(
                    onClick = onTriggerKillSwitchTest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElevatedCarbon,
                        contentColor = HandshakeAmber
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_kill_switch_button")
                ) {
                    Icon(Icons.Default.LockReset, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Packet Drop (Test Kill Switch & Auto-Reconnect)",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Custom Zero-Log DNS Resolver
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENCRYPTED DNS RESOLVER",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberCyan
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                dnsOptions.forEach { dns ->
                    val selected = settings.customDns == dns
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectDns(dns) },
                        label = { Text(dns, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan
                        ),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    )
                }
            }
        }

        // Strict No-Logs Policy & Encryption Verification Card
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ShieldEmerald.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.GppGood, contentDescription = null, tint = ShieldEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "STRICT NO-LOGS POLICY • RAM-ONLY DISKFREE NODES",
                        style = MaterialTheme.typography.labelLarge,
                        color = ShieldEmerald
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Zero browsing history, DNS queries, or source IP addresses are ever written to disk.\n" +
                        "• Ephemeral session keys rotate every 180 seconds using Perfect Forward Secrecy (PFS).\n" +
                        "• Local preferences use isolated storage and can be wiped in one tap below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondarySteel
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onPurgeZeroLogData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("purge_zero_log_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = AlertCrimson
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zero-Trace Purge (Wipe Custom Nodes & Reset Session)",
                        style = MaterialTheme.typography.labelMedium,
                        color = AlertCrimson
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryIce
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondarySteel
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ObsidianVoid,
                checkedTrackColor = ShieldEmerald,
                uncheckedThumbColor = TextSecondarySteel,
                uncheckedTrackColor = ObsidianVoid
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
fun VipAndBlueprintScreen(
    settings: VpnSettingsEntity,
    isVipActive: Boolean,
    rewardAdCountdown: Int?,
    onWatchRewardedAd: () -> Unit,
    onActivatePlan: (String) -> Unit,
    onResetFreeTier: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBlueprintTab by remember { mutableIntStateOf(0) }
    val blueprintTitles = listOf(
        "Flutter Architecture",
        "WireGuard & V2Ray",
        "Kill Switch & Native",
        "AdMob & IAP"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // VIP Status Banner
        Surface(
            color = ElevatedCarbon,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.5.dp,
                    if (isVipActive) VipGold else CyberCyan.copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = VipGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "N VPN VIP PRO",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = VipGold
                            )
                            Text(
                                text = "Current Plan: ${settings.vipPlanName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isVipActive) ShieldEmerald else TextSecondarySteel
                            )
                        }
                    }

                    if (isVipActive) {
                        OutlinedButton(onClick = onResetFreeTier) {
                            Text("Reset Free", style = MaterialTheme.typography.labelSmall, color = TextSecondarySteel)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Unlock 10Gbps Gaming & Streaming VIP Nodes (Singapore, London, Tokyo, Zurich, Silicon Valley, Dubai), Multi-Hop Stealth V2Ray, and 100% Ad-Free experience.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimaryIce
                )
            }
        }

        // AdMob Rewarded Video Ad Card (Free 24-Hour VIP Unlock)
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FREE VIP ACCESS • GOOGLE ADMOB REWARDED AD",
                    style = MaterialTheme.typography.labelLarge,
                    color = HandshakeAmber
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Watch a short Rewarded Ad to unlock all VIP 10Gbps servers for 24 hours without paying.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondarySteel
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onWatchRewardedAd,
                    enabled = rewardAdCountdown == null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HandshakeAmber,
                        contentColor = ObsidianVoid
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("watch_rewarded_ad_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rewardAdCountdown != null) {
                            "Playing AdMob Rewarded Video… (${rewardAdCountdown}s)"
                        } else {
                            "Watch Rewarded Ad → Unlock 24h VIP Pass"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // In-App Purchase Subscription Tiers
        Text(
            text = "IN-APP PURCHASE (GOOGLE PLAY BILLING / STOREKIT)",
            style = MaterialTheme.typography.labelLarge,
            color = CyberCyan
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VipPlanCard(
                title = "Monthly",
                price = "$3.99",
                period = "/month",
                badge = "FLEXIBLE",
                isHighlighted = false,
                onSelect = { onActivatePlan("VIP Monthly ($3.99/mo)") },
                modifier = Modifier.weight(1f)
            )
            VipPlanCard(
                title = "Annual Pro",
                price = "$29.99",
                period = "/year",
                badge = "SAVE 37%",
                isHighlighted = true,
                onSelect = { onActivatePlan("VIP Annual Pro ($29.99/yr)") },
                modifier = Modifier.weight(1f)
            )
            VipPlanCard(
                title = "Lifetime",
                price = "$69.99",
                period = "one-time",
                badge = "FOREVER",
                isHighlighted = false,
                onSelect = { onActivatePlan("VIP Lifetime ($69.99)") },
                modifier = Modifier.weight(1f)
            )
        }

        // Developer Cross-Platform Architecture & Code Blueprint Section
        Surface(
            color = MidnightSlate,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "N VPN ARCHITECTURE & CODE BLUEPRINT (ANDROID / iOS)",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberCyan
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    blueprintTitles.forEachIndexed { index, title ->
                        FilterChip(
                            selected = selectedBlueprintTab == index,
                            onClick = { selectedBlueprintTab = index },
                            label = { Text(title, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianVoid)
                        .padding(12.dp)
                ) {
                    Text(
                        text = when (selectedBlueprintTab) {
                            0 -> FLUTTER_STRUCTURE_SNIPPET
                            1 -> WIREGUARD_V2RAY_SNIPPET
                            2 -> KILL_SWITCH_SNIPPET
                            else -> ADMOB_IAP_SNIPPET
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            lineHeight = 16.sp
                        ),
                        color = TextPrimaryIce
                    )
                }
            }
        }
    }
}

@Composable
private fun VipPlanCard(
    title: String,
    price: String,
    period: String,
    badge: String,
    isHighlighted: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                if (isHighlighted) 1.5.dp else 1.dp,
                if (isHighlighted) VipGold else GlassBorderSubtle,
                RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("vip_plan_${title.lowercase().replace(" ", "_")}"),
        color = if (isHighlighted) ElevatedCarbon else MidnightSlate,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = if (isHighlighted) VipGold else CyberCyan.copy(alpha = 0.16f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isHighlighted) ObsidianVoid else CyberCyan,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryIce
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isHighlighted) VipGold else CyberCyan
            )
            Text(
                text = period,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondarySteel
            )
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Activate",
                tint = if (isHighlighted) VipGold else ShieldEmerald,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private const val FLUTTER_STRUCTURE_SNIPPET = """
# pubspec.yaml (Flutter Android & iOS Packages)
dependencies:
  flutter_riverpod: ^2.6.1
  wireguard_flutter: ^0.1.3
  openvpn_flutter: ^1.3.2
  flutter_v2ray: ^1.0.8
  google_mobile_ads: ^5.2.0
  in_app_purchase: ^3.2.0
  flutter_secure_storage: ^9.2.2

# Folder Structure (Clean Architecture)
lib/
 ├── core/theme/nvpn_dark_theme.dart
 ├── data/models/vpn_server.dart
 ├── domain/services/vpn_engine_service.dart
 └── presentation/
      ├── splash/nvpn_splash_screen.dart
      ├── home/home_connect_screen.dart
      └── servers/server_list_screen.dart
"""

private const val WIREGUARD_V2RAY_SNIPPET = """
// WireGuard & V2Ray Multi-Protocol Engine (Dart / Kotlin Bridge)
final wireguard = WireGuardFlutter.instance;

Future<void> connectWireGuard(VpnServer node) async {
  await wireguard.initialize(interfaceName: 'nvpn_wg0');
  await wireguard.startVpn(
    serverAddress: '${'$'}{node.ip}:${'$'}{node.port}',
    wgQuickConfig: buildWgQuickConfig(node),
    providerBundleIdentifier: 'com.nvpn.ios.TunnelExtension',
  );
}

// V2Ray / Xray VMess Connection
final flutterV2ray = FlutterV2ray(onStatusChanged: (status) {
  // Real-time uploadSpeed & downloadSpeed callback
});
"""

private const val KILL_SWITCH_SNIPPET = """
// Android VpnService Kill Switch + Always-On Lockdown
val builder = Builder()
  .setSession("N VPN • Fast. Safe. Unlimited.")
  .setMtu(1420)
  .addAddress("10.8.0.2", 24)
  .addDnsServer("1.1.1.1")
  .addRoute("0.0.0.0", 0)
  .setBlocking(false)
// Omit allowBypass() when Kill Switch is active!

// iOS NetworkExtension Kill Switch (NEOnDemandRuleConnect)
let rule = NEOnDemandRuleConnect()
rule.interfaceTypeMatch = .any
vpnManager.onDemandRules = [rule]
vpnManager.isOnDemandEnabled = true
"""

private const val ADMOB_IAP_SNIPPET = """
// Rewarded Ad Unlock for 24h VIP Server Access
RewardedAd.load(
  adUnitId: AdHelper.rewardedAdUnitId,
  request: const AdRequest(),
  rewardedAdLoadCallback: RewardedAdLoadCallback(
    onAdLoaded: (ad) => ad.show(
      onUserEarnedReward: (_, reward) => unlockVipFor24Hours(),
    ),
  ),
);

// In-App Purchase (VIP Subscription Verification)
InAppPurchase.instance.purchaseStream.listen((purchases) {
  for (final p in purchases) {
    if (p.status == PurchaseStatus.purchased) enableVipPro();
  }
});
"""
