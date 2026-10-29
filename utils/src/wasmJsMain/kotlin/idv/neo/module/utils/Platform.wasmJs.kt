package idv.neo.module.utils

actual fun getPlatform(): Platform = WasmJSPlatform()

class WasmJSPlatform : Platform {
    override val name: String = "WasmJS"
    override val deviceModel: String = "Browser/Node"
    override val osVersion: String = "Unknown"

    override fun isRooted(): Boolean = false

    override suspend fun getFcmToken(): String? = null

    override suspend fun forceRefreshToken(): String? = null

    override fun encryptWithBiometric(data: String): String? = null

    override fun decryptWithBiometric(encryptedData: String): String? = null

    override suspend fun compressImage(bytes: ByteArray, maxSizeBytes: Long): ByteArray = bytes
}
