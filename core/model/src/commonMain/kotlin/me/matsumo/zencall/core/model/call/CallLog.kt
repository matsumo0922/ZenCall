package me.matsumo.zencall.core.model.call

data class CallLog(
    val id: Long,
    val number: String?,
    val cachedName: String?,
    val type: Type,
    val dateMillis: Long,
    val durationSec: Int,
    val location: String?,
) {
    enum class Type {
        INCOMING,
        OUTGOING,
        MISSED,
        VOICEMAIL,
        REJECTED,
        BLOCKED,
        ANSWERED_EXTERNALLY,
    }
}