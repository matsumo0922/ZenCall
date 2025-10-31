package me.matsumo.zencall.core.ui.utils

import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
expect fun formatDateTime(dateMillis: Long): String