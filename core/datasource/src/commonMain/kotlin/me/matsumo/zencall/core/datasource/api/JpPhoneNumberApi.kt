package me.matsumo.zencall.core.datasource.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.matsumo.zencall.core.model.number.ParsedPhoneNumber

class JpPhoneNumberApi(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getDetail(parsedPhoneNumber: ParsedPhoneNumber) = withContext(ioDispatcher) {
        val normalizedPhoneNumber = parsedPhoneNumber.info.telFormat.replace("-", "_")
        val url = "$JP_PHONE_NUMBER_BASE_URL/numberinfo_$normalizedPhoneNumber.html"

        val document = Ksoup.parseGetRequest(url, httpClient = httpClient)
        val result = JpPhoneNumberParser.parse(document)

        Napier.d { "document: $result" }
    }

    companion object {
        private const val JP_PHONE_NUMBER_BASE_URL = "https://www.jpnumber.com"
    }
}