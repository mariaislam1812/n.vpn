package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VpnProtocol(
    val displayName: String,
    val shortTag: String,
    val cipherSuite: String,
    val defaultPort: Int,
    val description: String
) {
    WIREGUARD(
        displayName = "WireGuard®",
        shortTag = "WG-UDP",
        cipherSuite = "ChaCha20-Poly1305 • Curve25519",
        defaultPort = 51820,
        description = "Ultra-fast modern kernel-level UDP tunnel with instant handshake & roaming."
    ),
    OPENVPN(
        displayName = "OpenVPN (UDP/TCP)",
        shortTag = "OVPN-TLS",
        cipherSuite = "AES-256-GCM • TLS 1.3 • SHA-512",
        defaultPort = 1194,
        description = "Battle-tested enterprise SSL/TLS VPN protocol for maximum firewall compatibility."
    ),
    V2RAY_VMESS(
        displayName = "V2Ray (VMess / VLESS)",
        shortTag = "XRAY-WS",
        cipherSuite = "XTLS-Vision • WebSocket + TLS",
        defaultPort = 443,
        description = "Stealth obfuscated proxy protocol designed to bypass deep packet inspection (DPI)."
    ),
    SHADOWSOCKS(
        displayName = "Shadowsocks AEAD",
        shortTag = "SS-AEAD",
        cipherSuite = "2022-blake3-aes-256-gcm",
        defaultPort = 8388,
        description = "Lightweight encrypted SOCKS5 tunnel optimized for low-latency mobile streaming."
    )
}

enum class VpnConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    KILL_SWITCH_LOCKED
}

@Entity(tableName = "vpn_servers")
data class VpnServerEntity(
    @PrimaryKey val id: String,
    val country: String,
    val city: String,
    val flagEmoji: String,
    val ipAddress: String,
    val port: Int,
    val pingMs: Int,
    val isVip: Boolean,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val protocol: String = VpnProtocol.WIREGUARD.name,
    val loadPercent: Int = 28,
    val customConfigUri: String = "",
    val publicKey: String = "",
    val mapX: Float = 0.5f, // Normalized 0..1 world map X
    val mapY: Float = 0.4f  // Normalized 0..1 world map Y
)

@Entity(tableName = "vpn_settings")
data class VpnSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val selectedServerId: String = "sg_1",
    val selectedProtocol: String = VpnProtocol.WIREGUARD.name,
    val killSwitchEnabled: Boolean = true,
    val autoReconnectEnabled: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val customDns: String = "1.1.1.1 (Cloudflare Zero-Log)",
    val isVipUnlocked: Boolean = false,
    val vipPlanName: String = "Free Tier",
    val vipRewardExpiryEpochMs: Long = 0L,
    val noLogsStrictMode: Boolean = true
)

data class NetworkTelemetry(
    val downloadBps: Long = 0L,
    val uploadBps: Long = 0L,
    val sessionRxBytes: Long = 0L,
    val sessionTxBytes: Long = 0L,
    val downloadHistory: List<Float> = List(24) { 0f },
    val uploadHistory: List<Float> = List(24) { 0f },
    val assignedVirtualIp: String = "10.8.0.2",
    val publicExitIp: String = "Unmasked (Direct ISP)",
    val mtu: Int = 1420
)
