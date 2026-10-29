package idv.neo.module.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun produceDataStore(context: Any?, fileName: String): DataStore<Preferences> {
    return createDataStore(
        fileName = fileName,
        producePath = { name ->
            File(System.getProperty("user.home"), name).absolutePath
        }
    )
}
