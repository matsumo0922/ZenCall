package me.matsumo.zencall.core.model.number

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParsedPhoneNumber(
    @SerialName("data")
    val info: Info,
    @SerialName("status")
    val status: String
) {
    @Serializable
    data class Info(
        @SerialName("kind")
        val kind: String,
        @SerialName("area_code")
        val areaCode: String,
        @SerialName("local_office_number")
        val localOfficeNumber: String,
        @SerialName("subscriber_number")
        val subscriberNumber: String,
        @SerialName("tel_format")
        val telFormat: String
    )
}