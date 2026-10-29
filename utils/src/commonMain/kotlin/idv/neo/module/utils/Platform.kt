package idv.neo.module.utils

interface Platform {
    val name: String
    val deviceModel: String
    val osVersion: String
    fun isRooted(): Boolean
    suspend fun getFcmToken(): String?
    suspend fun forceRefreshToken(): String?

    // Biometric Cryptography
    fun encryptWithBiometric(data: String): String?
    fun decryptWithBiometric(encryptedData: String): String?
    /**
     * Resamples and compresses the image to be less than the specified size.
     */
    suspend fun compressImage(bytes: ByteArray, maxSizeBytes: Long): ByteArray
}

expect fun getPlatform(): Platform
