package me.matsumo.zencall.core.model.call

data class ContactInfo(
    val id: Long,
    val displayName: String,
    val phoneNumbers: List<String>
)