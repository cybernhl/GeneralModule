# WebSocket 傳輸層引擎對比與超時機制分析

本文件總結了在 KMP 專案中，Android (CIO) 與 iOS (Darwin) 在處理大量並發 WebSocket 連線時的底層行為差異，以及對 `HttpTimeout` 的敏感度分析。

## 1. 引擎核心差異：CIO vs. OkHttp vs. Darwin

| 特性 | Ktor CIO (Android 預設) | OkHttp (Android 建議) | Darwin (iOS) |
| :--- | :--- | :--- | :--- |
| **實現技術** | 純 Kotlin 協程 (SelectorManager) | Java 執行緒池 (Thread-based) | 原生 `NSURLSession` |
| **超時敏感度** | **極高**。嚴格執行 `socketTimeout` | 中等。擁有成熟的連線緩衝 | 低。由 iOS 系統託管超時 |
| **高並發能力** | 較弱。30+ 連線易觸發協程調度延遲 | **強大**。成熟的 Connection Pool | 強大。系統級連線管理 |
| **保活機制** | Ktor 框架層模擬 PING/PONG | **TCP/原生層自動 PING** | 系統層級自動保活 |

### A. 為何 Android (CIO) 容易斷線？
*   **協程調度壓力**：CIO 的所有 I/O 輪詢與心跳 (`delay`) 都跑在協程調度器上。當 Android App 負載高（如進入直播間、處理大量 UI 渲染、頻繁 GC）時，心跳協程可能延遲幾秒才執行。若伺服器檢查極嚴，這幾秒的延遲足以導致連線被重置。
*   **執行緒 vs. 協程**：OkHttp 使用專屬執行緒處理網路，受應用程式邏輯（如 Compose 渲染）影響較小；CIO 則與應用邏輯共用協程池，容易產生競爭。

## 2. 超時機制 (HttpTimeout) 的陷阱

Ktor 的 `HttpTimeout` 插件中，`socketTimeoutMillis` 的定義是：**「兩次數據包交換之間的最大時間間隔」**。

*   **現狀**：專案全局設定了 `15000L` (15秒)。
*   **後果**：對於 WebSocket，如果聊天室在 15 秒內沒有新訊息 (MSG)，且我們也沒有發送 PING，CIO 引擎會精確地在第 15.001 秒判定超時並主動切斷 Socket。這解釋了為什麼 Android 端常在交握成功後的一分鐘內無預警斷開。
*   **佐證文件**：[Ktor 官方文檔 - HttpTimeout](https://ktor.io/docs/http-timeout.html)

## 3. 解決方案：單獨禁用或放寬 WS 超時

針對 WebSocket 應採取獨立的 `HttpClient` 配置：
1.  **Rest API Client**：維持 15s 超時，確保介面不卡死。
2.  **WebSocket Client**：將 `socketTimeoutMillis` 設為 `HttpTimeout.INFINITE` 或至少 `60000L` 以上，確保它大於業務心跳間隔 (45s)。

## 4. 進階優化：切換至 OkHttp
若放寬超時後 Android 依然在 30+ 併發時不穩，建議修改 `HttpClient.android.kt`：
```kotlin
actual fun provideDefaultEngineFactory(): HttpClientEngineFactory<*> = OkHttp.config {
    config {
        pingInterval(30, TimeUnit.SECONDS) // 底層原生保活
    }
}
```
*   **優勢連結**：[OkHttpClient.Builder.pingInterval Doc](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/-builder/ping-interval/)
