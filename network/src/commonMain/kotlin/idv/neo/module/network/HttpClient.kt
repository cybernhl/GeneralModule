package idv.neo.module.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 核心 HttpClient 建立邏輯，不再包含硬編碼的網域切換
 */
fun createHttpClient(
    engineFactory: HttpClientEngineFactory<*>,
    customConfigs: List<HttpClientConfig<*>.() -> Unit> = emptyList()
): HttpClient {
    return HttpClient(engineFactory) {
        expectSuccess = false

        // 應用外部傳入的所有配置 (例如 AuthPlugin, HtmlErrorPlugin, installDomainRetry, ContentNegotiation, HttpTimeout, Logging)
        customConfigs.forEach { it() }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}

expect fun provideDefaultEngineFactory(): HttpClientEngineFactory<*>