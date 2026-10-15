package idv.neo.module.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import okio.FileSystem

actual val fileSystem: FileSystem = FileSystem.SYSTEM

actual fun produceDataStore(context: Any?, fileName: String): DataStore<Preferences> {
    val appContext = (context as? Context)?.applicationContext 
        ?: error("Android context is required to produce DataStore.")

    return createDataStore(
        fileName = fileName,
        producePath = { name -> appContext.filesDir.resolve(name).absolutePath }
    )
}
