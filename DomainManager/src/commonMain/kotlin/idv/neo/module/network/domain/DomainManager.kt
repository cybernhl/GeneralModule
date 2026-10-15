package idv.neo.module.network.domain

import io.ktor.http.*

/**
 * 管理域名系統，支援動態端點遷移與多協議組裝
 */
class DomainManager(
    initialUrls: List<String>,
    private val patterns: Map<String, ServicePattern> = emptyMap()
) {
    // 儲存原始端點 Host (例如: api.lang.live, spay2appapi.xyz)
    private var endpointSeeds = initialUrls.map { extractHost(it) }.filter { it.isNotEmpty() }.distinct()
    
    private val servicePatterns = patterns.toMutableMap()

    /**
     * 從 URL 中提取 Host 部分
     */
    private fun extractHost(input: String): String {
        return try {
            if (input.contains("://")) Url(input).host else input.substringBefore("/")
        } catch (e: Exception) {
            input
        }
    }

    /**
     * 根據基礎 API 端點組裝目標服務 URL
     * 邏輯：在基礎 Host 中尋找 "API" 的標識符，並替換為目標服務的標識符
     */
    fun getUrlForRoot(serviceKey: String, baseHost: String): String {
        val targetPattern = servicePatterns[serviceKey]
        val apiPattern = servicePatterns["API"] ?: ServicePattern("https", "api")

        if (targetPattern == null || serviceKey == "API") {
            // 如果是 API 本身或找不到規則，直接回傳帶有協議的 BaseHost
            val protocol = if (serviceKey == "API") apiPattern.protocol else "https"
            return normalizeUrl("$protocol://$baseHost")
        }

        // 執行替換邏輯：例如 spay2appapi.xyz 中找 "api" 換成 "chat" -> spay2appchat.xyz
        val newHost = if (apiPattern.identifier.isNotEmpty()) {
            baseHost.replace(apiPattern.identifier, targetPattern.identifier)
        } else {
            // 如果 API 沒標籤，則嘗試以前綴方式加上去
            "${targetPattern.identifier}.$baseHost"
        }

        return normalizeUrl("${targetPattern.protocol}://$newHost")
    }

    fun getUrl(serviceKey: String): String {
        val activeHost = endpointSeeds.firstOrNull() ?: return ""
        return getUrlForRoot(serviceKey, activeHost)
    }

    fun getActiveUrl(): String = getUrl("API")

    fun switchToNextUrl(): Boolean {
        if (endpointSeeds.isEmpty()) return false
        endpointSeeds = endpointSeeds.drop(1)
        println("DomainManager: Switched to next endpoint. Remaining: ${endpointSeeds.size}")
        return endpointSeeds.isNotEmpty()
    }

    fun addUrl(url: String) {
        val host = extractHost(url)
        if (host.isNotEmpty() && !endpointSeeds.contains(host)) {
            endpointSeeds = endpointSeeds + host
        }
    }

    fun updateSortedUrls(newUrls: List<String>) {
        val newHosts = newUrls.map { extractHost(it) }.filter { it.isNotEmpty() }.distinct()
        if (newHosts.isNotEmpty()) {
            endpointSeeds = newHosts
            println("DomainManager: Endpoints reordered. Current: ${endpointSeeds.firstOrNull()}")
        }
    }

    private fun normalizeUrl(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    val urlCount: Int get() = endpointSeeds.size
    val allUrls: List<String> get() = endpointSeeds
}
