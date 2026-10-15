package idv.neo.module.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import okio.FileSystem
import java.io.File

actual val fileSystem: FileSystem = FileSystem.SYSTEM

actual fun produceDataStore(context: Any?, fileName: String): DataStore<Preferences> {
    return createDataStore(
        fileName = fileName,
        producePath = { name ->
            File(System.getProperty("user.home"), name).absolutePath
        }
    )
}
