package idv.neo.di.moudle.network.restful

import idv.neo.module.network.domain.DomainManager
import idv.neo.module.network.retrofitBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

//FIXME
val resfulJVMModule = module {
    includes(resfulModule)
    single<Retrofit> {
        val url = getOrNull<DomainManager>()?.getActiveUrl()?.takeIf { it.isNotEmpty() }
            ?: get<String>(named(idv.neo.module.di.tag.APP_API_URL_TAG))
        retrofitBuilder(url,get(),get())
    }
}