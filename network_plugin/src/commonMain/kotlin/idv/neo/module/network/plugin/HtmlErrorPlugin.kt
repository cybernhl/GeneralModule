package idv.neo.module.network.plugin

import idv.neo.entity.common.ApiResponse
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 專門處理當 API 回傳 HTML (通常是 Nginx 或 Cloudflare 錯誤頁面) 而非 JSON 時的 Plugin
 */
val HtmlErrorPlugin = createClientPlugin("HtmlErrorPlugin") {
    onResponse { response ->
        val contentType = response.contentType()
        // 檢查 Content-Type 是否為 HTML
        if (contentType?.match(ContentType.Text.Html) == true) {
            val body = response.bodyAsText()

            // 判斷是否為常見的伺服器錯誤頁面特徵
            if (body.trim().startsWith("<html", ignoreCase = true) ||
                body.contains("nginx error!", ignoreCase = true) ||
                body.contains("Web server is returning an unknown error", ignoreCase = true) ||
                body.contains("<body", ignoreCase = true)
            ) {
                throw ApiResponse.Error(
                    code = 5003,
                    message = "Service Temporarily Unavailable (HTML Error Page)",
                )
            }
        }
    }
}