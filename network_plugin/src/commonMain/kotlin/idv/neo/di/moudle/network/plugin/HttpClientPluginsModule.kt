package idv.neo.di.moudle.network.plugin

import idv.neo.module.network.domain.DomainManager
import idv.neo.module.network.plugin.AuthPlugin
import idv.neo.module.network.plugin.DeviceIdentityPlugin
import idv.neo.module.network.plugin.HtmlErrorPlugin
import idv.neo.module.network.plugin.RetryDomain
import io.ktor.client.HttpClientConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module

val httpClientPluginModule = module {
    single<List<HttpClientConfig<*>.() -> Unit>>(named(idv.neo.module.di.tag.HTTP_CLIENT_PLUGINS_TAG)) {
        listOf(
            {   install(AuthPlugin)},
            {  install(HtmlErrorPlugin) },
            {  install(DeviceIdentityPlugin) },
            { getOrNull<DomainManager>()?.let { RetryDomain(it) } }
        )
    }
}