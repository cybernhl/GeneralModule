package idv.neo.di.moudle.storage

import idv.neo.module.di.tag.APP_DATASTORE_FILE_TAG
import idv.neo.module.storage.BuildConfig
import idv.neo.module.storage.produceDataStore
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformStorageModule: Module = module {
    single {
        getOrNull<String>(named(APP_DATASTORE_FILE_TAG))?.let {
            produceDataStore(fileName = it)
        } ?: produceDataStore()
    }
}
