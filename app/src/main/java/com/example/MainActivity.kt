package com.example

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.VpnServerEntity
import com.example.ui.NVpnViewModel
import com.example.ui.components.NVpnSplashScreen
import com.example.ui.screens.HomeConnectScreen
import com.example.ui.screens.SecurityAndProtocolScreen
import com.example.ui.screens.ServerListScreen
import com.example.ui.screens.VipAndBlueprintScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElevatedCarbon
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.HandshakeAmber
import com.example.ui.theme.MidnightSlate
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.ShieldEmerald
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextPrimaryIce
import com.example.ui.theme.TextSecondarySteel
import com.example.ui.theme.VipGold
import kotlinx.coroutines.delay

enum class NVpnTab(val label: String, val icon: ImageVector, val tag: String) {
    CONNECT("Connect", Icons.Default.PowerSettingsNew, "nav_tab_connect"),
    SERVERS("Servers", Icons.Default.Dns, "nav_tab_servers"),
    SECURITY("Security", Icons.Default.Security, "nav_tab_security"),
    VIP("VIP & Code", Icons.Default.WorkspacePremium, "nav_tab_vip")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NVpnAppRoot()
            }
        }
    }
}

@Composable
fun NVpnAppRoot(vpnViewModel: NVpnViewModel = viewModel()) {
    val context = LocalContext.current
    val showSplash by vpnViewModel.showSplash.collectAsStateWithLifecycle()
    val servers by vpnViewModel.servers.collectAsStateWithLifecycle()
    val settings by vpnViewModel.settings.collectAsStateWithLifecycle()
    val connectionState by vpnViewModel.connectionState.collectAsStateWithLifecycle()
    val sessionDurationSeconds by vpnViewModel.sessionDurationSeconds.collectAsStateWithLifecycle()
    val telemetry by vpnViewModel.telemetry.collectAsStateWithLifecycle()
    val isPingingAll by vpnViewModel.isPingingAll.collectAsStateWithLifecycle()
    val rewardAdCountdown by vpnViewModel.rewardAdCountdown.collectAsStateWithLifecycle()
    val bannerMessage by vpnViewModel.statusBannerMessage.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableStateOf(NVpnTab.CONNECT) }
    var vipPromptServer by remember { mutableStateOf<VpnServerEntity?>(null) }

    val selectedServer = vpnViewModel.getSelectedServer(servers, settings)
    val isVipActive = vpnViewModel.isVipEffective(settings)

    // Real Android VpnService.prepare() permission launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val granted = result.resultCode == Activity.RESULT_OK
        vpnViewModel.onVpnPermissionResult(granted)
    }

    val requestVpnConnect: () -> Unit = {
        val prepareIntent = try {
            VpnService.prepare(context)
        } catch (_: Exception) {
            null
        }
        if (prepareIntent != null) {
            try {
                vpnPermissionLauncher.launch(prepareIntent)
            } catch (_: Exception) {
                vpnViewModel.onVpnPermissionResult(false)
            }
        } else {
            vpnViewModel.onVpnPermissionResult(true)
        }
    }

    // Auto-dismiss status banner after 3.5 seconds
    LaunchedEffect(bannerMessage) {
        if (bannerMessage != null) {
            delay(3500L)
            vpnViewModel.clearBannerMessage()
        }
    }

    // BackHandler on secondary tabs returns to Connect tab
    if (currentTab != NVpnTab.CONNECT) {
        BackHandler {
            currentTab = NVpnTab.CONNECT
        }
    }

    Crossfade(targetState = showSplash, label = "splash_crossfade") { isSplash ->
        if (isSplash) {
            NVpnSplashScreen(
                onDismissSplash = { vpnViewModel.dismissSplashEarly() }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = ObsidianVoid,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    NavigationBar(
                        containerColor = MidnightSlate,
                        tonalElevation = 0.dp,
                        modifier = Modifier.border(1.dp, GlassBorderSubtle)
                    ) {
                        NVpnTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianVoid,
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextSecondarySteel,
                                    unselectedTextColor = TextMutedSlate
                                ),
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        NVpnTab.CONNECT -> HomeConnectScreen(
                            servers = servers,
                            selectedServer = selectedServer,
                            settings = settings,
                            isVipActive = isVipActive,
                            connectionState = connectionState,
                            sessionDurationSeconds = sessionDurationSeconds,
                            telemetry = telemetry,
                            onToggleConnect = {
                                vpnViewModel.toggleVpnConnection(requestVpnConnect)
                            },
                            onOpenServerSelector = { currentTab = NVpnTab.SERVERS },
                            onSelectProtocol = { vpnViewModel.selectProtocol(it) },
                            onOpenVipScreen = { currentTab = NVpnTab.VIP },
                            onReplaySplash = { vpnViewModel.replaySplashScreen() }
                        )

                        NVpnTab.SERVERS -> ServerListScreen(
                            servers = servers,
                            selectedServerId = settings.selectedServerId,
                            isVipActive = isVipActive,
                            isPingingAll = isPingingAll,
                            onSelectServer = { server ->
                                vpnViewModel.selectServer(
                                    server = server,
                                    onNeedVipUnlock = { lockedServer ->
                                        vipPromptServer = lockedServer
                                    }
                                )
                            },
                            onToggleFavorite = { vpnViewModel.toggleFavoriteServer(it) },
                            onPingAll = { vpnViewModel.pingAllServers() },
                            onAddCustomServer = { country, city, ip, port, proto, uri ->
                                vpnViewModel.addCustomVpsServer(country, city, ip, port, proto, uri)
                            },
                            onDeleteCustomServer = { vpnViewModel.deleteCustomServer(it) }
                        )

                        NVpnTab.SECURITY -> SecurityAndProtocolScreen(
                            settings = settings,
                            configPreview = vpnViewModel.generateActiveConfigPreview(),
                            onSelectProtocol = { vpnViewModel.selectProtocol(it) },
                            onToggleKillSwitch = { vpnViewModel.toggleKillSwitch(it) },
                            onToggleAutoReconnect = { vpnViewModel.toggleAutoReconnect(it) },
                            onToggleSplitTunneling = { vpnViewModel.toggleSplitTunneling(it) },
                            onSelectDns = { vpnViewModel.updateCustomDns(it) },
                            onTriggerKillSwitchTest = {
                                currentTab = NVpnTab.CONNECT
                                vpnViewModel.triggerKillSwitchHandshakeTest()
                            },
                            onPurgeZeroLogData = { vpnViewModel.purgeZeroLogData() },
                            onCopiedConfig = {}
                        )

                        NVpnTab.VIP -> VipAndBlueprintScreen(
                            settings = settings,
                            isVipActive = isVipActive,
                            rewardAdCountdown = rewardAdCountdown,
                            onWatchRewardedAd = { vpnViewModel.watchRewardedAdToUnlockVip() },
                            onActivatePlan = { vpnViewModel.activateVipSubscription(it) },
                            onResetFreeTier = { vpnViewModel.resetToFreeTierForTesting() }
                        )
                    }

                    // Top Floating Telemetry & Action Feedback Banner
                    AnimatedVisibility(
                        visible = bannerMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Surface(
                            color = ElevatedCarbon,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ShieldEmerald.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = bannerMessage.orEmpty(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimaryIce,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { vpnViewModel.clearBannerMessage() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss notification",
                                        tint = TextSecondarySteel,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // VIP Unlock Dialog when user taps a locked VIP server
    vipPromptServer?.let { lockedServer ->
        AlertDialog(
            onDismissRequest = { vipPromptServer = null },
            containerColor = MidnightSlate,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = VipGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock ${lockedServer.country} VIP",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimaryIce
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${lockedServer.flagEmoji} ${lockedServer.city} (${lockedServer.pingMs}ms) is a 10Gbps VIP node.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimaryIce
                    )
                    Text(
                        text = "You can unlock all VIP nodes for 24 hours by watching a short Rewarded Ad, or upgrade to N VPN VIP Pro.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondarySteel
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vipPromptServer = null
                        currentTab = NVpnTab.VIP
                        vpnViewModel.watchRewardedAdToUnlockVip()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HandshakeAmber,
                        contentColor = ObsidianVoid
                    )
                ) {
                    Text("Watch 5s Ad (Free 24h VIP)")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        vipPromptServer = null
                        currentTab = NVpnTab.VIP
                    }
                ) {
                    Text("View VIP Plans", color = CyberCyan)
                }
            }
        )
    }
}
