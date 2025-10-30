package me.matsumo.zencall.core.model.number

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class PhoneDetail(
    val phoneNumber: String,        // 例: 0120964886
    val head: String,               // 頭番号/市外局番: 0120
    val middle: String,             // 中間番号/市内局番: 964
    val subscriber: String,         // 加入者番号: 886
    val numberType: String?,        // 番号種類
    val provider: String?,          // 番号提供事業者
    val accessCount: Int?,          // アクセス回数
    val searchCount: Int?,          // 検索回数
    val accessGraph: List<GraphPoint>, // アクセス遷移グラフ（見つからなければ空）
    val searchGraph: List<GraphPoint>,
    val business: BusinessInfo?,    // 事業者情報（見つからなければ null）
    val reviews: List<Review>       // 口コミ（0件可）
) {

    data class GraphPoint(
        val date: LocalDate,     // 日付
        val count: Int,         // アクセス回数
    )

    data class BusinessInfo(
        val name: String?,
        val category: String?,
        val address: String?,
        val contact: String?,    // 問い合わせ先（電話番号）
        val website: String?
    )

    data class Review(
        val userName: String?,
        val postedAt: LocalDateTime?,
        val body: String
    )
}