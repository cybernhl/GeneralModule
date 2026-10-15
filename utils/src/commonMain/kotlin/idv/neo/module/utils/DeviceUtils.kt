package idv.neo.module.utils

import kotlin.time.Clock

object DeviceUtils {
    fun getUniqueDeviceId(): String {
        val platform = getPlatform()
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val rawId = "${platform.deviceModel}|${platform.osVersion}|${timestamp}"
        val hash = rawId.fold(0L) { acc, c -> acc * 31 + c.code.toLong() }
        return hash.toULong().toString(16).uppercase()
    }
}