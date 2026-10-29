package idv.neo.di.moudle.network

import idv.neo.module.di.tag.HTTP_CLIENT_DEFAULT_PLUGINS_TAG
import idv.neo.module.di.tag.HTTP_CLIENT_PLUGINS_TAG
import idv.neo.module.di.tag.HTTP_CLIENT_WEBSOCKET_TAG
import idv.neo.module.network.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val httpClientModule = module {
    single<HttpClient> {
        createHttpClient(
            get(),
            mutableListOf<HttpClientConfig<*>.() -> Unit>().apply {
                getOrNull<List<HttpClientConfig<*>.() -> Unit>>(named(
                    HTTP_CLIENT_DEFAULT_PLUGINS_TAG
                ))?.let {
                    addAll(it)
                }

                getOrNull<List<HttpClientConfig<*>.() -> Unit>>(named(HTTP_CLIENT_PLUGINS_TAG))?.let {
                    addAll(it)
                }
            }
        )
    }

    single<HttpClient>(named(HTTP_CLIENT_WEBSOCKET_TAG)) {
        HttpClient(get<HttpClientEngineFactory<*>>()) {
            install(WebSockets) {
                pingInterval = 30.seconds
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor-WS Log: $message")
                    }
                }
                level = LogLevel.INFO
            }
        }
    }
}
