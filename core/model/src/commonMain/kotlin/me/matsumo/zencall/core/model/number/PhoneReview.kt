package me.matsumo.zencall.core.model.number

import kotlinx.datetime.LocalDateTime

data class PhoneReview(
    val userName: String?,
    val postedAt: LocalDateTime?,
    val body: String
)