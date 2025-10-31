package me.matsumo.zencall.core.datasource

import me.matsumo.zencall.core.model.call.ContactInfo

interface ContactsDataSource {
    suspend fun getContacts(limit: Int, offset: Int = 0): List<ContactInfo>
    suspend fun getContactByPhoneNumber(phoneNumber: String): ContactInfo?
}
