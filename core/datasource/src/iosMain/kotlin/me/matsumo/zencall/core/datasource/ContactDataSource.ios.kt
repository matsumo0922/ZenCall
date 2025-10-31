package me.matsumo.zencall.core.datasource

import me.matsumo.zencall.core.model.call.ContactInfo

class IosContactsDataSource : ContactsDataSource {
    override suspend fun getContacts(): List<ContactInfo> = emptyList()

    @Suppress("UNUSED_PARAMETER")
    override suspend fun getContactByPhoneNumber(phoneNumber: String): ContactInfo? = null
}
