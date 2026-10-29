package idv.neo.module.utils

actual fun getPlatform(): Platform = JSPlatform()

class JSPlatform : Platform {
    override val name: String = "JS"
    override val deviceModel: String = "Browser/Node"
    override val osVersion: String = "Unknown"

    override fun isRooted(): Boolean = false

    override suspend fun getFcmToken(): String? = null

    override suspend fun forceRefreshToken(): String? = null

    override fun encryptWithBiometric(data: String): String? = null

    override fun decryptWithBiometric(encryptedData: String): String? = null

    override suspend fun compressImage(bytes: ByteArray, maxSizeBytes: Long): ByteArray = bytes
}
