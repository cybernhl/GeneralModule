package idv.neo.module.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToURL
import platform.LocalAuthentication.LABiometryTypeFaceID
import platform.LocalAuthentication.LABiometryTypeNone
import platform.LocalAuthentication.LABiometryTypeTouchID
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorBiometryNotAvailable
import platform.LocalAuthentication.LAErrorBiometryNotEnrolled
import platform.LocalAuthentication.LAErrorPasscodeNotSet
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIDevice
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val deviceModel: String = UIDevice.currentDevice.model
    override val osVersion: String = UIDevice.currentDevice.systemVersion

    override fun isRooted(): Boolean {
        val fileManager = NSFileManager.defaultManager()
        val paths = listOf(
            "/Applications/Cydia.app",
            "/Library/MobileSubstrate/MobileSubstrate.dylib",
            "/bin/bash",
            "/usr/sbin/sshd",
            "/etc/apt",
            "/private/var/lib/apt/"
        )
        return paths.any { fileManager.fileExistsAtPath(it) }
    }


    override suspend fun getFcmToken(): String? = null

    override suspend fun forceRefreshToken(): String? = null

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun encryptWithBiometric(data: String): String? {
        val nsData = NSString.create(string = data).dataUsingEncoding(NSUTF8StringEncoding) ?: return null
        return nsData.base64EncodedStringWithOptions(0u)
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun decryptWithBiometric(encryptedData: String): String? {
        val nsData = NSData.create(base64EncodedString = encryptedData, options = 0u) ?: return null
        return NSString.create(data = nsData, encoding = NSUTF8StringEncoding)?.toString()
    }

    override suspend fun compressImage(bytes: ByteArray, maxSizeBytes: Long): ByteArray {
        // Basic implementation for iOS: Return as is for now
        // In a real app, use UIImage and UIImageJPEGRepresentation with compression quality
        return bytes
    }
}
actual fun getPlatform(): Platform = IOSPlatform()