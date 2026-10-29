package idv.neo.di.moudle.network.restful

import de.jensklingenberg.ktorfit.Ktorfit
import idv.neo.module.network.domain.DomainManager
import idv.neo.module.network.resful.ktorfitBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module

val resfulModule = module {
    single<Ktorfit> {
        val url = getOrNull<DomainManager>()?.getActiveUrl()?.takeIf { it.isNotEmpty() }
            ?: get<String>(named(idv.neo.module.di.tag.APP_API_URL_TAG))
        ktorfitBuilder(url, get())
    }
}