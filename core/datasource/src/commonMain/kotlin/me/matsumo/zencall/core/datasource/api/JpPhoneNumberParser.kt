package me.matsumo.zencall.core.datasource.api

import com.fleeksoft.ksoup.nodes.Document
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import me.matsumo.zencall.core.common.formatter
import me.matsumo.zencall.core.model.number.PhoneDetail

object JpPhoneNumberParser {

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val reviewTimeFormatter = LocalDateTime.Format {
        byUnicodePattern("yyyy/MM/dd HH:mm:ss")
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val graphTimeFormatter = LocalDate.Format {
        byUnicodePattern("yyyy-MM-dd")
    }

    /** ラベル付テーブルから「ラベルの右隣のセル」を安全に取得するユーティリティ */
    private fun Document.cellTextByLabel(label: String): String? {
        // "td" で完全一致するラベルセルを探し、その nextElementSibling のテキストを返す
        return select("table.table-border td")
            .firstOrNull { it.text().trim() == label }
            ?.nextElementSibling()
            ?.text()
            ?.trim()
            ?.ifEmpty { null }
    }

    /** 事業者詳細テーブル（見出し: “…の事業者詳細情報”）を起点に、ラベル→値を引く */
    private fun Document.bizCellTextByLabel(label: String): String? {
        val bizSection = select("div.frame-728-green-l:has(h2:matches(事業者詳細情報))").firstOrNull() ?: return null
        return bizSection.select("table.table-border td")
            .firstOrNull { it.text().trim() == label }
            ?.nextElementSibling()
            ?.let { td ->
                // aリンク（公式サイト）などは href を優先して取得
                td.selectFirst("a")?.let { a ->
                    a.attr("href").takeIf { it.isNotBlank() }
                } ?: td.text()
            }
            ?.trim()
            ?.ifEmpty { null }
    }

    /** div.frame-728-green-l（基本情報）からヘッダ／カウント／種別などを抜く */
    private fun parseBasicInfo(doc: Document): Triple<String, Map<String, String>, Pair<Int?, Int?>> {
        // 頭番号・中間番号・加入者番号はテーブルのラベルから拾う
        val head = doc.cellTextByLabel("頭番号") ?: ""
        val middle = doc.cellTextByLabel("中間番号") ?: ""
        val subscriber = doc.cellTextByLabel("加入者番号") ?: ""

        val access = doc.cellTextByLabel("アクセス回数")?.filter { it.isDigit() }?.toIntOrNull()
        val search = doc.cellTextByLabel("検索回数")?.filter { it.isDigit() }?.toIntOrNull()

        val map = mapOf(
            "head" to head,
            "middle" to middle,
            "subscriber" to subscriber,
            "numberType" to (doc.cellTextByLabel("番号種類") ?: ""),
            "provider" to (doc.cellTextByLabel("番号提供事業者") ?: "")
        )
        return Triple("$head$middle$subscriber", map, access to search)
    }

    /** 事業者情報の抽出 */
    private fun parseBusiness(doc: Document): PhoneDetail.BusinessInfo? {
        doc.selectFirst("div.frame-728-green-l:has(h2:matches(事業者詳細情報))") ?: return null
        fun get(label: String) = doc.bizCellTextByLabel(label)
        val name = get("事業者名称")
        val category = get("業種")
        val address = get("住所")
        val contact = get("問い合わせ先")
        val website = get("公式サイト")

        if (name == null && category == null && address == null && contact == null && website == null) {
            return null
        }
        return PhoneDetail.BusinessInfo(name, category, address, contact, website)
    }

    /** 口コミの抽出（ピンク見出しのブロックのみ） */
    private fun parseReviews(doc: Document): List<PhoneDetail.Review> {
        return doc.select("div.frame-728-gray-l:has(.title-background-pink)").mapNotNull { box ->
            val user = box.selectFirst(".title-background-pink span.red-dark")?.text()?.trim()
            val timeText = box.selectFirst(".title-background-pink td:matches(\\d{4}/\\d{2}/\\d{2})")
                ?.text()
                ?.trim()
            val postedAt = runCatching { LocalDateTime.parse(timeText ?: "", reviewTimeFormatter) }.getOrNull()

            val bodyHtml = box.selectFirst(".content dt")?.html()?.trim() ?: return@mapNotNull null
            val body = bodyHtml
                .replace("<br>", "\n", ignoreCase = true)
                .replace("<br/>", "\n", ignoreCase = true)
                .replace("<BR>", "\n", ignoreCase = true)
                .replace(Regex("<[^>]+>"), "") // 最低限のタグ除去
                .trim()

            PhoneDetail.Review(userName = user, postedAt = postedAt, body = body)
        }
    }

    /**
     * アクセス遷移グラフの抽出。
     */
    private fun parseAccessGraph(doc: Document): Pair<List<PhoneDetail.GraphPoint>, List<PhoneDetail.GraphPoint>> {
        val scripts = doc.select("script").joinToString("\n") { it.data() }

        fun extractBetween(start: String, end: String): String? {
            val pattern = "(?<=${Regex.escape(start)}).*?(?=${Regex.escape(end)})"
            return Regex(pattern).find(scripts)?.groupValues?.get(0)
        }

        fun parseList(raw: String?): List<PhoneDetail.GraphPoint> {
            val array = formatter.parseToJsonElement(raw?.replace("'", "\"").orEmpty()).jsonArray
            val result = array.map {
                PhoneDetail.GraphPoint(
                    date = LocalDate.parse(it.jsonArray[0].jsonPrimitive.content, graphTimeFormatter),
                    count = it.jsonArray[1].jsonPrimitive.int
                )
            }

            return result
        }

        val accessPointsRaw = extractBetween("var accessPoints=", ";")
        val searchPointsRaw = extractBetween("var searchPoints=", ";")

        val accessPoints = parseList(accessPointsRaw)
        val searchPoints = parseList(searchPointsRaw)

        return accessPoints to searchPoints
    }

    /** メインのパース関数 */
    fun parse(doc: Document): PhoneDetail {
        val (rawPhone, basicMap, counts) = parseBasicInfo(doc)
        val head = basicMap["head"].orEmpty()
        val middle = basicMap["middle"].orEmpty()
        val subscriber = basicMap["subscriber"].orEmpty()

        val phone = rawPhone.ifBlank { (head + middle + subscriber) }

        val numberType = basicMap["numberType"]
        val provider = basicMap["provider"]
        val accessCount = counts.first
        val searchCount = counts.second

        val business = parseBusiness(doc)
        val reviews = parseReviews(doc)
        val graph = parseAccessGraph(doc)

        val accessGraph = graph.first
        val searchGraph = graph.second

        return PhoneDetail(
            phoneNumber = phone,
            head = head,
            middle = middle,
            subscriber = subscriber,
            numberType = numberType,
            provider = provider,
            accessCount = accessCount,
            searchCount = searchCount,
            accessGraph = accessGraph,
            searchGraph = searchGraph,
            business = business,
            reviews = reviews
        )
    }
}
