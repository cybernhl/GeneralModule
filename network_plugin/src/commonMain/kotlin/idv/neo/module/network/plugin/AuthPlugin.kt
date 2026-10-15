package idv.neo.module.network.plugin

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform

val AuthPlugin = createClientPlugin("AuthPlugin") {
    onRequest { request, _ ->
        val url = request.url.buildString()
//        println("[AuthPlugin] Intercepting request: $url")

        // 關鍵：每次請求發起時，動態從 Koin 容器中解析 Provider
        KoinPlatform.getKoin().getOrNull<CredentialsProvider>()?.let { provider ->
            provider.currentToken?.takeIf { it.isNotEmpty() }?.let { token ->
//                println("[AuthPlugin] Token found, applying to header for: $url")
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                request.header("Authorization", bearerToken)
            } ?: run {
                println("[AuthPlugin] No token available in provider for: $url")
            }
        } ?: run {
            println("[AuthPlugin] No CredentialsProvider found in Koin context.")
        }
    }
}
