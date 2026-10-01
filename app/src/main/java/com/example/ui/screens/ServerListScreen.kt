package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VpnProtocol
import com.example.data.VpnServerEntity
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElevatedCarbon
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.HandshakeAmber
import com.example.ui.theme.MidnightSlate
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.ShieldEmerald
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextPrimaryIce
import com.example.ui.theme.TextSecondarySteel
import com.example.ui.theme.VipGold

enum class ServerFilterTab(val label: String) {
    ALL("All Nodes"),
    FREE("Free"),
    VIP("VIP 10Gbps"),
    CUSTOM("Custom VPS")
}

@Composable
fun ServerListScreen(
    servers: List<VpnServerEntity>,
    selectedServerId: String,
    isVipActive: Boolean,
    isPingingAll: Boolean,
    onSelectServer: (VpnServerEntity) -> Unit,
    onToggleFavorite: (VpnServerEntity) -> Unit,
    onPingAll: () -> Unit,
    onAddCustomServer: (String, String, String, Int, VpnProtocol, String) -> Unit,
    onDeleteCustomServer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var activeFilter by rememberSaveable { mutableStateOf(ServerFilterTab.ALL) }
    var showAddVpsDialog by rememberSaveable { mutableStateOf(false) }

    val filteredServers = remember(servers, searchQuery, activeFilter) {
        servers.filter { srv ->
            val matchesQuery = searchQuery.isBlank() ||
                srv.country.contains(searchQuery, ignoreCase = true) ||
                srv.city.contains(searchQuery, ignoreCase = true) ||
                srv.ipAddress.contains(searchQuery, ignoreCase = true)
            val matchesTab = when (activeFilter) {
                ServerFilterTab.ALL -> true
                ServerFilterTab.FREE -> !srv.isVip && !srv.isCustom
                ServerFilterTab.VIP -> srv.isVip
                ServerFilterTab.CUSTOM -> srv.isCustom
            }
            matchesQuery && matchesTab
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Row + Live Ping All Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Global Server Network",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimaryIce
                    )
                    Text(
                        text = "${servers.count { !it.isVip }} Free • ${servers.count { it.isVip }} VIP 10Gbps • Real-Time Latency",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondarySteel
                    )
                }

                Button(
                    onClick = onPingAll,
                    enabled = !isPingingAll,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElevatedCarbon,
                        contentColor = CyberCyan
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("ping_all_button")
                ) {
                    if (isPingingAll) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CyberCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Ping All",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPingingAll) "Pinging…" else "Ping All",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search country, city, or IP…", color = TextMutedSlate)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search servers",
                        tint = CyberCyan
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("server_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServerFilterTab.entries.forEach { tab ->
                    val selected = activeFilter == tab
                    FilterChip(
                        selected = selected,
                        onClick = { activeFilter = tab },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan,
                            containerColor = MidnightSlate,
                            labelColor = TextSecondarySteel
                        ),
                        modifier = Modifier.testTag("filter_tab_${tab.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredServers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No matching servers found",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondarySteel
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the + button to add your own V2Ray, Shadowsocks, or WireGuard VPS.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedSlate
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 84.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredServers, key = { it.id }) { server ->
                        ServerItemCard(
                            server = server,
                            isSelected = server.id == selectedServerId,
                            isVipUnlocked = isVipActive,
                            onSelect = { onSelectServer(server) },
                            onToggleFavorite = { onToggleFavorite(server) },
                            onDeleteCustom = { onDeleteCustomServer(server.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddVpsDialog = true },
            containerColor = CyberCyan,
            contentColor = ObsidianVoid,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(18.dp)
                .testTag("add_custom_vps_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom VPS")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add VPS / V2Ray",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    if (showAddVpsDialog) {
        AddCustomVpsDialog(
            onDismiss = { showAddVpsDialog = false },
            onConfirm = { country, city, ip, port, proto, uri ->
                onAddCustomServer(country, city, ip, port, proto, uri)
                showAddVpsDialog = false
            }
        )
    }
}

@Composable
private fun ServerItemCard(
    server: VpnServerEntity,
    isSelected: Boolean,
    isVipUnlocked: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteCustom: () -> Unit
) {
    val pingColor = when {
        server.pingMs < 50 -> ShieldEmerald
        server.pingMs <= 110 -> HandshakeAmber
        else -> AlertCrimson
    }

    val borderColor = when {
        isSelected -> ShieldEmerald
        server.isVip -> VipGold.copy(alpha = 0.35f)
        else -> GlassBorderSubtle
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .testTag("server_item_${server.id}"),
        color = if (isSelected) ElevatedCarbon else MidnightSlate,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.country,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimaryIce
                        )
                        if (server.isVip) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = VipGold.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!isVipUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "VIP Locked",
                                            tint = VipGold,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                    }
                                    Text(
                                        text = "VIP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VipGold
                                    )
                                }
                            }
                        }
                        if (server.isCustom) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = CyberCyan.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "VPS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberCyan,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${server.city} • ${server.protocol} • Load ${server.loadPercent}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondarySteel
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Signal bars + Ping ms pill
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val activeBars = when {
                            server.pingMs < 50 -> 4
                            server.pingMs <= 90 -> 3
                            server.pingMs <= 140 -> 2
                            else -> 1
                        }
                        for (bar in 1..4) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height((bar * 3 + 4).dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (bar <= activeBars) pingColor
                                        else TextMutedSlate.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${server.pingMs} ms",
                        style = MaterialTheme.typography.labelMedium,
                        color = pingColor
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (server.isCustom) {
                    IconButton(onClick = onDeleteCustom) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Custom VPS",
                            tint = AlertCrimson
                        )
                    }
                } else {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (server.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite Server",
                            tint = if (server.isFavorite) VipGold else TextMutedSlate
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = ShieldEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCustomVpsDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, VpnProtocol, String) -> Unit
) {
    var country by rememberSaveable { mutableStateOf("Private VPS") }
    var city by rememberSaveable { mutableStateOf("Frankfurt Dedicated") }
    var ipAddress by rememberSaveable { mutableStateOf("203.0.113.77") }
    var portText by rememberSaveable { mutableStateOf("443") }
    var selectedProtocol by rememberSaveable { mutableStateOf(VpnProtocol.V2RAY_VMESS) }
    var configUri by rememberSaveable {
        mutableStateOf("vmess://eyJhZGQiOiIyMDMuMC4xMTMuNzciLCJwb3J0IjoiNDQzIiwiaWQiOiJiODMxMzgxZC02MzI0LTRkNTMifQ==")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightSlate,
        title = {
            Text(
                text = "Add Custom VPS / V2Ray / WG Node",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimaryIce
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Connect N VPN to your own WireGuard, OpenVPN, V2Ray (VMess/VLESS), or Shadowsocks VPS:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondarySteel
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VpnProtocol.entries.forEach { proto ->
                        FilterChip(
                            selected = selectedProtocol == proto,
                            onClick = {
                                selectedProtocol = proto
                                portText = proto.defaultPort.toString()
                            },
                            label = {
                                Text(proto.shortTag, style = MaterialTheme.typography.labelSmall)
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Node Label / Country") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Datacenter / City") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ipAddress,
                        onValueChange = { ipAddress = it },
                        label = { Text("VPS IP / Host") },
                        singleLine = true,
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = portText,
                        onValueChange = { portText = it },
                        label = { Text("Port") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = configUri,
                    onValueChange = { configUri = it },
                    label = { Text("vmess://, ss://, or WireGuard PublicKey") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val port = portText.toIntOrNull() ?: selectedProtocol.defaultPort
                    onConfirm(country, city, ipAddress, port, selectedProtocol, configUri)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = ObsidianVoid
                )
            ) {
                Text("Save VPS Node")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondarySteel)
            }
        }
    )
}
