package me.matsumo.zencall.core.datasource.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.matsumo.zencall.core.model.number.ParsedPhoneNumber

class PhoneNumberParseApi(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun parse(phoneNumber: String): ParsedPhoneNumber? = withContext(ioDispatcher) {
        val normalizedPhoneNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        val response = httpClient.get("$PHONE_NUMBER_PARSE_BASE_URL/$normalizedPhoneNumber")

        return@withContext runCatching { response.body<ParsedPhoneNumber>() }.getOrNull()
    }

    companion object {
        private const val PHONE_NUMBER_PARSE_BASE_URL = "https://nb3.jp/api/tel/v4"
    }
}