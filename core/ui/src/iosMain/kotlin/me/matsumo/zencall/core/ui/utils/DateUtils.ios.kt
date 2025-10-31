package me.matsumo.zencall.core.ui.utils

import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import kotlin.time.ExperimentalTime

@OptIn(markerClass = [ExperimentalTime::class, FormatStringsInDatetimeFormats::class])
actual fun formatDateTime(dateMillis: Long): String {
    val df = NSDateFormatter().apply {
        this.timeZone = NSTimeZone.localTimeZone
        this.dateFormat = "MMM d HH:mm"
    }

    val date = NSDate.dateWithTimeIntervalSince1970(dateMillis.toDouble() / 1000.0)
    return df.stringFromDate(date)
}