package idv.neo.module.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual fun provideDefaultEngineFactory(): HttpClientEngineFactory<*> {
    return Js
}
