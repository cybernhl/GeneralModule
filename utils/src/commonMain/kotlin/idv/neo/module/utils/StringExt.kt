package idv.neo.module.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import okio.ByteString.Companion.encodeUtf8
import kotlin.random.Random

fun String.toBigDecimal(): BigDecimal = BigDecimal.parseString(this)

fun String.toB64Url() = this.encodeUtf8().base64Url().replace("=", "").replace("+", "-").replace("/", "_")
