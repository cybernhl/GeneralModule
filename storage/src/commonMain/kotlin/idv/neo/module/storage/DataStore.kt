package idv.neo.module.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

fun createDataStore(fileName: String, producePath: (String) -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath(fileName).toPath() }
    )
}

expect fun produceDataStore(
    context: Any? = null, 
    fileName: String = "DataStore.preferences_pb"
): DataStore<Preferences>
