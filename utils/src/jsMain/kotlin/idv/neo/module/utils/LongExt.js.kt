package idv.neo.module.utils

actual fun getCurrentTimeMillis(): Long = kotlin.js.Date.now().toLong()
