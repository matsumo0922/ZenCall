package me.matsumo.zencall.core.datasource

import me.matsumo.zencall.core.model.call.ContactInfo

class IosContactsDataSource : ContactsDataSource {
    @Suppress("UNUSED_PARAMETER")
    override suspend fun getContacts(limit: Int, offset: Int): List<ContactInfo> = emptyList()

    @Suppress("UNUSED_PARAMETER")
    override suspend fun getContactByPhoneNumber(phoneNumber: String): ContactInfo? = null
}
