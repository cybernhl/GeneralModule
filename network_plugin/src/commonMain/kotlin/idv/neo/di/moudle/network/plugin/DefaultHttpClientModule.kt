package idv.neo.di.moudle.network.plugin

import idv.neo.module.network.plugin.BuildConfig
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

/**
 * 提供預設的 HttpClient Plugins 配置，供 HttpClientModule 注入使用
 */
val defaultHttpClientPluginModule = module {
    single<List<HttpClientConfig<*>.() -> Unit>>(named(idv.neo.module.di.tag.HTTP_CLIENT_DEFAULT_PLUGINS_TAG)) {
        val kotlinx_json = getOrNull<Json>(named(idv.neo.module.di.tag.GENERAL_JSON_TAG)) ?: Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            coerceInputValues = true
            isLenient = true
        }

        listOf(
            {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            println("Ktor Log: $message")
                            if (message.contains("101 Switching Protocols")) {
                                println("[WS-LOG] $message")
                            }
                        }
                    }
                    level = LogLevel.ALL
                }
            },
            {
                install(ContentNegotiation) {
                    json(kotlinx_json)
                    json(kotlinx_json, contentType = ContentType.Application.Json)
                    json(kotlinx_json, contentType = ContentType.Text.Html)
                    register(ContentType.Text.Html, KotlinxSerializationConverter(kotlinx_json))
                }
            },
            {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000L
                    connectTimeoutMillis = 60000L
                    socketTimeoutMillis = 60000L
                }
            },
            {
                install(WebSockets) {
                    pingInterval = 30.seconds
                }
            }
        )
    }
}
