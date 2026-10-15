package idv.neo.module.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun provideDefaultEngineFactory(): HttpClientEngineFactory<*> {
    return CIO
}
