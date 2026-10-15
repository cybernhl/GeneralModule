package idv.neo.module.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode

/**
 * 數值顯示格式化擴充
 * 對於金流顯示，通常保留兩位小數並進行千分位格式化
 */
fun BigDecimal.toDisplayString(precision: Int = 2): String {
    // 進行捨入處理以供顯示，使用 ROUND_HALF_TO_EVEN (Banker's Rounding)
    val displayValue = if (precision >= 0) {
        try {
            this.roundToDigitPositionAfterDecimalPoint(precision.toLong(), RoundingMode.ROUND_HALF_TO_EVEN)
        } catch (_: Exception) {
            this
        }
    } else {
        this
    }

    val displayRaw = displayValue.toPlainString()
    val isNegative = displayRaw.startsWith("-")
    val absDisplayRaw = if (isNegative) displayRaw.substring(1) else displayRaw

    val parts = absDisplayRaw.split(".")
    val integerPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
    
    val result = if (precision > 0) {
        val decimalPart = if (parts.size > 1) {
            val dp = parts[1]
            if (dp.length >= precision) dp.take(precision) else dp.padEnd(precision, '0')
        } else {
            "0".repeat(precision)
        }
        "$integerPart.$decimalPart"
    } else {
        integerPart
    }
    
    return if (isNegative) "-$result" else result
}

/**
 * 格式化為貨幣字串 (例如 ฿1,234.56)
 */
fun BigDecimal.toCurrencyString(precision: Int = 2): String {
    val displayRaw = this.toPlainString()
    val isNegative = displayRaw.startsWith("-")
    
    val formatted = this.toDisplayString(precision)
    // 如果是負數，toDisplayString 會帶負號，我們要把 ฿ 放在負號後面或前面
    // 通常格式是 -฿1,234.56
    return if (isNegative) {
        "-฿${formatted.substring(1)}"
    } else {
        "฿$formatted"
    }
}



