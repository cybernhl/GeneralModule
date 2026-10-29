package idv.neo.module.network.domain

/**
 * 定義服務的協議與轉換識別標籤
 * @param protocol 協議 (https, wss)
 * @param identifier 該服務在域名中的標識符 (如 "api", "chat-app", "www")
 */
data class ServicePattern(
    val protocol: String,
    val identifier: String
)
