package idv.neo.module.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun Double.toCurrencyString(): String {
    return BigDecimal.fromDouble(this).toCurrencyString(2)
}

fun Double.toBigDecimal(): BigDecimal = BigDecimal.fromDouble(this)

/**
 * 將 Double 數值靠攏為合適的圓滑數值 (Nice Number)
 */
fun Double.snapToNiceNumber(): Double {
    if (this <= 0) return 0.0
    val exponent = floor(log10(this))
    val p = 10.0.pow(exponent)
    val fraction = this / p
    return when {
        fraction < 1.5 -> 1.0
        fraction < 3.0 -> 2.5
        fraction < 7.5 -> 5.0
        else -> 10.0
    } * p
}

/**
 * 格式化 Double 數值為簡短預設標籤 (例如: 1M, 500K)
 */
fun Double.formatPresetLabel(): String {
    return when {
        this >= 1_000_000 -> {
            val millions = this / 1_000_000
            if (millions == millions.toLong().toDouble()) "${millions.toLong()}M" else "${millions}M"
        }
        this >= 1000 -> {
            val k = this / 1000
            if (k == k.toLong().toDouble()) "${k.toLong()}K" else "${k}K"
        }
        else -> this.toLong().toString()
    }
}
