package com.example.vpn

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Real Android VpnService implementation for "N VPN — Fast. Safe. Unlimited."
 * Configures the virtual TUN interface (10.8.0.2/24), DNS, MTU, and Kill Switch bypass rules.
 */
class NVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_CONNECT
        if (action == ACTION_DISCONNECT) {
            stopVpnTunnel()
            stopSelf()
            return START_NOT_STICKY
        }

        val serverName = intent?.getStringExtra(EXTRA_SERVER_NAME) ?: "Singapore"
        val dnsServer = intent?.getStringExtra(EXTRA_DNS)?.substringBefore(" ")?.trim() ?: "1.1.1.1"
        val killSwitch = intent?.getBooleanExtra(EXTRA_KILL_SWITCH, true) ?: true

        serviceScope.launch {
            establishVpnTunnel(serverName, dnsServer, killSwitch)
        }
        return START_STICKY
    }

    private fun establishVpnTunnel(serverName: String, dnsServer: String, killSwitch: Boolean) {
        try {
            vpnInterface?.close()
            val builder = Builder()
                .setSession("N VPN • $serverName")
                .setMtu(1420)
                .addAddress("10.8.0.2", 24)
                .addDnsServer(dnsServer)
                .addRoute("0.0.0.0", 0)

            // When Kill Switch is disabled, allow apps to bypass the VPN if needed
            if (!killSwitch) {
                builder.allowBypass()
            }

            vpnInterface = builder.establish()
            _isTunnelEstablished.value = vpnInterface != null
        } catch (_: Exception) {
            // If system VPN consent is handled in preview mode, mark tunnel state gracefully
            _isTunnelEstablished.value = false
        }
    }

    private fun stopVpnTunnel() {
        try {
            vpnInterface?.close()
        } catch (_: Exception) {
        } finally {
            vpnInterface = null
            _isTunnelEstablished.value = false
        }
    }

    override fun onDestroy() {
        stopVpnTunnel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVpnTunnel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.ACTION_DISCONNECT"
        const val EXTRA_SERVER_NAME = "extra_server_name"
        const val EXTRA_DNS = "extra_dns"
        const val EXTRA_KILL_SWITCH = "extra_kill_switch"

        private val _isTunnelEstablished = MutableStateFlow(false)
        val isTunnelEstablished: StateFlow<Boolean> = _isTunnelEstablished.asStateFlow()
    }
}
