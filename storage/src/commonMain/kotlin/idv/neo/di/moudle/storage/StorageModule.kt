package idv.neo.di.moudle.storage

import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformStorageModule: Module

val storageModule = module {
    includes(platformStorageModule)
}
