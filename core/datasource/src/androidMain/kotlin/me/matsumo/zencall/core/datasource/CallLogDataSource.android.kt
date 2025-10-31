package me.matsumo.zencall.core.datasource

import android.content.Context
import android.provider.CallLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.matsumo.zencall.core.model.call.CallLog as CallLogModel

class AndroidCallLogDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) : CallLogDataSource {

    override suspend fun getCallLogs(limit: Int, offset: Int): List<CallLogModel> = withContext(ioDispatcher) {
        if (limit <= 0) return@withContext emptyList()

        val resolver = context.contentResolver
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
        )
        val appliedOffset = offset.coerceAtLeast(0)
        val sortOrder = buildString {
            append("${CallLog.Calls.DATE} DESC LIMIT $limit")
            if (appliedOffset > 0) {
                append(" OFFSET ")
                append(appliedOffset)
            }
        }

        resolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder,
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(CallLog.Calls._ID)
            val numberIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val cachedNameIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
            val typeIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val dateIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val durationIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)

            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val number = cursor.getString(numberIndex)
                    val cachedName = cursor.getString(cachedNameIndex)
                    val typeValue = cursor.getInt(typeIndex)
                    val dateMillis = cursor.getLong(dateIndex)
                    val duration = cursor.getInt(durationIndex)

                    add(
                        CallLogModel(
                            id = id,
                            number = number,
                            cachedName = cachedName,
                            type = typeValue.toCallLogType(),
                            dateMillis = dateMillis,
                            durationSec = duration,
                        ),
                    )
                }
            }
        } ?: emptyList()
    }

    private fun Int.toCallLogType(): CallLogModel.Type {
        return when (this) {
            CallLog.Calls.INCOMING_TYPE -> CallLogModel.Type.INCOMING
            CallLog.Calls.OUTGOING_TYPE -> CallLogModel.Type.OUTGOING
            CallLog.Calls.MISSED_TYPE -> CallLogModel.Type.MISSED
            CallLog.Calls.VOICEMAIL_TYPE -> CallLogModel.Type.VOICEMAIL
            CallLog.Calls.REJECTED_TYPE -> CallLogModel.Type.REJECTED
            CallLog.Calls.BLOCKED_TYPE -> CallLogModel.Type.BLOCKED
            CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> CallLogModel.Type.ANSWERED_EXTERNALLY
            else -> CallLogModel.Type.MISSED
        }
    }
}
