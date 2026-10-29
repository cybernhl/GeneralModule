# DomainManager 設計與重構規劃 (2026-09-04)

## 1. 背景與需求分析
目前系統需要支援多協議 (HTTPS/WSS) 與多子網域 (Sub-domain) 服務，且需具備應對 DNS 污染與動態域名切換的能力。現有的 `DomainManager` 僅支援單一 URL 列表，無法靈活組裝不同服務的位址。

### 核心挑戰：
*   **域名多變性**：Root Domain 可能頻繁更動（如 `kkman99.xyz` -> `dpp88.xyz`）。
*   **服務標識符穩定性**：服務前綴（如 `payapi`）通常固定，但需對應不同 Root。
*   **安全性要求**：所有生成的 Base URL 必須嚴格以 `/` 結尾，以防止 Ktor/Ktorfit 拋出異常。

## 2. 解決方案：基於「Root Seed」與「Service Pattern」的組裝機制

### 2.1 數據結構定義
將域名管理拆分為兩個維度：
1.  **Root Seeds**: 管理核心域名列表 (e.g., `18.live`, `klock99.xyz`)。
2.  **Service Patterns**: 管理服務的協議與前綴規則。

```kotlin
data class ServicePattern(
    val protocol: String, // "https", "wss"
    val prefix: String    // "api", "chat-app", "www"
)
```

### 2.2 核心功能規劃
*   **Root Domain 提取**：接收完整 URL 並解析出 Root 部分作為 Seed。
*   **動態組裝 (Assembly)**：根據傳入的 `serviceKey`，結合當前優先級最高的 `Seed` 與對應的 `Pattern` 拼接 URL。
*   **標準化 (Normalization)**：內部強制檢查生成的 URL 結尾，確保包含 `/`。
*   **外部注入 (Injection)**：`ServicePattern` 映射表由 Application 層注入，使 `GeneralModule` 保持通用。

## 3. 使用範例 (虛擬代碼)
```kotlin
// 獲取 API 位址
val apiUrl = domainManager.getUrl("API") // https://api.99.live/

// 獲取 WebSocket 位址
val wsUrl = domainManager.getUrl("CHAT") // wss://chat-app.99.live/
```

## 4. 未來展望
*   **連動切換**：當 `DomainProbe` 發現當前 Seed 失敗時，切換 Seed 會導致所有 Service URL 同步更新。
*   **Origin 管理**：統一提供 `Origin` 與 `Referer` Header 所需的正確域名。

/**
* 管理 API 網域的類別，支援動態切換與更新
  */

class DomainManager (initialUrls: List<String>) {
private var urls = initialUrls

    fun getActiveUrl(): String = urls.firstOrNull() ?: ""

    fun switchToNextUrl(): Boolean {
        val current = urls
        if (current.isEmpty()) return false
        val next = current.drop(1)
        urls = next

        println("Dropped failing URL. Remaining count: ${next.size}")
        if (next.isNotEmpty()) {
            println("New active URL: ${next[0]}")
        }
        return next.isNotEmpty()
    }

    fun addUrl(url: String) {
        val current = urls
        if (!current.contains(url)) {
            urls = current + url
        }
    }

    /**
     * 更新並重新排序網域列表 (例如測速後結果)
     */
    fun updateSortedUrls(newUrls: List<String>) {
        if (newUrls.isNotEmpty()) {
            urls = newUrls
            println("DomainManager : Domains reordered. New primary: ${urls.firstOrNull()}")
        }
    }

    val urlCount: Int get() = urls.size
    val allUrls: List<String> get() = urls
}
