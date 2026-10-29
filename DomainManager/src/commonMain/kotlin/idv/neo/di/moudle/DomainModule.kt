package idv.neo.di.moudle

import idv.neo.module.network.domain.DomainManager
import idv.neo.module.network.domain.ServicePattern
import idv.neo.module.network.domain.ServicePatternContribution
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainModule = module {
    single<DomainManager> {
        val urls = mutableListOf<String>().apply {
            // 先獲取單一預設網址並加入列表
            getOrNull<String>(named(idv.neo.module.di.tag.APP_API_URL_TAG))?.let { add(it) }
            // 再獲取備用網址列表並全部加入
            getOrNull<List<String>>(named(idv.neo.module.di.tag.APP_API_URLS_TAG))?.let { addAll(it) }
        }.distinct()

        // 1. 建立初始的 Pattern Map
        val patternMap = mutableMapOf<String, ServicePattern>()

//        // 2. 添加來自命名為 DOMAIN_SERVICE_PATTERNS 的整體 Map (舊有模式兼容)
//        getOrNull<Map<String, ServicePattern>>(named("DOMAIN_SERVICE_PATTERNS"))?.let {
//            patternMap.putAll(it)
//        }

        // 3. 獲取所有獨立註冊的 ServicePatternContribution 並「拆解添加」進入 Map
        // getAll() 就算沒東西也會回傳空列表，之後的 associate 會生成空 Map
        val contributions = getAll<ServicePatternContribution>()
        println("DomainModule: Found ${contributions.size} ServicePatternContributions")

        contributions.forEach { contribution ->
            println("DomainModule: Adding pattern ${contribution.key} -> ${contribution.pattern}")
            patternMap[contribution.key] = contribution.pattern
        }

        DomainManager(urls, patternMap)
    }
}
