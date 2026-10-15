package idv.neo.module.utils

import kotlin.random.Random

fun generateBrowserId(): String {
    val chars = "0123456789abcdef"
    return (1..32).map { chars[Random.nextInt(chars.length)] }.joinToString("")
}