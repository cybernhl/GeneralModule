package idv.neo.module.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.Settings
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.core.graphics.scale
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume

class AndroidPlatform  : Platform {
    override val name: String = "ANDROID"
    override val deviceModel: String = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
    override val osVersion: String = "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"

    override fun isRooted(): Boolean {
        return false //temp
    }

    override suspend fun getFcmToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val task = FirebaseMessaging.getInstance().token
                Tasks.await(task)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun forceRefreshToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                Tasks.await(FirebaseMessaging.getInstance().deleteToken())
                val task = FirebaseMessaging.getInstance().token
                Tasks.await(task)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Android 實作：目前暫時使用 Base64 作為示意，
    // 實際生產環境應結合 BiometricPrompt.CryptoObject 與 KeyStore
    override fun encryptWithBiometric(data: String): String? {
        return try {
            android.util.Base64.encodeToString(data.toByteArray(), android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    override fun decryptWithBiometric(encryptedData: String): String? {
        return try {
            val decoded = android.util.Base64.decode(encryptedData, android.util.Base64.NO_WRAP)
            String(decoded)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun compressImage(bytes: ByteArray, maxSizeBytes: Long): ByteArray = withContext(Dispatchers.Default) {
        if (bytes.size <= maxSizeBytes) return@withContext bytes

        val options = android.graphics.BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) ?: return@withContext bytes

        var quality = 90
        var outputStream = ByteArrayOutputStream()

        // Use JPEG for compression as it's more efficient for photos
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        var compressedBytes = outputStream.toByteArray()

        // If still too large, resample
        if (compressedBytes.size > maxSizeBytes) {
            var scale = 0.8
            while (compressedBytes.size > maxSizeBytes && scale > 0.1) {
                val width = (bitmap.width * scale).toInt()
                val height = (bitmap.height * scale).toInt()
                val scaledBitmap = bitmap.scale(width, height)

                outputStream = java.io.ByteArrayOutputStream()
                scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, outputStream)
                compressedBytes = outputStream.toByteArray()

                scale -= 0.1
            }
        }

        // Further reduce quality if needed
        while (compressedBytes.size > maxSizeBytes && quality > 20) {
            quality -= 10
            outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, outputStream)
            compressedBytes = outputStream.toByteArray()
        }

        println("AndroidPlatform: Image compressed from ${bytes.size} to ${compressedBytes.size} bytes (Format: JPEG)")
        compressedBytes
    }
}

private fun createNotificationChannel(context: Context) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val name = "Default Channel"
        val descriptionText = "Default notification channel"
        val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
        val channel = android.app.NotificationChannel("default_notification_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: android.app.NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()