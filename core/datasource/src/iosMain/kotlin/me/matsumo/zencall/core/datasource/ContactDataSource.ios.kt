package me.matsumo.zencall.core.datasource

import me.matsumo.zencall.core.model.call.ContactInfo

class IosContactDataSource : ContactDataSource {
    @Suppress("UNUSED_PARAMETER")
    override suspend fun getContacts(limit: Int, offset: Int): List<ContactInfo> = emptyList()
}
