package idv.neo.module.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.FileSystem
import okio.Path.Companion.toPath

expect val fileSystem: FileSystem
fun createDataStore(fileName: String, producePath: (String) -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        storage = androidx.datastore.core.okio.OkioStorage(
            fileSystem = fileSystem,
            producePath = { producePath(fileName).toPath() },
            serializer = androidx.datastore.preferences.core.PreferencesSerializer
        )
    )
}

expect fun produceDataStore(
    context: Any? = null, 
    fileName: String = "DataStore.preferences_pb"
): DataStore<Preferences>
