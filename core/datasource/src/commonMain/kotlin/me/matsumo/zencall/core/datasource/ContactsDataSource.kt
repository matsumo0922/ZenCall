package me.matsumo.zencall.core.datasource

import me.matsumo.zencall.core.model.call.ContactInfo

interface ContactsDataSource {
    suspend fun getContacts(): List<ContactInfo>
    suspend fun getContactByPhoneNumber(phoneNumber: String): ContactInfo?
}
