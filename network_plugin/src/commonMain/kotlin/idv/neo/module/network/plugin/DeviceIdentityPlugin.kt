package idv.neo.module.network.plugin

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform

val DeviceIdentityPlugin = createClientPlugin("DeviceIdentityPlugin") {
    onRequest { request, _ ->
        val url = request.url.buildString()

        // 每次請求發起時，動態從 Koin 容器中解析 DeviceIdentityProvider 並帶入裝置識別 Headers
        KoinPlatform.getKoin().getOrNull<DeviceIdentityProvider>()?.let { provider ->
            val deviceNo = provider.device_id.ifEmpty { "x" }
            val deviceType = provider.ostype.ifEmpty { "android" }
            val appVersion = provider.app_version.ifEmpty { "1.0.0" }

            request.header("X-Device-No", deviceNo)
            request.header("X-Device-Type", deviceType)
            request.header("X-App-Version", appVersion)
        } ?: run {
            println("[DeviceIdentityPlugin] No DeviceIdentityProvider found in Koin context for: $url. Using default headers.")
            request.header("X-Device-No", "x")
            request.header("X-Device-Type", "android")
            request.header("X-App-Version", "1.0.0")
        }
    }
}
