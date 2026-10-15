package idv.neo.module.network.plugin

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface dedicated for Networking Layer (Ktor Plugin) and Session Management.
 * Isolates the responsibility of providing credentials from the full account model.
 */
interface CredentialsProvider {
    val currentToken: String?
    val isValidated: StateFlow<Boolean>
    fun setValidated(value: Boolean)
}