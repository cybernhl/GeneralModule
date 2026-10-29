package idv.neo.module.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun provideDefaultEngineFactory(): HttpClientEngineFactory<*> {
    return Darwin
}
