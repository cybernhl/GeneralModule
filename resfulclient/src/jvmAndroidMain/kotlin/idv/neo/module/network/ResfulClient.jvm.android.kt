package idv.neo.module.network

import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * 建立 Retrofit 實例的建構器
 * 
 * @param url Base URL
 * @param client OkHttpClient 實例
 * @param factories Converter Factory 列表
 */
fun retrofitBuilder(url: String, client: OkHttpClient, factories: List<Converter.Factory>): Retrofit =
    Retrofit.Builder()
        .baseUrl(url)
        .client(client)
        .apply { factories.forEach { addConverterFactory(it) } }
        .build()
