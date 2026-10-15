package idv.neo.module.utils

import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.IIOImage
import javax.imageio.ImageIO

actual fun getPlatform(): Platform = JVMPlatform()

class JVMPlatform : Platform {
    override val name: String = "ANDROID"//For desktop FCM test
    //    override val deviceModel: String = "${System.getProperty("os.name")} ${System.getProperty("os.arch")}"
    override val deviceModel: String = "ANDROID"
    override val osVersion: String = System.getProperty("os.version") ?: "Unknown"

    override fun isRooted(): Boolean = false

    override suspend fun getFcmToken(): String {
        val randomSuffix = (1000..9999).random()
        return "mock_fcm_token_for_desktop_$randomSuffix"
    }

    override suspend fun forceRefreshToken(): String {
        return getFcmToken()
    }

    override fun encryptWithBiometric(data: String): String? {
        return java.util.Base64.getEncoder().encodeToString(data.toByteArray())
    }

    override fun decryptWithBiometric(encryptedData: String): String? {
        return try {
            String(java.util.Base64.getDecoder().decode(encryptedData))
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun compressImage(bytes: ByteArray, maxSizeBytes: Long): ByteArray {
        if (bytes.size <= maxSizeBytes) return bytes

        return try {
            val input = java.io.ByteArrayInputStream(bytes)
            val originalImage = ImageIO.read(input) ?: return bytes

            // Handle Alpha channel: Create a new RGB image with a WHITE background to flatten the alpha
            val rgbImage = BufferedImage(
                originalImage.width,
                originalImage.height,
                BufferedImage.TYPE_INT_RGB
            )
            val g2d = rgbImage.createGraphics()
            g2d.color = Color.WHITE
            g2d.fillRect(0, 0, originalImage.width, originalImage.height)
            g2d.drawImage(originalImage, 0, 0, null)
            g2d.dispose()

            var quality = 0.8f
            var compressedBytes: ByteArray
            var currentImage = rgbImage

            do {
                val outputStream = java.io.ByteArrayOutputStream()
                val writers = javax.imageio.ImageIO.getImageWritersByFormatName("jpg")
                if (!writers.hasNext()) return bytes

                val writer = writers.next()
                val ios = javax.imageio.ImageIO.createImageOutputStream(outputStream)
                writer.output = ios

                val param = writer.defaultWriteParam
                if (param.canWriteCompressed()) {
                    param.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
                    param.compressionQuality = quality
                }

                writer.write(null, IIOImage(currentImage, null, null), param)
                writer.dispose()
                ios.close()

                compressedBytes = outputStream.toByteArray()

                // If still too large after quality reduction, downscale the image iteratively
                if (compressedBytes.size > maxSizeBytes && quality <= 0.3f) {
                    val scale = 0.7
                    val newWidth = (currentImage.width * scale).toInt()
                    val newHeight = (currentImage.height * scale).toInt()
                    if (newWidth < 10 || newHeight < 10) break // Too small to scale further

                    val scaledImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
                    val g = scaledImage.createGraphics()
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                    g.drawImage(currentImage, 0, 0, newWidth, newHeight, null)
                    g.dispose()
                    currentImage = scaledImage
                    quality = 0.8f // Reset quality for the new resolution
                } else {
                    quality -= 0.1f
                }
            } while (compressedBytes.size > maxSizeBytes && (quality > 0.1f || currentImage != rgbImage))

            println("JVMPlatform: Image compressed from ${bytes.size} to ${compressedBytes.size} bytes (Format: JPEG)")
            compressedBytes
        } catch (e: Exception) {
            println("JVMPlatform: Compression failed: ${e.message}")
            bytes
        }
    }
}