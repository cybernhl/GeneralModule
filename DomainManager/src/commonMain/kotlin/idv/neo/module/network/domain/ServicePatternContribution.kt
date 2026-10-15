package idv.neo.module.network.domain

/**
 * 用於 Koin 貢獻服務模式的包裝類別
 */
data class ServicePatternContribution(
    val key: String,
    val pattern: ServicePattern
)