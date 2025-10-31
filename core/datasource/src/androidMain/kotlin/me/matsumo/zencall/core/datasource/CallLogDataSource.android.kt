package me.matsumo.zencall.core.datasource

import android.content.Context
import android.os.Bundle
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
        val queryArgs = Bundle().apply {
            putStringArray(
                android.content.ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(CallLog.Calls.DATE),
            )
            putInt(
                android.content.ContentResolver.QUERY_ARG_SORT_DIRECTION,
                android.content.ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
            )
            putInt(android.content.ContentResolver.QUERY_ARG_LIMIT, limit)
            putInt(android.content.ContentResolver.QUERY_ARG_OFFSET, appliedOffset)
        }
        val cursor = resolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            queryArgs,
            null,
        )

        cursor?.use { c ->
            val idIndex = c.getColumnIndexOrThrow(CallLog.Calls._ID)
            val numberIndex = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val cachedNameIndex = c.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
            val typeIndex = c.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val dateIndex = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val durationIndex = c.getColumnIndexOrThrow(CallLog.Calls.DURATION)

            buildList {
                while (c.moveToNext()) {
                    val id = c.getLong(idIndex)
                    val number = c.getString(numberIndex)
                    val cachedName = c.getString(cachedNameIndex)
                    val typeValue = c.getInt(typeIndex)
                    val dateMillis = c.getLong(dateIndex)
                    val duration = c.getInt(durationIndex)

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
                    if (size >= limit) {
                        break
                    }
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
