package idv.neo.module.network.plugin

import idv.neo.entity.common.ApiResponse
import idv.neo.entity.common.NetworkEventBus
import idv.neo.module.network.domain.DomainManager
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.Url
import io.ktor.http.isSuccess

/**
 * 將動態網域重試邏輯封裝為 HttpClientConfig 擴充功能
 * 當遭遇 502, 503, 504, 520-524 或網路異常時，自動從 ApiUrlManager 切換至下一個可用網域並重試。
 */
fun HttpClientConfig<*>.RetryDomain(urlManager: DomainManager) {
    val retryableStatuses = listOf(502, 503, 504, 520, 521, 522, 523, 524)

    install(createClientPlugin("GlobalUrlInterceptor") {
        onRequest { request, _ ->
            val activeUrl = urlManager.getActiveUrl()
            if (activeUrl.isNotEmpty()) {
                val uri = Url(activeUrl)
                // 更新請求的基礎路徑，解決 Ktorfit 初始化後 baseUrl 固定的問題
                request.url.host = uri.host
                request.url.protocol = uri.protocol
                // 如果有特殊連接埠則更新，否則保持原樣
                if (uri.port != 0 && uri.port != 80 && uri.port != 443) {
                    request.url.port = uri.port
                }
            }
        }
    })

    // 2. 安裝重試機制
    install(HttpRequestRetry) {
        maxRetries = 10
        
        // 處理 HTTP 錯誤碼 (包含 Cloudflare 52x 錯誤)
        retryIf { _, response ->
            val isRetryableStatus = response.status.value in retryableStatuses
            isRetryableStatus && urlManager.urlCount > 1
        }
        
        // 處理網路層異常 (如 DNS 失敗、連線超時等)
        retryOnExceptionIf { _, cause ->
            val causeString = cause.toString()
            val isNetworkError = cause is HttpRequestTimeoutException ||
                               causeString.contains("UnresolvedAddressException", true) ||
                               causeString.contains("ConnectException", true) ||
                               causeString.contains("SocketException", true) ||
                               causeString.contains("IOException", true)
            
            isNetworkError && urlManager.urlCount > 1
        }

        modifyRequest { request ->
            // 重試時觸發切換下一個網域
            if (urlManager.switchToNextUrl()) {
                val nextUrl = urlManager.getActiveUrl()
                if (nextUrl.isNotEmpty()) {
                    val uri = Url(nextUrl)
                    request.url.host = uri.host
                    request.url.protocol = uri.protocol
                    if (uri.port != 0 && uri.port != 80 && uri.port != 443) {
                        request.url.port = uri.port
                    }
                    println("DomainRetry: [Retry] Switched to new URL -> $nextUrl")
                }
            }
        }
    }

    // 3. 驗證回應 (處理最終失敗)
    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                if (response.status.value in retryableStatuses) {
                    if (urlManager.urlCount <= 1) {
                        urlManager.switchToNextUrl() 
                        val apiException = ApiResponse.Error(
                            code = 5004,
                            message = "All API domains failed. Last error: ${response.status.value}",
                        )
                        NetworkEventBus.emitError(apiException)
                        throw apiException
                    }
                }
            }
        }
    }
}
