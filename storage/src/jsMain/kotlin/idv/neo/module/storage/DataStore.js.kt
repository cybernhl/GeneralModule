package idv.neo.module.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual fun produceDataStore(context: Any?, fileName: String): DataStore<Preferences> {
    return createDataStore(
        fileName = fileName,
        producePath = { name -> name }
    )
}
