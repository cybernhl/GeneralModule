package idv.neo.module.network.resful

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import io.ktor.client.HttpClient

fun ktorfitBuilder(url:String,client:HttpClient):Ktorfit=Ktorfit.Builder()
    .baseUrl(url)
    .httpClient(client)
    .converterFactories(
        FlowConverterFactory(),
        CallConverterFactory(),
    )
    .build()