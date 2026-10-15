package idv.neo.module.network.domain.probe

import idv.neo.module.network.domain.DomainManager
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.date.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * 網域智慧測速擴展函數
 * 
 * @param urlManager 域名管理員
 * @param engineFactory 用於建立臨時 Client 的引擎工廠 (如 CIO, Darwin 等)
 * @param probe_path 探針檔案路徑
 * @param probe_timeout 單次請求超時時間
 * @param useInternalClient 是否建立一個「乾淨」且獨立的 HttpClient 進行測速，避免業務插件干擾
 * @param probeServiceKey 指定用哪個服務前綴來測速 (例如 "API")
 */
suspend fun HttpClient.probeAndSortDomains(
    urlManager: DomainManager,
    engineFactory: HttpClientEngineFactory<*>,
    probe_path: String = BuildConfig.PROBE_PATH,
    probe_timeout: Long = BuildConfig.PROBE_TIMEOUT,
    useInternalClient: Boolean = false,
    probeServiceKey: String = "API"
) = coroutineScope {
    val roots = urlManager.allUrls
    if (roots.isEmpty()) return@coroutineScope

    // 決定要使用的 HttpClient
    val testClient = if (useInternalClient) {
        HttpClient(engineFactory) {
            install(HttpTimeout) {
                connectTimeoutMillis = 2000
                requestTimeoutMillis = probe_timeout
            }
            // 不安裝任何攔截器，確保測速純淨
        }
    } else {
        this@probeAndSortDomains
    }

    data class ProbeResult(val root: String, val timeMs: Long, val isSuccess: Boolean)
    println("DomainProbe: Starting probe (useInternalClient=$useInternalClient, service=$probeServiceKey) for ${roots.size} roots...")

    try {
        // --- 第一階段：Header (TTFB) 測速 ---
        val headerResults = roots.map { root ->
            async {
                val baseUrl = urlManager.getUrlForRoot(probeServiceKey, root)
                val fullUrl = "${baseUrl.removeSuffix("/")}$probe_path"
                val startTime = getTimeMillis()
                val success = try {
                    withTimeoutOrNull(probe_timeout.milliseconds) {
                        testClient.prepareGet(fullUrl).execute { response ->
                            response.status.value in 200..299
                        }
                    } ?: false
                } catch (_: Exception) {
                    false
                }
                val duration = getTimeMillis() - startTime
                ProbeResult(root, duration, success)
            }
        }.awaitAll()

        val fastTrack = headerResults.filter { it.isSuccess }.sortedBy { it.timeMs }.map { it.root }
        if (fastTrack.isNotEmpty()) {
            urlManager.updateSortedUrls(fastTrack)
            println("DomainProbe: Phase 1 (TTFB) done. Fastest Root: ${fastTrack.first()}")
        }

        // --- 第二階段：Body 深度測速 (背景執行) ---
        launch(Dispatchers.Default) {
            val bodyResults = roots.map { root ->
                async {
                    val baseUrl = urlManager.getUrlForRoot(probeServiceKey, root)
                    val fullUrl = "${baseUrl.removeSuffix("/")}$probe_path"
                    val startTime = getTimeMillis()
                    val success = try {
                        val result = withTimeoutOrNull((probe_timeout * 3).milliseconds) {
                            val response = testClient.get(fullUrl)
                            val bytes = response.bodyAsBytes()
                            bytes.isNotEmpty()
                        }
                        result ?: false
                    } catch (_: Exception) {
                        false
                    }
                    val duration = getTimeMillis() - startTime
                    ProbeResult(root, duration, success)
                }
            }.awaitAll()

            val finalTrack = bodyResults.filter { it.isSuccess }.sortedBy { it.timeMs }.map { it.root }
            if (finalTrack.isNotEmpty()) {
                urlManager.updateSortedUrls(finalTrack)
                println("DomainProbe: Phase 2 (Body) done. Best Root: ${finalTrack.first()}")
            }
            
            // 如果是臨時產生的 Client，在背景任務結束後才關閉
            if (useInternalClient) {
                testClient.close()
                println("DomainProbe: Internal test client closed.")
            }
        }
    } catch (e: Exception) {
        println("DomainProbe: Error during probing: ${e.message}")
        if (useInternalClient) testClient.close()
    }
}
