package idv.neo.module.utils

import kotlinx.serialization.json.Json

val json= Json {
     // 忽略 JSON 中存在但 Kotlin 類別中未定義的欄位，避免解析失敗
     ignoreUnknownKeys = true

     // 當 JSON 中的值與類別定義的型別不符時，嘗試強制轉換（如將 null 轉為預設值，或列舉值不符時使用預設）
     coerceInputValues = true

     // 是否將 null 值明確序列化到 JSON 中。設為 false 可減少 JSON 體積
     explicitNulls = false

     // 放寬 JSON 格式限制（如允許不帶引號的鍵名或特殊的數值格式）
     isLenient = true

     // 格式化輸出 JSON，使其具有縮排，便於閱讀調試
     prettyPrint = true

     // 是否序列化具有預設值的屬性。設為 true 可確保輸出的 JSON 包含所有欄位
     encodeDefaults = true
     //Specifies whether Json instance makes use of JsonNames annotation.
     //Disabling this flag when one does not use JsonNames at all may sometimes result in better performance,
     // particularly when a large count of fields is skipped with ignoreUnknownKeys.
     // true by default.
     useAlternativeNames=true
 }