package me.matsumo.zencall.core.ui.utils

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

actual fun formatDateTime(dateMillis: Long): String {
    val locale = Locale.getDefault()
    val time = LocalDateTime.ofEpochSecond(dateMillis / 1000, 0, ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern(if (locale.language == "ja") "M月d日 HH:mm" else "MMM d HH:mm", locale)

    return time.format(formatter)
}