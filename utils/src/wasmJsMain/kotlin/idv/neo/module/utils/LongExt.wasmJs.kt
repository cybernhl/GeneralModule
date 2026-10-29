package idv.neo.module.utils

private fun dateNow(): Double = js("Date.now()")

actual fun getCurrentTimeMillis(): Long = dateNow().toLong()
