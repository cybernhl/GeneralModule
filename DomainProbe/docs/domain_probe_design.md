# DomainProbe 設計與測速策略規劃 (2026-09-04)

## 1. 測速邏輯優化需求
目前的測速邏輯直接針對字串列表進行，無法感知不同服務類型。在引入 `DomainManager` 的 Root Seed 機制後，測速必須針對「具備業務代表性」的服務地址進行。

## 2. 核心改進點

### 2.1 臨時 HttpClient 機制 (Isolated Client)
為了解決業務 Client 插件干擾測速的問題，建議在 `DomainProbe` 執行時：
*   **獨立性**：預設在內部建立一個臨時 `HttpClient`。
*   **純淨性**：不攜帶 `AuthPlugin`、`Logging` 等業務插件，避免測速請求被攔截或導向 401 頁面。
*   **高效性**：設定極短的 `ConnectTimeout` (e.g., 2000ms)，並在結束後自動 `close()`。

### 2.2 兩階段智慧測速
1.  **快速階段 (Header/TTFB)**：
    *   對所有 Root Seed 組裝出 API 探針路徑。
    *   使用 `prepareGet` 僅讀取 Header，快速篩選出前 3 名可用域名並立即更新 `DomainManager`。
2.  **背景深度階段 (Body Download)**：
    *   在不阻塞啟動流的前提下，完整下載探針檔案 (如 `probe.png`)。
    *   精確計算吞吐量，對域名進行二次排序。

### 2.3 參數化控制
*   `useInternalClient: Boolean`: 允許調用方選擇使用臨時 Client 或傳入現有的業務 Client。
*   `probeServiceKey: String`: 指定測速時要組裝哪種服務（預設為 `"API"`）。

## 3. 邏輯流程圖
1.  輸入 `rootSeeds`。
2.  調用 `DomainManager.getUrl(probeServiceKey, seed)` 組裝測試位址。
3.  啟動併發 Coroutines 進行 TTFB 測速。
4.  將排序後的 `seeds` 回填至 `DomainManager`。
5.  背景執行頻寬測速。

## 4. 效益
*   **更準確**：避開業務邏輯干擾。
*   **更穩定**：隔離連線池，不佔用業務位額。
*   **更通用**：支援變動的 Root Domain 與多樣化的服務前綴。

/**
* 兩階段智慧測速：
* 1. 快速階段：僅確認 Header 回傳 (TTFB)，立即更新 DomainManager。
* 2. 深度階段：背景執行 Body 下載，獲取精確延遲後二次更新。
     */
     suspend fun HttpClient.probeAndSortDomains(
     urlManager: DomainManager,
     probe_path: String = BuildConfig.PROBE_PATH,
     probe_timeout: Long = BuildConfig.PROBE_TIMEOUT
     ) = coroutineScope {
     val urls = urlManager.allUrls
     if (urls.isEmpty()) return@coroutineScope
     /**
    * 網域測速結果資料結構
      */
      data class ProbeResult(val url: String, val timeMs: Long, val isSuccess: Boolean)
      println("DomainProbe: Starting smart two-phase probe for ${urls.size} domains...")

// --- 第一階段：Header (TTFB) 測速 ---
// 目標：最快速度找出可用的伺服器
val headerResults = urls.map { url ->
async {
val fullUrl = if (url.endsWith("/")) "${url.dropLast(1)}$probe_path" else "$url$probe_path"
val startTime = getTimeMillis()
val success = try {
withTimeoutOrNull(probe_timeout.milliseconds) {
// 使用 prepareGet 確保只讀取 Header 即回傳
this@probeAndSortDomains.prepareGet(fullUrl).execute { response ->
response.status.value in 200..299
}
} ?: false
} catch (_: Exception) {
false
}
val duration = getTimeMillis() - startTime
ProbeResult(url, duration, success)
}
}.awaitAll()

val fastTrack = headerResults.filter { it.isSuccess }.sortedBy { it.timeMs }.map { it.url }
if (fastTrack.isNotEmpty()) {
urlManager.updateSortedUrls(fastTrack)
println("DomainProbe: Phase 1 (Header) done. Fastest TTFB: ${fastTrack.first()}")
}

// --- 第二階段：Body 深度測速 (背景執行) ---
// 目標：在不影響 App 啟動的前提下，找出真正頻寬最好的網域
launch(Dispatchers.Default) {
val bodyResults = urls.map { url ->
async {
val fullUrl = if (url.endsWith("/")) "${url.dropLast(1)}$probe_path" else "$url$probe_path"
val startTime = getTimeMillis()
val success = try {
withTimeoutOrNull((probe_timeout * 3).milliseconds) { // 給予下載 Body 3 倍的寛容時間
val response = this@probeAndSortDomains.get(fullUrl)
val bytes = response.bodyAsBytes()
bytes.isNotEmpty()
} ?: false
} catch (e: Exception) {
false
}
val duration = getTimeMillis() - startTime
ProbeResult(url, duration, success)
}
}.awaitAll()

     val finalTrack = bodyResults.filter { it.isSuccess }.sortedBy { it.timeMs }.map { it.url }
     if (finalTrack.isNotEmpty()) {
         urlManager.updateSortedUrls(finalTrack)
         println("DomainProbe: Phase 2 (Body) done. Best Overall: ${finalTrack.first()}")
     } else {
         println("DomainProbe: Phase 2 timed out or failed. Retaining Phase 1 ranking.")
     }
}
}

