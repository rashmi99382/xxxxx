package com.example.pharmacyinventory

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.example.pharmacyinventory.data.LocalSyncEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class LocalPeerDevice(
    val deviceId: String,
    val name: String,
    val host: String,
    val port: Int,
    val lastSeenMillis: Long,
    val trusted: Boolean
)

class LocalPeerSyncManager(
    context: Context,
    private val syncEngine: LocalSyncEngine
) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val nsdManager = appContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val syncMutex = Mutex()
    private val peerMap = ConcurrentHashMap<String, LocalPeerDevice>()
    private val prefs = appContext.getSharedPreferences("local_sync", Context.MODE_PRIVATE)
    private val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}".trim().ifBlank { "Android device" }
    private val deviceId = prefs.getString(KEY_DEVICE_ID, null)
        ?: UUID.randomUUID().toString().also {
            prefs.edit().putString(KEY_DEVICE_ID, it).apply()
        }
    private val serviceName = "MediStock-${deviceName.take(14)}-${deviceId.take(8)}"
    private val _peers = MutableStateFlow<List<LocalPeerDevice>>(emptyList())
    val peers: StateFlow<List<LocalPeerDevice>> = _peers.asStateFlow()
    private val _status = MutableStateFlow("Local sync is locked. Tap a trusted nearby device to share.")
    val status: StateFlow<String> = _status.asStateFlow()

    private var serverSocket: ServerSocket? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var multicastLock: WifiManager.MulticastLock? = null

    fun start() {
        acquireMulticastLock()
        startServer()
        startDiscoveryLoop()
    }

    fun stop() {
        runCatching { discoveryListener?.let { nsdManager.stopServiceDiscovery(it) } }
        runCatching { registrationListener?.let { nsdManager.unregisterService(it) } }
        runCatching { serverSocket?.close() }
        runCatching { multicastLock?.release() }
    }

    fun trustAndSync(deviceId: String) {
        val peer = peerMap[deviceId] ?: run {
            _status.value = "Device not found. Keep both apps open on the same local network."
            return
        }
        trustPeer(deviceId)
        scope.launch { syncWithPeer(peer.copy(trusted = true)) }
    }

    private fun trustedPeers(): Set<String> {
        return prefs.getStringSet(KEY_TRUSTED_PEERS, emptySet()).orEmpty()
    }

    private fun trustPeer(peerId: String) {
        val updated = trustedPeers() + peerId
        prefs.edit().putStringSet(KEY_TRUSTED_PEERS, updated).apply()
        peerMap[peerId]?.let { peerMap[peerId] = it.copy(trusted = true) }
        publishPeers()
    }

    private fun isTrusted(peerId: String): Boolean = peerId in trustedPeers()

    private fun acquireMulticastLock() {
        val wifiManager = appContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        multicastLock = wifiManager?.createMulticastLock("pharmacy-local-sync")?.apply {
            setReferenceCounted(false)
            acquire()
        }
    }

    private fun startServer() {
        scope.launch {
            val socket = ServerSocket(0)
            serverSocket = socket
            registerService(socket.localPort)
            while (!socket.isClosed) {
                val client = runCatching { socket.accept() }.getOrNull() ?: continue
                launch { handleClient(client) }
            }
        }
    }

    private fun registerService(port: Int) {
        val info = NsdServiceInfo().apply {
            serviceName = this@LocalPeerSyncManager.serviceName
            serviceType = SERVICE_TYPE
            setPort(port)
            setAttribute("deviceId", deviceId)
            setAttribute("deviceName", deviceName)
        }
        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) = Unit
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                _status.value = "Local sync advertise failed: $errorCode"
            }
            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) = Unit
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) = Unit
        }
        nsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    private fun startDiscoveryLoop() {
        scope.launch {
            while (true) {
                startDiscovery()
                delay(60_000)
            }
        }
    }

    private fun startDiscovery() {
        discoveryListener?.let { runCatching { nsdManager.stopServiceDiscovery(it) } }
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) = Unit
            override fun onDiscoveryStopped(serviceType: String) = Unit
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                _status.value = "Local sync discovery failed: $errorCode"
                runCatching { nsdManager.stopServiceDiscovery(this) }
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                runCatching { nsdManager.stopServiceDiscovery(this) }
            }
            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                val lost = peerMap.values.firstOrNull { serviceInfo.serviceName.contains(it.deviceId.take(8)) }
                if (lost != null) {
                    peerMap.remove(lost.deviceId)
                    publishPeers()
                }
            }
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceType != SERVICE_TYPE || serviceInfo.serviceName == serviceName) return
                resolvePeer(serviceInfo)
            }
        }
        runCatching {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        }
    }

    private fun resolvePeer(serviceInfo: NsdServiceInfo) {
        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) = Unit
            override fun onServiceResolved(resolved: NsdServiceInfo) {
                if (resolved.serviceName == serviceName) return
                val peerId = resolved.attribute("deviceId") ?: resolved.serviceName.substringAfterLast("-")
                if (peerId == deviceId) return
                val peerName = resolved.attribute("deviceName") ?: resolved.serviceName
                val host = resolved.host?.hostAddress ?: return
                peerMap[peerId] = LocalPeerDevice(
                    deviceId = peerId,
                    name = peerName,
                    host = host,
                    port = resolved.port,
                    lastSeenMillis = System.currentTimeMillis(),
                    trusted = isTrusted(peerId)
                )
                publishPeers()
            }
        }
        runCatching { nsdManager.resolveService(serviceInfo, resolveListener) }
    }

    private fun publishPeers() {
        _peers.value = peerMap.values
            .sortedWith(compareByDescending<LocalPeerDevice> { it.trusted }.thenBy { it.name.lowercase() })
    }

    private suspend fun syncWithPeer(peer: LocalPeerDevice) {
        syncMutex.withLock {
            runCatching {
                _status.value = "Syncing with ${peer.name}..."
                val localSnapshot = syncEngine.exportSnapshot(deviceId)
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(peer.host, peer.port), 4_000)
                    socket.soTimeout = 10_000
                    val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                    writer.write("POST /sync HTTP/1.1\r\n")
                    writer.write("Host: ${peer.host}\r\n")
                    writer.write("Content-Type: application/json\r\n")
                    writer.write("X-Device-Id: $deviceId\r\n")
                    writer.write("X-Device-Name: ${deviceName.replace("\r", " ").replace("\n", " ")}\r\n")
                    writer.write("Content-Length: ${localSnapshot.toByteArray().size}\r\n")
                    writer.write("Connection: close\r\n\r\n")
                    writer.write(localSnapshot)
                    writer.flush()
                    val response = readHttpResponse(socket)
                    when (response.statusCode) {
                        200 -> {
                            if (response.body.isNotBlank()) syncEngine.mergeSnapshot(response.body)
                            _status.value = "Sync completed with ${peer.name}."
                        }
                        403 -> _status.value = "${peer.name} has not trusted this device yet. Open sync on that phone and tap this device too."
                        else -> _status.value = "Sync failed with ${peer.name}: ${response.statusCode}."
                    }
                }
            }.onFailure {
                _status.value = "Unable to sync with ${peer.name}. Keep both apps open on the same local network."
            }
        }
    }

    private suspend fun handleClient(client: Socket) {
        client.use { socket ->
            socket.soTimeout = 10_000
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val requestLine = reader.readLine().orEmpty()
            var contentLength = 0
            var remoteDeviceId: String? = null
            var remoteDeviceName: String? = null
            while (true) {
                val header = reader.readLine() ?: break
                if (header.isBlank()) break
                val key = header.substringBefore(":").trim()
                val value = header.substringAfter(":", "").trim()
                when {
                    key.equals("Content-Length", ignoreCase = true) -> contentLength = value.toIntOrNull() ?: 0
                    key.equals("X-Device-Id", ignoreCase = true) -> remoteDeviceId = value
                    key.equals("X-Device-Name", ignoreCase = true) -> remoteDeviceName = value
                }
            }

            val peerId = remoteDeviceId
            if (!requestLine.startsWith("POST /sync") || peerId.isNullOrBlank() || !isTrusted(peerId)) {
                _status.value = "Blocked local sync request from ${remoteDeviceName ?: "unknown device"}. Trust it before sharing data."
                writeResponse(socket, 403, "")
                return
            }

            if (contentLength > 0) {
                val chars = CharArray(contentLength)
                var read = 0
                while (read < contentLength) {
                    val count = reader.read(chars, read, contentLength - read)
                    if (count <= 0) break
                    read += count
                }
                syncMutex.withLock {
                    syncEngine.mergeSnapshot(String(chars, 0, read))
                }
            }
            val response = syncMutex.withLock { syncEngine.exportSnapshot(deviceId) }
            writeResponse(socket, 200, response)
            _status.value = "Accepted trusted sync from ${remoteDeviceName ?: peerId.take(8)}."
        }
    }

    private fun writeResponse(socket: Socket, statusCode: Int, body: String) {
        val statusText = if (statusCode == 200) "OK" else "Forbidden"
        val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
        writer.write("HTTP/1.1 $statusCode $statusText\r\n")
        writer.write("Content-Type: application/json\r\n")
        writer.write("Content-Length: ${body.toByteArray().size}\r\n")
        writer.write("Connection: close\r\n\r\n")
        writer.write(body)
        writer.flush()
    }

    private fun readHttpResponse(socket: Socket): HttpResponse {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val statusCode = reader.readLine()
            ?.split(" ")
            ?.getOrNull(1)
            ?.toIntOrNull()
            ?: 0
        var contentLength = 0
        while (true) {
            val line = reader.readLine() ?: return HttpResponse(statusCode, "")
            if (line.isBlank()) break
            if (line.startsWith("Content-Length:", ignoreCase = true)) {
                contentLength = line.substringAfter(":").trim().toIntOrNull() ?: 0
            }
        }
        if (contentLength <= 0) return HttpResponse(statusCode, "")
        val chars = CharArray(contentLength)
        var read = 0
        while (read < contentLength) {
            val count = reader.read(chars, read, contentLength - read)
            if (count <= 0) break
            read += count
        }
        return HttpResponse(statusCode, String(chars, 0, read))
    }

    private fun NsdServiceInfo.attribute(name: String): String? {
        return attributes[name]?.toString(Charsets.UTF_8)?.takeIf { it.isNotBlank() }
    }

    private data class HttpResponse(val statusCode: Int, val body: String)

    companion object {
        private const val SERVICE_TYPE = "_pharmacysync._tcp."
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_TRUSTED_PEERS = "trusted_peer_ids"
    }
}
