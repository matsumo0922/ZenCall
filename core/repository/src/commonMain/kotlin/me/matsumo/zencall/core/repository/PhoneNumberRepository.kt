package me.matsumo.zencall.core.repository

import me.matsumo.zencall.core.datasource.JpPhoneNumberApi
import me.matsumo.zencall.core.datasource.PhoneNumberParseApi
import me.matsumo.zencall.core.model.number.ParsedPhoneNumber

class PhoneNumberRepository(
    private val jpPhoneNumberApi: JpPhoneNumberApi,
    private val phoneNumberParseApi: PhoneNumberParseApi,
) {
    suspend fun parse(phoneNumber: String): ParsedPhoneNumber? = phoneNumberParseApi.parse(phoneNumber)
}