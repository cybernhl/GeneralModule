package idv.neo.di.moudle.utils

import idv.neo.module.utils.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val utilsModule = module {
    single<Json>(named(idv.neo.module.di.tag.GENERAL_JSON_TAG)) {
        json
    }
}
