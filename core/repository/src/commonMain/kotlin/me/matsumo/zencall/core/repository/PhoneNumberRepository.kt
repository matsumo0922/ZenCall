package me.matsumo.zencall.core.repository

import io.github.aakira.napier.Napier
import me.matsumo.zencall.core.datasource.api.JpPhoneNumberApi
import me.matsumo.zencall.core.datasource.api.PhoneNumberParseApi
import me.matsumo.zencall.core.model.number.ParsedPhoneNumber

class PhoneNumberRepository(
    private val jpPhoneNumberApi: JpPhoneNumberApi,
    private val phoneNumberParseApi: PhoneNumberParseApi,
) {
    suspend fun getDetail(phoneNumber: String) {
        val parsedPhoneNumber = parse(phoneNumber) ?: return
        val detail = jpPhoneNumberApi.getDetail(parsedPhoneNumber)

        Napier.d { "detail: $detail" }
    }

    suspend fun parse(phoneNumber: String): ParsedPhoneNumber? = phoneNumberParseApi.parse(phoneNumber)
}